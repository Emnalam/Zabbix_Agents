package workers.apsys;

import java.io.File;
import java.util.Arrays;

import data.apsys.ErrorsMonitorResult;
import data.common.LogFilesMonitorResult;
import workers.AgentLogger;
import workers.common.LogFilesMonitorWorker;

public class ErrorsMonitorWorker extends LogFilesMonitorWorker {

  
  /** 
   * @return LogFilesMonitorResult
   * @throws Exception
   */
  @Override
  protected LogFilesMonitorResult getDataResultObject() throws Exception
  {
    return new ErrorsMonitorResult();
  }

  
  /** 
   * @param file
   * @param lines
   * @throws Exception
   */
  @Override
  protected void handleLogDataBlock(final File file, final String[] lines) throws Exception {
    final StringBuffer sb = new StringBuffer();
    boolean isFetchingCrash = false;

    for (final String line : lines) {
      AgentLogger.logTrace(line);

      if (isFetchingCrash) {
        if (line.indexOf(" (I) ") > -1 || line.indexOf(" (D) ") > -1) {
          isFetchingCrash = false;
          this.appendCrashOutput(file, sb.toString());
        } else if (line.indexOf(" (E) ") > -1) {
          isFetchingCrash = false;
          this.appendCrashOutput(file, sb.toString());
          this.appendErrorOutput(file, line);
        } else if (line.indexOf(" (F) ") > -1) {
          isFetchingCrash = false;
          this.appendCrashOutput(file, sb.toString());
          this.appendFatalOutput(file, line);
        } else {
          sb.append(line).append(System.lineSeparator());
        }
      } else {
        if (line.indexOf("lwp#") > -1 || line.indexOf("tid#") > -1) {
          sb.delete(0, sb.length());
          sb.append(line).append(System.lineSeparator());
          isFetchingCrash = true;
        }
        if (line.indexOf(" (E) ") > -1) {
          this.appendErrorOutput(file, line);
        }

        if (line.indexOf(" (F) ") > -1) {

          this.appendFatalOutput(file, line);
        }

        final String rc = "return code ";
        final int i = line.indexOf(rc);

        if (i > -1) {
          final int j = i + rc.length();
            
            final String code = line.substring(j).trim();
            
            if (!code.equals("1"))
            {
              this.appendReturnCodeErrorOutput(file, line);
            }
          }
        }
      }

      if (isFetchingCrash) //crash is the end of the block
      {
        this.appendCrashOutput(file, Arrays.toString(lines));
      }
    }
  }
