
package data.apsys;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import javax.xml.bind.annotation.XmlAccessType;
@XmlRootElement(name="databaseoperationalstatusresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class DatabaseOperationalStatusResult extends CheckMorningResult {
  
  @XmlElement(required=true) 
  protected String operatingMode;

  @XmlElement(required=true) 
  protected String currentStage;

  public DatabaseOperationalStatusResult() throws Exception {
    super();
    
  }

  
  /** 
   * @return String
   */
  public String getOperatingMode() {
    return operatingMode;
  }

  
  /** 
   * @param operatingMode
   */
  public void setOperatingMode(String operatingMode) {
    this.operatingMode = operatingMode;
  }

  
  /** 
   * @return String
   */
  public String getCurrentStage() {
    return currentStage;
  }

  
  /** 
   * @param currentStage
   */
  public void setCurrentStage(String currentStage) {
    this.currentStage = currentStage;
  }
}