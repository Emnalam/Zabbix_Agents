package workers.morning.apsys.validators;

import java.util.HashMap;
import java.util.List;

import config.morning.Check;
import data.apsys.CheckSecuritiesRatesResult;
import data.apsys.SecurityRateInfo;
import data.apsys.SecurityRatesInfos;
import data.common.Result;
import workers.morning.MorningCheckValidator;

public class CheckSecuritiesRatesValidator extends MorningCheckValidator {

  
  /** 
   * @param check
   * @param xmlFragment
   * @return Result
   * @throws Exception
   */
  @Override
  public Result validate(Check check, String xmlFragment)  throws Exception {
    CheckSecuritiesRatesResult result = (CheckSecuritiesRatesResult)this.loadXml(xmlFragment, CheckSecuritiesRatesResult.class); 
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

          SecurityRatesInfos ci = result.getData();
          List<SecurityRateInfo> securityRatesInfos = ci.getRatesInfos();

          for (SecurityRateInfo securityRateInfo : securityRatesInfos)
          {
            int scount = securityRateInfo.getCount();
            
            result.setCheckOk(scount >= count);
          }
        }
      }
    }

    return result;
  }


}