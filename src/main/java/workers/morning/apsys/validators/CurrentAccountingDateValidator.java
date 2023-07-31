package workers.morning.apsys.validators;

import java.util.HashMap;

import config.morning.Check;
import data.apsys.CurrentAccountingDateResult;
import data.common.Result;
import workers.morning.MorningCheckValidator;

public class CurrentAccountingDateValidator extends MorningCheckValidator {

  
  /** 
   * @param check
   * @param xmlFragment
   * @return Result
   * @throws Exception
   */
  @Override
  public Result validate(Check check, String xmlFragment)  throws Exception {
    CurrentAccountingDateResult result = (CurrentAccountingDateResult)this.loadXml(xmlFragment, CurrentAccountingDateResult.class); 
    HashMap<String, String> conditions = check.getOk();

    if (conditions.size() == 0)
    {
      result.setCheckOk(true);
    }
    else {
      String strAccountingDate = conditions.get("accountingDate");

      if (strAccountingDate != null)
      {
        String accountingDate = result.getAccountingDate();
        if (accountingDate != null)
        {
          result.setCheckOk(accountingDate.equals(strAccountingDate));
        }
      }
    }

    return result;
  }


}