package config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="scheduling")
@XmlAccessorType(XmlAccessType.FIELD)
public class Scheduling {

	@XmlAttribute(required=true, name="enabled")
	protected boolean enabled;

	@XmlElement(required=true, name="periodic")
	protected Periodic periodic;
	
	@XmlElement(required=true, name="days")
	protected Days days;
	
	
  /** 
   * @return boolean
   */
  public boolean isEnabled() {
		return enabled;
	}

	
  /** 
   * @param enabled
   */
  public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}


	
  /** 
   * @return Periodic
   */
  public Periodic getPeriodic() {
		return periodic;
	}

	
  /** 
   * @param periodic
   */
  public void setPeriodic(Periodic periodic) {
		this.periodic = periodic;
	}

	
  /** 
   * @return Days
   */
  public Days getDays() {
		return days;
	}

	
  /** 
   * @param days
   */
  public void setDays(Days days) {
		this.days = days;
	}
}
