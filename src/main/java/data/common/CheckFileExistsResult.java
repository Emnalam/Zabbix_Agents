package data.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="checkfileexistsresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckFileExistsResult extends Result {
	public CheckFileExistsResult() throws Exception {
		super();
	}

	@XmlElement(required=true) 
	protected String file;
	
	@XmlElement(required=true) 
	protected boolean exists;
	
	@XmlElement(required=true) 
	protected String status;

	
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
   * @return boolean
   */
  public boolean isExists() {
		return exists;
	}

	
  /** 
   * @param exists
   */
  public void setExists(boolean exists) {
		this.exists = exists;
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
