package workers.equalizer;


import data.equalizer.CmdExcEqz;
import workers.AgentLogger;
import workers.Worker;

public class EQZwebapi extends Worker{

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
	
	@Override
	protected void doWork() throws Exception {
		// TODO Auto-generated method stub
		
        String execCommand = this.command;
		
		if (this.additionalParameters != null && this.additionalParameters.trim().length() > 0) 
		{
			execCommand = execCommand + " \"" + this.additionalParameters + "\"";
		}
		
		AgentLogger.logTrace("Command to execute is: " + execCommand);
		AgentLogger.logInfos("Command to execute is: " + execCommand);
		
		String commandOutput = this.executeCommand(execCommand, this.workerInfos.getSuccessExitCode(), null, null);
		commandOutput = commandOutput.trim();
		//String rslt = commandOutput.substring(30,35);
		
		AgentLogger.logTrace("Command output is: " + commandOutput);
		AgentLogger.logInfos("commandOutput : " + commandOutput);
	    CmdExcEqz result = new CmdExcEqz();
        result.setStatus(commandOutput);
		this.save(result);
		
	}

	@Override
	protected void cleanUp() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
