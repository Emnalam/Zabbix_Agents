package workers.morning.apsys;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import data.apsys.DatabaseOperationalStatusResult;

public class DatabaseOperationalStatus extends ApsysMorningWorker {

	private final static String DEFAULT_ARG = "databaseoperationalstatus_out.tmp";
	private final static String DATA_SPLIT_DELIMITOR = "#";

   
  
  /** 
   * @throws Exception
   */
  @Override 
  protected void doCheck() throws Exception {

    this.executeCommand(getOperationInfoCommand + " " + DEFAULT_ARG, this.workerInfos.getSuccessExitCode(), null, System.getProperty("user.dir"));
    
    String content = new String(Files.readAllBytes(Paths.get(DEFAULT_ARG)));
    
    String[] parts = content.split(DATA_SPLIT_DELIMITOR);

    String operatingMode = parts[3];
    String currentStage = parts[4];
    
    DatabaseOperationalStatusResult result = new DatabaseOperationalStatusResult();
    result.setCheckOk(operatingMode.equals("A") && currentStage.equals("J"));
    result.setOperatingMode(operatingMode);
    result.setCurrentStage(currentStage);
    
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