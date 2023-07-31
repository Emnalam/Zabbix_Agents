package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import config.Settings;
import workers.AgentLogger;

public class Transfer {
	
	private static Settings settings;
  private static ExecutorService transferExecutor;
  private static ExecutorService transferUpdatesExecutor;
	
	static {
		try {
			settings = Settings.getInstance();
      transferExecutor = Executors.newFixedThreadPool(settings.getTransferConcurrentRequests());
      transferUpdatesExecutor = Executors.newFixedThreadPool(1);
			AgentLogger.logInfos("Maximum Transfer concurrent requests: " + settings.getTransferConcurrentRequests());
		} catch (Exception e) {
			AgentLogger.logStdErr(e, null);
		}
  }
  
  
  /** 
   * @throws Exception
   */
  public static void checkUpdates() throws Exception
  {
    transferUpdatesExecutor.invokeAll(Arrays.asList(toCallable(new Runnable() {
      @Override 
      public void run() {
        AgentLogger.logTrace("begin Check Updates");
        try {
        executeCheckUpdates(settings.getTransferServer(), settings.getTransferPort(), settings.getTransferLogin(), settings.getTransferPassword());
        Thread.sleep(10000);
      } catch (Exception exc) {
        AgentLogger.logError(exc, null);
      }
        AgentLogger.logTrace("end Check Updates");
      }
    })));
  }
	
	
  /** 
   * @param file
   * @param remoteFile
   * @throws Exception
   */
  public static void send(final String file, final String remoteFile) throws Exception
	{
		if (settings.isUseTransfer())
		{
			transferExecutor.execute(new Runnable() {
			    @Override 
			    public void run() {
			    	AgentLogger.logTrace("begin Transfer for file: " + file);
			    	try {
						executeTransfer(settings.getTransferServer(), settings.getTransferPort(), settings.getTransferLogin(), settings.getTransferPassword(), file, remoteFile);
					} catch (Exception exc) {
						AgentLogger.logError(exc, null);
					}
			    	AgentLogger.logTrace("end Transfer");
			    }
			});
		}
		else
		{
			AgentLogger.logTrace("Transfer is not enabled in config");
		}
  }
  
  
  /** 
   * @param server
   * @param port
   * @param login
   * @param password
   * @throws Exception
   */
  private static void executeCheckUpdates(String server, int port, String login, String password) throws Exception {
    
    Process p;

		String command = getCommand("check_updates", server, port, login, password, null, null);
		
		AgentLogger.logTrace("check_updates command is: " + command);
		
    p = getProcess(command);

    p.waitFor();
    
    handleProcessOutputContent(p);
    handleProcessErrorContent(p);
	}
	
	
  /** 
   * @param server
   * @param port
   * @param login
   * @param password
   * @param file
   * @param remoteFile
   * @throws Exception
   */
  private static void executeTransfer(String server, int port, String login, String password, String file, String remoteFile) throws Exception {

		Process p;

		String command = getCommand("transfer", server, port, login, password, file, remoteFile);
		
		AgentLogger.logTrace("transfer command is: " + command);
		
    p = getProcess(command);

    p.waitFor();
    
    handleProcessOutputContent(p);
    handleProcessErrorContent(p);
  }
  
  
  /** 
   * @param scriptBaseName
   * @param server
   * @param port
   * @param login
   * @param password
   * @param file
   * @param remoteFile
   * @return String
   * @throws Exception
   */
  private static String getCommand(String scriptBaseName, String server, int port, String login, String password, String file, String remoteFile) throws Exception
  {
    String command = null;
    String transferScript = null;
    String transferBin = Settings.getInstance().getTransferBin();

		if (transferBin == null || transferBin.length() == 0)
		{
			transferBin = "./";
		}

		if (!OS.isWindows())
		{
			transferScript = Paths.get(transferBin, scriptBaseName + ".sh").toAbsolutePath().toString();
			command = String.format("%s -s=%s -pt=%s -l=%s -p=%s -f=%s -rf=%s", transferScript, server, "" + port, login, password, file, remoteFile);
		}
		else
		{
			transferScript = Paths.get(transferBin, scriptBaseName + ".bat").toAbsolutePath().toString();
			command = String.format("%s %s %s %s %s \"%s\" \"%s\"", transferScript, server, "" + port, login, password, file, remoteFile);
    }
    
    return command;
  }

  
  /** 
   * @param command
   * @return Process
   * @throws Exception
   */
  private static Process getProcess(String command) throws Exception
  {
    Process p;
    if (!OS.isWindows())
		{
			p = Runtime.getRuntime().exec(new String[] { "sh", "-c", command });
		}
		else
		{
			p = Runtime.getRuntime().exec(new String[] { "cmd", "/c", command });
		}
		return p;
  }

  
  /** 
   * @param p
   * @return String
   * @throws Exception
   */
  private static String handleProcessOutputContent(Process p) throws Exception
  {
    StringBuffer output = new StringBuffer();
    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

    String line = "";	  
    while ((line = reader.readLine())!= null) {
      output.append(line + System.lineSeparator());
    }
    reader.close();
    String processOutput = output.toString();
    AgentLogger.logTrace("transfer command output is: " + processOutput);
    return processOutput;
  }

  
  /** 
   * @param p
   * @return String
   * @throws Exception
   */
  private static String handleProcessErrorContent(Process p) throws Exception
  {
    StringBuffer errorOutput = new StringBuffer();
    BufferedReader readerError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

    String line = "";	 
    while ((line = readerError.readLine())!= null) {
      errorOutput.append(line + System.lineSeparator());
    }
    readerError.close();
    String processErrorOutput = errorOutput.toString();

    if (errorOutput.length() > 0)
    {
      AgentLogger.logTrace("transfer command ERROR is: " + processErrorOutput.toString());
    }
    return processErrorOutput;
  }

  
  /** 
   * @param runnable
   * @return Callable<Void>
   */
  private static Callable<Void> toCallable(final Runnable runnable) {
    return new Callable<Void>() {
        @Override
        public Void call() {
            runnable.run();
            return null;
        }
    };
}
}
