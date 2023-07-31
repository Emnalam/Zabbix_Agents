package data.integrator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import data.common.Result;

@XmlRootElement(name="integratorcommonslogmonitorresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class IntegratorCommonsLogMonitorResult extends Result {

	public IntegratorCommonsLogMonitorResult() throws Exception {
		super();
		
	}

	protected int commonsLogcount;
	protected String commonsLogResult;
	
	
	
  /** 
   * @return String
   */
  public String getCommonsLogResult() {
		return commonsLogResult;
	}
	
	
  /** 
   * @param commonsLogResult
   */
  public void setCommonsLogResult(String commonsLogResult) {
		this.commonsLogResult = commonsLogResult;
	}
	
	
  /** 
   * @return int
   */
  public int getCommonsLogcount() {
		return commonsLogcount;
	}
	
	
  /** 
   * @param commonsLogcount
   */
  public void setCommonsLogcount(int commonsLogcount) {
		this.commonsLogcount = commonsLogcount;
	}
}
