package config.morning;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Configuration")
public class Configuration
{
    private Products products;

    
    /** 
     * @return Products
     */
    public Products getProducts ()
    {
        return products;
    }

    
    /** 
     * @param products
     */
    public void setProducts (Products products)
    {
        this.products = products;
    }

    
    /** 
     * @return String
     */
    @Override
    public String toString()
    {
        return "ClassPojo [products = "+products+"]";
    }
}