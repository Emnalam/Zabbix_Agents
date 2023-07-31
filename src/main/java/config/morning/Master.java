package config.morning;

public class Master
{
    private Morning morning;

    
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
        return "ClassPojo [morning = "+morning+"]";
    }
}
   
   
   