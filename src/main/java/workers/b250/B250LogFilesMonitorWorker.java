package workers.b250;

import java.io.File;

import workers.common.LogFilesMonitorWorker;

public class B250LogFilesMonitorWorker extends LogFilesMonitorWorker {
	
	
  /** 
   * @param file
   * @param lines
   * @throws Exception
   */
  @Override
	protected void handleLogDataBlock(File file, String[] lines) throws Exception {
		
		boolean isFetchingCrash = false;
    StringBuffer sb = new StringBuffer();
    
		for(String line : lines) {

			if (isFetchingCrash)
			{
				if (line.indexOf(" INFO ") > -1 || line.indexOf(" DEBUG ") > -1)
				{
					isFetchingCrash = false;
					this.appendOutput(file, sb.toString(), "ERROR", "B250ERROR");
					sb.delete(0, sb.length());
				}
        else if (line.indexOf(" ERROR ") > -1)
        {
          this.appendOutput(file, sb.toString(), "ERROR", "B250ERROR");
          sb.delete(0, sb.length());
          sb.append(line).append(System.lineSeparator());
        }
        else if (line.indexOf(" WARN ") > -1)
        {
          isFetchingCrash = false;
          this.appendOutput(file, sb.toString(), "ERROR", "B250ERROR");
          sb.delete(0, sb.length());
          sb.append(line).append(System.lineSeparator());
          this.appendOutput(file, line, "WARNING", "B250WARNING");
        }
        else
				{
					sb.append(line).append(System.lineSeparator());
				}
			}
			else
			{
				if (line.indexOf(" FATAL ") > -1 || line.indexOf(" ERROR ") > -1)
				{
					sb.delete(0, sb.length());
					sb.append(line).append(System.lineSeparator());
					isFetchingCrash = true;
				}
				else if (line.toLowerCase().indexOf("error") > -1)
				{
					this.appendOutput(file, line, "ERROR", "B250ERROR");
				}
				else if (line.indexOf(" WARN ") > -1)
				{
					this.appendOutput(file, line, "WARNING", "B250WARNING");
				}
			}
    }
    
    if (isFetchingCrash)
    {
      this.appendOutput(file, sb.toString(), "ERROR", "B250ERROR");
    }
		sb.delete(0, sb.length());
	}
}
