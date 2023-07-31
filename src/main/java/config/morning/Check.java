package config.morning;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Check
{
    @XmlAttribute(required=true)
    private String tagname;

    @XmlAttribute(required=true)
    private String validator;

    private HashMap<String, String> ok;

    
    /** 
     * @return String
     */
    public String getTagname ()
    {
        return tagname;
    }

    
    /** 
     * @param tagname
     */
    public void setTagname (String tagname)
    {
        this.tagname = tagname;
    }

    
    /** 
     * @return String
     */
    public String getValidator() {
      return validator;
    }

    
    /** 
     * @param validator
     */
    public void setValidator(String validator) {
      this.validator = validator;
    }

    
    /** 
     * @return HashMap<String, String>
     */
    public HashMap<String, String> getOk() {
      return ok;
    }

    
    /** 
     * @param ok
     */
    public void setOk(HashMap<String, String> ok) {
      this.ok = ok;
    }

    
    /** 
     * @param key
     * @param value
     */
    public void addOkCondition(String key, String value)
    {
      ok.put(key, value);
    }
}