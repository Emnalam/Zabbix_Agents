package data.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "user")
@XmlAccessorType (XmlAccessType.FIELD)
public class DBUserStatus {
	@XmlElement(required=true) 
	protected String name;
	@XmlElement(required=true) 
	protected String status;
	
	
  /** 
   * @return String
   */
  public String getName() {
		return name;
	}

	
  /** 
   * @param name
   */
  public void setName(String name) {
		this.name = name;
	}

	
  /** 
   * @return String
   */
  public String getStatus() {
		return status;
	}

	
  /** 
   * @param status
   */
  public void setStatus(String status) {
		this.status = status;
	}

}
