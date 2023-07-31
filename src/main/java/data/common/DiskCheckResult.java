package data.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="diskcheckresult")

@XmlAccessorType(XmlAccessType.FIELD)
public class DiskCheckResult extends Result {

	public DiskCheckResult() throws Exception {
		super();
	}

	@XmlElement(required=true) 
	protected DisksUsage disks = new DisksUsage();
	 
	
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
   * @param disk
   * @param free
   * @param used
   * @param threshold
   */
  public void addDisk(String disk, String free, String used, String threshold)
	{
		if (disks == null)
		{
			disks = new DisksUsage();
		}
		
		DiskUsage diskUsage = new DiskUsage();
		diskUsage.setDiskName(disk);
		diskUsage.setFree(free);
		diskUsage.setUsed(used);
		diskUsage.setThreshold(threshold);
		this.disks.addDiskUsage(diskUsage);
	}
}
