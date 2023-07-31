package workers.eventlog;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import config.Settings;
import config.WorkerInfos;
import data.eventlog.EventLogMonitorResult;
import workers.AgentLogger;
import workers.Worker;

public class EventLogMonitorWorker extends Worker {

	private Map<String,String[]> sourceElements= new HashMap<String, String[]>();
	
	
  /** 
   * @throws Exception
   */
  @Override
	protected void init() throws Exception {
    super.init();
		
		if ((this.workerInfos.getScheduling() == null && Settings.getInstance().getUseScheduler()) || Settings.getInstance().getUseScheduler() && this.workerInfos.getScheduling().isEnabled())
		{
			if (this.workerInfos.getCommand() != null && this.workerInfos.getCommand().toLowerCase().contains("eventlogs"))
			{
				throw new Exception(this.getClass().getName() + ": You cannot enable scheduling on this agent because it is already running continuously with the EventLogs command");
			}
		}
		
		for(String source : parameters.keySet())
		{
			String strElements = parameters.get(source);
			AgentLogger.logInfos("Monitoring EventLog source: " + source + ", with filter on: " + strElements);
			String[] elements = strElements.split(",");
			sourceElements.put(source,elements);
		}
		
	}

	
  /** 
   * @throws Exception
   */
  @Override
	protected void doWork() throws Exception {
		EventLogWatcherRunner runner = new EventLogWatcherRunner(this.workerInfos, this);
		
		Thread t = new Thread(runner);
		t.start();
	}

	
  /** 
   * @throws Exception
   */
  @Override
	protected void cleanUp() throws Exception {
		
	}
	
	
  /** 
   * @param data
   * @throws Exception
   */
  protected void handleLogData(String data) throws Exception
	{
		if (data.startsWith("ERROR"))
		{
			AgentLogger.logError(data, this.workerInfos);
		}
		else
		{
			String[] eventLogLines = data.split("[\\r\\n]+");
			
			String source = eventLogLines[0].trim().replace("SOURCE=", "");
			//String date = eventLogLines[1].trim().replace("DATE=", "");
			String type = eventLogLines[2].trim().replace("TYPE=", "");;
			
			StringBuilder sb = new StringBuilder();
			
			for(int i = 3; i < eventLogLines.length; i++)
			{
				sb.append(eventLogLines[i]).append("\r\n");
			}
			
			String message = sb.toString().trim();
			
			EventLogMonitorResult result = new EventLogMonitorResult();
			result.setSource(source);
			result.setDescription(message);
			result.setType(type);
			
			this.save(result);
		}
			
	}
	
	class EventLogWatcherRunner implements Runnable
	{
		private EventLogMonitorWorker worker;
		private WorkerInfos workerInfos;
		private Process p;

		public EventLogWatcherRunner(WorkerInfos workerInfos, EventLogMonitorWorker worker)
		{
			this.worker = worker;
			this.workerInfos = workerInfos;
		}
		
		@Override
		public void run() {

			String command = this.workerInfos.getCommand() + " \"" + this.workerInfos.getAdditionalParameters() + "\"";
			Scanner s = null;
			try {

				p = Runtime.getRuntime().exec(command);

				Worker.addRunningProcess(p);
				
				s = new Scanner(p.getInputStream());
				
				StringBuffer sb = new StringBuffer();

				while (s.hasNextLine()) {
					String line = s.nextLine();
					AgentLogger.logTrace(line);
					if (line.contains("--BEGIN EVT LOG--"))
					{
						sb = new StringBuffer();
					}
					else if (line.contains("--END EVT LOG--"))
					{
						String eventLog = sb.toString();
						AgentLogger.logTrace(eventLog);
						this.worker.handleLogData(eventLog);
					}
					else
					{
						sb.append(line).append("\r\n");
					}
				}
			} catch (Exception e) {
				AgentLogger.logError(e, this.workerInfos);
			}
			finally {
				if (s != null)
				{
					s.close();
				}
			}
		}
		
		public void stop()
		{
			p.destroy();
		}
		
		protected void handleLogLine(String data) throws Exception
		{
			this.worker.handleLogData(data);
		}
	}

}
