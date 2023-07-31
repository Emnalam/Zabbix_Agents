package data.apsys;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name="monitortransactionlogresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionsMonitorResult extends ApsysResult {
	
	public TransactionsMonitorResult() throws Exception {
		super();
	}


	@XmlElement(required=true) 
	protected TransactionErrors transactionerrors = new TransactionErrors();
	
	
  /** 
   * @return TransactionErrors
   */
  public TransactionErrors getErrors() {
		return transactionerrors;
	}

	
  /** 
   * @param errors
   */
  public void setErrors(TransactionErrors errors) {
		this.transactionerrors = errors;
	}

	
	
  /** 
   * @param transactionNumber
   * @param logfile
   * @param lineNumber
   * @param service
   * @param message
   */
  public void addError(String transactionNumber, String logfile, String lineNumber, String service, String message)
	{
		if (transactionerrors == null)
		{
			transactionerrors = new TransactionErrors();
		}
		
		TransactionError error = new TransactionError();
		error.setTransactionnumber(transactionNumber);
		error.setLogfile(logfile);
		error.setLine(lineNumber);
		error.setMessage(message);
		error.setCdApps(service);
		
		transactionerrors.addError(error);
	}
}
