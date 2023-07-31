package workers.morning.apsys;

import data.apsys.CheckProgramAbortResult;
import workers.morning.LogGrep;


public class CheckProgramAbort extends LogGrep {

  
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
    return "return code is 0|return is code 99";
  }

  
  /** 
   * @throws Exception
   */
  @Override
  protected void handleGrep() throws Exception {
    CheckProgramAbortResult result = new CheckProgramAbortResult();
    result.setCheckOk(!this.hasGrepOutpt());
    result.setBatchProgramAborts(this.commandOutput);
    this.save(result);
  }
}