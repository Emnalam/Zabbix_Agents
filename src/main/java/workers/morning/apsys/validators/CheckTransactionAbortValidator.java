package workers.morning.apsys.validators;

import java.util.HashMap;

import config.morning.Check;
import data.apsys.CheckTransactionAbortResult;
import data.apsys.TransactionErrors;
import data.common.Result;
import workers.morning.MorningCheckValidator;

public class CheckTransactionAbortValidator extends MorningCheckValidator {

  
  /** 
   * @param check
   * @param xmlFragment
   * @return Result
   * @throws Exception
   */
  @Override
  public Result validate(Check check, String xmlFragment)  throws Exception {
    CheckTransactionAbortResult result = (CheckTransactionAbortResult)this.loadXml(xmlFragment, CheckTransactionAbortResult.class); 
    HashMap<String, String> conditions = check.getOk();

    if (conditions.size() == 0)
    {
      result.setCheckOk(true);
    }
    else {
      String strCount = conditions.get("transactionErrors");
      if (strCount != null)
      {
        int count = new Integer(strCount).intValue();

        TransactionErrors errors = result.getErrors();
        
        result.setCheckOk(errors.getErrors().size() <= count);
      }
    }

    return result;
  }


}