package data.apsys;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import data.common.LogFilesMonitorResult;

import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name="apsyserrormonitorresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorsMonitorResult extends LogFilesMonitorResult { 

  @XmlElement(required=true) 
  protected String ap_Base;
  
	public ErrorsMonitorResult() throws Exception {
    super();
    ap_Base = System.getenv(ApsysResult.ENV_VARIABLE_AP_BASE);
    this.setAp_Base(ap_Base);
  }
  
  @XmlElement(required=true) 
	protected String agentExecutionDate;
	
	
  /** 
   * @return String
   */
  public String getAp_Base() {
		return ap_Base;
	}
	
	
  /** 
   * @param ap_Base
   */
  public void setAp_Base(final String ap_Base) {
		this.ap_Base = ap_Base;
	}
}
