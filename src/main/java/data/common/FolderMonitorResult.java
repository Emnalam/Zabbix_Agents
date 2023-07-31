package data.common;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="foldermonitorresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class FolderMonitorResult extends Result {
	public FolderMonitorResult() throws Exception {
		super();
	}

	@XmlElement(required=true) 
	protected String folder;
	
	@XmlElement(required=true) 
	protected int fileCount;
	
	@XmlElement(required=true) 
	protected ArrayList<String> files = new ArrayList<String>();
	
	@XmlElement(required=true) 
	protected String status;

	
  /** 
   * @return String
   */
  public String getFolder() {
		return folder;
	}

	
  /** 
   * @param folder
   */
  public void setFolder(String folder) {
		this.folder = folder;
	}

	
  /** 
   * @return int
   */
  public int getFileCount() {
		return fileCount;
	}

	
  /** 
   * @param fileCount
   */
  public void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}

	
  /** 
   * @return ArrayList<String>
   */
  public ArrayList<String> getFiles() {
		return files;
	}

	
  /** 
   * @param files
   */
  public void setFiles(ArrayList<String> files) {
		this.files = files;
	}

	
  /** 
   * @return String
   */
  public String getStatus() {
		return status;
	}

	
  /** 
   * @param status
   */
  public void setStatus(String status) {
		this.status = status;
	}
	
	
  /** 
   * @param file
   */
  public void AddFile(String file)
	{
		this.files.add(file);
	}

}
