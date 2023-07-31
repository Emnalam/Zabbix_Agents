package data.common;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "disks")
@XmlAccessorType (XmlAccessType.FIELD)
public class DisksUsage {
	@XmlElement(required=true, name="disk") 
	List<DiskUsage> disks = new ArrayList<DiskUsage>();

	
  /** 
   * @return List<DiskUsage>
   */
  public List<DiskUsage> getDisks() {
		return disks;
	}

	
  /** 
   * @param disks
   */
  public void setDisks(List<DiskUsage> disks) {
		this.disks = disks;
	}
	
  /** 
   * @param disk
   */
  public void addDiskUsage(DiskUsage disk)
	{
		if (disks == null)
		{
			disks = new ArrayList<DiskUsage>();
		}
		disks.add(disk);
	}
	
}
