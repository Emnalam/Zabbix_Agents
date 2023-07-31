package config;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name = "morning")
@XmlAccessorType (XmlAccessType.FIELD)
public class Morning {

	@XmlElement(required=true, name="workerInfos") 
  List<WorkerInfos> workersInfos = new ArrayList<WorkerInfos>();
  
  @XmlElement(required=true) 
	protected Scheduling scheduling;// = new Scheduling();

	
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
  
  
  /** 
   * @return Scheduling
   */
  public Scheduling getScheduling() {
		return scheduling;
	}

	
  /** 
   * @param scheduling
   */
  public void setScheduling(Scheduling scheduling) {
		this.scheduling = scheduling;
	}
}
