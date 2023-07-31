package workers.morning.apsys;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import data.apsys.CheckSavesBackupResult;
import utils.DateUtils;
import workers.AgentLogger;

public class CheckSavesBackup extends ApsysMorningWorker {

  
  /** 
   * @throws Exception
   */
  @Override
  protected void doCheck() throws Exception {

    final String currentDateFormat = DateUtils.getDateFormat(new Date(), "yyyyMMdd");

    AgentLogger.logTrace(this.workerInfos.getName() + ", Last Accounting Date: " + lastAccountingDate);
    AgentLogger.logTrace(this.workerInfos.getName() + ", Saves Directory: " + this.savesDirectory);

    final Path aFolder = Paths.get(this.savesDirectory, String.format("run%s_1a", lastAccountingDate));
    final Path bFolder = Paths.get(this.savesDirectory, String.format("run%s_1b", lastAccountingDate));
    final Path cFolder = Paths.get(this.savesDirectory, String.format("run%s_1c", lastAccountingDate));

    final Path aFolder2 = Paths.get(this.savesDirectory, String.format("run%s_1a", currentDateFormat));
    final Path bFolder2 = Paths.get(this.savesDirectory, String.format("run%s_1b", currentDateFormat));
    final Path cFolder2 = Paths.get(this.savesDirectory, String.format("run%s_1c", currentDateFormat));

    final CheckSavesBackupResult result = new CheckSavesBackupResult();

    if (Files.exists(aFolder) && Files.exists(bFolder) && Files.exists(cFolder))
    {
      if (aFolder.toFile().listFiles().length > 0 && bFolder.toFile().listFiles().length > 0 && cFolder.toFile().listFiles().length > 0)
      {
        if (Files.exists(aFolder2) && Files.exists(bFolder2) && Files.exists(cFolder2))
        {
          result.setSavesBackupOk(true);
          result.setCheckOk(true);
        }
      }
    }
    
    this.save(result);
  }

  
  /** 
   * @throws Exception
   */
  @Override
  protected void cleanUp() throws Exception {
  }
  
}