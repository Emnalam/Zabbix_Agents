package workers.apsys;

import java.nio.file.Files;
import java.nio.file.Paths;
import data.apsys.GetOperationInfoResult;

import java.io.File;

import workers.Worker;

public class GetOperationInfoWorker extends Worker{

	private String shellCommand;

	private final static String DEFAULT_ARG = "getoperationinfo_out.tmp";
	private final static String DATA_SPLIT_DELIMITOR = "#";
	

	
  /** 
   * @throws Exception
   */
  @Override
	protected void init() throws Exception {
		
		this.shellCommand = this.workerInfos.getCommand();
	}

	
  /** 
   * @throws Exception
   */
  @Override 
	protected void doWork() throws Exception 
	{
		this.executeCommand(shellCommand + " " + DEFAULT_ARG, this.workerInfos.getSuccessExitCode(), null, System.getProperty("user.dir"));
		
		String content = new String(Files.readAllBytes(Paths.get(DEFAULT_ARG)));
		
		String[] parts = content.split(DATA_SPLIT_DELIMITOR);
		
		GetOperationInfoResult result = new GetOperationInfoResult();
		
		result.setAccountingDate(parts[1]);
		result.setAccountingSessionNumber(parts[2]);
		result.setOperatingMode(parts[3]);
		result.setCurrentStage(parts[4]);
		result.setStepHasAborted(parts[5]);
		result.setStepInInitializationPase(parts[6]);
		result.setOperationIs24x7(parts[7]);
		
		
		this.save(result);
	}

	
  /** 
   * @throws Exception
   */
  @Override
	protected void cleanUp() throws Exception {

		File file = new File(DEFAULT_ARG);
		
		file.delete();
	}
}

