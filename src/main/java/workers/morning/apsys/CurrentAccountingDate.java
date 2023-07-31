package workers.morning.apsys;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import data.apsys.CurrentAccountingDateResult;
import utils.DateUtils;

public class CurrentAccountingDate extends ApsysMorningWorker {

	private final static String DEFAULT_ARG = "currentaccountingDate_out.tmp";
	private final static String DATA_SPLIT_DELIMITOR = "#";

  
  /** 
   * @throws Exception
   */
  @Override
  protected void doCheck() throws Exception {
    this.executeCommand(getOperationInfoCommand + " " + DEFAULT_ARG, this.workerInfos.getSuccessExitCode(), null, System.getProperty("user.dir"));
    
    final String content = new String(Files.readAllBytes(Paths.get(DEFAULT_ARG)));

    final String[] parts = content.split(DATA_SPLIT_DELIMITOR);

    final String accountingDateStr = parts[1];

    final Date accountingDate = DateUtils.parseDate(accountingDateStr, "yyyyMMdd");

    final CurrentAccountingDateResult result = new CurrentAccountingDateResult();
    result.setCheckOk(DateUtils.isToday(accountingDate));
    result.setAccountingDate(parts[1]);

    this.save(result);
  }

  
  /** 
   * @throws Exception
   */
  @Override
  protected void cleanUp() throws Exception {
    final File file = new File(DEFAULT_ARG);
  	file.delete();
  }
  
}