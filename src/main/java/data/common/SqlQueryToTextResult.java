package data.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="sqlquerytotextresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class SqlQueryToTextResult extends SqlQueryMonitorResult {
	
	public SqlQueryToTextResult() throws Exception {
		super();
	}
}
