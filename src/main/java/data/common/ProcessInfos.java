package data.common;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "processinfos")
@XmlAccessorType (XmlAccessType.FIELD)
public class ProcessInfos {
	@XmlElement(required=true, name="processinfo") 
	List<ProcessInfo> processInfos = new ArrayList<ProcessInfo>();

	
  /** 
   * @return List<ProcessInfo>
   */
  public List<ProcessInfo> getProcessInfos() {
		return processInfos;
	}

	
  /** 
   * @param processInfos
   */
  public void setProcessInfos(List<ProcessInfo> processInfos) {
		this.processInfos = processInfos;
	}
	
  /** 
   * @param processInfo
   */
  public void addServiceInfo(ProcessInfo processInfo)
	{
		if (processInfos == null)
		{
			processInfos = new ArrayList<ProcessInfo>();
		}
		processInfos.add(processInfo);
	}
	
}
