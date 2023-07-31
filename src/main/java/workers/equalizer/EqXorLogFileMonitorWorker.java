package workers.equalizer;

import java.io.File;

import workers.common.LogFilesMonitorWorker;

public class EqXorLogFileMonitorWorker extends LogFilesMonitorWorker{

	
  /** 
   * @throws Exception
   */
  @Override
	protected void init() throws Exception {
		super.init();
		this.workerInfos.setSubproduct("eqxor");
	}
	
	
  /** 
   * @param file
   * @param lines
   * @throws Exception
   */
  @Override
	protected void handleLogDataBlock(File file, String[] lines) throws Exception {
	
		for(String line : lines) {
			if (line.contains("%-ERR"))
			{
        if (line.contains("No records fetched") ||
            line.contains("Error in getting field values") ||
            line.contains("Status map code for") ||
            line.contains(", continuing")
        )
        {
          continue;
        }
				this.appendOutput(file, line, "ERROR", "EQXORERROR");
			}
			else if (line.contains("%-WRN"))
			{
				this.appendOutput(file, line, "WARNING", "EQXORWARNING");
			}
		}
	}

}
