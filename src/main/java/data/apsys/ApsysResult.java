package data.apsys;

import java.text.SimpleDateFormat;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import data.common.Result;

@XmlRootElement(name="getopertioninforesult")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class ApsysResult extends Result {
	
	public ApsysResult() throws Exception {
		super();
		agent_Host = System.getenv(ENV_VARIABLE_APSYS_HOST);
		ap_Base = System.getenv(ENV_VARIABLE_AP_BASE);
		
		this.setAgent_Host(agent_Host);
		this.setAp_Base(ap_Base);
	}

	@XmlTransient
	protected final static String ENV_VARIABLE_APSYS_HOST 		= "APSYS_HOST";
	@XmlTransient
	protected final static String ENV_VARIABLE_AP_BASE 			= "AP_BASE";
	
	@XmlTransient
	private SimpleDateFormat sdf;
	
	@XmlElement(required=true) 
	protected String ap_Base;
	
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
  public void setAp_Base(String ap_Base) {
		this.ap_Base = ap_Base;
	}
}
