package workers.equalizer;

import java.io.File;

import workers.common.LogFilesMonitorWorker;


public class EqWsLogFileMonitorWorker extends LogFilesMonitorWorker{
	
	
  /** 
   * @throws Exception
   */
  @Override
	protected void init() throws Exception {
		super.init();
		this.workerInfos.setSubproduct("eqws");
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

			if (line.toLowerCase().contains("error"))
			{
				this.appendOutput(file, line, "ERROR", "EQWSERROR");
			}
			
		}
	}

}
