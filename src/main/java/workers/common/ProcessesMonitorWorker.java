package workers.common;

import java.util.ArrayList;
import java.util.List;
import data.common.ProcessInfo;
import data.common.ProcessesMonitorResult;
import workers.AgentLogger;
import workers.Worker;

public class ProcessesMonitorWorker extends Worker {

	private List<String> processes = new ArrayList<String>();
	
	
  /** 
   * @throws Exception
   */
  @Override
	protected void init() throws Exception {

		super.init();
		
		if (!parameters.containsKey("processes"))
		{
			throw new Exception("additional parameters do not contain the services parameter: processes=xxx");
		}
		
		String _services = parameters.get("processes");
		
		String[] svcs = _services.split(",");
		
		for (String service : svcs)
		{
			AgentLogger.logTrace("Registered Process: " + service + " for monitoring");	
			this.processes.add(service.toLowerCase().trim());
		}
	}

	
  /** 
   * @throws Exception
   */
  @Override
	protected void doWork() throws Exception {
		
		String processOutput = this.executeDefaultCommand(this.workerInfos);
		

		if (processOutput != null)
		{
			String[] lines = processOutput.split("\\r?\\n");
			
			ArrayList<String> currentProcesses = new ArrayList<String>();
			
			for(String line : lines)
			{
				String str = line;
				
				if (str.trim().length() > 0)
				{
					if (str.startsWith("Name="))
					{
						str = str.replace("Name=", "");
					}
					currentProcesses.add(str.trim());
				}
			}
			
			ArrayList<ProcessInfo> servicesInfos = new ArrayList<ProcessInfo>();
			
			for(int i = 0; i < processes.size(); i++)
			{
				String service = processes.get(i);
				
				boolean found = false;
				
				for ( int j = 0; j < currentProcesses.size(); j++)
				{
					String currentService = currentProcesses.get(j);
					
					if (currentService.equalsIgnoreCase(service))
					{
						found = true;
						break;
					}
				}
				
				
				if (!found)
				{
					String message = "Process " + service + " not found.";
					ProcessInfo si = new ProcessInfo();
					si.setProcess(service);
					si.setMessage("NotFound");
					servicesInfos.add(si);
					AgentLogger.logTrace(message);
				}
				else
				{
					String message = "Process " + service + " is running.";
					ProcessInfo si = new ProcessInfo();
					si.setProcess(service);
					si.setMessage("Running");
					servicesInfos.add(si);
					AgentLogger.logTrace(message);
				}
			}
			
			if (servicesInfos.size() > 0)
			{
				this.writeOutput(servicesInfos);
			}
		}
		else
		{
			AgentLogger.logError("ProcessesMonitorWorker: invalid process output:\n" + processOutput, workerInfos);
		}
		
	}

	
  /** 
   * @throws Exception
   */
  @Override
	protected void cleanUp() throws Exception {
		
	}
	
	
  /** 
   * @param services
   * @throws Exception
   */
  public void writeOutput(ArrayList<ProcessInfo> services) throws Exception
	{
		ProcessesMonitorResult result = new ProcessesMonitorResult();
		for(ProcessInfo si : services)
		{
			result.addProcessInfo(si);
		}

		this.save(result);
	}

}
