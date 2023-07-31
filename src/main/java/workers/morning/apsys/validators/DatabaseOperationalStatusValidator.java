package workers.morning.apsys.validators;

import java.util.HashMap;

import config.morning.Check;
import data.apsys.DatabaseOperationalStatusResult;
import data.common.Result;
import workers.morning.MorningCheckValidator;

public class DatabaseOperationalStatusValidator extends MorningCheckValidator {

  
  /** 
   * @param check
   * @param xmlFragment
   * @return Result
   * @throws Exception
   */
  @Override
  public Result validate(Check check, String xmlFragment)  throws Exception {
    DatabaseOperationalStatusResult result = (DatabaseOperationalStatusResult)this.loadXml(xmlFragment, DatabaseOperationalStatusResult.class); 
    HashMap<String, String> conditions = check.getOk();

    if (conditions.size() == 0)
    {
      result.setCheckOk(true);
    }
    else {
      String strCurrentStatge = conditions.get("currentStage");

      if (strCurrentStatge != null)
      {
        String srtOperationMode = conditions.get("operatingMode");

        if (srtOperationMode != null)
        {
          result.setCheckOk(strCurrentStatge.equals(result.getCurrentStage()) && srtOperationMode.equals(result.getOperatingMode()));
        }
      }
    }

    return result;
  }


}