
package data.apsys;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import javax.xml.bind.annotation.XmlAccessType;
@XmlRootElement(name="checktransactionabortresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckTransactionAbortResult extends CheckMorningResult {

  
  @XmlElement(required=true) 
	protected TransactionErrors transactionerrors = new TransactionErrors();
	
  
  public CheckTransactionAbortResult() throws Exception {
    super();
   
  }

	
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
   * @param service
   * @param message
   */
  public void addError(String transactionNumber, String service, String message)
	{
		if (transactionerrors == null)
		{
			transactionerrors = new TransactionErrors();
		}
		
		TransactionError error = new TransactionError();
		error.setTransactionnumber(transactionNumber);
		error.setMessage(message);
		error.setCdApps(service);
		
		transactionerrors.addError(error);
	}
}