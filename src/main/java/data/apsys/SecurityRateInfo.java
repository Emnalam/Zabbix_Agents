package data.apsys;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ratesinfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class SecurityRateInfo {
  
	@XmlElement(required=true) 
  protected String ratesType;

  @XmlElement(required=true) 
	protected int count;

    
    /** 
     * @return String
     */
    public String getRatesType() {
      return ratesType;
    }

    
    /** 
     * @param ratesType
     */
    public void setRatesType(String ratesType) {
      this.ratesType = ratesType;
    }

    
    /** 
     * @return int
     */
    public int getCount() {
      return count;
    }

    
    /** 
     * @param count
     */
    public void setCount(int count) {
      this.count = count;
    }

}
