package workers.morning;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXB;

import config.morning.Morning;
import config.morning.Product;
import config.morning.Products;
import config.morning.Check;
import config.morning.Client;
import config.morning.Configuration;
import config.morning.Master;
import utils.TripleDes;

public class MasterFile {

  private static MasterFile instanceFile;
  private Master master;
  private String masterXmlFile;

  private MasterFile(String masterXmlFile) throws Exception {
    this.masterXmlFile = masterXmlFile;
    this.master = loadXml(masterXmlFile);
    this.validateMasterContent();
  }

  
  /** 
   * @param clientXmlFile
   * @return ClientFile
   * @throws Exception
   */
  public ClientFile inferClientFile(String clientXmlFile) throws Exception {
    ClientFile clientFile = ClientFile.getClientFile(clientXmlFile);
    this.checkClientFileContents(clientFile);
    return clientFile;
  }

  
  /** 
   * @param clientName
   * @return ClientFile
   * @throws Exception
   */
  public ClientFile createClientFile(String clientName) throws Exception
  {
    Master copy = loadXml(this.masterXmlFile);

    Client client = new Client();
    client.setName(clientName);
    client.setMorning(copy.getMorning());

    return  ClientFile.getClientFile(client);
  }

  
  /** 
   * @return String
   */
  @Override
  public String toString() {

    return this.master.toString();
  }

  
  /** 
   * @param masterXmlFile
   * @return MasterFile
   * @throws Exception
   */
  public static MasterFile getInstance(String masterXmlFile) throws Exception {
    if (instanceFile == null) {
      instanceFile = new MasterFile(masterXmlFile);
    }
    return instanceFile;
  }

  
  /** 
   * @param productName
   * @return boolean
   */
  private boolean containsProduct(String productName) {
    Products products = master.getMorning().getConfiguration().getProducts();

    boolean match = false;

    for (Product product : products.getProduct()) {
      if (product.getName().equalsIgnoreCase(productName)) {
        match = true;
      }
    }
    return match;
  }

  
  /** 
   * @param productName
   * @param check
   * @return boolean
   */
  private boolean containsProductCheck(String productName, Check check) {
    Products products = master.getMorning().getConfiguration().getProducts();

    boolean match = false;

    for (Product product : products.getProduct()) {
      if (product.getName().equalsIgnoreCase(productName)) {
        for (Check c : product.getCheck()) {
          if (c.getTagname().equalsIgnoreCase(check.getTagname())) {
            match = true;
          }
        }

      }
    }
    return match;
  }

  
  /** 
   * @throws Exception
   */
  private void validateMasterContent() throws Exception {
    if (master == null) {
      throw new Exception("Master tag is missing in the master file");
    }

    Morning morning = master.getMorning();

    if (morning == null) {
      throw new Exception("Morning tag is missing in the master file");
    }

    Configuration configuration = morning.getConfiguration();

    if (configuration == null) {
      throw new Exception("Configuration tag is missing in the master file");
    }

    Products products = configuration.getProducts();

    if (products == null) {
      throw new Exception("Products tag is missing in the master file");
    }

    ArrayList<Product> product = products.getProduct();

    if (product == null || product.size() == 0) {
      throw new Exception("No products are defined in the master file");
    }

    String lastName = null;

    for (Product p : product) {
      String name = p.getName();
      ArrayList<Check> checks = p.getCheck();

      if (name == null || name.trim().length() == 0) {
        throw new Exception("Names are missing in the product list of the master file");
      }

      if (lastName != null) {
        if (lastName.equalsIgnoreCase(name)) {
          throw new Exception("Duplicated product name: " + name + ", in master file.");
        }
      }

      lastName = name;

      if (checks == null || checks.size() == 0) {
        throw new Exception("Product " + name + " has no checks defined");
      }

      String lastCheck = null;

      for (Check check : checks) {
        String tagname = check.getTagname();

        if (tagname == null || tagname.trim().length() == 0) {
          throw new Exception("There are empty or missing tagname attributes in the checks for the product: " + name);
        }

        if (lastCheck != null) {
          if (lastCheck.equalsIgnoreCase(tagname)) {
            throw new Exception("Duplicated check tag name: " + tagname + ", for product " + name + " in master file.");
          }
        }

        lastCheck = tagname;
      }
    }
  }

  
  /** 
   * @param clientFile
   * @throws Exception
   */
  private void checkClientFileContents(ClientFile clientFile) throws Exception {
    if (clientFile == null) {
      throw new Exception("ClientFile is null");
    }

    Client data = clientFile.getData();

    if (data == null) {
      throw new Exception("Client data is null. Probably a problem in parsing XML");
    }

    Morning morning = data.getMorning();

    if (morning == null) {
      throw new Exception("Morning tag is missing in the client file");
    }

    String clientName = data.getName();
    String clientEnvironment = data.getEnvironment();

    if (clientName == null)
    {
      throw new Exception("Client tag does not contain the name attribute");
    }

    if (clientEnvironment == null)
    {
      throw new Exception("Client tag does not contain the environment attribute");
    }

    Configuration configuration = morning.getConfiguration();

    if (configuration == null) {
      throw new Exception("Configuration tag is missing in the client file");
    }

    Products products = configuration.getProducts();

    if (products != null) {
      String lastName = null;
      String lastEnvironment = null;

      for (Product product : products.getProduct()) {
        String name = product.getName();
        String environment = product.getEnvironment();

        if (name == null || name.trim().length() == 0) {
          throw new Exception("Product name is missing in client file");
        }
 
        if (environment == null || environment.trim().length() == 0) {
          product.setEnvironment(clientEnvironment);
        }

        if (!this.containsProduct(name)) {
          throw new Exception("Product: " + name + " was not found in the master file");
        }

        if (lastName != null && lastEnvironment != null) {
          if (lastName.equalsIgnoreCase(name) && lastEnvironment.equalsIgnoreCase(environment)) {
            throw new Exception("Duplicated product and environment names found for product: " + name
                + " and environment: " + environment + " in client file.");
          }
        }

        lastName = name;
        lastEnvironment = environment;

        String lastCheck = null;

        for (Check check : product.getCheck()) {
          String tagname = check.getTagname();

          if (tagname == null || tagname.trim().length() == 0) {
            throw new Exception("the attribute tagname is missing in the product checks");
          }

          if (lastCheck != null) {
            if (lastCheck.equalsIgnoreCase(tagname)) {
              throw new Exception(
                  "Duplicated check tag name: " + tagname + ", for product " + name + " in master file.");
            }
          }

          if (!this.containsProductCheck(name, check)) {
            throw new Exception(
                "Product: " + name + " and check tag name: " + tagname + ", are not found in the master file");
          }

          lastCheck = tagname;
        }
      }
    }
  }

  
  /** 
   * @param masterXmlFile
   * @return Master
   * @throws Exception
   */
  private static Master loadXml(String masterXmlFile) throws Exception {
    File file = new File(masterXmlFile);

    if (!file.exists()) {
      throw new Exception("Master morning file " + masterXmlFile + " does not exist");
    }

    FileInputStream fis = new FileInputStream(file);
    byte[] data = new byte[(int) file.length()];
    fis.read(data);
    fis.close();

    String xmlString = new String(data, "UTF-8");

    return loadFromXmlString(xmlString);
  }

  
  /** 
   * @param xmlData
   * @return Master
   * @throws Exception
   */
  private static Master loadFromXmlString(String xmlData) throws Exception {
    Pattern REGEX_PATTERN = Pattern.compile("enc\\(.*\\)");
    Matcher matcher = REGEX_PATTERN.matcher(xmlData);

    while (matcher.find()) {
      String matched = matcher.group();
      String decrypted = new TripleDes().decrypt(matched);
      xmlData = xmlData.replace(matched, decrypted);
    }

    StringReader reader = new StringReader(xmlData);
    Master master;

    try {
      master = JAXB.unmarshal(reader, Master.class);
    } catch (Exception exc) {
      throw exc;
    } finally {
      reader.close();
    }

    return master;
  }
}