
package data.apsys;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import javax.xml.bind.annotation.XmlAccessType;
@XmlRootElement(name="checkprogramabortresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckProgramAbortResult extends CheckMorningResult {

  @XmlElement(required=true)
  protected String batchProgramAborts;

  public CheckProgramAbortResult() throws Exception {
    super();
   
  }

  
  /** 
   * @return String
   */
  public String getBatchProgramAborts() {
    return batchProgramAborts;
  }

  
  /** 
   * @param batchProgramAborts
   */
  public void setBatchProgramAborts(String batchProgramAborts) {
    this.batchProgramAborts = batchProgramAborts;
  }
}