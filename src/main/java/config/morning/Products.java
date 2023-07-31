package config.morning;

import java.util.ArrayList;

public class Products
{
    private ArrayList<Product> product = new ArrayList<Product>();

    
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
    public void setProduct (ArrayList<Product> product)
    {
        this.product = product;
    }

    
    /** 
     * @return String
     */
    @Override
    public String toString()
    {
        return "ClassPojo [product = "+product+"]";
    }
}