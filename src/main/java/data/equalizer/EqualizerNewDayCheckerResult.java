package data.equalizer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import data.common.Result;

@XmlRootElement(name="equalizernewdaycheckerresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class EqualizerNewDayCheckerResult extends Result {
	public EqualizerNewDayCheckerResult() throws Exception {
		super();
	}

	@XmlElement(required=true) 
	protected String equalizerPath;
	
	@XmlElement(required=true) 
	protected String dayInfo;
	
	@XmlElement(required=true) 
	protected String dayStatus;

	
  /** 
   * @return String
   */
  public String getEqualizerPath() {
		return equalizerPath;
	}

	
  /** 
   * @param equalizerPath
   */
  public void setEqualizerPath(String equalizerPath) {
		this.equalizerPath = equalizerPath;
	}

	
  /** 
   * @return String
   */
  public String getDayInfo() {
		return dayInfo;
	}

	
  /** 
   * @param dayInfo
   */
  public void setDayInfo(String dayInfo) {
		this.dayInfo = dayInfo;
	}

	
  /** 
   * @return String
   */
  public String getDayStatus() {
		return dayStatus;
	}

	
  /** 
   * @param dayStatus
   */
  public void setDayStatus(String dayStatus) {
		this.dayStatus = dayStatus;
	}

	
}
