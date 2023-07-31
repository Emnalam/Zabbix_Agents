package data.eventlog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import data.common.Result;

@XmlRootElement(name="eventlogmonitorresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class EventLogMonitorResult extends Result{

	public EventLogMonitorResult() throws Exception {
		super();
	}
	
	@XmlElement(required=true) 
    protected String source;
    
    @XmlElement(required=true) 
    protected String description;
    
    @XmlElement(required=true) 
    protected String type;

	
  /** 
   * @return String
   */
  public String getSource() {
		return source;
	}

	
  /** 
   * @param source
   */
  public void setSource(String source) {
		this.source = source;
	}

	
  /** 
   * @return String
   */
  public String getDescription() {
		return description;
	}

	
  /** 
   * @param description
   */
  public void setDescription(String description) {
		this.description = description;
	}

	
  /** 
   * @return String
   */
  public String getType() {
		return type;
	}

	
  /** 
   * @param type
   */
  public void setType(String type) {
		this.type = type;
	}
    
}
