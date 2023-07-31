package data.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="error")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiskUsage {
	@XmlElement(required=true) 
	protected String diskName;
	
	@XmlElement(required=true) 
	protected String free;

	@XmlElement(required=true) 
	protected String used;

	@XmlElement(required=true) 
	protected String threshold;

	
  /** 
   * @return String
   */
  public String getDiskName() {
		return diskName;
	}

	
  /** 
   * @param disk
   */
  public void setDiskName(String disk) {
		this.diskName = disk;
	}

	
  /** 
   * @return String
   */
  public String getFree() {
		return free;
	}

	
  /** 
   * @param free
   */
  public void setFree(String free) {
		this.free = free;
	}

	
  /** 
   * @return String
   */
  public String getUsed() {
		return used;
	}

	
  /** 
   * @param used
   */
  public void setUsed(String used) {
		this.used = used;
	}

	
  /** 
   * @return String
   */
  public String getThreshold() {
		return threshold;
	}

	
  /** 
   * @param threshold
   */
  public void setThreshold(String threshold) {
		this.threshold = threshold;
	}
}
