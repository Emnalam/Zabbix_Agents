package data.apsys;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
@XmlRootElement(name="apsysWebApi")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApsysWebApi extends ApsysResult{

public ApsysWebApi() throws Exception{

    super();
}



    @XmlElement(required=true) 
    protected String idscts;

    @XmlElement(required=true) 
    protected String dtcptl;

    @XmlElement(required=true) 
    protected String nusesi;

    @XmlElement(required=true) 
    protected String statee;

    @XmlElement(required=true) 
    protected String availableb;

    @XmlElement(required=true) 
    protected String writeb;

/** 
   * @return String
   */
public String getIdscts(){
    return idscts;
}

 /** 
   * @param idscts
   */
public void setIdscts(String idscts){
    this.idscts = idscts;
}

/** 
   * @return String
   */
public String getDtcptl(){
    return dtcptl;
}

/** 
   * @param dtcptl
   */
public void setDtcptl(String dtcptl){
    this.dtcptl = dtcptl;
}

/** 
   * @return String
   */
public String getNusesi(){
    return nusesi;
}

/** 
   * @param nusesi
   */
public void setNusesi(String nusesi){

    this.nusesi = nusesi;
}

/** 
   * @return String
   */
public String getStatee(){
    return statee;
}

/** 
   * @param statee
   */
public void setStatee(String statee){
    this.statee = statee;
}
/** 
   * @return String
   */
public String getAvailableb (){
    return availableb;
}

/** 
   * @param availableb
   */
public void setAvailableb(String availableb){
    this.availableb = availableb;
}

/** 
   * @return String
   */
public String getWriteb(){
    return writeb;
}

/** 
   * @param writeb
   */
public void setWriteb(String writeb){
    this.writeb = writeb;
}


}