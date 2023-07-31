package data.apsys;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name = "transactionerrors")
@XmlAccessorType (XmlAccessType.FIELD)
public class TransactionErrors {
	@XmlElement(required=true, name="error") 
	List<TransactionError> errors = new ArrayList<TransactionError>();

	
  /** 
   * @return List<TransactionError>
   */
  public List<TransactionError> getErrors() {
		return errors;
	}

	
  /** 
   * @param errors
   */
  public void setErrors(List<TransactionError> errors) {
		this.errors = errors;
	}
	
  /** 
   * @param error
   */
  public void addError(TransactionError error)
	{
		if (errors == null)
		{
			errors = new ArrayList<TransactionError>();
		}
		errors.add(error);
	}
	
}