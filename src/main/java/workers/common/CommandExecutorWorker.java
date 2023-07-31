package workers.common;

import data.common.CommandExecutorResult;
import workers.AgentLogger;
import workers.Worker;

public class CommandExecutorWorker extends Worker {
	
	private String additionalParameters;
	private String command;
	
	
  /** 
   * @throws Exception
   */
  @Override
	protected void init() throws Exception {
		this.additionalParameters = this.workerInfos.getAdditionalParameters();
		this.command = this.workerInfos.getCommand();
		
		if (this.command == null || this.command.trim().length() == 0)
		{
			throw new Exception("Command parameter was not set for " + this.getClass().getName());
		}
	}

	
  /** 
   * @throws Exception
   */
  @Override
	protected void doWork() throws Exception {
		
		String execCommand = this.command;
		
		if (this.additionalParameters != null && this.additionalParameters.trim().length() > 0) 
		{
			execCommand = execCommand + " \"" + this.additionalParameters + "\"";
		}
		
		AgentLogger.logTrace("Command to execute is: " + execCommand);
		
		String commandOutput = this.executeCommand(execCommand, this.workerInfos.getSuccessExitCode(), null, null);
		commandOutput = commandOutput.trim();
		
		AgentLogger.logTrace("Command output is: " + commandOutput);
		
		CommandExecutorResult result = new CommandExecutorResult();
		result.setCommandOutput(commandOutput);
		
		this.save(result);
	}

	
  /** 
   * @throws Exception
   */
  @Override
	protected void cleanUp() throws Exception {
	}

}
