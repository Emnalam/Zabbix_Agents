package data.apsys;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name="getopertioninforesult")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetOperationInfoResult  extends ApsysResult {

	public GetOperationInfoResult() throws Exception {
		super();
		
	}
 
	@XmlElement(required=true) 
    protected String accountingDate;
    
    @XmlElement(required=false) 
    protected String accountingSessionNumber;
    
    @XmlElement(required=true) 
    protected String operatingMode;
    
    @XmlElement(required=true)
    protected String currentStage;
    
    @XmlElement(required=true)
    protected String stepHasAborted;
 
    @XmlElement (required=true)
    protected String stepInInitializationPase;
    
    @XmlElement (required=true)
    protected String operationIs24x7;
    
	
  /** 
   * @return String
   */
  public String getAccountingDate() {
		return accountingDate;
	}

	
  /** 
   * @param accountingDate
   */
  public void setAccountingDate(String accountingDate) {
		this.accountingDate = accountingDate;
	}

	
  /** 
   * @return String
   */
  public String getAccountingSessionNumber() {
		return accountingSessionNumber;
	}

	
  /** 
   * @param accountingSessionNumber
   */
  public void setAccountingSessionNumber(String accountingSessionNumber) {
		this.accountingSessionNumber = accountingSessionNumber;
	}

	
  /** 
   * @return String
   */
  public String getOperatingMode() {
		return operatingMode;
	}

	
  /** 
   * @param operatingMode
   */
  public void setOperatingMode(String operatingMode) {
		this.operatingMode = operatingMode;
	}

	
  /** 
   * @return String
   */
  public String getCurrentStage() {
		return currentStage;
	}

	
  /** 
   * @param currentStage
   */
  public void setCurrentStage(String currentStage) {
		this.currentStage = currentStage;
	}

	
  /** 
   * @return String
   */
  public String getStepHasAborted() {
		return stepHasAborted;
	}

	
  /** 
   * @param stepHasAborted
   */
  public void setStepHasAborted(String stepHasAborted) {
		this.stepHasAborted = stepHasAborted;
	}

	
  /** 
   * @return String
   */
  public String getStepInInitializationPase() {
		return stepInInitializationPase;
	}

	
  /** 
   * @param stepInInitializationPase
   */
  public void setStepInInitializationPase(String stepInInitializationPase) {
		this.stepInInitializationPase = stepInInitializationPase;
	}

	
  /** 
   * @return String
   */
  public String getOperationIs24x7() {
		return operationIs24x7;
	}

	
  /** 
   * @param operationIs24x7
   */
  public void setOperationIs24x7(String operationIs24x7) {
		this.operationIs24x7 = operationIs24x7;
	}
}
