package workers.common;

import data.common.ResourcesMonitorResult;
import utils.OS;
import workers.AgentLogger;
import workers.Worker;


public class ResourcesMonitorWorker extends Worker {

	
  /** 
   * @throws Exception
   */
  @Override
	protected void doWork() throws Exception {
		
		String processOutput = this.executeDefaultCommand(this.workerInfos);
		
		if (processOutput != null && processOutput.startsWith("USED"))
		{
			String[] lines = processOutput.split("\\r?\\n");
			
			if (lines.length <= 4)
			{
				AgentLogger.logError("ResourcesMonitorWorker: invalid process output:\n" + processOutput, workerInfos);
			}
			else
			{
				ResourcesMonitorResult result = new ResourcesMonitorResult();
				
				String usedMemoryMb = lines[0].split("=")[1].trim();
				
				String totalMemoryMb = lines[1].split("=")[1].trim();
				
				String cpuLine = lines[2].split("=")[1];
				
				String[] cpuElements = cpuLine.split("\\s+");
				
				String cpuUser = cpuElements[0].split(":")[1].trim();
				String cpuSystem = cpuElements[1].split(":")[1].trim();
				String cpuWait = cpuElements[2].split(":")[1].trim();
				String cpuIdle = cpuElements[3].split(":")[1].trim();
				
				result.setUsedRam(usedMemoryMb);
				result.setTotalRam(totalMemoryMb);
				result.setCpuIdle(cpuIdle);
				result.setCpuSystem(cpuSystem);
				result.setCpuUser(cpuUser);
				result.setCpuWait(cpuWait);
				
				for(int i = 4; i< lines.length;i++)
				{
					String diskLine = lines[i];
					
					String[] diskInfos = diskLine.split("\\s+");
					
					String disk = diskInfos[0].trim();
					String free = diskInfos[1].trim();
					String used = diskInfos[2].trim();
					
					result.addDisk(disk, free, used, null);
        }
        
        result.setSystemUpTime(OS.getSystemUptime());
				
				this.save(result);
			}
		}
		else
		{
			AgentLogger.logError("ResourcesMonitorWorker: invalid process output:\n" + processOutput, workerInfos);
		}
		

	}

	
  /** 
   * @throws Exception
   */
  @Override
	protected void cleanUp() throws Exception {
		
	}
}

