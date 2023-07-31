package data.cim;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import data.common.Result;

@XmlRootElement(name="cimprocessmanagermonitorresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CIMProcessManagerMonitorResult extends Result {
	
	public CIMProcessManagerMonitorResult() throws Exception {
		super();
	}
	
	protected String expectedPlugins;
	protected String expectedThreads;
	protected String totalThreads;
	protected String idleThreads;
	protected String errorProcess;
	protected String errorThread;
	protected String errorChecker;
	protected String errorPluginName;
	protected String errorLog;
	
	
  /** 
   * @return String
   */
  public String getExpectedPlugins() {
		return expectedPlugins;
	}
	
  /** 
   * @param expectedPlugins
   */
  public void setExpectedPlugins(String expectedPlugins) {
		this.expectedPlugins = expectedPlugins;
	}
	
  /** 
   * @return String
   */
  public String getExpectedThreads() {
		return expectedThreads;
	}
	
  /** 
   * @param expectedThreads
   */
  public void setExpectedThreads(String expectedThreads) {
		this.expectedThreads = expectedThreads;
	}
	
  /** 
   * @return String
   */
  public String getTotalThreads() {
		return totalThreads;
	}
	
  /** 
   * @param totalThreads
   */
  public void setTotalThreads(String totalThreads) {
		this.totalThreads = totalThreads;
	}
	
  /** 
   * @return String
   */
  public String getIdleThreads() {
		return idleThreads;
	}
	
  /** 
   * @param idleThreads
   */
  public void setIdleThreads(String idleThreads) {
		this.idleThreads = idleThreads;
	}
	
  /** 
   * @return String
   */
  public String getErrorProcess() {
		return errorProcess;
	}
	
  /** 
   * @param errorProcess
   */
  public void setErrorProcess(String errorProcess) {
		this.errorProcess = errorProcess;
	}
	
  /** 
   * @return String
   */
  public String getErrorThread() {
		return errorThread;
	}
	
  /** 
   * @param errorThread
   */
  public void setErrorThread(String errorThread) {
		this.errorThread = errorThread;
	}
	
	
  /** 
   * @return String
   */
  public String getErrorChecker() {
		return errorChecker;
	}
	
  /** 
   * @param errorChecker
   */
  public void setErrorChecker(String errorChecker) {
		this.errorChecker = errorChecker;
	}
	
  /** 
   * @return String
   */
  public String getErrorLog() {
		return errorLog;
	}
	
  /** 
   * @param errorLog
   */
  public void setErrorLog(String errorLog) {
		this.errorLog = errorLog;
	}
	
  /** 
   * @return String
   */
  public String getErrorPluginName() {
		return errorPluginName;
	}
	
  /** 
   * @param errorPluginName
   */
  public void setErrorPluginName(String errorPluginName) {
		this.errorPluginName = errorPluginName;
	}
	
	
	
}