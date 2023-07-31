package data.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="processinfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessInfo {
	@XmlElement(required=true) 
	protected String process;
	
	@XmlElement(required=true) 
	protected String message;

	
  /** 
   * @return String
   */
  public String getProcess() {
		return process;
	}

	
  /** 
   * @param process
   */
  public void setProcess(String process) {
		this.process = process;
	}

	
  /** 
   * @return String
   */
  public String getMessage() {
		return message;
	}

	
  /** 
   * @param message
   */
  public void setMessage(String message) {
		this.message = message;
	}

}
