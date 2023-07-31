package data.equalizer;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import data.common.Result;

@XmlRootElement(name="checkeqzwebapiresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CmdExcEqz extends Result {
	
	
	public CmdExcEqz() throws Exception {
		super();
	}

	protected String status;

	
	
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
