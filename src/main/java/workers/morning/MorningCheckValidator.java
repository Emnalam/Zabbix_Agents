package workers.morning;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;


import config.morning.Check;
import data.common.Result;

public abstract class MorningCheckValidator {

  
  /** 
   * @param check
   * @param xmlFragment
   * @param classToLoad
   * @return Result
   * @throws JAXBException
   */
  public abstract Result validate(Check check, String xmlFragment)  throws Exception;

  
  /** 
   * @param xmlFragment
   * @param classToLoad
   * @return Result
   * @throws JAXBException
   */
  protected <T> Result loadXml(final String xmlFragment, final Class<T> classToLoad) throws JAXBException {
    final StringReader reader = new StringReader(xmlFragment);
    final JAXBContext jaxbContext = JAXBContext.newInstance(classToLoad);
    return (Result)jaxbContext.createUnmarshaller().unmarshal(reader);
  }

  
  /** 
   * @param tagName
   * @return String
   * @throws Exception
   */
  public static String getDefaultCheckValidatorName(String tagName) throws Exception {
    switch (tagName) {
      case "checkbatchlogresult":
        return "workers.morning.apsys.validators.CheckBatchLogValidator";
      case "checkcurrenciesupdatedresult":
        return "workers.morning.apsys.validators.CheckCurrenciesUpdatedValidator";
      case "checkprogramabortresult":
        return "workers.morning.apsys.validators.CheckProgramAbortValidator";
      case "checksavesbackup":
        return "workers.morning.apsys.validators.CheckSavesBackupValidator";
      case "checksecuritiesratesresult":
        return "workers.morning.apsys.validators.CheckSecuritiesRatesValidator";
      case "checktransactionabortresult":
        return "workers.morning.apsys.validators.CheckTransactionAbortValidator";
      case "currentaccountingdateresult":
        return "workers.morning.apsys.validators.CurrentAccountingDateValidator";
      case "databaseoperationalstatusresult":
        return "workers.morning.apsys.validators.DatabaseOperationalStatusValidator";
      case "runconfigurationresult":
        return "workers.morning.apsys.validators.RunConfigurationValidator";
    }
    throw new Exception("Cannot find default valiator for tagName " + tagName);
  }
}