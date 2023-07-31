import java.lang.reflect.Constructor;
import config.Periodic;
import config.Scheduling;
import config.WorkerInfos;
import utils.DateUtils;
import workers.AgentLogger;
import workers.Worker;

public class RunnableTask implements Runnable{

	private final WorkerInfos workerInfos;

  public RunnableTask(final WorkerInfos workerInfos) {
    this.workerInfos = workerInfos;
  }

  @Override
	public void run() {
		
		try {

      if (workerInfos != null)
      {
        final Scheduling scheduling = this.workerInfos.getScheduling();
        if (scheduling != null)
        {
          final Periodic periodic = scheduling.getPeriodic();
          if (periodic != null)
          {
            final String strDays = periodic.getDays();
            final String from = periodic.getFrom();
            final String to = periodic.getTo();

            if (strDays != null)
            {
              String dayOfWeek = DateUtils.getTodayName();
              
        	    if (strDays.toLowerCase().indexOf(dayOfWeek) == -1)
        	    { 
                AgentLogger.logTrace("NOT executing " + workerInfos.getName() + " because current day is not in: " + strDays);
                return;
              }
            }

            if (from != null && to != null)
            {
              if (!DateUtils.isCurrentTimeBetween(from, to))
              {
                AgentLogger.logTrace("NOT executing " + workerInfos.getName() + " because current time is not between " + from + " and " + to);
                return;
              }
            }
          }
        }
      }

			AgentLogger.logInfos("Begin scheduled execution of worker: Name = " + workerInfos.getName() + ", Class = " + workerInfos.getClassname());
			
			final String classname = workerInfos.getClassname();
			
			final Class<?> clazz = Class.forName(classname);
			final Constructor<?> ctor = clazz.getConstructor();
			final Worker worker = (Worker)ctor.newInstance();
			worker.initOnly(workerInfos);
			worker.start();
			AgentLogger.logInfos("End scheduled execution of worker: Name = " + workerInfos.getName() + ", Class = " + workerInfos.getClassname());
		
		} catch (final Exception e) {
			
			AgentLogger.logError(e, workerInfos);
		}
  }
}
