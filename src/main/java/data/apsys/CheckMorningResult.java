
package data.apsys;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import javax.xml.bind.annotation.XmlAccessType;
@XmlRootElement(name="checkmorningresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckMorningResult extends ApsysResult {

  @XmlElement(required=true) 
  protected boolean isBatchAborted;

  @XmlElement(required=true) 
  protected Boolean isCheckOk;

  public CheckMorningResult() throws Exception {
    super();
    
  }

  
  /** 
   * @return boolean
   */
  public boolean isBatchAborted() {
    return isBatchAborted;
  }

  
  /** 
   * @param isBatchAborted
   */
  public void setBatchAborted(boolean isBatchAborted) {
    this.isBatchAborted = isBatchAborted;
  }

  
  /** 
   * @return Boolean
   */
  public Boolean isCheckOk() {
    return isCheckOk;
  }

  
  /** 
   * @param isCheckOk
   */
  public void setCheckOk(Boolean isCheckOk) {
    this.isCheckOk = isCheckOk;
  }

}