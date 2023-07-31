package config.morning;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlAccessType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Data
{
    private ArrayList<Product> product = new ArrayList<Product>();

    @XmlAttribute
    private String ok;

    @XmlAttribute
    private String batchAborted;

    @XmlAttribute
    private String timestamp;

    
    /** 
     * @return ArrayList<Product>
     */
    public ArrayList<Product> getProduct ()
    {
        return product;
    }

    
    /** 
     * @param product
     */
    public void setProduct (final ArrayList<Product> product) {
      this.product = product;
    }

    
    /** 
     * @return String
     */
    public String getOk() {
      return ok;
    }

    
    /** 
     * @param ok
     */
    public void setOk(final String ok) {
      this.ok = ok;
    }

    
    /** 
     * @return String
     */
    public String getTimestamp() {
      return timestamp;
    }

    
    /** 
     * @param timestamp
     */
    public void setTimestamp(final String timestamp)
    {
        this.timestamp = timestamp;
    }

    
    /** 
     * @return String
     */
    public String getBatchAborted() {
      return batchAborted;
    }

    
    /** 
     * @param batchAborted
     */
    public void setBatchAborted(String batchAborted) {
      this.batchAborted = batchAborted;
    }
}
