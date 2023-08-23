package data.common;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import data.apsys.ApsysResult;


@XmlRootElement(name="IPLabelData")
@XmlAccessorType(XmlAccessType.FIELD)
public class IPLabelGlobalData extends ApsysResult{
	
	
	@XmlElement(required=true) 
    protected List<IPLabelintegrationData> DataList;

	public IPLabelGlobalData() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<IPLabelintegrationData> getDataList() {
		return DataList;
	}

	public void setDataList(List<IPLabelintegrationData> dataList) {
		DataList = dataList;
	}

	@Override
	public String toString() {
		return "IPLabelGlobalData [DataList=" + DataList + "]";
	}
	
	
	

}
