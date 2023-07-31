package workers.las;

import java.io.File;

import workers.common.LogFilesMonitorWorker;

public class LasLogFilesMonitorWorker extends LogFilesMonitorWorker {

	
  /** 
   * @throws Exception
   */
  @Override
	protected void init() throws Exception {
		super.init();
		this.workerInfos.setSubproduct("las");
	}
	
	
  /** 
   * @param file
   * @param lines
   * @throws Exception
   */
  @Override
	protected void handleLogDataBlock(File file, String[] lines) throws Exception {
    
		for(String line : lines) {
			//AgentLogger.logTrace(line);

			if (line.toLowerCase().contains("[err]"))
			{
				this.appendOutput(file, line, "ERROR", "LAERROR");
			}
		}
	}

}
