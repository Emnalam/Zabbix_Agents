package workers.morning;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import config.morning.Check;
import config.morning.CheckResults;
import config.morning.Client;
import config.morning.Data;
import config.morning.Morning;
import config.morning.Product;
import data.apsys.CheckMorningResult;
import data.common.Result;
import utils.DateUtils;
import utils.FileUtils;
import utils.TripleDes;

public class ClientFile {

  private final Client client;
  private final HashMap<Product, TreeMap<String, Result>> filesFragments = new HashMap<Product, TreeMap<String, Result>>();

  private ClientFile(final String clientXmlFile) throws Exception {
    this.client = loadXml(clientXmlFile);
  }

  private ClientFile(final Client data)
  {
    this.client = data;
  }

  
  /** 
   * @param morningXmlFile
   * @throws Exception
   */
  public void save(final String morningXmlFile) throws Exception {
    final StringWriter sw = new StringWriter();
    JAXB.marshal(client, sw);

    String xmlString = sw.toString();

    sw.close();

    for (final Product product : this.filesFragments.keySet()) {
      final TreeMap<String, Result> productFragments = this.filesFragments.get(product);
      for (final String tagName : productFragments.keySet()) {
        Result result = productFragments.get(tagName);
        if (result != null)
        {
          StringWriter swResult = new StringWriter();
          JAXBContext jaxbContext = JAXBContext.newInstance(result.getClass());
          Marshaller marshaller = jaxbContext.createMarshaller();
          marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
          marshaller.marshal(result, swResult);
          final String xmlFragment = swResult.toString();
          xmlString = xmlString.replace("<result>" + tagName + "</result>", "<result>" + xmlFragment + "</result>");
        }
      }
    }

    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    final DocumentBuilder db = dbf.newDocumentBuilder();
    final InputSource is = new InputSource(new StringReader(xmlString));
    final Document doc = db.parse(is);

    final Transformer xformer = TransformerFactory.newInstance().newTransformer(new StreamSource(this.getClass().getResourceAsStream("/indent.xsl")));
    xformer.setOutputProperty("http://www.oracle.com/xml/is-standalone", "yes");
    final StreamResult result = new StreamResult(new StringWriter());
    final DOMSource source = new DOMSource(doc);
    xformer.transform(source, result);
    xmlString = result.getWriter().toString();

    this.save(xmlString, morningXmlFile);
  }

  
  /** 
   * @param filesPath
   * @throws Exception
   */
  public void mergeOutputs(final String filesPath) throws Exception {
    if (!Files.exists(Paths.get(filesPath))) {
      throw new Exception("The path " + filesPath + " does not exist or access is not possible");
    }

    final File path = new File(filesPath);
    final File[] fragments = path.listFiles();

    this.filesFragments.clear();

    for (final Product p : this.client.getMorning().getConfiguration().getProducts().getProduct()) {
      int checkIndex = 0;

      TreeMap<String, Result> productFragments = this.filesFragments.get(p);

      if (productFragments == null) {
        productFragments = new TreeMap<String, Result>();
        this.filesFragments.put(p, productFragments);
      }

      String currentTagName = null;

      for (final Check productCheck : p.getCheck()) {
        boolean productCheckFound = false;
        currentTagName = productCheck.getTagname();
        for (final File fragment : fragments) {
          final Document doc = this.getXmlDocument(fragment);
          final Element root = doc.getDocumentElement();
          final String tagName = root.getTagName();
          if (tagName.equalsIgnoreCase(productCheck.getTagname())) {
            final Node product = doc.getElementsByTagName("product").item(0);
            final Node environment = doc.getElementsByTagName("environment").item(0);

            if (product.getTextContent().equalsIgnoreCase(p.getName())
                && environment.getTextContent().equalsIgnoreCase(p.getEnvironment())) {
              productCheckFound = true;
              final String fragmentText = this.getFragmentText(doc);
              final Result fragmentObject = this.validateFragment(productCheck, fragmentText);
              productFragments.put(++checkIndex + "_" + currentTagName, fragmentObject);
            }
          }
        }
        if (!productCheckFound) {
          productFragments.put(++checkIndex + "_" + currentTagName, null);
        }
      }
    }

    this.setResults();
  }

  
  /** 
   * @return String
   */
  @Override
  public String toString() {
    return this.client.toString();
  }

  
  /** 
   * @param clientXmlFile
   * @return ClientFile
   * @throws Exception
   */
  public static ClientFile getClientFile(final String clientXmlFile) throws Exception {
    return new ClientFile(clientXmlFile);
  }

  
  /** 
   * @param data
   * @return ClientFile
   */
  protected static ClientFile getClientFile(final Client data) {
    return new ClientFile(data);
  }

  
  /** 
   * @return Client
   */
  protected Client getData() {
    return this.client;
  }

  
  /** 
   * @param check
   * @param xmlFragment
   * @return Result
   * @throws Exception
   */
  private Result validateFragment(final Check check, final String xmlFragment) throws Exception {
    String validatorName = check.getValidator();
    if (validatorName == null || validatorName.trim().length() == 0)
    {
      validatorName = MorningCheckValidator.getDefaultCheckValidatorName(check.getTagname());
    }
    final Class<?> clazz = Class.forName(validatorName);
    final Constructor<?> ctor = clazz.getConstructor();
    final MorningCheckValidator validator = (MorningCheckValidator) ctor.newInstance();
    
    return validator.validate(check, xmlFragment);
  }

  
  /** 
   * @param fragment
   * @return Document
   * @throws Exception
   */
  private Document getXmlDocument(final File fragment) throws Exception {
    final String xmlRecords = FileUtils.readFile(fragment);
    final InputSource is = new InputSource();
    is.setCharacterStream(new StringReader(xmlRecords));

    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    final DocumentBuilder db = dbf.newDocumentBuilder();

    return db.parse(is);
  }

  
  /** 
   * @param doc
   * @return String
   * @throws Exception
   */
  private String getFragmentText(final Document doc) throws Exception {
    final StringWriter sw = new StringWriter();
    final TransformerFactory tf = TransformerFactory.newInstance();
    final Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

    transformer.transform(new DOMSource(doc), new StreamResult(sw));

    return sw.toString().trim();
  }

  private void setResults() {
    final Morning morning = client.getMorning();
    final Data results = new Data();
    results.setTimestamp(DateUtils.getCurrentTime("yyyyMMddHHmm"));

    boolean isCheckOk = true;

    for (final Product p : this.filesFragments.keySet()) {
      final TreeMap<String, Result> productFragments = this.filesFragments.get(p);
      final SortedSet<String> keys = new TreeSet<>(productFragments.keySet());

      final Product product = new Product();
      product.setName(p.getName());
      product.setEnvironment(p.getEnvironment());
      if (product.getCheckResults() == null) {
        product.setCheckResults(new CheckResults());
      }
      for (final String tagName : keys) {
        final Result value = productFragments.get(tagName);
        if (value == null) {
          isCheckOk = false;
          String missing = product.getCheckResults().getMissing();
          if (missing == null) {
            missing = tagName.substring(tagName.indexOf("_") + 1);
          } else {
            missing += "," + tagName.substring(tagName.indexOf("_") + 1);
          }
          product.getCheckResults().setMissing(missing);
        } else {
          CheckMorningResult mresult = (CheckMorningResult)value;
          Boolean bCheck = mresult.isCheckOk();
          if (bCheck == null || !bCheck.booleanValue())
          {
            isCheckOk = false;
          }
          
          results.setBatchAborted(new Boolean(mresult.isBatchAborted()).toString());

          product.getCheckResults().getResult().add(tagName);
        }
      }

      results.getProduct().add(product);
    }

    results.setOk(new Boolean(isCheckOk).toString());
    morning.setData(results);
  }

  
  /** 
   * @param xmlString
   * @param outputFile
   * @throws Exception
   */
  private void save(final String xmlString, final String outputFile) throws Exception {
    final File newTextFile = new File(outputFile);
    String directory = newTextFile.getParent();
    if (directory != null)
    {
      File dCheck = new File(directory);
      if (!dCheck.exists())
      {
        dCheck.mkdirs();
      }
    }
    
    final FileWriter fw = new FileWriter(newTextFile);
    fw.write(xmlString);
    fw.close();
  }

  
  /** 
   * @param masterXmlFile
   * @return Client
   * @throws Exception
   */
  private static Client loadXml(final String masterXmlFile) throws Exception {
    final File file = new File(masterXmlFile);

    if (!file.exists()) {
      throw new Exception("Master morning file " + masterXmlFile + " does not exist");
    }

    final FileInputStream fis = new FileInputStream(file);
    final byte[] data = new byte[(int) file.length()];
    fis.read(data);
    fis.close();

    final String xmlString = new String(data, "UTF-8");

    return loadFromXmlString(xmlString);
  }

  
  /** 
   * @param xmlData
   * @return Client
   * @throws Exception
   */
  private static Client loadFromXmlString(String xmlData) throws Exception {
    final Pattern REGEX_PATTERN = Pattern.compile("enc\\(.*\\)");
    final Matcher matcher = REGEX_PATTERN.matcher(xmlData);

    while (matcher.find()) {
      final String matched = matcher.group();
      final String decrypted = new TripleDes().decrypt(matched);
      xmlData = xmlData.replace(matched, decrypted);
    }

    final StringReader reader = new StringReader(xmlData);
    Client client;

    try {
      client = JAXB.unmarshal(reader, Client.class);
    } catch (final Exception exc) {
      throw exc;
    } finally {
      reader.close();
    }

    return client;
  }
}