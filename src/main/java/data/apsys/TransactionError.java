package data.apsys;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="error")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionError {
	@XmlElement(required=true) 
	protected String transactionnumber;
	
	@XmlElement(required=true) 
	protected String logfile;

	@XmlElement(required=true) 
	protected String line;
	
	@XmlElement(required=true) 
	protected String message;
	
	@XmlElement(required=true) 
	protected String cdApps;

	
	
  /** 
   * @return String
   */
  public String getTransactionnumber() {
		return transactionnumber;
	}

	
  /** 
   * @param transactionnumber
   */
  public void setTransactionnumber(String transactionnumber) {
		this.transactionnumber = transactionnumber;
	}

	
  /** 
   * @return String
   */
  public String getLogfile() {
		return logfile;
	}

	
  /** 
   * @param logfile
   */
  public void setLogfile(String logfile) {
		this.logfile = logfile;
	}

	
  /** 
   * @return String
   */
  public String getLine() {
		return line;
	}

	
  /** 
   * @param line
   */
  public void setLine(String line) {
		this.line = line;
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

	
  /** 
   * @return String
   */
  public String getCdApps() {
		return cdApps;
	}

	
  /** 
   * @param service
   */
  public void setCdApps(String service) {
		this.cdApps = service;
	}
}
