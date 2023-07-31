package workers.morning.apsys.validators;

import java.util.HashMap;

import config.morning.Check;
import data.apsys.CheckProgramAbortResult;
import data.common.Result;
import workers.morning.MorningCheckValidator;

public class CheckProgramAbortValidator extends MorningCheckValidator {

  
  /** 
   * @param check
   * @param xmlFragment
   * @return Result
   * @throws Exception
   */
  @Override
  public Result validate(Check check, String xmlFragment)  throws Exception {
    CheckProgramAbortResult result = (CheckProgramAbortResult)this.loadXml(xmlFragment, CheckProgramAbortResult.class); 
    HashMap<String, String> conditions = check.getOk();

    if (conditions.size() == 0)
    {
      result.setCheckOk(true);
    }
    else {
      String strCount = conditions.get("batchProgramAborts");
      if (strCount != null)
      {
        int count = new Integer(strCount).intValue();

        String errors = result.getBatchProgramAborts();

        if (errors.trim().length() == 0)
        {
          result.setCheckOk(true);
        }
        else 
        {
          String[] lines = errors.split("\\r?\\n");

          result.setCheckOk(lines.length <= count);
        }
      }
    }

    return result;
  }


}