package config;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name = "workers")
@XmlAccessorType (XmlAccessType.FIELD)
public class Workers {
	@XmlElement(required=true, name="workerInfos") 
	List<WorkerInfos> workersInfos = new ArrayList<WorkerInfos>();

	
  /** 
   * @return List<WorkerInfos>
   */
  public List<WorkerInfos> getWorkersInfos() {
		return workersInfos;
	}

	
  /** 
   * @param workers
   */
  public void setWorkersInfos(List<WorkerInfos> workers) {
		this.workersInfos = workers;
	}
	
  /** 
   * @param workerInfos
   */
  public void addWorkerInfos(WorkerInfos workerInfos)
	{
		if (workersInfos == null)
		{
			workersInfos = new ArrayList<WorkerInfos>();
		}
		workersInfos.add(workerInfos);
	}
	
}
