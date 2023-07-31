package data.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "checkcertificateexpiryresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckCertificateExpiryResult extends Result{

  public CheckCertificateExpiryResult() throws Exception {
    super();
    
  }

  @XmlElement(required = true)
  protected String url;
  
  @XmlElement(required = true)
  protected String file;

  @XmlElement(required=true) 
	protected CertificatesInfos certificatesInfos = new CertificatesInfos();
	
	
  /** 
   * @param name
   * @param daysLeft
   */
  public void addCertificateInfo(String name, String daysLeft)
	{
    CertificateInfo ci = new CertificateInfo();
    ci.setCn(name);
    ci.setDaysLeft(daysLeft);
		this.certificatesInfos.addCertificateInfo(ci);
	}

    
    /** 
     * @return CertificatesInfos
     */
    public CertificatesInfos getCertificatesInfos() {
      return certificatesInfos;
    }

    
    /** 
     * @param certificatesInfos
     */
    public void setCertificatesInfos(CertificatesInfos certificatesInfos) {
      this.certificatesInfos = certificatesInfos;
    }

    
    /** 
     * @return String
     */
    public String getUrl() {
      return url;
    }

    
    /** 
     * @param url
     */
    public void setUrl(String url) {
      this.url = url;
    }

    
    /** 
     * @return String
     */
    public String getFile() {
      return file;
    }

    
    /** 
     * @param file
     */
    public void setFile(String file) {
      this.file = file;
    }

}