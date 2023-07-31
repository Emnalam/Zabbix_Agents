package workers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;

import config.Property;
import config.Settings;
import config.WorkerInfos;
import data.common.AgentGlobalErrorsResult;
import data.common.Result;
import utils.OS;
import utils.Transfer;

public abstract class Worker {
	
	private static List<Process> runningProcesses = new ArrayList<Process>();
	private static String errorPath;
		
	private SimpleDateFormat sdf;
	
	private String outputFileName;

	private int count;

  protected WorkerInfos workerInfos;
  protected final Map<String, String> properties = new HashMap<String, String>();    
  protected final Map<String, String> parameters = new HashMap<String, String>();
	
	
  /** 
   * @param init(
   * @throws Exception
   */
  protected abstract void doWork() throws Exception;
	
  /** 
   * @param init(
   * @throws Exception
   */
  protected abstract void cleanUp() throws Exception;
  
  
  /** 
   * @throws Exception
   */
  protected void init() throws Exception
  {
    final String params = this.workerInfos.getAdditionalParameters();

    if (params != null)
    {
      final String[] additionalParamLines = params.split(";");

      for (final String paramLine : additionalParamLines) {
        final String[] paramPair = paramLine.split("=");

        if (paramPair.length != 2) {
          throw new Exception("Invalid additional parameter: " + paramLine + ". Format: key=value");
        }
        parameters.put(paramPair[0].toLowerCase(), paramPair[1]);
      }
    }
  }
	
  
  /** 
   * @throws Exception
   */
  public void start() throws Exception
	{
		try {
			doWork();
			cleanUp();
		}
		catch(Exception exc)
		{
			AgentLogger.logError(exc, workerInfos);
		}
  }
  
  
  /** 
   * @param result
   * @throws Exception
   */
  protected void save(Result result) throws Exception 
	{
	  
		result.setEnvironment(Settings.getInstance().getEnvironment());
		
		result.setProduct(this.workerInfos.getProduct());
	
    	result.setSubproduct(this.workerInfos.getSubproduct());
    	
    	result.setzTag(this.workerInfos.getZTag());
    	

		String outputFile = this.getFormattedOutputFileName();
	
		
		StringWriter sw = new StringWriter();
		JAXB.marshal(result, sw);
    String xmlString = sw.toString();
   
    
    //Test comment
		
		sw.close();
		
		
		this.save(xmlString, outputFile);
	
	}
	
	
  /** 
   * @param error
   * @param workerInfos
   * @throws Exception
   */
  public static void addGlobalError(String error, WorkerInfos workerInfos) throws Exception
	{
		String[] lines = error.split("[\\r\\n]+");
		
		String errorInfo = (workerInfos != null ? workerInfos.getClassname() : "General") + " error: " + lines[0];
		
		if (errorPath == null)
		{
			File f = new File(workerInfos.getOutputfile());
			
      errorPath = f.getParent();
      
      if (errorPath == null)
      {
        errorPath = "";
      }
		}
    
    String environment = Settings.getInstance().getEnvironment();

    if (environment == null)
    {
      environment = "nwamon";
    }
    
		Path errorFile = Paths.get(errorPath, environment + "_agenterrors");
		
		String outputFile = errorFile.toString();
		
		sendGlobalErrors(errorInfo, outputFile);
	}

	
  /** 
   * @param p
   */
  public static void addRunningProcess(Process p)
	{
		runningProcesses.add(p);
	}
	
	public static void destroyRunningProcesses()
	{
		for(Process p : runningProcesses)
		{
			try {
				if (p != null)
				{
					p.destroy();
				}
			}
			catch(Exception exc)
			{
				AgentLogger.logStdErr(exc, null);
				AgentLogger.logError(exc, null);
			}
		}
		runningProcesses.clear();
		AgentLogger.logInfos("Closed external running processes");
	}
	
	
  /** 
   * @return String
   */
  public String getName()
	{
		if (this.workerInfos != null)
		{
			return this.workerInfos.getName();
		}
		return null;
	}
	
	
	
  /** 
   * @return String
   */
  public String getOutputFileName()
	{
		return outputFileName;
  }
  

	
  /** 
   * @param workerInfos
   * @return String
   * @throws Exception
   */
  protected String executeDefaultCommand(WorkerInfos workerInfos) throws Exception
	{
		String command = workerInfos.getCommand();
		int successExitCode = workerInfos.getSuccessExitCode();
		
		return this.executeCommand(command, successExitCode, null, null);
  }
  
  
  /** 
   * @param property
   * @param text
   * @return boolean
   */
  protected boolean tryMatchPropertyValues(Property property, String text)
  { 
    String propertyValue = property.getValue();

    String[] values = propertyValue.split("\\|");

    for(String value : values)
    {
      if (text.matches(value.trim()))
      {
        return true;
      }
    }
    return false;
  }

  
  /** 
   * @param filename
   * @return Property
   */
  protected Property tryGetMatchingProperty(String filename)
  {
    if (workerInfos.getProperties() != null && workerInfos.getProperties().getProperties() != null)
    {
      for(Property prop : workerInfos.getProperties().getProperties())
      {
        if (filename.matches(prop.getName()))
        {
          return prop;
        }
      }
    }
    return null;
  }
	
	
  /** 
   * @param workerInfos
   * @throws Exception
   */
  public void initOnly(WorkerInfos workerInfos) throws Exception
	{
		this.workerInfos = workerInfos;
		setDateFormat();
    setOutputFileName(workerInfos.getOutputfile());
    initProperties();
    init();
  }

	
  /** 
   * @param command
   * @param successExitCode
   * @param errors
   * @param workingDirectory
   * @return String
   * @throws Exception
   */
  protected String executeCommand(String command, int successExitCode, StringBuilder errors, String workingDirectory) throws Exception {

		StringBuffer output = new StringBuffer();
		StringBuffer errorOutput = new StringBuffer();

		Process p;

		if (!OS.isWindows())
		{
			if (workingDirectory == null)
			{
				p = Runtime.getRuntime().exec(new String[] { "sh", "-c", command });
			}
			else
			{
				p = Runtime.getRuntime().exec(new String[] { "sh", "-c", command }, null, new File(workingDirectory));
			}
		}
		else
		{
			if (workingDirectory == null)
			{
				p = Runtime.getRuntime().exec(new String[] { "cmd", "/c", command });
			}
			else
			{
				p = Runtime.getRuntime().exec(new String[] { "cmd", "/c", command }, null, new File(workingDirectory));
			}
		}
		
		AgentLogger.logTrace("Waiting for command: " + command + " to complete.");
		
		InputStream errorStream = p.getErrorStream();
		
		BufferedReader readerError = new BufferedReader(new InputStreamReader(errorStream));

		String line = "";	  
		try {
			if (errorStream.available() > 0)
			{
		        while ((line = readerError.readLine())!= null) {
		        	errorOutput.append(line + System.lineSeparator());
		        }
			}
	        readerError.close();
		}
		catch(Exception e)
		{
			
		}

        if (errorOutput.length() > 0)
        {
        	if (errors == null)
        	{
        		errors = new StringBuilder();
        	}
        	errors.append(errorOutput.toString());
        	AgentLogger.logError(this.getClass().getName() + " command ERROR: " + errorOutput.toString(), this.workerInfos);
        	p.waitFor();
        	return errors.toString();
        }
 
		BufferedReader reader =  new BufferedReader(new InputStreamReader(p.getInputStream()));
		OutputStreamWriter oStream = new OutputStreamWriter(p.getOutputStream());

		oStream.flush();
		oStream.close();
		
		line = "";
		while ((line = reader.readLine())!= null) {
			output.append(line + System.lineSeparator());
		}
		
		
		reader.close();
		
		String o = output.toString();

		p.waitFor();
		
		int exitCode = p.exitValue();
		
		AgentLogger.logTrace("Command completed with exit code: " + exitCode);
		
		if (exitCode != successExitCode)
		{
			AgentLogger.logInfos("WARNING: Process exit code for " + command + " was: " + exitCode + " and this is different from the configured success exit code: " + successExitCode);
		}
		
		AgentLogger.logTrace(this.getClass().getName() + " command output: " + o);
    
		return o;
	}
	
	
  /** 
   * @param xmlString
   * @param outputFile
   * @throws Exception
   */
  private void save(String xmlString, String outputFile) throws Exception
	{
		File newTextFile = new File(outputFile);

        FileWriter fw = new FileWriter(newTextFile);
        fw.write(xmlString);
        fw.close();
        
        AgentLogger.logTrace("Transfer parameters: enabled in settings: " + Settings.getInstance().isUseTransfer()+  ", disabled in worker: " + this.workerInfos.isDisableTransfer());
        
        if (Settings.getInstance().isUseTransfer() && !this.workerInfos.isDisableTransfer())
        {
        	TransferRunnable ftp = new TransferRunnable(newTextFile.getAbsolutePath(), newTextFile.getName());
        	ftp.run();
        }
	}
	
	
  /** 
   * @param xmlString
   * @param outputFile
   * @throws Exception
   */
  private static  void saveError(String xmlString, String outputFile) throws Exception
	{
		File newTextFile = new File(outputFile);

        FileWriter fw = new FileWriter(newTextFile);
        fw.write(xmlString);
        fw.close();
        

        if (Settings.getInstance().isUseTransfer())
        {
        	Transfer.send(newTextFile.getAbsolutePath(), newTextFile.getName());
        }
	}
	
	
  /** 
   * @param error
   * @param outputFile
   * @throws Exception
   */
  private static void sendGlobalErrors(String error, String outputFile) throws Exception
	{
		AgentGlobalErrorsResult result = new AgentGlobalErrorsResult();
		result.setEnvironment(Settings.getInstance().getEnvironment());

		result.setErrors(error);
		
		StringWriter sw = new StringWriter();
		JAXB.marshal(result, sw);
		
		String xmlString = sw.toString();
		sw.close();
		
		saveError(xmlString, outputFile);
	}
	
	
	
  /** 
   * @throws Exception
   */
  private void setDateFormat() throws Exception
	{
		 sdf = new SimpleDateFormat(Settings.getInstance().getDateFormat());
	}
	
	
  /** 
   * @param path
   * @throws Exception
   */
  private void setOutputFileName(String path) throws Exception
	{
		this.outputFileName = path;
		
		File file = new File(path);
		
		String directory = file.getParent();
		
		if (directory != null && directory != "")
		{
			File dir = new File(directory);
			
			if (!dir.exists())
			{
				dir.mkdirs();
			}
		}
  }
  
  private void initProperties()
  {
    if (workerInfos.getProperties() != null && workerInfos.getProperties().getProperties() != null)
    {
      for(Property prop : workerInfos.getProperties().getProperties())
      {
        properties.put(prop.getName(), prop.getValue());
        AgentLogger.logTrace(workerInfos.getName() + ": Property Name = " + prop.getName());
        AgentLogger.logTrace(workerInfos.getName() + ": Property Value = " + prop.getValue());
      }
    }
  }

	
  /** 
   * @return String
   */
  private String getFormattedOutputFileName()
	{
		String fileName = this.outputFileName;
		
		
		if (this.outputFileName.contains("{date}"))
		{
			fileName = fileName.replace("{date}", sdf.format(Calendar.getInstance().getTime()));
		}
		if (this.outputFileName.contains("{count}"))
		{
			fileName = fileName.replace("{count}", (++count) + "");
		}
		
		return fileName;
	}

	class TransferRunnable implements Runnable
	{
		private String file;
		private String remoteFile;
		
		TransferRunnable(String file, String remoteFile)
		{
			this.file = file;
			this.remoteFile = remoteFile;
		}
		
		@Override
		public void run() {
			try {
				Transfer.send(file, remoteFile);
			}
			catch(Exception exc)
			{
				AgentLogger.logError(exc, null);
			}
		}
	}
}
