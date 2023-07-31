package config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="properties")
@XmlAccessorType(XmlAccessType.FIELD)
public class Properties {
	@XmlElement(required=true, name="property") 
	List<Property> properties = new ArrayList<Property>();

	
  /** 
   * @return List<Property>
   */
  public List<Property> getProperties() {
		return properties;
	}

	
  /** 
   * @param properties
   */
  public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

}
