package workers.common;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;

import data.common.FolderMonitorResult;
import workers.AgentLogger;
import workers.Worker;

public class FolderMonitorWorker extends Worker{

	private Map<String,WatchService> watchers= new HashMap<String, WatchService>();
	private Map<WatchKey,Path> watchKeys= new HashMap<WatchKey, Path>();

	private String[] directories;
	private String statusOnExists;
	private String statusOnNotExist;
	
	
  /** 
   * @throws Exception
   */
  @Override
	protected void init() throws Exception {
		super.init();
		
		if (!parameters.containsKey("directories"))
		{
			throw new Exception("additional parameters do not contain the directories parameter: directories=xxx");
		}
		
		if (!parameters.containsKey("filesexist"))
		{
			throw new Exception("additional parameters do not contain the filesexist parameter: FILESEXIST=YOUR_STATUS");
		}
		
		if (!parameters.containsKey("filesnotexist"))
		{
			throw new Exception("additional parameters do not contain the filesnotexist parameter: FILESNOTEXIST=YOUR_STATUS");
		}
		
		this.statusOnExists = parameters.get("filesexist");
		this.statusOnNotExist = parameters.get("filesnotexist");
		
		String _directories = parameters.get("directories");
		
		directories = _directories.split(",");
		
		for (String directory : directories)
		{
			File folder = new File(directory);
			if (folder.isDirectory() && folder.exists()) 
			{
				AgentLogger.logTrace("Folder Monitor is Monitoring " + directory + ". if files exist or appear in this directory, then the status is: " + this.statusOnExists + ". Otherwise the status is: " + this.statusOnNotExist);
			}
			else
			{
				throw new Exception("Directory " + directory + " is not a directory or it does not exist or access is denied");
			}
		}
	}

	
  /** 
   * @throws Exception
   */
  @Override
	protected void doWork() throws Exception {
		
		for (String directory : directories)
		{
			this.checkDirectory(directory);
			
			File folder = new File(directory);

			Path dir = Paths.get(folder.getAbsolutePath());
			
			WatchService watcher = dir.getFileSystem().newWatchService();
			
			WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
			
			watchers.put(folder.getAbsolutePath(), watcher);
			watchKeys.put(key,  dir);
			
			
			WatchServiceRunner wsr = new WatchServiceRunner(watcher, dir, this);
			
			Thread watchTrd = new Thread(wsr);
			watchTrd.start();

		}
	}

	
  /** 
   * @throws Exception
   */
  @Override
	protected void cleanUp() throws Exception {
		
	}
	
	
  /** 
   * @param file
   * @throws Exception
   */
  protected  void handleFileCreatedEvent(File file) throws Exception
	{
		this.checkDirectory(file.getParent());
	}
	
	
  /** 
   * @param file
   * @throws Exception
   */
  protected void handleFileDeletedEvent(File file) throws Exception
	{
		this.checkDirectory(file.getParent());
	}
	
	
  /** 
   * @param directory
   * @throws Exception
   */
  private void checkDirectory(String directory) throws Exception
	{
		String[] files = this.getFiles(directory);
		
		int count = files.length;
		
		FolderMonitorResult result = new FolderMonitorResult();
		
		result.setFolder(directory);
		result.setFileCount(count); 
		
		for(String file : files)
		{
			result.AddFile(file);
		}
		
		if (count > 0)
		{
			result.setStatus(this.statusOnExists);
		}
		else
		{
			result.setStatus(this.statusOnNotExist);
		}
		
		this.save(result);
	}
	
	
  /** 
   * @param directory
   * @return String[]
   */
  private String[] getFiles(String directory)
	{
		return new File(directory).list();
	}
	
	
	class WatchServiceRunner implements Runnable
	{
		private Path directory;
		private WatchService watcher;
		private FolderMonitorWorker worker;
		
		public WatchServiceRunner(WatchService watcher, Path directory, FolderMonitorWorker worker)
		{
			this.directory = directory;
			this.watcher = watcher;
			this.worker = worker;
		}

		@Override
		public void run() {
			try {
				processEvents();
			}
			catch(Exception exc) {
				AgentLogger.logError(exc, workerInfos);
			}
		}
		
		 private void processEvents() throws Exception {

		        for (;;) {
		 
		            WatchKey key;
		            try {
		                key = watcher.take();
		            } catch (InterruptedException x) {
		                return;
		            }
		 
		            
		            if (directory == null) {
		                AgentLogger.logError("WatchKey " + key + " not recognized!!", workerInfos);
		                continue;
		            }
		 
		            for (WatchEvent<?> event : key.pollEvents()) {
		                @SuppressWarnings("unchecked") 
		                Path name = ((WatchEvent<Path>)event).context();
		                
		                if (name != null)
		                {
			                Path child = directory.resolve(name);
			                
			                if (child != null)
			                {
				                File f = new File(child.toString());
				                
				                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE)
				                {
				                	if (f.isFile())
				                	{
				                		this.worker.handleFileCreatedEvent(f);
				                	}
				                }
				                
				                if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE)
				                {
				                	this.worker.handleFileDeletedEvent(f);
				                }
			                }
		                }
		            }
		 
		            boolean valid = key.reset();
		            if (!valid) {
		                watchKeys.remove(key);
		 
		                if (watchKeys.isEmpty()) {
		                    break;
		                }
		            }
		        }
		 }
	}

}
