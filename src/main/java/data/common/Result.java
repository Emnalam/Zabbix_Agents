package data.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import config.Settings;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Result {

	@XmlTransient
	private SimpleDateFormat sdf;
  
  @XmlElement(required=true) 
	protected String agent_Version;
	
	@XmlElement(required=true) 
	protected String agent_Host;

	@XmlElement(required=true) 
	protected String environment;
	
	@XmlElement(required=true) 
	protected String product;
	
	@XmlElement(required=true) 
  protected String subproduct;
  
  @XmlElement(required=true) 
	protected String zTag;
	
	@XmlElement(required=true) 
	protected String agentExecutionDate;
	
	
  /** 
   * @return String
   */
  public String getSubproduct() {
		return subproduct;
	}

	
  /** 
   * @param subproduct
   */
  public void setSubproduct(String subproduct) {
		this.subproduct = subproduct;
	}

	
  /** 
   * @return String
   */
  public String getEnvironment() {
		return environment;
	}

	
  /** 
   * @param environment
   */
  public void setEnvironment(String environment) {
		this.environment = environment;
	}

	
  /** 
   * @return String
   */
  public String getProduct() {
		return product;
	}

	
  /** 
   * @param product
   */
  public void setProduct(String product) {
		this.product = product;
	}

	public Result() throws Exception
	{
		sdf = new SimpleDateFormat(Settings.getInstance().getDateFormat());

		agent_Host = System.getenv("COMPUTERNAME");
		
		if (agent_Host == null || agent_Host == "")
		{
			agent_Host = System.getenv("HOSTNAME");
		}
		
		if (agent_Host == null || agent_Host == "")
		{
			agent_Host = System.getenv("APSYS_HOST");
		}
		
		this.setAgentExecutionDate(sdf.format(Calendar.getInstance().getTime()));
    this.setAgent_Host(agent_Host);
    this.setAgent_Version(AgentVersion.implementationVersion);
	}

	
  /** 
   * @return String
   */
  public String getAgent_Host() {
		return agent_Host;
	}
	
	
  /** 
   * @param agent_Host
   */
  public void setAgent_Host(String agent_Host) {
		this.agent_Host = agent_Host;
	}
	 
	
  /** 
   * @return String
   */
  public String getAgentExecutionDate() {
		return agentExecutionDate;
	}
	
	
  /** 
   * @param agentExecutionDate
   */
  public void setAgentExecutionDate(String agentExecutionDate) {
		this.agentExecutionDate = agentExecutionDate;
	}

  
  /** 
   * @return String
   */
  public String getzTag() {
    return zTag;
  }

  
  /** 
   * @param zTag
   */
  public void setzTag(String zTag) {
    this.zTag = zTag;
  }

  
  /** 
   * @return String
   */
  public String getAgent_Version() {
    return agent_Version;
  }

  
  /** 
   * @param agent_Version
   */
  public void setAgent_Version(String agent_Version) {
    this.agent_Version = agent_Version;
  }

}
