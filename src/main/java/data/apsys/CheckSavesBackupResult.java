
package data.apsys;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import javax.xml.bind.annotation.XmlAccessType;
@XmlRootElement(name="checksavesbackup")
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckSavesBackupResult extends CheckMorningResult {

  @XmlElement(required=true) 
  protected boolean savesBackupOk;

  public CheckSavesBackupResult() throws Exception {
    super();
   
  }

  
  /** 
   * @return boolean
   */
  public boolean isSavesBackupOk() {
    return savesBackupOk;
  }

  
  /** 
   * @param savesBackupOk
   */
  public void setSavesBackupOk(boolean savesBackupOk) {
    this.savesBackupOk = savesBackupOk;
  }
}