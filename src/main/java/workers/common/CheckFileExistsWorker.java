package workers.common;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import data.common.CheckFileExistsResult;
import workers.AgentLogger;
import workers.Worker;

public class CheckFileExistsWorker extends Worker {

	private String[] filesToCheck;
	private String statusOnExists;
	private String statusOnNotExist;
	
	
  /** 
   * @throws Exception
   */
  @Override
	protected void init() throws Exception {
    super.init();
    
		if (!parameters.containsKey("files"))
		{
			throw new Exception("additional parameters do not contain the files parameter: files=YOUR_FILE1,YOUR_FILE2,...");
		}
		
		if (!parameters.containsKey("exist"))
		{
			throw new Exception("additional parameters do not contain the exist parameter: exist=YOUR_STATUS");
		}
		
		if (!parameters.containsKey("notexist"))
		{
			throw new Exception("additional parameters do not contain the notexist parameter: notexist=YOUR_STATUS");
		}
		
		this.statusOnExists = parameters.get("exist");
		this.statusOnNotExist = parameters.get("notexist");
		
		String files = parameters.get("files");
		
		filesToCheck = files.split(",");
		
		for (String file : filesToCheck)
		{
			AgentLogger.logTrace("Checking if file " + file + " exists and if it exists, the status is: " + this.statusOnExists + ". Otherwise the status is: " + this.statusOnNotExist);
		}
	}

	
  /** 
   * @throws Exception
   */
  @Override
	protected void doWork() throws Exception {
		
		for (String file : filesToCheck)
		{
			File f = new File(file);
			
			File folder = f.getParentFile();
			
			if (folder == null)
			{
				folder = new File(Paths.get("").toAbsolutePath().toString());
			}

			final Pattern p = Pattern.compile(f.getName(), Pattern.CASE_INSENSITIVE);
			File[] listOfFiles = folder.listFiles(new FileFilter() {
			    @Override
			    public boolean accept(File f) {
			        return p.matcher(f.getName()).matches();
			    }
			});
			
			CheckFileExistsResult result = new CheckFileExistsResult();
			if (listOfFiles.length == 0)
			{
				result.setFile(file);
				result.setExists(false);
			    result.setStatus(this.statusOnNotExist);
			    this.save(result);
			}
			else {
				for(File fFile : listOfFiles)
				{
					if(!f.isDirectory()) { 
						result.setFile(fFile.getAbsolutePath());
					    result.setExists(true);
					    result.setStatus(this.statusOnExists);
					    this.save(result);
					}
				}
			}
		}
	}

	
  /** 
   * @throws Exception
   */
  @Override
	protected void cleanUp() throws Exception {
		
	}

}
