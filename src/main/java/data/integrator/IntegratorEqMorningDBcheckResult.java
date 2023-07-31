package data.integrator;

import data.common.Result;

public class IntegratorEqMorningDBcheckResult extends Result {
	public IntegratorEqMorningDBcheckResult() throws Exception {
		super();
	}

	protected String morningStartDate = ""; 
	protected String morningEndDate = ""; 
	protected String morningDate = "";
	protected String morningStatus = "";
	protected String morningDesc = "";
	
	
  /** 
   * @return String
   */
  public String getMorningStartDate() {
		return morningStartDate;
	}
	
  /** 
   * @param morningStartDate
   */
  public void setMorningStartDate(String morningStartDate) {
		this.morningStartDate = morningStartDate;
	}
	
  /** 
   * @return String
   */
  public String getMorningEndDate() {
		return morningEndDate;
	}
	
  /** 
   * @param morningEndDate
   */
  public void setMorningEndDate(String morningEndDate) {
		this.morningEndDate = morningEndDate;
	}
	
  /** 
   * @return String
   */
  public String getMorningDate() {
		return morningDate;
	}
	
  /** 
   * @param morningDate
   */
  public void setMorningDate(String morningDate) {
		this.morningDate = morningDate;
	}
	
  /** 
   * @return String
   */
  public String getMorningStatus() {
		return morningStatus;
	}
	
  /** 
   * @param morningStatus
   */
  public void setMorningStatus(String morningStatus) {
		this.morningStatus = morningStatus;
	}
	
  /** 
   * @return String
   */
  public String getMorningDesc() {
		return morningDesc;
	}
	
  /** 
   * @param morningDesc
   */
  public void setMorningDesc(String morningDesc) {
		this.morningDesc = morningDesc;
	}
}
