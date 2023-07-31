package workers.morning.apsys;

import data.apsys.CheckBatchLogResult;
import workers.morning.LogGrep;


public class CheckBatchLog extends LogGrep {

  
  /** 
   * @return String
   */
  @Override
  protected String getFilesToGrepPattern() {
    return "batsrv_*";
  }

  
  /** 
   * @return String
   */
  @Override
  protected String getDirectoryToGrep() {
    return String.format(savesDirectory + "/run%s_1c", lastAccountingDate);
  }

  
  /** 
   * @return String
   */
  @Override
  protected String getGrepPattern() {
    return "\\(E\\)";
  }

  
  /** 
   * @throws Exception
   */
  @Override
  protected void handleGrep() throws Exception {
    CheckBatchLogResult result = new CheckBatchLogResult();
    result.setCheckOk(!this.hasGrepOutpt());
    result.setBatchErrors(this.commandOutput);
    this.save(result);
  }
}