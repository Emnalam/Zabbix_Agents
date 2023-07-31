package config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="periodic")
@XmlAccessorType(XmlAccessType.FIELD)
public class Periodic {

  @XmlAttribute(required=false) 
  protected String days;

  @XmlAttribute(required=false) 
  protected String from;

  @XmlAttribute(required=false) 
  protected String to;

	@XmlAttribute(required=false) 
	protected int frequence;

	
  /** 
   * @return int
   */
  public int getFrequence() {
		return frequence;
	}

	
  /** 
   * @param frequence
   */
  public void setFrequence(int frequence) {
		this.frequence = frequence;
	}

  
  /** 
   * @return String
   */
  public String getFrom() {
    return from;
  }

  
  /** 
   * @param from
   */
  public void setFrom(String from) {
    this.from = from;
  }

  
  /** 
   * @return String
   */
  public String getTo() {
    return to;
  }

  
  /** 
   * @param to
   */
  public void setTo(String to) {
    this.to = to;
  }

  
  /** 
   * @return String
   */
  public String getDays() {
    return days;
  }

  
  /** 
   * @param days
   */
  public void setDays(String days) {
    this.days = days;
  }
}
