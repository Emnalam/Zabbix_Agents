package data.common;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "certificatesinfos")
@XmlAccessorType (XmlAccessType.FIELD)
public class CertificatesInfos {
	@XmlElement(required=true, name="certificateinfo") 
	List<CertificateInfo> data = new ArrayList<CertificateInfo>();

	
	
  /** 
   * @param certificateInfo
   */
  public void addCertificateInfo(CertificateInfo certificateInfo)
	{
		if (data == null)
		{
			data = new ArrayList<CertificateInfo>();
		}
		data.add(certificateInfo);
	}

    
    /** 
     * @return List<CertificateInfo>
     */
    public List<CertificateInfo> getData() {
      return data;
    }

    
    /** 
     * @param data
     */
    public void setData(List<CertificateInfo> data) {
      this.data = data;
    }
	
}
