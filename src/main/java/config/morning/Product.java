package config.morning;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Product
{
    @XmlAttribute(required=true)
    private String name;

    @XmlAttribute(required=true)
    private String environment;

    private ArrayList<Check> check = new ArrayList<Check>();

    private CheckResults checkResults;

    
    /** 
     * @return String
     */
    public String getName ()
    {
        return name;
    }

    
    /** 
     * @param name
     */
    public void setName (final String name) {
      this.name = name;
    }

    
    /** 
     * @return ArrayList<Check>
     */
    public ArrayList<Check> getCheck() {
      return check;
    }

    
    /** 
     * @param check
     */
    public void setCheck(final ArrayList<Check> check) {
      this.check = check;
    }

    
    /** 
     * @return String
     */
    @Override
    public String toString() {
      return "ClassPojo [name = " + name + ", check = " + check + "]";
    }

    
    /** 
     * @return String
     */
    public String getEnvironment() {
      return environment;
    }

    
    /** 
     * @param environment
     */
    public void setEnvironment(final String environment) {
      this.environment = environment;
    }

    
    /** 
     * @return CheckResults
     */
    public CheckResults getCheckResults() {
      return checkResults;
    }

    
    /** 
     * @param checkResults
     */
    public void setCheckResults(final CheckResults checkResults) {
      this.checkResults = checkResults;
    }
}