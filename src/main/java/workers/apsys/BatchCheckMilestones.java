package workers.apsys;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import config.Day;
import config.Property;
import data.apsys.BatchCheck;
import data.apsys.BatchCheckMainClass;
import workers.AgentLogger;
import workers.Worker;

public class BatchCheckMilestones extends Worker{

	String directory;
	
	 HashMap<String, String> schedulingDays = new HashMap<String, String>();

	    private List<Property> properties;
	    
	    
	@Override
	protected void init() throws Exception{
		super.init();
		
		if (!parameters.containsKey("directory"))
		{
			throw new Exception("additional parameters do not contain the database name: directory=xxx");
		}
		
		this.directory = parameters.get("directory");
		AgentLogger.logTrace("Registered directory: " + this.directory + " for monitoring");
		
		 this.properties =this.workerInfos.getProperties().getProperties();
	        
	        
	        for(Day day:this.workerInfos.getScheduling().getDays().getDays()){
	        	
	        	this.schedulingDays.put(day.getName(), day.getTime());
	        	
	        	
	        }
		
	}
	@Override
	protected void doWork() throws Exception {
		// TODO Auto-generated method stub
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");	
		if (this.schedulingDays.containsKey(LocalDateTime.now().getDayOfWeek().toString()) && this.schedulingDays.containsValue(LocalDateTime.now().toLocalTime().format(formatter))){
			
					BatchCheckMainClass Mainres=new BatchCheckMainClass();
	            	List<BatchCheck> resList=new ArrayList<BatchCheck>();

	                // Iterate through each given path and search for the file name
	                for (Property property : properties) {
	                	
	                	BatchCheck res=new BatchCheck();
	                	
	                    String fileName ="Zabbix_"+ property.getName();
	                    String filePath = this.directory;
	                    
	                   
	                    
	                    res.setDirectory(filePath);
	                    res.setFileName(fileName);
	          
	                    
	                    res.setDayOfCheck(LocalDateTime.now().getDayOfWeek().toString());
	                    res.setTimeOfCheck(LocalDateTime.now().toLocalTime().toString());
	                    
	                    
	                    
	                    
	                    Path path = Paths.get(filePath, fileName);
	                    if (Files.exists(path)) {
	                    	
	                    	res.setStatus("OK");
	                    
	                        
	                    } else {
	                    	res.setStatus("NOK");  
	                    	
	                    	}
	                    resList.add(res);
	                    System.out.println(resList);
	                   
	                }
	               
	                Mainres.setChecks(resList);
	               
	                this.save(Mainres);
	             
	              
		}
		
	}

	@Override
	protected void cleanUp() throws Exception {
		// TODO Auto-generated method stub
		
	}

	
	
	
}
