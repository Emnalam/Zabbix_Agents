package data.apsys;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name = "currenciesinfos")
@XmlAccessorType (XmlAccessType.FIELD)
public class CurrenciesInfos {
	@XmlElement(required=true, name="currenciesinfo") 
	List<CurrencyInfo> currenciesInfos = new ArrayList<CurrencyInfo>();

	
	
  /** 
   * @param rateInfo
   */
  public void addRatesInfo(CurrencyInfo rateInfo)
	{
		if (currenciesInfos == null)
		{
			currenciesInfos = new ArrayList<CurrencyInfo>();
		}
		currenciesInfos.add(rateInfo);
	}

    
    /** 
     * @return List<CurrencyInfo>
     */
    public List<CurrencyInfo> getCurrenciesInfos() {
      return currenciesInfos;
    }

    
    /** 
     * @param currenciesInfos
     */
    public void setCurrenciesInfos(List<CurrencyInfo> currenciesInfos) {
      this.currenciesInfos = currenciesInfos;
    }
	
}