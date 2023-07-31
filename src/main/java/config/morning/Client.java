package config.morning;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Client")
@XmlAccessorType(XmlAccessType.FIELD)             
public class Client
{
    @XmlAttribute(required=true)
    private String environment;

    @XmlAttribute(required=true)
    private String name;

    private Morning morning;

    
    /** 
     * @return String
     */
    public String getEnvironment ()
    {
        return environment;
    }

    
    /** 
     * @param environment
     */
    public void setEnvironment (String environment)
    {
        this.environment = environment;
    }

    
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
    public void setName (String name)
    {
        this.name = name;
    }

    
    /** 
     * @return Morning
     */
    public Morning getMorning ()
    {
        return morning;
    }

    
    /** 
     * @param morning
     */
    public void setMorning (Morning morning)
    {
        this.morning = morning;
    }

    
    /** 
     * @return String
     */
    @Override
    public String toString()
    {
        return "ClassPojo [environment = "+environment+", name = "+name+", morning = "+morning+"]";
    }
}
			
			