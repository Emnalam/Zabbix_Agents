package data.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="agentglobalerrors")
@XmlAccessorType(XmlAccessType.FIELD)
public class AgentGlobalErrorsResult extends Result {

	public AgentGlobalErrorsResult() throws Exception {
		super();
	}

	@XmlElement(required=true)
	protected String errors;

	
  /** 
   * @return String
   */
  public String getErrors() {
		return errors;
	}

	
  /** 
   * @param errors
   */
  public void setErrors(String errors) {
		this.errors = errors;
	}
	
	
}
