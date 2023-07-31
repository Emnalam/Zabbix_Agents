package config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="days")
@XmlAccessorType(XmlAccessType.FIELD)
public class Days {
	@XmlElement(required=true, name="day") 
	List<Day> days = new ArrayList<Day>();

	
  /** 
   * @return List<Day>
   */
  public List<Day> getDays() {
		return days;
	}

	
  /** 
   * @param days
   */
  public void setDays(List<Day> days) {
		this.days = days;
	}

}
