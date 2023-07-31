package data.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="servicesmonitorresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessesMonitorResult extends Result {

	public ProcessesMonitorResult() throws Exception {
		super();
		
	}
	
	@XmlElement(required=true) 
	protected ProcessInfos processInfos = new ProcessInfos();
	
	
  /** 
   * @return ProcessInfos
   */
  public ProcessInfos getProcessInfos() {
		return processInfos;
	}

	
  /** 
   * @param processInfos
   */
  public void setProcessInfos(ProcessInfos processInfos) {
		this.processInfos = processInfos;
	}
	
	
  /** 
   * @param si
   */
  public void addProcessInfo(ProcessInfo si)
	{
		this.processInfos.addServiceInfo(si);
	}

}
