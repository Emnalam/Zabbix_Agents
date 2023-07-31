package config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="day")
@XmlAccessorType(XmlAccessType.FIELD)
public class Day {
	@XmlAttribute(required=false) 
	protected String name;
	
	@XmlAttribute(required=false) 
	protected String time;

	
  /** 
   * @return String
   */
  public String getName() {
		return name;
	}

	
  /** 
   * @param name
   */
  public void setName(String name) {
		this.name = name;
	}

	
  /** 
   * @return String
   */
  public String getTime() {
		return time;
	}

	
  /** 
   * @param time
   */
  public void setTime(String time) {
		this.time = time;
	}
}
