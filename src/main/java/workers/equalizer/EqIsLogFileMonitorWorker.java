package workers.equalizer;

import java.io.File;

import workers.common.LogFilesMonitorWorker;


public class EqIsLogFileMonitorWorker extends LogFilesMonitorWorker{
	
	
  /** 
   * @throws Exception
   */
  @Override
	protected void init() throws Exception {
		super.init();
		this.workerInfos.setSubproduct("eqis");
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

			if (line.toLowerCase().contains("[eqerror]"))
			{
				this.appendOutput(file, line, "ERROR", "EQISERROR");
			}
			else if (line.toLowerCase().contains("[rxerror]"))
			{
				this.appendOutput(file, line, "ERROR", "EQISRXERROR");
			}
		}
		
	}

}
