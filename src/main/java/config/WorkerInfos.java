package config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="workerinfos")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkerInfos {
	
	@XmlAttribute(required=false) 
	protected String name;

	@XmlAttribute(required=false) 
	protected boolean disableTransfer;
	
	@XmlElement(required=true) 
	protected int successExitCode;
	
	@XmlElement(required=true) 
	protected String classname;
	
	@XmlElement(required=false) 
	protected String command;
	
	@XmlElement(required=false) 
	protected String additionalParameters;
	
	@XmlElement(required=true) 
	protected String outputfile;
	
	@XmlElement(required=true) 
	protected Scheduling scheduling;// = new Scheduling();
	
	@XmlElement(required=false) 
	protected String product;
	
	@XmlElement(required=false) 
  protected String subproduct;

  @XmlElement(required=false) 
  protected String zTag;
  
  @XmlElement(required=true, name="properties")
	protected Properties properties;
	
	
  /** 
   * @return String
   */
  public String getSubproduct() {
		return subproduct;
	}

	
  /** 
   * @param subProduct
   */
  public void setSubproduct(String subProduct) {
		this.subproduct = subProduct;
	}

	
  /** 
   * @return String
   */
  public String getProduct() {
		return product;
	}

	
  /** 
   * @param product
   */
  public void setProduct(String product) {
		this.product = product;
	}

	
  /** 
   * @return Scheduling
   */
  public Scheduling getScheduling() {
		return scheduling;
	}

	
  /** 
   * @param scheduling
   */
  public void setScheduling(Scheduling scheduling) {
		this.scheduling = scheduling;
	}

	
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
   * @return boolean
   */
  public boolean isDisableTransfer() {
		return disableTransfer;
	}

	
  /** 
   * @param disableTransfer
   */
  public void setDisableTransfer(boolean disableTransfer) {
		this.disableTransfer = disableTransfer;
	}

	
  /** 
   * @return int
   */
  public int getSuccessExitCode() {
		return successExitCode;
	}

	
  /** 
   * @param successExitCode
   */
  public void setSuccessExitCode(int successExitCode) {
		this.successExitCode = successExitCode;
	}
	
	
  /** 
   * @return String
   */
  public String getAdditionalParameters() {
		return additionalParameters;
	}

	
  /** 
   * @param additionalparameters
   */
  public void setAdditionalParameters(String additionalparameters) {
		this.additionalParameters = additionalparameters;
	}

	
  /** 
   * @return String
   */
  public String getOutputfile() {
		return outputfile;
	}

	
  /** 
   * @param outputfile
   */
  public void setOutputfile(String outputfile) {
		this.outputfile = outputfile;
	}

	
  /** 
   * @return String
   */
  public String getClassname() {
		return classname;
	}

	
  /** 
   * @param classname
   */
  public void setClassname(String classname) {
		this.classname = classname;
	}

	
  /** 
   * @return String
   */
  public String getCommand() {
		return command;
	}

	
  /** 
   * @param command
   */
  public void setCommand(String command) {
		this.command = command;
  }
  
  
  /** 
   * @return Properties
   */
  public Properties getProperties() {
		return properties;
	}

	
  /** 
   * @param properties
   */
  public void setProperties(Properties properties) {
		this.properties = properties;
	}

  
  /** 
   * @return String
   */
  public String getZTag() {
	  return zTag;
  }

  
  /** 
   * @param ztag
   */
  public void setZTag(String ztag) {
	  this.zTag = ztag;
  }
} 
