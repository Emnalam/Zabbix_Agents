package workers.common;

import java.io.File;

import config.Property;
import workers.AgentLogger;

public class GenericLogFilesMonitorWorker extends LogFilesMonitorWorker {
	
  
  /** 
   * @param file
   * @param lines
   * @throws Exception
   */
  @Override
  protected void handleLogDataBlock(File file, String[] lines) throws Exception {
    String fileName = file.getName();
    
    Property matchedProperty = this.tryGetMatchingProperty(fileName);

      if (matchedProperty != null)
      {
        for(String line : lines) {
            if (this.tryMatchPropertyValues(matchedProperty, line))
            {
              this.appendOutput(file, line, "ERROR", "GENERICERROR");
            }
        }
      }
      else
      {
        AgentLogger.logError(this.getName() + ": No Properties with patterns found in the configuration", workerInfos);
      }
  }
}
