package workers.morning.apsys.validators;

import java.util.HashMap;

import config.morning.Check;
import data.apsys.CheckBatchLogResult;
import data.common.Result;
import workers.morning.MorningCheckValidator;

public class CheckBatchLogValidator extends MorningCheckValidator {

  
  /** 
   * @param check
   * @param xmlFragment
   * @return Result
   * @throws Exception
   */
  @Override
  public Result validate(Check check, String xmlFragment)  throws Exception {
    CheckBatchLogResult result = (CheckBatchLogResult)this.loadXml(xmlFragment, CheckBatchLogResult.class); 
    HashMap<String, String> conditions = check.getOk();

    if (conditions.size() == 0)
    {
      result.setCheckOk(true);
    }
    else {
      String strCount = conditions.get("count");
      if (strCount != null)
      {
        int count = new Integer(strCount).intValue();

        String errors = result.getBatchErrors();
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