package data.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="resourcesmonitorresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResourcesMonitorResult extends Result {

	public ResourcesMonitorResult() throws Exception {
		super();
	}

	@XmlElement(required=true) 
	protected DisksUsage disks = new DisksUsage();
   
  @XmlElement(required=true) 
	protected String systemUpTime;
	
	@XmlElement(required=true) 
	protected String totalRam;
	
	@XmlElement(required=true) 
	protected String usedRam;
	
	
	@XmlElement(required=true) 
	protected String cpuUser;

	@XmlElement(required=true) 
	protected String cpuSystem;
	
	@XmlElement(required=true) 
	protected String cpuWait;
	
	@XmlElement(required=true) 
	protected String cpuIdle;
	
	
	
	
  /** 
   * @return String
   */
  public String getTotalRam() {
		return totalRam;
	}

	
  /** 
   * @param totalRam
   */
  public void setTotalRam(String totalRam) {
		this.totalRam = totalRam;
	}

	
  /** 
   * @return String
   */
  public String getCpuUser() {
		return cpuUser;
	}

	
  /** 
   * @param cpuUser
   */
  public void setCpuUser(String cpuUser) {
		this.cpuUser = cpuUser;
	}

	
  /** 
   * @return String
   */
  public String getCpuSystem() {
		return cpuSystem;
	}

	
  /** 
   * @param cpuSystem
   */
  public void setCpuSystem(String cpuSystem) {
		this.cpuSystem = cpuSystem;
	}

	
  /** 
   * @return String
   */
  public String getCpuWait() {
		return cpuWait;
	}

	
  /** 
   * @param cpuWait
   */
  public void setCpuWait(String cpuWait) {
		this.cpuWait = cpuWait;
	}

	
  /** 
   * @return String
   */
  public String getCpuIdle() {
		return cpuIdle;
	}

	
  /** 
   * @param cpuIdle
   */
  public void setCpuIdle(String cpuIdle) {
		this.cpuIdle = cpuIdle;
	}

	
  /** 
   * @return DisksUsage
   */
  public DisksUsage getDisks() {
		return disks;
	}

	
  /** 
   * @param disks
   */
  public void setDisks(DisksUsage disks) {
		this.disks = disks;
	}

	
	
	
  /** 
   * @return String
   */
  public String getUsedRam() {
		return usedRam;
	}

	
  /** 
   * @param usedRam
   */
  public void setUsedRam(String usedRam) {
		this.usedRam = usedRam;
	}

	
  /** 
   * @param disk
   * @param free
   * @param used
   * @param threshold
   */
  public void addDisk(String disk, String free, String used, String threshold)
	{
		if (disks == null) {
			disks = new DisksUsage();
		}
		
		DiskUsage diskUsage = new DiskUsage();
		diskUsage.setDiskName(disk);
		diskUsage.setFree(free);
		diskUsage.setUsed(used);
		diskUsage.setThreshold(threshold);
		this.disks.addDiskUsage(diskUsage);
	}

    
    /** 
     * @return String
     */
    public String getSystemUpTime() {
      return systemUpTime;
    }

    
    /** 
     * @param systemUpTime
     */
    public void setSystemUpTime(String systemUpTime) {
      this.systemUpTime = systemUpTime;
    }
}
