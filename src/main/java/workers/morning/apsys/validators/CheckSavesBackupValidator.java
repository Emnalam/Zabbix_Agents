package workers.morning.apsys.validators;

import java.util.HashMap;

import config.morning.Check;
import data.apsys.CheckSavesBackupResult;
import data.common.Result;
import workers.morning.MorningCheckValidator;

public class CheckSavesBackupValidator extends MorningCheckValidator {

  
  /** 
   * @param check
   * @param xmlFragment
   * @return Result
   * @throws Exception
   */
  @Override
  public Result validate(Check check, String xmlFragment)  throws Exception {
    CheckSavesBackupResult result = (CheckSavesBackupResult)this.loadXml(xmlFragment, CheckSavesBackupResult.class); 
    HashMap<String, String> conditions = check.getOk();

    if (conditions.size() == 0)
    {
      result.setCheckOk(true);
    }
    else {
      String strValue = conditions.get("savesBackupOk");
      if (strValue != null)
      {
        boolean b = new Boolean(strValue).booleanValue();

        result.setCheckOk(b);
      }
    }

    return result;
  }


}