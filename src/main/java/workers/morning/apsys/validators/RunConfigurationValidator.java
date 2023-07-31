package workers.morning.apsys.validators;

import java.util.HashMap;

import config.morning.Check;
import data.apsys.RunConfigurationResult;
import data.common.Result;
import workers.morning.MorningCheckValidator;

public class RunConfigurationValidator extends MorningCheckValidator {

  
  /** 
   * @param check
   * @param xmlFragment
   * @return Result
   * @throws Exception
   */
  @Override
  public Result validate(Check check, String xmlFragment)  throws Exception {
    RunConfigurationResult result = (RunConfigurationResult)this.loadXml(xmlFragment, RunConfigurationResult.class); 
    HashMap<String, String> conditions = check.getOk();

    if (conditions.size() == 0)
    {
      result.setCheckOk(true);
    }
    else {
      String strCount = conditions.get("runCount");
      if (strCount != null)
      {
        int count = new Integer(strCount).intValue();

        int runs = result.getRunCount();

        result.setCheckOk(runs >= count);
      }
    }

    return result;
  }


}