package data.apsys;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;




@XmlRootElement(name="File")
@XmlAccessorType(XmlAccessType.FIELD)
public class BatchCheck{

	
	@XmlElement(required=true) 
    protected String DayOfCheck;

    @XmlElement(required=true) 
    protected String TimeOfCheck;

    @XmlElement(required=true) 
    protected String Directory;

    @XmlElement(required=true) 
    protected String FileName;

    @XmlElement(required=true) 
    protected String Status;
    
    
	public BatchCheck() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}


	public String getDayOfCheck() {
		return DayOfCheck;
	}


	public void setDayOfCheck(String dayOfCheck) {
		DayOfCheck = dayOfCheck;
	}


	public String getTimeOfCheck() {
		return TimeOfCheck;
	}


	public void setTimeOfCheck(String timeOfCheck) {
		TimeOfCheck = timeOfCheck;
	}


	public String getDirectory() {
		return Directory;
	}


	public void setDirectory(String directory) {
		Directory = directory;
	}


	public String getFileName() {
		return FileName;
	}


	public void setFileName(String fileName) {
		FileName = fileName;
	}


	public String getStatus() {
		return Status;
	}


	public void setStatus(String status) {
		Status = status;
	}


	@Override
	public String toString() {
		return "BatchCheck [DayOfCheck=" + DayOfCheck + ", TimeOfCheck=" + TimeOfCheck + ", Directory=" + Directory
				+ ", FileName=" + FileName + ", Status=" + Status + "]";
	}
	
	

}
