package data.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="filepusherresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class FilePusherResult extends Result{

	public FilePusherResult() throws Exception {
		super();
		
	}

	@XmlElement(required=true) 
	protected String sentFiles;

	
  /** 
   * @return String
   */
  public String getSentFiles() {
		return sentFiles;
	}

	
  /** 
   * @param sentFiles
   */
  public void setSentFiles(String sentFiles) {
		this.sentFiles = sentFiles;
	}
	
	
}
