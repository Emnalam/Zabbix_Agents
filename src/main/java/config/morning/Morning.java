package config.morning;

public class Morning
{
    private Configuration configuration;

    private Data data;

    
    /** 
     * @return Configuration
     */
    public Configuration getConfiguration ()
    {
        return configuration;
    }

    
    /** 
     * @param configuration
     */
    public void setConfiguration (Configuration configuration)
    {
        this.configuration = configuration;
    }

    
    /** 
     * @return String
     */
    @Override
    public String toString()
    {
        return "ClassPojo [configuration = "+configuration+"]";
    }

    
    /** 
     * @return Data
     */
    public Data getData() {
        return data;
    }

    
    /** 
     * @param data
     */
    public void setData(Data data) {
        this.data = data;
    }
}