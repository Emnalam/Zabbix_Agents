package workers.common;

import java.io.File;
import workers.AgentLogger;

public class FileProgressMonitorWorker extends LogFilesMonitorWorker {

  private int viewSize = 50;
  private long idleTimeoutMinutes = -1;

  
  /** 
   * @throws Exception
   */
  @Override
  protected void init() throws Exception {
    super.init();

    final String _viewSize = parameters.get("viewsize");
    final String _idleTimeoutMinutes = parameters.get("idletimeoutminutes");

    if (_viewSize != null)
    {
      this.viewSize = new Integer(_viewSize.trim());
    }

    if (_idleTimeoutMinutes != null)
    {
      this.idleTimeoutMinutes = new Integer(_idleTimeoutMinutes.trim());
    }

    AgentLogger.logTrace(this.workerInfos.getName() + ": using idleTimeoutMinutes=" + this.idleTimeoutMinutes);
    AgentLogger.logTrace(this.workerInfos.getName() + ": using viewsize=" + this.viewSize);
  }

  
  /** 
   * @param file
   * @throws Exception
   */
  @Override
  protected void handleIdleEvent(File file) throws Exception {
    super.handleIdleEvent(file);

    if (this.idleTimeoutMinutes > 0)
    {
      long lastModified = file.lastModified();
      long now = System.currentTimeMillis();
      long diff = now - lastModified;

      if (diff / 60000 >= this.idleTimeoutMinutes)
      {
        this.appendOutput(file, "", "PROGRESS", "IDLE");
      }
      AgentLogger.logTrace(this.getName() + ", HandleIdleEvent for " + file.getAbsolutePath() + ": lastModified=" + lastModified + ", now=" + now + ", diffMinutes=" + (diff / 60000));
    }
  }

  
  /** 
   * @param file
   * @param lines
   * @throws Exception
   */
  @Override
  protected void handleLogDataBlock(final File file, final String[] lines) throws Exception {
    final StringBuffer sb = new StringBuffer();
    for (int i = 0; i < this.viewSize; i++)
    {
      if (lines.length > i)
      {
        sb.append(lines[i]).append(System.lineSeparator());
      }
      else {
        break;
      }
    }
    this.appendOutput(file, sb.toString(), "PROGRESS", "INFO");
  }
}