package workers.morning.apsys.validators;

import java.util.HashMap;
import java.util.List;

import config.morning.Check;
import data.apsys.CheckCurrenciesUpdatedResult;
import data.apsys.CurrenciesInfos;
import data.apsys.CurrencyInfo;
import data.common.Result;
import workers.morning.MorningCheckValidator;

public class CheckCurrenciesUpdatedValidator extends MorningCheckValidator {

  
  /** 
   * @param check
   * @param xmlFragment
   * @return Result
   * @throws Exception
   */
  @Override
  public Result validate(Check check, String xmlFragment) throws Exception {
    CheckCurrenciesUpdatedResult result = (CheckCurrenciesUpdatedResult) this.loadXml(xmlFragment,
        CheckCurrenciesUpdatedResult.class);
    HashMap<String, String> conditions = check.getOk();

    if (!result.isCtrFilesFound()) {
      result.setCheckOk(false);
    } else {
      // errorsCount
      if (conditions.size() == 0) {
        result.setCheckOk(true);
      } else {
        String strCount = conditions.get("count");
        if (strCount != null) {
          int count = new Integer(strCount).intValue();

          String strErrorCount = conditions.get("errorsCount");

          int errors = new Integer(strErrorCount);

          CurrenciesInfos ci = result.GetData();
          List<CurrencyInfo> currenciesInfos = ci.getCurrenciesInfos();

          for (CurrencyInfo currencyInfo : currenciesInfos)
          {
            int ccount = currencyInfo.getCount();
            int cerrors = currencyInfo.getErrorsCount();

            result.setCheckOk(ccount >= count && cerrors <= errors);
          }
        }
      }
    }

    return result;
  }

}