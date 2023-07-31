package data.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="checkapsyswebapiresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CmdExc extends Result{

	public CmdExc() throws Exception {
		super();
	}

	protected String status;
  protected String httpCode;
	
	
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
   * @return String
   */
  public String gethttpCode(){
    return httpCode;
  }
   /** 
   * @param httpCode
   */
  public void setHttpCode(String httpCode){
    this.httpCode = httpCode;
  }
}
