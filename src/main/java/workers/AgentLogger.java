package workers;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import config.WorkerInfos;

public class AgentLogger {
  private static Logger logger;
  private static Logger loggerUpdates;
	private static String logLevel;
	private static boolean logToConsole;
	private static boolean logToFile;
  private static boolean hasInit;
  private static boolean throwErrors;
	 
	
  /** 
   * @param level
   * @param logFile
   * @param fileSize
   * @param fileCount
   * @param enableLogToConsole
   * @param enableLogToFile
   * @param isTestMode
   * @throws Exception
   */
  public static void init(String level, String logFile, int fileSize, int fileCount, boolean enableLogToConsole, boolean enableLogToFile, boolean isTestMode) throws Exception
	{
		logLevel = level; 
		logToConsole = enableLogToConsole;
    logToFile = enableLogToFile;
    throwErrors = isTestMode;
		
		if (!logLevel.equals("error") && !logLevel.equals("info") && !logLevel.equals("trace"))
		{
			throw new Exception("Log levels possible values: info, error, trace");
    }
    
    File file = new File(logFile);
			
		String directory = file.getParent();

		if (logFile != null && logToFile)
		{
			if (directory != null)
			{
				File dir = new File(directory);
				
				if (!dir.exists())
				{
					dir.mkdirs();
				}
			}
			
      logger = Logger.getLogger(AgentLogger.class.getName());
      loggerUpdates = Logger.getLogger("UpdateChecker");
			
      FileHandler handler = new FileHandler(logFile, fileSize, fileCount, true);
      handler.setFormatter(new SimpleFormatter());

      FileHandler handlerUpdates = new FileHandler(Paths.get(directory, "agents_updates.log").toString(), fileSize, fileCount, true);
      handlerUpdates.setFormatter(new SimpleFormatter());
      
      if (logLevel.equals("info"))
      {
        handler.setLevel(Level.INFO);
        handlerUpdates.setLevel(Level.INFO);
        Logger.getLogger("").setLevel( Level.INFO );
      }
      else if (logLevel.equals("error"))
      {
        handler.setLevel(Level.SEVERE);
        handlerUpdates.setLevel(Level.SEVERE);
        Logger.getLogger("").setLevel( Level.SEVERE );
      }
      else if (logLevel.equals("trace"))
      {
        handler.setLevel(Level.FINE);
        handlerUpdates.setLevel(Level.FINE);
        Logger.getLogger("").setLevel( Level.FINE );
      }

      logger.addHandler(handler);
      loggerUpdates.addHandler(handlerUpdates);
      
      if (logToConsole)
      {
        ConsoleHandler handlerConsole = new ConsoleHandler();
        handlerConsole.setFormatter(new SimpleFormatter());
        handlerConsole.setLevel(Level.ALL);
        logger.addHandler(handlerConsole);
        loggerUpdates.addHandler(handlerConsole);
      }
      
      logger.setUseParentHandlers(false);
      loggerUpdates.setUseParentHandlers(false);

      hasInit = true;
		}
  }
  
  
  /** 
   * @param message
   */
  public static void logUpdatesInfos(String message)
	{
		if (logLevel == null || logLevel.equals("info")  || logLevel.equals("trace"))
		{
			if (loggerUpdates != null && logToFile)
			{
				loggerUpdates.log(Level.INFO, message);
			}
		}
  }
  
  
  /** 
   * @param trace
   */
  public static void logUpdatesTrace(String trace)
	{
		if (logLevel != null && logLevel.equals("trace"))
		{
			if (loggerUpdates != null && logToFile)
			{
				loggerUpdates.log(Level.FINE, ": Trace - " + trace);
			}
		}
	}
  
  
  /** 
   * @param e
   */
  public static void logUpdatesError(Exception e)
	{
    String error = getStackTrace(e);
		if (logLevel == null || logLevel.equals("info") || logLevel.equals("error")  || logLevel.equals("trace"))
		{
			if (loggerUpdates != null && logToFile)
			{
				loggerUpdates.log(Level.SEVERE, error);
			}
		}
  }
  
  
  /** 
   * @param error
   */
  public static void logUpdatesError(String error)
	{
		if (logLevel == null || logLevel.equals("info") || logLevel.equals("error")  || logLevel.equals("trace"))
		{
			if (loggerUpdates != null && logToFile)
			{
				loggerUpdates.log(Level.SEVERE, error);
			}
		}
	}
	
	
  /** 
   * @param message
   */
  public static void logInfos(String message)
	{
		if (logLevel == null || logLevel.equals("info")  || logLevel.equals("trace"))
		{
			if (logger != null && logToFile)
			{
				logger.log(Level.INFO, message);
			}
		}
	}
	
	
  /** 
   * @param trace
   */
  public static void logTrace(String trace)
	{
		if (logLevel != null && logLevel.equals("trace"))
		{
			if (logger != null && logToFile)
			{
				logger.log(Level.FINE, ": Trace - " + trace);
			}
		}
	}
	
	
  /** 
   * @param error
   * @param workerInfos
   */
  public static void logError(String error, WorkerInfos workerInfos)
	{
		if (logLevel == null || logLevel.equals("info") || logLevel.equals("error")  || logLevel.equals("trace"))
		{
			if (logger != null && logToFile)
			{
				logger.log(Level.SEVERE, error);
			}
			try {
        if (workerInfos != null)
        {
          Worker.addGlobalError(error, workerInfos);
        }
			} catch (Exception e) {
				e.printStackTrace();
      }
      
      if (throwErrors)
      {
        throw new RuntimeException(error);
      }
		}
	}
	
	
  /** 
   * @param e
   * @param workerInfos
   */
  public static void logStdErr(Exception e, WorkerInfos workerInfos)
	{
		String error = getStackTrace(e);
		System.err.println("Error - " + error);
		
		if (hasInit)
		{
			logError(e, workerInfos);
    }
    else
    {
      try {
        PrintWriter writer = new PrintWriter("ERRORS_UNHANDLED.txt", "UTF-8");
        writer.println(error);
        writer.println();
        writer.close();
      }
      catch(Exception exc)
      {
        System.err.println("Cannot write to log file: " + exc.getMessage());
      }
      throw new RuntimeException(error);
    }
	}
	
	
  /** 
   * @param exc
   * @param workerInfos
   */
  public static void logError(Exception exc, WorkerInfos workerInfos)
	{
		String error = getStackTrace(exc);
		logError(error, workerInfos);
	}
	
	
  /** 
   * @param throwable
   * @return String
   */
  private static String getStackTrace(final Throwable throwable) {
	     final StringWriter sw = new StringWriter();
	     final PrintWriter pw = new PrintWriter(sw, true);
	     throwable.printStackTrace(pw);
	     return sw.getBuffer().toString();
	}
	
	public static class MyLogManager extends LogManager {
        static MyLogManager instance;
        public MyLogManager() { instance = this; }
        @Override public void reset() { /* don't reset yet. */ }
        private void reset0() { super.reset(); }
        public static void resetFinally() { instance.reset0(); }
    }
}
