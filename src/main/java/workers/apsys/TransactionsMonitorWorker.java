package workers.apsys;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import data.apsys.TransactionsMonitorResult;
import utils.DateUtils;
import utils.FileUtils;
import workers.AgentLogger;
import workers.Worker;

public class TransactionsMonitorWorker extends Worker {

  private static Map<String, Long> transactionFiles = new HashMap<String, Long>();
  private final String ALREADY_PROCESSED ="already processed";

  private static boolean handledNewFiles;
	
	
  /** 
   * @throws Exception
   */
  @Override
	protected void init() throws Exception {
    super.init();
    String fileData = FileUtils.readTempFile(this.getClass().getName());
    if (fileData != null && fileData.toLowerCase().equals("true"))
    {
      handledNewFiles = true;
    }
    else{
      handledNewFiles = false;
    }
	}

	
  /** 
   * @throws Exception
   */
  @Override
	protected void doWork() throws Exception { 
		
		this.trimCache();
		
		StringBuilder errors = new StringBuilder();
		
		String grepOutput = this.executeCommand(this.workerInfos.getCommand(), this.workerInfos.getSuccessExitCode(), errors, null);

		if (errors.length() > 0)
		{
			AgentLogger.logError(errors.toString(), this.workerInfos);
		}
		else 
		{
			String[] lines = grepOutput.split("\\r?\\n");
			
			List<String>  filesHandled = new ArrayList<String>();

      if (lines.length == 1 && lines[0].trim().length() == 0)
      {
        if (handledNewFiles)
        {
          this.save(new TransactionsMonitorResult());
          updateHandledNewFiles(false);
        }
      }
      else 
      {
	        for (String line : lines) 
	        {
	        	TransactionsMonitorResult result = new TransactionsMonitorResult();
	        	int index = line.indexOf(":");
	        	if (index > 0)
	        	{ 
	        		String logfile = line.substring(0, index);
	        		
	        		if (shouldHandleLogFile(logfile))
	        		{
	        			filesHandled.add(logfile);
	        			
		        		AgentLogger.logTrace("logFile: " + logfile);
                
                if (!isTransactionAlreadyProcessed(logfile))
                {
                  String transaction = this.GetTransactionNumber(logfile);
                  
                  AgentLogger.logTrace("Transaction: " + transaction);
                  
                  String service = this.GetServiceName(logfile);
                  
                  AgentLogger.logTrace("Service: " + service);
                  
                  int index2 = line.indexOf(":", index+1);
                  
                  if (index2 > 0)
                  {
                    String linenumber = line.substring(index+1, index2);
                    
                    AgentLogger.logTrace("Line number: " + linenumber);
                    
                    String message = line.substring(index2+1);
                    
                    AgentLogger.logTrace("Message: " + message);
                    
                    result.addError(transaction, logfile, linenumber, service, message);
                    this.save(result);
                    updateHandledNewFiles(true);
                  }
                }
	        		}
	        		else
	        		{
	        			AgentLogger.logTrace("Not handling " + logfile + " because it is already handled");
	        		}
	        	}
	        }
	        
	        for (String handledFile : filesHandled)
	        {
	        	addLogFileInfos(handledFile);
          }
        }
      }
  }

  
  /** 
   * @param value
   * @throws Exception
   */
  private void updateHandledNewFiles(boolean value) throws Exception
  {
    handledNewFiles = value;
    FileUtils.writeTempFile(this.getClass().getName(), Boolean.toString(value), false);
  }
  
  
  /** 
   * @param logFile
   * @return boolean
   * @throws Exception
   */
  private boolean isTransactionAlreadyProcessed(String logFile) throws Exception
  {
    String content = readFile(logFile);
    return content.indexOf(ALREADY_PROCESSED) > -1;
  }

  
  /** 
   * @param pathname
   * @return String
   * @throws IOException
   */
  private String readFile(String pathname) throws IOException {

    File file = new File(pathname);
    StringBuilder fileContents = new StringBuilder((int)file.length());        

    try (Scanner scanner = new Scanner(file)) {
        while(scanner.hasNextLine()) {
            fileContents.append(scanner.nextLine() + System.lineSeparator());
        }
        scanner.close();
        return fileContents.toString();
    }
}
	
	
  /** 
   * @param logFile
   * @return boolean
   */
  private boolean shouldHandleLogFile(String logFile)
	{
		File f = new File(logFile);
		long lastModified = f.lastModified();
		
		if (lastModified >= DateUtils.getStartupTime())
		{
			if (transactionFiles.containsKey(logFile))
			{
				long previousModified = transactionFiles.get(logFile);
				if (previousModified >= lastModified)
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	
  /** 
   * @param logFile
   * @throws IOException
   */
  private void addLogFileInfos(String logFile) throws IOException
	{
		File f = new File(logFile);
		transactionFiles.put(logFile, f.lastModified());
	}
	
	private void trimCache()
	{
		List<String> toRemove = new ArrayList<String>();
		
		for(String trxFile : transactionFiles.keySet())
		{
			File fcheck = new File(trxFile);
			if (!fcheck.exists())
			{
				toRemove.add(trxFile);
			}
		}
		
		for(String trxFile : toRemove)
		{
			transactionFiles.remove(trxFile);
			AgentLogger.logTrace("Removed " + trxFile + " from cache");
		}
	}

	
  /** 
   * @throws Exception
   */
  @Override
	protected void cleanUp() throws Exception {
		
	}
	
	
  /** 
   * @param filename
   * @return String
   */
  private String GetServiceName(String filename)
	{
		String[] lines = filename.split("_");
		if (lines.length < 3)
		{
			return "NotAvailable";
		}
		
		return lines[1];
	}
	
	
  /** 
   * @param filename
   * @return String
   */
  private String GetTransactionNumber(String filename)
	{
		String[] lines = filename.split("_");
		if (lines.length < 3)
		{
			return "NotAvailable";
		}
		
		return lines[2].replace(".LOG", "").replace(".log", "");
	}

}
