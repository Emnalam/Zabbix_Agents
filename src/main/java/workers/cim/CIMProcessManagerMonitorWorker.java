package workers.cim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import config.WorkerInfos;
import data.cim.CIMProcessManagerMonitorResult;
import utils.DateUtils;
import workers.AgentLogger;
import workers.Worker;


public class CIMProcessManagerMonitorWorker extends Worker {

	private static ProcessManagerThreadMonitor processManagerThreadMonitor;
	private static Thread processManagerThreadMonitorThread;
	private static int rowCount;
	private static boolean isRefreshing;
	private static Object syncRoot = new Object();
	private static long lastCheckTime1;
	private static long lastCheckTime2;
	
	private static int expectedThreadCountRealCheck;
	
	private Map<Integer,PluginConfiguration> pluginConfigurations = new HashMap<Integer, PluginConfiguration>();

	private String jdbcDriver;
	private String dbUrl;
	private String dbUser;
	private String dbPwd;
	private String dbSchema;	
	private String query;
	
	
  /** 
   * @return long
   */
  public static long GetLastCheckTime1()
	{
		return lastCheckTime1;
	}
	
	
  /** 
   * @return long
   */
  public static long GetLastCheckTime2()
	{
		return lastCheckTime2;
	}
	
	
  /** 
   * @throws Exception
   */
  @Override
	protected void init() throws Exception {
		if (lastCheckTime1 > 0)
		{
			lastCheckTime1 = lastCheckTime2;
		}
		else
		{
			lastCheckTime1 = System.currentTimeMillis();
		}
		
		lastCheckTime2 = System.currentTimeMillis();	

		String additionalParameter = this.workerInfos.getAdditionalParameters();
		
		if (additionalParameter == null || additionalParameter.length() == 0)
		{
			throw new Exception("additional parameters were not set for " + this.getClass().getName());
		}
		
		String[] elements = additionalParameter.split("\\|\\|");

		for (String paramLine : elements)
		{
			String[] paramPair = paramLine.split("\\|");
			
			if (paramPair.length != 2)
			{
				throw new Exception("Invalid additional parameter: " + paramLine + ". Format: key|value");
			}
			parameters.put(paramPair[0].toLowerCase(), paramPair[1]);
		}
		
		if (!parameters.containsKey("dburl") || !parameters.containsKey("dbuser") || !parameters.containsKey("dbpwd") || !parameters.containsKey("dbschema")) {
			throw new Exception("Mandatory additional parameters missing: dburl/dbuser/dbpwd/dbschema");
		}

		this.jdbcDriver = "oracle.jdbc.driver.OracleDriver";
		this.dbUrl      = parameters.get("dburl").trim();
		this.dbUser     = parameters.get("dbuser").trim();
		this.dbPwd      = parameters.get("dbpwd").trim();
		this.dbSchema   = parameters.get("dbschema").trim();
		this.query      = "select * from " + this.dbSchema + ".prozesssetup";
				
		this.RefreshPluginConfigurations(false);
		
		
		for(PluginConfiguration configuration : pluginConfigurations.values())
		{
			PluginChecker checker = new PluginChecker(this, configuration, this.workerInfos);
			checker.ValidateConfiguration();
		}
	}

	
  /** 
   * @throws Exception
   */
  @Override
	protected void doWork() throws Exception {
		
		if (processManagerThreadMonitor == null)
		{
			processManagerThreadMonitor = new ProcessManagerThreadMonitor(this, this.workerInfos);
			processManagerThreadMonitorThread = new Thread(processManagerThreadMonitor);
			processManagerThreadMonitorThread.start();
			AgentLogger.logInfos("CIM Process Manager plugin configuration: " + rowCount + " configuration row(s) retrieved from database");
		}
		
		this.RefreshPluginConfigurations(true);
    
    ArrayList<String> expectedPlugins = this.getExpectedPlugins();
    
		for(PluginConfiguration configuration : pluginConfigurations.values())
		{
      String pluginName = configuration.getProcess();
      if (expectedPlugins.contains(pluginName))
      {
			  PluginChecker checker = new PluginChecker(this, configuration, this.workerInfos);
			  Thread t = new Thread(checker);
        t.start();
        AgentLogger.logTrace("Checking expected plugin: " + pluginName);
      }
		}
	}

	
  /** 
   * @throws Exception
   */
  @Override
	protected void cleanUp() throws Exception {
		
	}
	
	
  /** 
   * @param pluginName
   * @param output
   * @throws Exception
   */
  protected void appendPluginCheckerOutput(String pluginName, String output) throws Exception
	{
		CIMProcessManagerMonitorResult result = new CIMProcessManagerMonitorResult();
		result.setErrorPluginName(pluginName);
		result.setErrorChecker(output.replaceAll("\r\n", ";;;;NewLine;;;;"));
		
		this.save(result);
	}
	
	
  /** 
   * @param pluginName
   * @param output
   * @throws Exception
   */
  protected void appendPluginLogErrorOutput(String pluginName, String output) throws Exception
	{
		CIMProcessManagerMonitorResult result = new CIMProcessManagerMonitorResult();
		result.setErrorPluginName(pluginName);
		result.setErrorLog(output.replaceAll("\r\n", ";;;;NewLine;;;;"));
		
		this.save(result);
	}
	
	
  /** 
   * @param output
   * @throws Exception
   */
  protected void appendThreadMonitorOutput(String output) throws Exception
	{
		if (isRefreshing)
		{
			synchronized(syncRoot)
			{
				syncRoot.wait(30000);
			}
		}
		
		CIMProcessManagerMonitorResult result = new CIMProcessManagerMonitorResult();
		
		ArrayList<String> expectedPlugins = this.getExpectedPlugins();
		
		StringBuilder out = new StringBuilder();
		for (String s : expectedPlugins)
		{
		  out.append(s);
		  out.append(";;;;");
		}
		
		int expectedThreads = expectedPlugins.size();
		
		result.setExpectedThreads(String.valueOf(expectedThreads));
		result.setExpectedPlugins(out.toString());
		
		String[] elements = output.split(",");
		
		if (elements[0].equals("0") && elements[1].equals("0"))
		{
			result.setTotalThreads("0");
			result.setIdleThreads("0");
			result.setErrorProcess("NotFound");
		}
		else if (elements[0].equals("-1") && elements[1].equals("-1"))
		{
			result.setTotalThreads("-1");
			result.setIdleThreads("-1");
			result.setErrorProcess("MultipleInstancesFound");
		}
		else if (elements[0].equals("-2") && elements[1].equals("-2")) //Process is running but no threads. Bug found by Fred
		{
			result.setTotalThreads("0");
			result.setIdleThreads("0");
			result.setErrorProcess("OK");
    }
    else
		{
			int idleThreads = new Integer(elements[0]);
			int totalThreads = new Integer(elements[1]);
			
			if (totalThreads < expectedThreads && expectedThreadCountRealCheck <= 3)
			{
				++expectedThreadCountRealCheck;
				return;
			}
			
			expectedThreadCountRealCheck = 0;
			
			result.setTotalThreads(String.valueOf(totalThreads));
			result.setIdleThreads(String.valueOf(idleThreads));
			result.setErrorProcess("OK");
			result.setErrorThread("OK");
			
			if (expectedThreads > totalThreads)
			{
				result.setErrorThread("PluginsNotRunning");
			}
			
			if (idleThreads > 0)
			{
				result.setErrorThread("IdlePlugins");
			}
		}
		
		this.save(result);
	}
	
	
  /** 
   * @return ArrayList<String>
   */
  private ArrayList<String> getExpectedPlugins()
	{
		int today = DateUtils.getToday();
		String strTime = DateUtils.getCurrentTime("Hmm");
		int time = new Integer(strTime);
		
		ArrayList<String> expectedPlugins = new ArrayList<String>();
		
		AgentLogger.logTrace(this.getName() + ", getExpectedPlugins: today: " + today + ", time: " + strTime);
		
		for(PluginConfiguration pluginconfig : this.pluginConfigurations.values())
		{
			String monday = pluginconfig.getMondayCd();
			String tuesday = pluginconfig.getTuesdayCd();
			String wednesday = pluginconfig.getWednesdayCd();
			String thrusday = pluginconfig.getThursdayCd();
			String friday = pluginconfig.getFridayCd();
			String saturday = pluginconfig.getSaturdayCd();
			String sunday = pluginconfig.getSundayCd();
			
			double startTime = pluginconfig.getStartTime();
			double endTime = pluginconfig.getEndTime();
			
			boolean isInTime = time >= startTime && time <= endTime;
		
			
			if (!isInTime)
			{
				continue;
			}
			
			if (today == Calendar.MONDAY)
			{
				if (monday != null && monday.toLowerCase().equals("j"))
				{
					expectedPlugins.add(pluginconfig.getProcess());
				}
			}
			else  
				if (today == Calendar.TUESDAY)
				{
					if (tuesday != null && tuesday.toLowerCase().equals("j"))
					{
						expectedPlugins.add(pluginconfig.getProcess());
					}
				}
			else
				if (today == Calendar.WEDNESDAY)
				{
					if (wednesday != null && wednesday.toLowerCase().equals("j"))
					{
						expectedPlugins.add(pluginconfig.getProcess());
					}
				}
			else
				if (today == Calendar.THURSDAY)
				{
					if (thrusday != null && thrusday.toLowerCase().equals("j"))
					{
						expectedPlugins.add(pluginconfig.getProcess());
					}
				}
			else
				if (today == Calendar.FRIDAY)
				{
					if (friday != null && friday.toLowerCase().equals("j"))
					{
						expectedPlugins.add(pluginconfig.getProcess());
					}
				}
			else
				if (today == Calendar.SATURDAY)
				{
					if (saturday != null && saturday.toLowerCase().equals("j"))
					{
						expectedPlugins.add(pluginconfig.getProcess());
					}
				}
			else
				if (today == Calendar.SUNDAY)
				{
					if (sunday != null && sunday.toLowerCase().equals("j"))
					{
						expectedPlugins.add(pluginconfig.getProcess());
					}
				}
		}
		
		int numberOfThreads = expectedPlugins.size();
		
		AgentLogger.logTrace(this.getName() + ", numberOfThreads: " + numberOfThreads);
		
		return expectedPlugins;
	}
	

	
  /** 
   * @param writeOutputOnError
   * @throws Exception
   */
  private void RefreshPluginConfigurations(boolean writeOutputOnError) throws Exception
	{
		Connection con = null;
		ResultSet rs = null;
		int count = 0;
		
		try {
			
			isRefreshing = true;
			Class.forName(this.jdbcDriver);
			con = DriverManager.getConnection(this.dbUrl,this.dbUser,this.dbPwd);
			

			rs = con.createStatement().executeQuery(this.query);
			
			pluginConfigurations.clear();
		   
			while (rs.next()) {
		      
				PluginConfiguration pluginConfig = new PluginConfiguration();
				
		        int processId = rs.getInt("PROZESSID");
		        String process = rs.getString("PROZESS");
		        String inputDirectory = rs.getString("INPUTVERZEICHNIS");
		        String inputExtensionCd = rs.getString("INPUTEXTENSION_CD");
		        String localWorkingDirectory = rs.getString("LOKALESVERZEICHNIS");
		        int processTimeoutSec = rs.getInt("PROZESSTIMEOUT_SEC");
		        int startTime = rs.getInt("STARTTIME");
		        int endTime = rs.getInt("ENDTIME");
		        String mondayCd = rs.getString("MONTAG_CD");
		        String tuesdayCd = rs.getString("DIENSTAG_CD");
		        String wednesdayCd = rs.getString("MITTWOCH_CD");
		        String thursdayCd = rs.getString("DONNERSTAG_CD");
		        String fridayCd = rs.getString("FREITAG_CD");
		        String saturdayCd = rs.getString("SAMSTAG_CD");
		        String sundayCd = rs.getString("SONNTAG_CD");
		        
		        pluginConfig.setProcessId(processId);
		        pluginConfig.setProcess(process);
		        pluginConfig.setInputDirectory(inputDirectory);
		        pluginConfig.setInputExtensionCd(inputExtensionCd);
		        pluginConfig.setLocalWorkingDirectory(localWorkingDirectory);
		        pluginConfig.setProcessTimeoutSec(processTimeoutSec);
		        pluginConfig.setStartTime(startTime);
		        pluginConfig.setEndTime(endTime);
		        
		        pluginConfig.setMondayCd(mondayCd);
		        pluginConfig.setTuesdayCd(tuesdayCd);
		        pluginConfig.setWednesdayCd(wednesdayCd);
		        pluginConfig.setThursdayCd(thursdayCd);
		        pluginConfig.setFridayCd(fridayCd);
		        pluginConfig.setSaturdayCd(saturdayCd);
		        pluginConfig.setSundayCd(sundayCd);
		        
		        pluginConfigurations.put(processId, pluginConfig);
		        
		        count++;
		    }
			rowCount = count;
		}
		catch(Exception exc)
		{
			if (writeOutputOnError)
			{
				this.appendPluginLogErrorOutput("RefreshPluginConfigurations", "Failed to connect to oracle database: " + exc.getMessage());
			}
			throw exc;
		}
		finally {
			if (con != null)
			{
				con.close();
			}
			if (rs != null)
			{
				rs.close();
			}
			isRefreshing = false;
			synchronized(syncRoot)
			{
				syncRoot.notifyAll();
			}
		}
		
		if (rowCount == 0)
	    {
	    	throw new Exception("The query: " + this.query + ", returned no rows");
	    }
		else
		{
			AgentLogger.logTrace("CIM Process Manager plugin configuration: " + rowCount + " configuration row(s) retrieved from database");
		}
	}

}

class ProcessManagerThreadMonitor implements Runnable
{
	CIMProcessManagerMonitorWorker worker;
	WorkerInfos workerInfos;
	Process p;
	
	public ProcessManagerThreadMonitor(CIMProcessManagerMonitorWorker worker, WorkerInfos workerInfos)
	{
		this.worker = worker;
		this.workerInfos = workerInfos;
	}
	
	@Override
	public void run() {
		
		BufferedReader br = null;
		
		try {
			
			StringBuffer sb = new StringBuffer();
			
			String command = this.workerInfos.getCommand();
			
			p = Runtime.getRuntime().exec(command);

			Worker.addRunningProcess(p);
			
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = null;
	        
			AgentLogger.logTrace("ProcessManagerThreadMonitor: listening to the tool output stream...");
			
			while ((line = br.readLine()) != null) {
				if (line.startsWith("-START-"))
				{
					sb.delete(0, sb.length());
				}
				else if (line.startsWith("-END-"))
				{
					String output = sb.toString();
					sb.delete(0, sb.length());
					this.worker.appendThreadMonitorOutput(output);
					AgentLogger.logTrace("CIM Process Thread Monitor output: " + output);
				}
				else
				{
					sb.append(line);
				}
	        }
		}
		catch(Exception exc)
		{
			AgentLogger.logError(exc, this.workerInfos);
		}
		finally {
			if (br != null)
			{
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}
	}
}

class PluginChecker implements Runnable
{
	private static Map<String, Long> activeLogFilesSeekPosition = new HashMap<String, Long>();
	private CIMProcessManagerMonitorWorker worker;
	private PluginConfiguration configuration;
	private String errorFolder = "FEHLER";
	private String inFolder = "IN";
	private String inputFolder = "Input";
	private String logFolder = "LOG";
	private String outFolder = "OUT";
	private String tempFolder = "TEMP";
	private String watchedFolder;
  private WorkerInfos workerInfos;
  private String inputExtensionCode;

  private static Map<String, String> extensionCodeMaps;
	
	public PluginChecker(CIMProcessManagerMonitorWorker worker, PluginConfiguration configuration, WorkerInfos workerInfos)
	{
		this.worker = worker;
		this.workerInfos = workerInfos;
		this.configuration = configuration;
		this.errorFolder = new File(configuration.localWorkingDirectory, this.errorFolder).getAbsolutePath();
		this.inFolder = new File(configuration.localWorkingDirectory, this.inFolder).getAbsolutePath();
		this.inputFolder = new File(configuration.localWorkingDirectory, this.inputFolder).getAbsolutePath();
		this.logFolder = new File(configuration.localWorkingDirectory, this.logFolder).getAbsolutePath();
		this.outFolder = new File(configuration.localWorkingDirectory, this.outFolder).getAbsolutePath();
		this.tempFolder = new File(configuration.localWorkingDirectory, this.tempFolder).getAbsolutePath();
    this.watchedFolder = configuration.inputDirectory;
    this.inputExtensionCode = configuration.inputExtensionCd;
		
		AgentLogger.logTrace("Plugin Checker paths for : " + configuration.process);
		AgentLogger.logTrace("errorFolder: " + this.errorFolder);
		AgentLogger.logTrace("inFolder: " + this.inFolder);
		AgentLogger.logTrace("inputFolder: " + this.inputFolder);
		AgentLogger.logTrace("logFolder: " + this.logFolder);
		AgentLogger.logTrace("outFolder: " + this.outFolder);
		AgentLogger.logTrace("tempFolder: " + this.tempFolder);
    AgentLogger.logTrace("watchedFolder: " + this.watchedFolder);
    AgentLogger.logTrace("InputExtensionCode: " + this.inputExtensionCode);

    if (extensionCodeMaps == null)
    {
      extensionCodeMaps = new HashMap<String, String>();
      extensionCodeMaps.put("0","EPS");
      extensionCodeMaps.put("1","PS");
      extensionCodeMaps.put("2","TXT");
      extensionCodeMaps.put("3","PDF");
      extensionCodeMaps.put("4","ADV_*");
      extensionCodeMaps.put("5","P00*");
      extensionCodeMaps.put("6","*_AR");
      extensionCodeMaps.put("7","RPT");
      extensionCodeMaps.put("8","PRT");
      extensionCodeMaps.put("9","ADV_????");
      extensionCodeMaps.put("10","*");
      extensionCodeMaps.put("11","TXT_*");
      extensionCodeMaps.put("12","ADV*");
      extensionCodeMaps.put("13","TIF");
      extensionCodeMaps.put("14","ARC");
      extensionCodeMaps.put("15","MIC");
      extensionCodeMaps.put("16","P00.PDF");
      extensionCodeMaps.put("17","ADV.PDF");
      extensionCodeMaps.put("18","ASC");
      extensionCodeMaps.put("19","XML");
      extensionCodeMaps.put("20","XLS");
      extensionCodeMaps.put("21","DOC");
      extensionCodeMaps.put("22","MSG");
      extensionCodeMaps.put("23","DOCX");
      extensionCodeMaps.put("24","XLSX");
      extensionCodeMaps.put("25","GIF");
      extensionCodeMaps.put("26","JPG");
      extensionCodeMaps.put("27","PPT");
      extensionCodeMaps.put("28","PPTX");
      extensionCodeMaps.put("29","CSV");
      extensionCodeMaps.put("30","XML,CSV,TXT");
      extensionCodeMaps.put("31","PDF,TXT");
      extensionCodeMaps.put("32","HTML");
    }
	}
	
	
	@Override
	public void run() {
		try {
			AgentLogger.logTrace("Start Plugin Checker for: " + configuration.process);
			
			if (this.checkFolder(this.outFolder))
			{
				checkEmptyFilesInOutputFolder();
			}
			if (this.checkFolder(this.errorFolder))
			{
				checkNewItemsInErrorFolder();
			}
			if (this.checkFolder(this.watchedFolder))
			{
				checkOrphanedItemsInWatchedFolder();
			}
			if (this.checkFolder(this.inFolder))
			{
				checkOrphanedItemsInInputFolder();
			}
			if (this.checkFolder(this.logFolder))
			{
				checkNewLogEntriesInLogFolder();
			}
		
			AgentLogger.logTrace("End Plugin Checker for: " + configuration.process);
		}
		catch(Exception exc)
		{
			AgentLogger.logError(exc, this.workerInfos);
		}
	}
	
	public void ValidateConfiguration() throws Exception
	{
		this.checkFolder(this.errorFolder);
		this.checkFolder(this.inFolder);
		this.checkFolder(this.logFolder);
		this.checkFolder(this.outFolder);
		this.checkFolder(this.watchedFolder);
  }
  
  private String getExtensionCodeMap()
  {
    if (this.inputExtensionCode== null || this.inputExtensionCode.length() == 0)
    {
      return null;
    }
    if (!extensionCodeMaps.containsKey(this.inputExtensionCode))
    {
      return null;
    }
    
    String codeMap = extensionCodeMaps.get(inputExtensionCode);
    codeMap = codeMap.replace(".", "\\.");

    String regex = null;

    if (codeMap.indexOf(",") > -1)
    {
      String[] extensions = codeMap.split(",");
      String str = "";
      for(String extension : extensions)
      {
        str += extension.trim() + "|";
      }
      str = str.substring(0, str.length()-1);

      regex = ".*\\.(" + str + ")";
    }
    else if (codeMap.indexOf("*") > -1)
    {
      regex = ".*" + codeMap.replace("*", ".*");
    }
    else if (codeMap.indexOf("?") > -1)
    {
      int count = codeMap.length() - codeMap.replace("?", "").length();

      String chars = "";

      for(int i = 1; i <= count; i++)
      {
        chars += "?";
      }
      
      regex = ".*\\." + codeMap.replace(chars, ".{0," + count + "}");
    }
    else
    {
      regex = ".*\\.(" + codeMap + ")";
    }

    return regex;
  }


  private boolean filterByExtensionCodeMap(File file)
  {
    String extensionCodeMap = getExtensionCodeMap();
    if (extensionCodeMap != null)
    {
      AgentLogger.logTrace("FilterByExtensionCodeMap: expression is " + extensionCodeMap + " and file name is: " + file.getName());
      return file.getName().toLowerCase().matches(extensionCodeMap.toLowerCase());
    }
    return true;
  }
	
	private boolean checkFolder(String folder) throws Exception
	{
		if (folder == null)
		{
			AgentLogger.logTrace("NULL folders in configuration. Set log level to 'trace' to get more infos.");
			return false;
		}
		else 
		{
			File dir = new File(folder);
		
			if (!dir.exists())
			{
				AgentLogger.logTrace("Folder " + folder + " does not exist");
				return false;
			}
			else if (!dir.canRead())
			{
				throw new Exception("Read permission is denied on folder: " + folder);
			}
		}
		return true;
	}
	
	private File lastEmptyFile(String dir) {
	    File fl = new File(dir);
	    File[] files = fl.listFiles(new FileFilter() {          
	        public boolean accept(File file) {
	            return file.isFile() && file.length() <= 3; //3 because of Unicode BOM
	        }
	    });
	    if (files != null)
	    {
	    	long lastMod = Long.MIN_VALUE;
		    File choice = null;
		    for (File file : files) {
		        if (file.lastModified() > lastMod) {
		            choice = file;
		            lastMod = file.lastModified();
		        }
		    }
		    return choice;
	    }
	    return null;
	}
	
	private void checkEmptyFilesInOutputFolder()
	{
		try {
			File lastModified = lastEmptyFile(this.outFolder);
			
			if (lastModified != null && lastModified.lastModified() > CIMProcessManagerMonitorWorker.GetLastCheckTime1())
			{
				this.worker.appendPluginCheckerOutput(this.configuration.getProcess(), "Output folder: new empty file(s) have reached the output folder");	
			}
		}
		catch(Exception exc)
		{
			AgentLogger.logError(exc, this.workerInfos);
		}
	}
	
	private void checkNewItemsInErrorFolder()
	{
		try {
			File directory = new File(this.errorFolder);
			if (directory.exists())
			{
				File[] files = directory.listFiles(new FileFilter() {
				    @Override
				    public boolean accept(File f) {
				    	return f.lastModified() > CIMProcessManagerMonitorWorker.GetLastCheckTime1();
				    }
				});
				
				
				if (files.length > 0)
				{
					this.worker.appendPluginCheckerOutput(this.configuration.getProcess(), "Error folder: " + files.length + " file(s) have reached the error folder");
				}
			}
		}
		catch(Exception exc)
		{
			AgentLogger.logError(exc, this.workerInfos);
		}
	}
	
	private void checkOrphanedItemsInWatchedFolder()
	{
		try {
			File directory = new File(this.watchedFolder);
			if (directory.exists())
			{
				File[] files = directory.listFiles(new FileFilter() {
				    @Override
				    public boolean accept(File f) {
				    	Date d1 = new Date(f.lastModified());
						Date d2 = new Date();
						long diff = DateUtils.getDateDiffSeconds(d1, d2);
            //double ntimeout = 3 * configuration.processTimeoutSec;
            double ntimeout = 900;
						return filterByExtensionCodeMap(f) && diff >= ntimeout && (f.lastModified() + (ntimeout * 1000)) > CIMProcessManagerMonitorWorker.GetLastCheckTime1();
				    }
				});
				
				if (files != null && files.length > 0)
				{
          String fileNames = "";
          for (File theIdleFiles : files)
          {
            fileNames += theIdleFiles.getAbsolutePath() + ", ";
          }
          fileNames = fileNames.substring(0, fileNames.length() - 2);

					this.worker.appendPluginCheckerOutput(this.configuration.getProcess(), "Watched folder: " + files.length + " file(s) have been in the watched folder for more than 3 occurences of the plugin timeout: " + fileNames);
				}
			}
		}
		catch(Exception exc)
		{
			AgentLogger.logError(exc, this.workerInfos);
		}
	}
	
	private void checkOrphanedItemsInInputFolder()
	{
		try {
			File directory = new File(this.inFolder);
			if (directory.exists())
			{
				File[] files = directory.listFiles(new FileFilter() {
				    @Override
				    public boolean accept(File f) {
             
				    	Date d1 = new Date(f.lastModified());
              Date d2 = new Date();
              long diff = DateUtils.getDateDiffSeconds(d1, d2);
              //double ntimeout = 3 * configuration.processTimeoutSec;
              double ntimeout = 900;
              return filterByExtensionCodeMap(f) && diff >= ntimeout && (f.lastModified() + (ntimeout * 1000)) > CIMProcessManagerMonitorWorker.GetLastCheckTime1();
				    }
				});
				
				
				if (files.length > 0)
				{
          String fileNames = "";
          for (File theIdleFiles : files)
          {
            fileNames += theIdleFiles.getAbsolutePath() + ", ";
          }
          fileNames = fileNames.substring(0, fileNames.length() - 2);

					this.worker.appendPluginCheckerOutput(this.configuration.getProcess(), "IN folder: " + files.length + " file(s) have been in the watched folder for more than 3 occurences of the plugin timeout: " + fileNames);
				}
			}
		}
		catch(Exception exc)
		{
			AgentLogger.logError(exc, this.workerInfos);
		}
	}
	
	private void checkNewLogEntriesInLogFolder()
	{
		RandomAccessFile raf = null;
		try {
			File directory = new File(this.logFolder);
			if (directory.exists())
			{
				File[] files = directory.listFiles(new FileFilter() {
				    @Override
				    public boolean accept(File f) {
				    	return DateUtils.isToday(new Date(f.lastModified()));
				    }
				});
				
			for(File file : files)
			{
				if (file.isFile())
				{
						String fpath = file.getAbsolutePath();
						if (!activeLogFilesSeekPosition.containsKey(fpath))
						{
							activeLogFilesSeekPosition.put(fpath, file.length());
						}
						else
						{
							long seekPosition = activeLogFilesSeekPosition.get(fpath);
							
							if (seekPosition < file.length())
							{
								raf = new RandomAccessFile(fpath, "r");
								raf.seek(seekPosition);
								
								byte[] buffer = new byte[(int) (file.length()-seekPosition)];
								if (raf.read(buffer) > 0)
								{
                  String newItems = new String(buffer);
                  
                  final String[] lines = newItems.split("[\\r\\n]+");
									
                  AgentLogger.logTrace(newItems);
                  
                  String process = this.configuration.getProcess();

                  for(String line : lines)
                  {
                    if (line.toLowerCase().contains("error") || line.toLowerCase().contains("warn") || line.contains("ORA-"))
                    {
                      this.worker.appendPluginLogErrorOutput(process, line);
                    }
                  }
								}
								
								raf.close();
								raf = null;
								
								activeLogFilesSeekPosition.put(fpath, file.length());
							}
						}
					}
				}
			}
		}
		catch(Exception exc)
		{
			AgentLogger.logError(exc, this.workerInfos);
			
			if (raf != null)
			{
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

class PluginConfiguration
{
	protected int processId;
	protected String process;
	protected String inputDirectory;
	protected String inputExtensionCd;
	protected String localWorkingDirectory;
	protected double processTimeoutSec;
	protected double startTime;
	protected double endTime;
	protected String mondayCd;
	protected String tuesdayCd;
	protected String wednesdayCd;
	protected String thursdayCd;
	protected String fridayCd;
	protected String saturdayCd;
	protected String sundayCd;

	public int getProcessId() {
		return processId;
	}
	public void setProcessId(int processId) {
		this.processId = processId;
	}
	public String getProcess() {
		return process;
	}
	public void setProcess(String process) {
		this.process = process;
	}
	public String getInputDirectory() {
		return inputDirectory;
	}
	public void setInputDirectory(String inputDirectory) {
		this.inputDirectory = inputDirectory;
	}
	public String getInputExtensionCd() {
		return inputExtensionCd;
	}
	public void setInputExtensionCd(String inputExtensionCd) {
		this.inputExtensionCd = inputExtensionCd;
	}
	public String getLocalWorkingDirectory() {
		return localWorkingDirectory;
	}
	public void setLocalWorkingDirectory(String localWorkingDirectory) {
		this.localWorkingDirectory = localWorkingDirectory;
	}
	public double getProcessTimeoutSec() {
		return processTimeoutSec;
	}
	public void setProcessTimeoutSec(double processTimeoutSec) {
		this.processTimeoutSec = processTimeoutSec;
	}
	public double getStartTime() {
		return startTime;
	}
	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}
	public double getEndTime() {
		return endTime;
	}
	public void setEndTime(double endTime) {
		this.endTime = endTime;
	}
	public String getMondayCd() {
		return mondayCd;
	}
	public void setMondayCd(String mondayCd) {
		this.mondayCd = mondayCd;
	}
	public String getTuesdayCd() {
		return tuesdayCd;
	}
	public void setTuesdayCd(String tuesdayCd) {
		this.tuesdayCd = tuesdayCd;
	}
	public String getWednesdayCd() {
		return wednesdayCd;
	}
	public void setWednesdayCd(String wednesdayCd) {
		this.wednesdayCd = wednesdayCd;
	}
	public String getThursdayCd() {
		return thursdayCd;
	}
	public void setThursdayCd(String thursdayCd) {
		this.thursdayCd = thursdayCd;
	}
	public String getFridayCd() {
		return fridayCd;
	}
	public void setFridayCd(String fridayCd) {
		this.fridayCd = fridayCd;
	}
	public String getSaturdayCd() {
		return saturdayCd;
	}
	public void setSaturdayCd(String saturdayCd) {
		this.saturdayCd = saturdayCd;
	}
	public String getSundayCd() {
		return sundayCd;
	}
	public void setSundayCd(String sundayCd) {
		this.sundayCd = sundayCd;
	}
}
