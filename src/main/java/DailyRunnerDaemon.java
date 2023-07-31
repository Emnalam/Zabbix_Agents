import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import utils.DateUtils;

public class DailyRunnerDaemon
{
   private final RunnableTask dailyTask;
   private final int hour;
   private final int minute;
   private final int second;
   private final String runThreadName;
   
   private String day;

   public DailyRunnerDaemon(String day, Calendar timeOfDay, RunnableTask dailyTask, String runThreadName)
   {
      this.dailyTask = dailyTask;
      this.hour = timeOfDay.get(Calendar.HOUR_OF_DAY);
      this.minute = timeOfDay.get(Calendar.MINUTE);
      this.second = timeOfDay.get(Calendar.SECOND);
      this.runThreadName = runThreadName;
      
      this.day = day;
   }

   public void start()
   {
      startTimer();
   }

   private void startTimer()
   {
	  final String configDay = this.day;
	  
      new Timer(runThreadName, true).schedule(new TimerTask()
      {
         @Override
         public void run()
         {
          String dayOfWeek = DateUtils.getTodayName();
        	if (dayOfWeek.equalsIgnoreCase(configDay))
        	{
        		dailyTask.run();
        	}
            startTimer();
         }
      }, getNextRunTime());
   }


   
   /** 
    * @return Date
    */
   private Date getNextRunTime()
   {
      Calendar startTime = Calendar.getInstance();
      Calendar now = Calendar.getInstance();
      startTime.set(Calendar.HOUR_OF_DAY, hour);
      startTime.set(Calendar.MINUTE, minute);
      startTime.set(Calendar.SECOND, second);
      startTime.set(Calendar.MILLISECOND, 0);

      if(startTime.before(now) || startTime.equals(now))
      {
         startTime.add(Calendar.DATE, 1);
      }

      return startTime.getTime();
   }
}
