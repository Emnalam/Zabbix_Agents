package data.equalizer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import data.common.Result;

@XmlRootElement(name="equalizerintegritycheckerresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class EqualizerIntegrityCheckerResult extends Result {

	public EqualizerIntegrityCheckerResult() throws Exception {
		super();
		
	}

	@XmlElement(required=true) 
	protected String equalizerPath;
	
	@XmlElement(required=true) 
	protected String integrityStatus;
	
	@XmlElement(required=true) 
	protected String eqconvResult;
	
	@XmlElement(required=true) 
	protected String dayChecked;

	
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
  public String getIntegrityStatus() {
		return integrityStatus;
	}

	
  /** 
   * @param integrityStatus
   */
  public void setIntegrityStatus(String integrityStatus) {
		this.integrityStatus = integrityStatus;
	}

	
  /** 
   * @return String
   */
  public String getEqconvResult() {
		return eqconvResult;
	}

	
  /** 
   * @param eqconvResult
   */
  public void setEqconvResult(String eqconvResult) {
		this.eqconvResult = eqconvResult;
	}

	
  /** 
   * @return String
   */
  public String getDayChecked() {
		return dayChecked;
	}

	
  /** 
   * @param dayChecked
   */
  public void setDayChecked(String dayChecked) {
		this.dayChecked = dayChecked;
	}
	
	
}
