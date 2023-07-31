
package data.apsys;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import javax.xml.bind.annotation.XmlAccessType;
@XmlRootElement(name="checksecuritiesratesresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckSecuritiesRatesResult extends CheckMorningResult {

  @XmlElement(required=true)
  protected boolean ctrFilesFound;

  @XmlElement(required=true) 
	protected SecurityRatesInfos data = new SecurityRatesInfos();
	
  public CheckSecuritiesRatesResult() throws Exception {
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
   */
  public void addRatesInfos(String type, int count)
	{
		if (data == null)
		{
			data = new SecurityRatesInfos();
		}
		
		SecurityRateInfo info = new SecurityRateInfo();
    info.setCount(count);
    info.setRatesType(type);
		
		data.addRatesInfo(info);
	}

    
    /** 
     * @return SecurityRatesInfos
     */
    public SecurityRatesInfos getData() {
      return data;
    }

    
    /** 
     * @param ratesInfos
     */
    public void setData(SecurityRatesInfos ratesInfos) {
      this.data = ratesInfos;
    }

}