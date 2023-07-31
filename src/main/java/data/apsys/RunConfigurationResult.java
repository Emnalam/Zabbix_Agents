
package data.apsys;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import javax.xml.bind.annotation.XmlAccessType;
@XmlRootElement(name="runconfigurationresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class RunConfigurationResult extends CheckMorningResult {

  @XmlElement(required=true)
  protected String warningThreshold;

  @XmlElement(required=true)
  protected int runCount;

  @XmlElement(required=true)
  protected Boolean isWarning;

  public RunConfigurationResult() throws Exception {
    super();
    
  }

  
  /** 
   * @return String
   */
  public String getWarningThreshold() {
    return warningThreshold;
  }

  
  /** 
   * @param warningThreshold
   */
  public void setWarningThreshold(String warningThreshold) {
    this.warningThreshold = warningThreshold;
  }

  
  /** 
   * @return int
   */
  public int getRunCount() {
    return runCount;
  }

  
  /** 
   * @param runCount
   */
  public void setRunCount(int runCount) {
    this.runCount = runCount;
  }

  
  /** 
   * @return Boolean
   */
  public Boolean getIsWarning() {
    return isWarning;
  }

  
  /** 
   * @param isWarning
   */
  public void setIsWarning(Boolean isWarning) {
    this.isWarning = isWarning;
  }
}