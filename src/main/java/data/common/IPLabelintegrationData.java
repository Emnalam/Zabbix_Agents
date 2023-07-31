package data.common;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="IPLabelIntegration")
@XmlAccessorType(XmlAccessType.FIELD)
public class IPLabelintegrationData extends Result{
	
	
    @XmlElement(required=true) 
	protected String robotName;
	
	@XmlElement(required=true) 
	protected String scenarioName;
	
	@XmlElement(required=true) 
	protected String currentStatusValue;
	
	@XmlElement(required=true) 
	protected String lastMessage;

	public IPLabelintegrationData() throws Exception {
		super();
	}

	public String getRobotName() {
		return robotName;
	}

	public void setRobotName(String robotName) {
		this.robotName = robotName;
	}

	public String getScenarioName() {
		return scenarioName;
	}

	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	public String getCurrentStatusValue() {
		return currentStatusValue;
	}

	public void setCurrentStatusValue(String currentStatusValue) {
		this.currentStatusValue = currentStatusValue;
	}

	public String getLastMessage() {
		return lastMessage;
	}

	public void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}

	@Override
	public String toString() {
		return "IPLabelintegrationData [robotName=" + robotName + ", scenarioName=" + scenarioName
				+ ", currentStatusValue=" + currentStatusValue + ", lastMessage=" + lastMessage + "]";
	}
	
	
    

}
