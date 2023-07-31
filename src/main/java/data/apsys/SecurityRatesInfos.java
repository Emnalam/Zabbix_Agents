package data.apsys;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name = "securityratesinfos")
@XmlAccessorType (XmlAccessType.FIELD)
public class SecurityRatesInfos {
	@XmlElement(required=true, name="securityratesinfo") 
	List<SecurityRateInfo> ratesInfos = new ArrayList<SecurityRateInfo>();

	
  /** 
   * @param rateInfo
   */
  public void addRatesInfo(SecurityRateInfo rateInfo)
	{
		if (ratesInfos == null)
		{
			ratesInfos = new ArrayList<SecurityRateInfo>();
		}
		ratesInfos.add(rateInfo);
	}

    
    /** 
     * @return List<SecurityRateInfo>
     */
    public List<SecurityRateInfo> getRatesInfos() {
      return ratesInfos;
    }

    
    /** 
     * @param ratesInfos
     */
    public void setRatesInfos(List<SecurityRateInfo> ratesInfos) {
      this.ratesInfos = ratesInfos;
    }
	
}