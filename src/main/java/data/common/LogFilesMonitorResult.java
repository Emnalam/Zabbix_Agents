package data.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="logfilesmonitorresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class LogFilesMonitorResult extends Result{
	
	public LogFilesMonitorResult() throws Exception {
		super();
	}
	
	@XmlElement(required=true) 
    protected String file;
    
    @XmlElement(required=true) 
    protected String agentDescription;
    
    @XmlElement(required=true) 
    protected String agentType;
    
    @XmlElement(required=true) 
    protected String agentCode;

	
  /** 
   * @return String
   */
  public String getAgentCode() {
		return agentCode;
	}

	
  /** 
   * @param code
   */
  public void setAgentCode(String code) {
		this.agentCode = code;
	}

	
  /** 
   * @return String
   */
  public String getFile() {
		return file;
	}

	
  /** 
   * @param file
   */
  public void setFile(String file) {
		this.file = file;
	}

	
  /** 
   * @return String
   */
  public String getAgentDescription() {
		return agentDescription;
	}

	
  /** 
   * @param description
   */
  public void setAgentDescription(String description) {
		this.agentDescription = description;
	}

	
  /** 
   * @return String
   */
  public String getAgentType() {
		return agentType;
	}

	
  /** 
   * @param type
   */
  public void setAgentType(String type) {
		this.agentType = type;
	}

}
