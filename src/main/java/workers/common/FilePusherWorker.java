package workers.common;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import data.common.FilePusherResult;
import utils.Transfer;
import workers.AgentLogger;
import workers.Worker;

public class FilePusherWorker extends Worker {
	
	private String directoryToWatch;
	private ArrayList<String> sentFiles = new ArrayList<String>();

	
  /** 
   * @throws Exception
   */
  @Override
	protected void init() throws Exception {
		super.init();
		
		if (!parameters.containsKey("directory"))
		{
			throw new Exception("additional parameters do not contain the directories parameter: directory=xxx");
		}
		
		this.directoryToWatch = parameters.get("directory");
		
		AgentLogger.logTrace(this.getName() + ": directory to watch is: " + this.directoryToWatch);
	}

	
  /** 
   * @throws Exception
   */
  @Override
	protected void doWork() throws Exception {
		File dir = new File(this.directoryToWatch);
		if (dir.isDirectory())
		{
			File [] files = dir.listFiles(new FilenameFilter() {
			    @Override
			    public boolean accept(File dir, String name) {
			        return name.endsWith(".xml.ok");
			    }
			});
	
			for (File okfile : files) {
				String sendFile = okfile.getAbsolutePath().replace(".ok", "");
				String remoteFile = okfile.getName().replace(".ok", "");
			    Transfer.send(sendFile, remoteFile);
			    sentFiles.add(sendFile);
			}
			
			StringBuilder sb = new StringBuilder();
			for (String s : sentFiles)
			{
			    sb.append(s);
			    sb.append(System.lineSeparator());
			}
			sentFiles.clear();
			
			FilePusherResult result = new FilePusherResult();
			result.setSentFiles(sb.toString());
			
			this.save(result);
		}
		else
		{
			AgentLogger.logError(this.getName() + ": directory " + this.directoryToWatch + " does not exist or access is denied or is not a directory", this.workerInfos);
		}
	}

	
  /** 
   * @throws Exception
   */
  @Override
	protected void cleanUp() throws Exception {
		
	}

}
