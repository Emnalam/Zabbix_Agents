package data.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="certificateinfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class CertificateInfo {
	@XmlElement(required=true) 
	protected String cn;
	
	@XmlElement(required=true) 
	protected String daysLeft;

    
    /** 
     * @return String
     */
    public String getCn() {
      return cn;
    }

    
    /** 
     * @param cn
     */
    public void setCn(String cn) {
      this.cn = cn;
    }

    
    /** 
     * @return String
     */
    public String getDaysLeft() {
      return daysLeft;
    }

    
    /** 
     * @param daysLeft
     */
    public void setDaysLeft(String daysLeft) {
      this.daysLeft = daysLeft;
    }
}
