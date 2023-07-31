package workers.equalizer;

import java.io.File;

import workers.common.LogFilesMonitorWorker;


public class EqconvLogFileMonitorWorker extends LogFilesMonitorWorker{
	
	
  /** 
   * @throws Exception
   */
  @Override
	protected void init() throws Exception {
		super.init();
		this.workerInfos.setSubproduct("eqconv");
	}

	
  /** 
   * @param file
   * @param lines
   * @throws Exception
   */
  @Override
	protected void handleLogDataBlock(File file, String[] lines) throws Exception{

    for(String line : lines) {
    
      String data = line.toLowerCase();

      if (data.contains("rc=1"))
      {
        this.appendOutput(file, line, "ERROR", "RC1");
      }
      else if (data.contains("rc=0"))
      {
        this.appendOutput(file, line, "OK", "RC0");
      }
      else if (data.contains("conflict.txt"))
      {
        this.appendOutput(file, line, "WARNING", "CONFLICT");
      }
      else if (data.contains("keyconfl.txt"))
      {
        this.appendOutput(file, line, "ERROR", "KEYCONFLICT");
      }
      else if (data.contains("failure"))
      {
        this.appendOutput(file, line, "CRASH", "General Failure");
      }
    }
	}
}
