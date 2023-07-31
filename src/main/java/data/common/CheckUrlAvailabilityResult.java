package data.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="checkurlavailabilityresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckUrlAvailabilityResult extends Result {

	public CheckUrlAvailabilityResult() throws Exception {
		super();
		
	}
	
	@XmlElement(required=true) 
	protected String url;

	@XmlElement(required=true) 
	protected String status;
	
	protected int httpCode;

	
  /** 
   * @return String
   */
  public String getUrl() {
		return url;
	}

	
  /** 
   * @param url
   */
  public void setUrl(String url) {
		this.url = url;
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

	
  /** 
   * @return int
   */
  public int getHttpCode() {
		return httpCode;
	}

	
  /** 
   * @param httpCode
   */
  public void setHttpCode(int httpCode) {
		this.httpCode = httpCode;
	}
	
	
}
