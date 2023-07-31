import java.nio.file.Files;
import java.nio.file.Paths;


import org.junit.Test;

import config.Morning;
import config.Settings;
import workers.morning.ClientFile;
import workers.morning.MasterFile;
import workers.morning.MorningReport;

public class MorningTestCases {

  private static String baseDirectory = System.getProperty("user.dir");
  private static String settingsFile = Paths.get(baseDirectory, "src", "test", "settings", "agentsSettings_morning.xml").toString();
  private static String masterFile = Paths.get(baseDirectory, "src", "test", "settings", "morning_master_file.xml").toString();
  private static String clientFile = Paths.get(baseDirectory, "src", "test", "settings", "morning_client_file.xml").toString();
  private static String morningFilesFolder = Paths.get(baseDirectory, "src", "test", "data", "morningFiles").toString();
  private static String morningOutputDirectory = Paths.get(baseDirectory, "src", "test", "output", "morning").toString();

  static {
    try {
      Starter.main(new String[] { settingsFile });
    } catch (Exception exc) {
      throw new RuntimeException(exc);
    }
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void checkSettings() throws Exception {
    Morning morning = Settings.getInstance().getMorning();
    morning.toString();
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void checkMasterFile() throws Exception {
    MasterFile mf = MasterFile.getInstance(masterFile);
    mf.toString();
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void createClientFile() throws Exception {
    MasterFile mf = MasterFile.getInstance(masterFile);
    ClientFile cf = mf.createClientFile("MyClient");
    cf.save(Paths.get(morningOutputDirectory, "MyClientFile.xml").toString());
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void generateDefaultHtml() throws Exception {

    MasterFile mf = MasterFile.getInstance(masterFile);
    ClientFile cf = mf.inferClientFile(clientFile);
    cf.mergeOutputs(morningFilesFolder);
    cf.save(Paths.get(morningOutputDirectory, "ClientMergedFile.xml").toString());
    
    String html = MorningReport.getDefaultHtml(Paths.get(morningOutputDirectory, "MyClientFile.xml").toString());
    Files.write(Paths.get(Paths.get(morningOutputDirectory, "MyClientFileReport.html").toString()), html.getBytes());
  }

}
