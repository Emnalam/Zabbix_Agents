package workers.morning;

import java.nio.file.Paths;

import workers.AgentLogger;
import workers.morning.apsys.ApsysMorningWorker;

public abstract class LogGrep extends ApsysMorningWorker {

  protected String command = "grep -E -- ";
  protected String commandOutput;

  
  /** 
   * @param init(
   * @return String
   * @throws Exception
   */
  protected abstract String getFilesToGrepPattern();
  
  /** 
   * @param init(
   * @return String
   * @throws Exception
   */
  protected abstract String getDirectoryToGrep();
  
  /** 
   * @param init(
   * @return String
   * @throws Exception
   */
  protected abstract String getGrepPattern();

  
  /** 
   * @param init(
   * @throws Exception
   */
  protected abstract void handleGrep() throws Exception;

  
  /** 
   * @throws Exception
   */
  @Override
  protected void init() throws Exception {
    super.init();

    String cmd = this.workerInfos.getCommand();

    if (cmd != null && cmd.length() > 0)
    {
      this.command = cmd;
    }
  }


  
  /** 
   * @throws Exception
   */
  @Override
  protected void doCheck() throws Exception {

    String execCommand = this.command + " \"" + this.getGrepPattern()  + "\" " + Paths.get(this.getDirectoryToGrep(), this.getFilesToGrepPattern()) ;
    
    AgentLogger.logTrace(this.workerInfos.getName() + ", Command to execute is: " + execCommand);
    
    StringBuilder sbErrors = new StringBuilder();
    
    this.commandOutput = this.executeCommand(execCommand, this.workerInfos.getSuccessExitCode(), sbErrors, System.getProperty("user.dir"));
    this.commandOutput = this.commandOutput.trim();
    
    if (sbErrors.length() > 0)
    {
      AgentLogger.logError(sbErrors.toString(), this.workerInfos);
    }

    AgentLogger.logTrace(this.workerInfos.getName() + ", Command output is: " + commandOutput);

    this.handleGrep();
  }

  
  /** 
   * @throws Exception
   */
  @Override
  protected void cleanUp() throws Exception {
  }
  
  
  /** 
   * @return boolean
   */
  protected boolean hasGrepOutpt()
  {
    return this.commandOutput != null && this.commandOutput.length() > 0;
  }
}