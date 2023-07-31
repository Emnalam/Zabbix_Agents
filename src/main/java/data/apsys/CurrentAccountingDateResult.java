
package data.apsys;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import javax.xml.bind.annotation.XmlAccessType;
@XmlRootElement(name="currentaccountingdateresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CurrentAccountingDateResult extends CheckMorningResult {
  
  @XmlElement(required=true) 
  protected String accountingDate;

  public CurrentAccountingDateResult() throws Exception {
    super();
   
  }

  
  /** 
   * @return String
   */
  public String getAccountingDate() {
    return accountingDate;
  }

  
  /** 
   * @param accountingDate
   */
  public void setAccountingDate(String accountingDate) {
    this.accountingDate = accountingDate;
  }
}