package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import workers.AgentLogger;

public class ProcessUtils {

	
  /** 
   * @param user
   * @return Map<String, ArrayList<String>>
   */
  public static Map<String,ArrayList<String>> getProcesses(String user)
	{
		Map<String,ArrayList<String>> processes = new HashMap<String,ArrayList<String>>();
		
		Process p = null;
		BufferedReader input = null;
		
		try {
		    String line;
		    p = Runtime.getRuntime().exec(getProcessCommand(user));
		    input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    while ((line = input.readLine()) != null) {

            if (line.length() == 0)
            {
              continue;
            }

		        int firstSpace = line.indexOf(" ");
		        if (firstSpace <= 0)
		        {
		        	AgentLogger.logError("Command: " + getProcessCommand(user) + " produced the line: " + line + " which is not valid", null);
		        }
		        else
		        {
		        	String pname = line.substring(0, firstSpace).trim().toLowerCase();
		        	String pid = line.substring(firstSpace).trim();
		        	
		        	if (!Character.isDigit(pid.charAt(0)))
        			{
		        		continue;
        			}
		        	
		        	if (!processes.containsKey(pname))
		        	{
		        		processes.put(pname, new ArrayList<String>());
		        	}
		        	
		        	processes.get(pname).add(pid);
		        	
		        }
		    }
		} catch (Exception err) {
		    AgentLogger.logError(err, null);
		}
		finally {
			if (p != null)
			{
				p.destroy();
			}
			if (input != null)
			{
				try {
					input.close();
				} catch (IOException e) {
				
				}
			}
		}
		return processes;
	}
	
	
  /** 
   * @param name
   * @param user
   * @return ArrayList<String>
   */
  public static ArrayList<String> getProcessId(String name, String user)
	{
		Map<String,ArrayList<String>> processes = getProcesses(user);
		
		String key = name.toLowerCase();
		
		if (processes.containsKey(key))
		{
			return processes.get(key);
		}
		return null;
	}
	
	
  /** 
   * @param user
   * @return String
   */
  private static String getProcessCommand(String user)
	{
		if (OS.isWindows())
		{
			return "wmic process get name,processid";
    }
		return String.format("ps -U %s -o comm,pid", user);
	}
}
