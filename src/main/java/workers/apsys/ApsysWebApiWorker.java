package workers.apsys;

import java.nio.file.Files;
import java.nio.file.Paths;
import data.apsys.ApsysWebApi;
import workers.Worker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;

public class ApsysWebApiWorker extends Worker{

    private String shellCommand;
	private final static String DEFAULT_ARG = "getApsysWebApi_out.tmp";
	//private final static String DEFAULT_ARG = "D:/testagentwebapi/getoperationinfo_out.tmp";
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
		
		 ApsysWebApi result = new ApsysWebApi();
		for (String str : parts) {
			result.setIdscts(str);
			result.setDtcptl(str);
			result.setNusesi(str);
			result.setStatee(str);
			result.setAvailableb(str); 
			result.setWriteb(str);
		}
/* 
		 result.setIdscts(parts[0]);
		 result.setDtcptl(parts[1]);
		 result.setNusesi(parts[2]);
		 result.setStatee(parts[3]);
		 result.setAvailableb(parts[4]); 
		 result.setWriteb(parts[5]);
		*/
	
		
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