package config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="property")
@XmlAccessorType(XmlAccessType.FIELD)
public class Property {
	@XmlAttribute(required=false) 
	protected String name;
	
	@XmlAttribute(required=false) 
	protected String value;

	
  /** 
   * @return String
   */
  public String getName() {
		return name;
	}

	
  /** 
   * @param name
   */
  public void setName(String name) {
		this.name = name;
	}

	
  /** 
   * @return String
   */
  public String getValue() {
		return value;
	}

	
  /** 
   * @param value
   */
  public void setValue(String value) {
		this.value = value;
	}


@Override
public String toString() {
	return "Property [name=" + name + ", value=" + value + "]";
}
  
  
}
