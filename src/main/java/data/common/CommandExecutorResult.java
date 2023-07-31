package data.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="commandexecutorresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CommandExecutorResult extends Result{

	public CommandExecutorResult() throws Exception {
		super();
	}

	protected String commandOutput;
	
	
  /** 
   * @return String
   */
  public String getCommandOutput() {
		return commandOutput;
	}
	
  /** 
   * @param status
   */
  public void setCommandOutput(String commandOutput) {
		this.commandOutput = commandOutput;
	}
}
