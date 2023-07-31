package data.apsys;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="BatchCheck")
@XmlAccessorType(XmlAccessType.FIELD)
public class BatchCheckMainClass extends ApsysResult{

	@XmlElement(required=true) 
    protected List<BatchCheck> Checks;
	
	public BatchCheckMainClass() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<BatchCheck> getChecks() {
		return Checks;
	}

	public void setChecks(List<BatchCheck> checks) {
		Checks = checks;
	}

	@Override
	public String toString() {
		return "BatchCheckMainClass [Checks=" + Checks + "]";
	}
	
	
	

}
