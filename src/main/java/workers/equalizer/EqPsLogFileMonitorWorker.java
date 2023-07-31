package workers.equalizer;

import java.io.File;

import workers.common.LogFilesMonitorWorker;


public class EqPsLogFileMonitorWorker extends LogFilesMonitorWorker{
	
	
  /** 
   * @throws Exception
   */
  @Override
	protected void init() throws Exception {
		super.init();
		this.workerInfos.setSubproduct("eqps");
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
				this.appendOutput(file, line, "ERROR", "EQPSERROR");
			}
			else if (line.toLowerCase().contains("[rxerror]"))
			{
				this.appendOutput(file, line, "ERROR", "EQPSERROR");
			}
			else if (line.contains("error"))
			{
				this.appendOutput(file, line, "ERROR", "EQPSERROR");
			}
		}
	}

}
