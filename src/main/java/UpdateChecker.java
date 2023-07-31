import workers.AgentLogger;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import utils.FileUtils;
import utils.OS;
import utils.Transfer;

public class UpdateChecker implements Runnable
{
  private final String updatePath = "updates";
  private static boolean booting = true;

  public void run() {
    if (booting)
    {
      booting = false;
    }
    else 
    {
      checkPackage();
    }
  }

  private void checkPackage() {
    try {
      Transfer.checkUpdates();
      final File packageZip = FileUtils.getMostRecentFile(updatePath, ".zip");
      if (packageZip != null) {
        final String path = packageZip.getAbsolutePath();
        final String lastChecksum = getLastChecksum();
        final String currentChecksum = FileUtils.getFileChecksum(packageZip);

        AgentLogger.logUpdatesTrace("Updater: lastChecksum: " + lastChecksum);
        AgentLogger.logUpdatesTrace("Updater: currentChecksum: " + currentChecksum);
        AgentLogger.logUpdatesTrace("Updater: packageZip: " + path);

        if (lastChecksum == null || !lastChecksum.equals(currentChecksum)) {
          AgentLogger.logUpdatesInfos("Updater: Received new package file " + path);
          AgentLogger.logUpdatesInfos(("Updater: Writing package checksum..."));
          FileUtils.writeFile(Paths.get(updatePath, "lastPackageChecksum.checksum").toString(), currentChecksum, false);
          
          final String packageFolder = FileUtils.extractFolder(path, updatePath);
          AgentLogger.logUpdatesInfos("Updater: Unzipped file in " + updatePath);
          AgentLogger.logUpdatesInfos("Update: Unzipped folder name is " + packageFolder);

          if (packageZip.delete()) {
            if (!OS.isWindows())
            {
              AgentLogger.logUpdatesInfos("Updater: Setting file permissions on update and install scripts....");
              File installScript = Paths.get(packageFolder, "unix", "install.sh").toFile();
              File updateScript = Paths.get(packageFolder, "unix", "update.sh").toFile();

              installScript.setReadable(true, false);
              installScript.setWritable(true, false);
              installScript.setExecutable(true, false);

              updateScript.setExecutable(true, false);
              updateScript.setWritable(true, false);
              updateScript.setReadable(true, false);
            }
            
            AgentLogger.logUpdatesInfos(("Updater: Launching update script and shutting down this nwamon instance..."));
            Path updateFilePath;
            if (OS.isWindows()) {
              updateFilePath = Paths.get(packageFolder, "windows", "update.bat");
            } else {
              updateFilePath = Paths.get(packageFolder, "unix", "update.sh");
            }

            final File file = updateFilePath.toFile();

            if (file.exists()) {
              AgentLogger.logUpdatesInfos("Updater: executing the following command " + file.getAbsolutePath() + " " + System.getProperty("user.dir"));

              if (!OS.isWindows())
              {
                Runtime.getRuntime().exec(new String[] { "sh", "-c", file.getAbsolutePath() + " " + System.getProperty("user.dir") }, null, new File(System.getProperty("user.dir")));
              }
              else
              {
                final ProcessBuilder pb = new ProcessBuilder(file.getAbsolutePath(), System.getProperty("user.dir"));
                pb.start();
              }

              System.exit(0);
            } else {
              AgentLogger.logUpdatesInfos("Updater: file " + file.getAbsolutePath() + " does not exist");
            }
          } else {
            AgentLogger.logUpdatesInfos(
                "Updater: zip file " + packageZip.getAbsolutePath() + " could not be deleted. Update cannot proceed");
          }
        } else {
          AgentLogger.logUpdatesTrace("Updater: the package file is the same as the last installed one. Deleting package file....");
          packageZip.delete();
        }
      }
    } catch (final Exception exc)
    {
      AgentLogger.logUpdatesError(exc);
    }
  }

  
  /** 
   * @return String
   * @throws Exception
   */
  private static String getLastChecksum() throws Exception
  {
    File checkSumFile = FileUtils.getMostRecentFile("updates", ".checksum");
    if (checkSumFile == null)
    {
      return null;
    }
    return FileUtils.readFile(checkSumFile);
  }
}
