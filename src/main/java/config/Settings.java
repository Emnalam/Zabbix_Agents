package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import utils.TripleDes;

@XmlRootElement(name="settings")
@XmlAccessorType(XmlAccessType.FIELD)
public class Settings {

  @XmlAttribute(required=true) 
	protected boolean isTestMode;	
	
	@XmlAttribute(required=true) 
	protected boolean useScheduler;	
	
	@XmlAttribute(required=true) 
	protected long defaultFrequenceMilliseconds;
	
	@XmlElement(required=true) 
  protected Workers workers = new Workers();
  
  @XmlElement(required=true) 
	protected Morning morning = new Morning();
	
	@XmlAttribute(required=true) 
	protected boolean logToConsole = true;
	
	@XmlAttribute(required=true) 
	protected boolean logToFile = true;
	
	@XmlAttribute(required=true) 
	protected String logFile = "agents.log";
	
	@XmlAttribute(required=true) 
	protected String logLevel = "info";
	
	@XmlAttribute(required=true) 
	protected int logFileCount = 1;
	
	@XmlAttribute(required=true) 
	protected int logFileSizeInBytes = 10 * 1000000;
	
	@XmlAttribute(required=true) 
	protected String dateFormat = "yyyy-MM-dd HH:mm:ss.SSS";
	
	@XmlAttribute(required=true) 
	protected boolean useTransfer;
	
	@XmlAttribute(required=true)
	protected String transferBin;
	
	@XmlAttribute(required=true) 
	protected String transferServer;
	
	@XmlAttribute(required=true) 
	protected String transferLogin;
	
	@XmlAttribute(required=true) 
	protected String transferPassword;
	
	@XmlAttribute(required=true) 
	protected int transferPort = 21;
	
	@XmlAttribute(required=true) 
	protected int transferConcurrentRequests = 10;
	
	@XmlAttribute(required=true)
	protected String environment;

	
  /** 
   * @return String
   */
  public String getEnvironment() {
		return environment;
	}

	
  /** 
   * @param environment
   */
  public void setEnvironment(String environment) {
		this.environment = environment;
	}

	
  /** 
   * @return String
   */
  public String getTransferBin() {
		return transferBin;
	}

	
  /** 
   * @param transferBin
   */
  public void setTransferBin(String transferBin) {
		this.transferBin = transferBin;
	}

	private static Settings instance;
	
	
	
  /** 
   * @return int
   */
  public int getTransferConcurrentRequests() {
		return transferConcurrentRequests;
	}

	
  /** 
   * @param transferConcurrentRequests
   */
  public void setTransferConcurrentRequests(int transferConcurrentRequests) {
		this.transferConcurrentRequests = transferConcurrentRequests;
	}

	
  /** 
   * @return int
   */
  public int getTransferPort() {
		return transferPort;
	}

	
  /** 
   * @param transferPort
   */
  public void setTransferPort(int transferPort) {
		this.transferPort = transferPort;
	}

	
  /** 
   * @return boolean
   */
  public boolean isUseTransfer() {
		return useTransfer;
	}

	
  /** 
   * @param useTransfer
   */
  public void setUseTransfer(boolean useTransfer) {
		this.useTransfer = useTransfer;
	}

	
  /** 
   * @return String
   */
  public String getTransferServer() {
		return transferServer;
	}

	
  /** 
   * @param transferServer
   */
  public void setTransferServer(String transferServer) {
		this.transferServer = transferServer;
	}

	
  /** 
   * @return String
   */
  public String getTransferLogin() {
		return transferLogin;
	}

	
  /** 
   * @param transferLogin
   */
  public void setTransferLogin(String transferLogin) {
		this.transferLogin = transferLogin;
	}

	
  /** 
   * @return String
   */
  public String getTransferPassword() {
		return transferPassword;
	}

	
  /** 
   * @param transferPassword
   */
  public void setTransferPassword(String transferPassword) {
		this.transferPassword = transferPassword;
	}

	
	
  /** 
   * @return String
   */
  public String getDateFormat() {
		return dateFormat;
	}

	
  /** 
   * @param dateFormat
   */
  public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	
  /** 
   * @return int
   */
  public int getLogFileCount() {
		return logFileCount;
	}

	
  /** 
   * @param logFileCount
   */
  public void setLogFileCount(int logFileCount) {
		this.logFileCount = logFileCount;
	}

	
  /** 
   * @return int
   */
  public int getLogFileSizeInBytes() {
		return logFileSizeInBytes;
	}

	
  /** 
   * @param logFileSizeInBytes
   */
  public void setLogFileSizeInBytes(int logFileSizeInBytes) {
		this.logFileSizeInBytes = logFileSizeInBytes;
	}

	
  /** 
   * @return String
   */
  public String getLogLevel() {
		return logLevel;
	}

	
  /** 
   * @param logLevel
   */
  public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	
	
  /** 
   * @return String
   */
  public String getLogFile() {
		return logFile;
	}

	
  /** 
   * @param logFile
   */
  public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	
  /** 
   * @return boolean
   */
  public boolean isLogToConsole() {
		return logToConsole;
	}

	
  /** 
   * @param logToConsole
   */
  public void setLogToConsole(boolean logToConsole) {
		this.logToConsole = logToConsole;
	}

	
  /** 
   * @return boolean
   */
  public boolean isLogToFile() {
		return logToFile;
	}

	
  /** 
   * @param logToFile
   */
  public void setLogToFile(boolean logToFile) {
		this.logToFile = logToFile;
	}

	
  /** 
   * @return Workers
   */
  public Workers getWorkers() {
		return workers;
	}

	
  /** 
   * @param workers
   */
  public void setWorkers(Workers workers) {
		this.workers = workers;
	}
	
	
  /** 
   * @return boolean
   */
  public boolean getUseScheduler() {
		return useScheduler;
	}

	
  /** 
   * @param useScheduler
   */
  public void setUseScheduler(boolean useScheduler) {
		this.useScheduler = useScheduler;
	}

	
  /** 
   * @return long
   */
  public long getDefaultFrequenceMilliseconds() {
		return defaultFrequenceMilliseconds;
	}

	
  /** 
   * @param defaultFrequenceMilliseconds
   */
  public void setDefaultFrequenceMilliseconds(long defaultFrequenceMilliseconds) {
		this.defaultFrequenceMilliseconds = defaultFrequenceMilliseconds;
	}

	
	
  /** 
   * @param workerInfos
   */
  public void addWorker(WorkerInfos workerInfos)
	{
		if (workers == null)
		{
			workers = new Workers();
		}
		workers.addWorkerInfos(workerInfos);
	}
	
	
  /** 
   * @param fileName
   * @throws Exception
   */
  public void save(String fileName) throws Exception
	{
		StringWriter sw = new StringWriter();
		JAXB.marshal(this, sw);
		String xmlString = sw.toString();
		
		File newTextFile = new File(fileName);

        FileWriter fw = new FileWriter(newTextFile);
        fw.write(xmlString);
        fw.close();
  }
  
  
  /** 
   * @param name
   * @return WorkerInfos
   */
  public WorkerInfos getWorkerInfos(String name)
  {
    for(WorkerInfos workerInfos : workers.getWorkersInfos())
    {
      if (workerInfos.getName().toLowerCase().equals(name.toLowerCase()))
      {
        return workerInfos;
      }
    }
    return null;
  }
	
	
  /** 
   * @return Settings
   * @throws Exception
   */
  public static Settings getInstance() throws Exception
	{
		if (instance == null)
		{
			throw new Exception("Settings have not been initialized with loadFromXml");
		}
		return instance;
	}
	
	
  /** 
   * @param xmlfile
   * @return Settings
   * @throws Exception
   */
  public static Settings loadFromXml(String xmlfile) throws Exception
	{
		File file = new File(xmlfile);
		
		if (!file.exists())
		{
			throw new Exception("Settings file " + xmlfile + " does not exist");
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
   * @return Settings
   * @throws Exception
   */
  public static Settings loadFromXmlString(String xmlData) throws Exception
	{
		Pattern REGEX_PATTERN = Pattern.compile("enc\\(.*\\)");
		Matcher matcher = REGEX_PATTERN.matcher(xmlData);

		while (matcher.find()) {
		    String matched = matcher.group();
		    String decrypted = new TripleDes().decrypt(matched);
		    xmlData = xmlData.replace(matched, decrypted);
		}
		
		StringReader reader = new StringReader(xmlData);
		
		try {
			instance = JAXB.unmarshal(reader, Settings.class);
		}
		catch(Exception exc)
		{
			throw exc;
		}
		finally {
			reader.close();
		}
		
		return instance;
	}

    
    /** 
     * @return boolean
     */
    public boolean isTestMode() {
      return isTestMode;
    }

    
    /** 
     * @param isTestMode
     */
    public void setTestMode(boolean isTestMode) {
      this.isTestMode = isTestMode;
    }

    
    /** 
     * @return Morning
     */
    public Morning getMorning() {
      return morning;
    }

    
    /** 
     * @param morning
     */
    public void setMorning(Morning morning) {
      this.morning = morning;
    }
}
