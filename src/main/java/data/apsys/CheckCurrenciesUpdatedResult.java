
package data.apsys;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import javax.xml.bind.annotation.XmlAccessType;
@XmlRootElement(name="checkcurrenciesupdatedresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckCurrenciesUpdatedResult extends CheckMorningResult {
  
  @XmlElement(required=true)
  protected boolean ctrFilesFound;

  @XmlElement(required=true) 
  protected CurrenciesInfos data = new CurrenciesInfos();
  
  public CheckCurrenciesUpdatedResult() throws Exception {
    super();
    
  }
	
  
  /** 
   * @return boolean
   */
  public boolean isCtrFilesFound() {
    return ctrFilesFound;
  }

  
  /** 
   * @param ctrFilesFound
   */
  public void setCtrFilesFound(boolean ctrFilesFound) {
    this.ctrFilesFound = ctrFilesFound;
  }

  
  /** 
   * @param type
   * @param count
   * @param errorsCount
   */
  public void addRatesInfos(String type, int count, int errorsCount)
	{
		if (data == null)
		{
			data = new CurrenciesInfos();
		}
		
		CurrencyInfo info = new CurrencyInfo();
    info.setCount(count);
    info.setErrorsCount(errorsCount);
    info.setRatesType(type);
		
		data.addRatesInfo(info);
	}

    
    /** 
     * @return CurrenciesInfos
     */
    public CurrenciesInfos GetData() {
      return data;
    }

    
    /** 
     * @param ratesInfos
     */
    public void setData(CurrenciesInfos ratesInfos) {
      this.data = ratesInfos;
    }

}