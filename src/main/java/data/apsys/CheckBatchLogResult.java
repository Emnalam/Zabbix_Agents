
package data.apsys;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import javax.xml.bind.annotation.XmlAccessType;
@XmlRootElement(name="checkbatchlogresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckBatchLogResult extends CheckMorningResult {

  @XmlElement(required=true)
  protected String batchErrors;

  public CheckBatchLogResult() throws Exception {
    super();
    
  }

  
  /** 
   * @return String
   */
  public String getBatchErrors() {
    return batchErrors;
  }

  
  /** 
   * @param batchErrors
   */
  public void setBatchErrors(String batchErrors) {
    this.batchErrors = batchErrors;
  }
}