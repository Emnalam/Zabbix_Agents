package config.morning;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class CheckResults
{
    private ArrayList<String> result = new ArrayList<String>();
    
    @XmlAttribute
    private String missing;

    
    /** 
     * @return ArrayList<String>
     */
    public ArrayList<String> getResult() {
      return result;
    }

    
    /** 
     * @param result
     */
    public void setResult(ArrayList<String> result) {
      this.result = result;
    }

    
    /** 
     * @return String
     */
    public String getMissing() {
      return missing;
    }

    
    /** 
     * @param missing
     */
    public void setMissing(String missing) {
      this.missing = missing;
    }
}