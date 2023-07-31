
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import config.Settings;
import config.WorkerInfos;
import utils.DateUtils;
import workers.Worker;
import workers.apsys.ErrorsMonitorWorker;
import workers.apsys.GetOperationInfoWorker;
import workers.apsys.ServiceInstanceCheckWithOracle;
import workers.apsys.TransactionsMonitorWorker;
import workers.b250.B250LogFilesMonitorWorker;
import workers.cim.CIMProcessManagerMonitorWorker;
import workers.cim.HqomApsysDocumentTableCheckWithOracle;
import workers.common.CheckCertificateExpiryWorker;
import workers.common.CheckFileExistsWorker;
import workers.common.CheckUrlAvailabilityWorker;
import workers.common.CommandExecutorWorker;
import workers.common.DBCheckUsersWorker;
import workers.common.FileProgressMonitorWorker;
import workers.common.FilePusherWorker;
import workers.common.FolderMonitorWorker;
import workers.common.GenericLogFilesMonitorWorker;
import workers.common.ProcessesMonitorWorker;
import workers.common.ResourcesMonitorWorker;
import workers.common.ServicesMonitorWorker;
import workers.common.SqlQueryMonitorWorker;
import workers.common.SqlQueryToTextWorker;
import workers.ebanking.EbankingLogFilesMonitorWorker;
import workers.equalizer.EqIsLogFileMonitorWorker;
import workers.equalizer.EqPsLogFileMonitorWorker;
import workers.equalizer.EqWsLogFileMonitorWorker;
import workers.equalizer.EqXorLogFileMonitorWorker;
import workers.equalizer.EqconvLogFileMonitorWorker;
import workers.equalizer.EqualizerIntegrityCheckerWorker;
import workers.equalizer.EqualizerNewDayCheckerWorker;
import workers.integrator.IntegratorCommonsLogMonitorWorker;
import workers.las.EdmsLogFilesMonitorWorker;
import workers.las.LasLogFilesMonitorWorker;

public class TestCases {

  private static String baseDirectory = System.getProperty("user.dir");
  private static String dataDirectory = Paths.get(baseDirectory, "src", "test", "data").toString();
  private static String dataTemplatesDirectory = Paths.get(baseDirectory, "src", "test", "data", "templates").toString();
  private static String outputDirectory = Paths.get(baseDirectory, "src", "test", "output").toString();
  private static String settingsFile = Paths.get(baseDirectory, "src", "test", "settings", "agentsSettings.xml").toString();

  static 
  {
    try {
      resetTestData();
      Starter.main(new String[] {settingsFile});
      createTestData();
    }
    catch(Exception exc)
    {
      throw new RuntimeException(exc);
    }
  }


  
  /** 
   * @throws Exception
   */
  @Test
  public void check_apsys_ErrorsMonitorWorker() throws Exception 
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - ErrorsMonitorWorker");
    final ErrorsMonitorWorker worker = new ErrorsMonitorWorker();

    runWorker(worker, workerInfos);

    final String outputFilePattern = workerInfos.getOutputfile();

    final boolean errorCatched = outputFileExists(outputFilePattern.replace("{count}", "1"));
    final boolean fatalCatched = outputFileExists(outputFilePattern.replace("{count}", "2"));
    final boolean crashCatched = outputFileExists(outputFilePattern.replace("{count}", "3"));

    assertEquals(true, errorCatched && fatalCatched && crashCatched);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_apsys_GetOperationInfoWorker() throws Exception 
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - GetOperationInfoWorker");
    final GetOperationInfoWorker worker = new GetOperationInfoWorker();

    runWorker(worker, workerInfos);

    assertOutputFileCreated(workerInfos);

  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_apsys_ServiceInstanceCheckWithOracle() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - ServiceInstanceCheckWithOracle");
    final ServiceInstanceCheckWithOracle worker = new ServiceInstanceCheckWithOracle();

    runWorker(worker, workerInfos);

    assertOutputFileCreated(workerInfos);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_apsys_TransactionsMonitorWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - TransactionsMonitorWorker");
    final TransactionsMonitorWorker worker = new TransactionsMonitorWorker();

    runWorker(worker, workerInfos);
    
    final String outputFilePattern = workerInfos.getOutputfile();

    final boolean errorCatched = outputFileExists(outputFilePattern.replace("{count}", "1"));

    final boolean alreadyProcessed = outputFileExists(outputFilePattern.replace("{count}", "2"));

    assertEquals(true, errorCatched && !alreadyProcessed);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_b250_B250LogFilesMonitorWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - B250LogFilesMonitorWorker");
    final B250LogFilesMonitorWorker worker = new B250LogFilesMonitorWorker();

    runWorker(worker, workerInfos);

    final String outputFilePattern = workerInfos.getOutputfile();

    final boolean warningCatched = outputFileExists(outputFilePattern.replace("{count}", "1"));
    final boolean errorCatched = outputFileExists(outputFilePattern.replace("{count}", "2"));

    assertEquals(true, warningCatched && errorCatched);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_cim_CIMProcessManagerMonitorWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - CIMProcessManagerMonitorWorker");
    final CIMProcessManagerMonitorWorker worker = new CIMProcessManagerMonitorWorker();

    runWorker(worker, workerInfos);

    Thread.sleep(20000);

    assertOutputFileCreated(workerInfos);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_cim_HqomApsysDocumentTableCheckWithOracle() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - HqomApsysDocumentTableCheckWithOracle");
    final HqomApsysDocumentTableCheckWithOracle worker = new HqomApsysDocumentTableCheckWithOracle();

    runWorker(worker, workerInfos);

    assertOutputFileCreated(workerInfos);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_common_CheckCertificateExpiryWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - CheckCertificateExpiryWorker");
    final CheckCertificateExpiryWorker worker = new CheckCertificateExpiryWorker();

    runWorker(worker, workerInfos);

    final String outputFilePattern = workerInfos.getOutputfile();

    final boolean found = outputFileExists(outputFilePattern);

    assertEquals(true, found);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_common_CheckFileExistsWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - CheckFileExistsWorker");
    final CheckFileExistsWorker worker = new CheckFileExistsWorker();

    runWorker(worker, workerInfos);

    final String outputFilePattern = workerInfos.getOutputfile();

    final boolean foundCatched = outputFileExists(outputFilePattern.replace("{count}", "1"));
    final boolean notFoundCatched = outputFileExists(outputFilePattern.replace("{count}", "2"));

    assertEquals(true, foundCatched && notFoundCatched);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_common_CheckUrlAvailabilityWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - CheckUrlAvailabilityWorker");
    final CheckUrlAvailabilityWorker worker = new CheckUrlAvailabilityWorker();

    runWorker(worker, workerInfos);

    assertOutputFileCreated(workerInfos);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_common_CommandExecutorWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - CommandExecutorWorker");
    final CommandExecutorWorker worker = new CommandExecutorWorker();

    runWorker(worker, workerInfos);

    assertOutputFileCreated(workerInfos);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_common_DBCheckUsersWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - DBCheckUsersWorker");
    final DBCheckUsersWorker worker = new DBCheckUsersWorker();

    runWorker(worker, workerInfos);

    assertOutputFileCreated(workerInfos);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_common_FileProgressMonitorWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - FileProgressMonitorWorker");
    final FileProgressMonitorWorker worker = new FileProgressMonitorWorker();

    runWorker(worker, workerInfos);

    assertOutputFileCreated(workerInfos);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_common_FolderMonitorWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - FolderMonitorWorker");
    final FolderMonitorWorker worker = new FolderMonitorWorker();

    runWorker(worker, workerInfos);

    assertOutputFileCreated(workerInfos);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_common_FilePusherWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - FilePusherWorker");
    final FilePusherWorker worker = new FilePusherWorker();

    runWorker(worker, workerInfos);

    assertOutputFileCreated(workerInfos);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_common_GenericLogFilesMonitorWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - GenericLogFilesMonitorWorker");
    final GenericLogFilesMonitorWorker worker = new GenericLogFilesMonitorWorker();

    runWorker(worker, workerInfos);

    final String outputFilePattern = workerInfos.getOutputfile();

    final boolean foundPattern1Catched = outputFileExists(outputFilePattern.replace("{count}", "1"));
    final boolean foundPattern2Catched = outputFileExists(outputFilePattern.replace("{count}", "2"));

    assertEquals(true, foundPattern1Catched && foundPattern2Catched);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_common_ProcessesMonitorWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - ProcessesMonitorWorker");
    final ProcessesMonitorWorker worker = new ProcessesMonitorWorker();

    runWorker(worker, workerInfos);

    assertOutputFileCreated(workerInfos);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_common_ResourcesMonitorWorker() throws Exception 
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - ResourcesMonitorWorker");
    final ResourcesMonitorWorker worker = new ResourcesMonitorWorker();

    runWorker(worker, workerInfos);

    assertOutputFileCreated(workerInfos);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_common_ServicesMonitorWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - ServicesMonitorWorker");
    final ServicesMonitorWorker worker = new ServicesMonitorWorker();

    runWorker(worker, workerInfos);

    assertOutputFileCreated(workerInfos);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_common_SqlQueryMonitorWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - SqlQueryMonitorWorker");
    final SqlQueryMonitorWorker worker = new SqlQueryMonitorWorker();

    runWorker(worker, workerInfos);

    assertOutputFileCreated(workerInfos);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_common_SqlQueryToTextWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - SqlQueryToTextWorker");
    final SqlQueryToTextWorker worker = new SqlQueryToTextWorker();

    runWorker(worker, workerInfos);

    assertOutputFileCreated(workerInfos);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_ebanking_EbankingLogFilesMonitorWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - EbankingLogFilesMonitorWorker");
    final EbankingLogFilesMonitorWorker worker = new EbankingLogFilesMonitorWorker();

    runWorker(worker, workerInfos);

    final String outputFilePattern = workerInfos.getOutputfile();

    final boolean warningCatched = outputFileExists(outputFilePattern.replace("{count}", "1"));
    final boolean errorCatched = outputFileExists(outputFilePattern.replace("{count}", "2"));

    assertEquals(true, warningCatched && errorCatched);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_equalizer_EqconvLogFileMonitorWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - EqconvLogFileMonitorWorker");
    final EqconvLogFileMonitorWorker worker = new EqconvLogFileMonitorWorker();

    runWorker(worker, workerInfos);

    final String outputFilePattern = workerInfos.getOutputfile();

    final boolean pattern1Catched = outputFileExists(outputFilePattern.replace("{count}", "1"));
    final boolean pattern2Catched = outputFileExists(outputFilePattern.replace("{count}", "2"));

    assertEquals(true, pattern1Catched && pattern2Catched);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_equalizer_EqIsLogFileMonitorWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - EqIsFileMonitorWorker");
    final EqIsLogFileMonitorWorker worker = new EqIsLogFileMonitorWorker();

    runWorker(worker, workerInfos);

    assertOutputFileCreated(workerInfos);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_equalizer_EqPsLogFileMonitorWorker() throws Exception 
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - EqPsLogFileMonitorWorker");
    final EqPsLogFileMonitorWorker worker = new EqPsLogFileMonitorWorker();

    runWorker(worker, workerInfos);

    assertOutputFileCreated(workerInfos);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_equalizer_EqualizerIntegrityCheckerWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - EqualizerIntegrityCheckerWorker");
    final EqualizerIntegrityCheckerWorker worker = new EqualizerIntegrityCheckerWorker();

    runWorker(worker, workerInfos);

    assertOutputFileCreated(workerInfos);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_equalizer_EqualizerNewDayCheckerWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - EqualizerNewDayCheckerWorker");
    final EqualizerNewDayCheckerWorker worker = new EqualizerNewDayCheckerWorker();

    runWorker(worker, workerInfos);

    assertOutputFileCreated(workerInfos);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_equalizer_EqWsLogFileMonitorWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - EqWsLogFileMonitorWorker");
    final EqWsLogFileMonitorWorker worker = new EqWsLogFileMonitorWorker();

    runWorker(worker, workerInfos);

    assertOutputFileCreated(workerInfos);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_equalizer_EqXorLogFileMonitorWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - EqXorLogFileMonitorWorker");
    final EqXorLogFileMonitorWorker worker = new EqXorLogFileMonitorWorker();

    runWorker(worker, workerInfos);

    assertOutputFileCreated(workerInfos);
    
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_integrator_IntegratorCommonsLogMonitorWorker() throws Exception 
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - IntegratorCommonsLogMonitorWorker");
    final IntegratorCommonsLogMonitorWorker worker = new IntegratorCommonsLogMonitorWorker();

    runWorker(worker, workerInfos);

    //There might not be errors in the database, so no files would be created with this case.
    //Testing ensures that all the rest of the worker runs correctly.
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_las_EdmsLogFilesMonitorWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - EdmsLogFilesMonitorWorker");
    final EdmsLogFilesMonitorWorker worker = new EdmsLogFilesMonitorWorker();

    runWorker(worker, workerInfos);

    assertOutputFileCreated(workerInfos);
  }

  
  /** 
   * @throws Exception
   */
  @Test
  public void check_las_LasLogFilesMonitorWorker() throws Exception
  {
    final WorkerInfos workerInfos = Settings.getInstance().getWorkerInfos("Test - LasLogFilesMonitorWorker");
    final LasLogFilesMonitorWorker worker = new LasLogFilesMonitorWorker();

    runWorker(worker, workerInfos);

    assertOutputFileCreated(workerInfos);
  }

  
  /** 
   * @param worker
   * @param workerInfos
   * @throws Exception
   */
  private static void runWorker(Worker worker, WorkerInfos workerInfos) throws Exception
  {
    if (workerInfos == null || worker ==  null)
    {
      throw new Exception("Failed to load the WorkferInfos from the settings file. Make sure it is present and has the correct name");
    }

    worker.initOnly(workerInfos);
    worker.start();
  }

  
  /** 
   * @param workerInfos
   */
  private static void assertOutputFileCreated(WorkerInfos workerInfos)
  {
    final String outputFilePattern = workerInfos.getOutputfile();
    
    final boolean created = outputFileExists(outputFilePattern);

    assertEquals(true, created);
  }

  
  /** 
   * @param file
   * @return boolean
   */
  private static boolean outputFileExists(final String file)
  {
    return Files.exists(Paths.get(baseDirectory, file));
  }

  private static void clearTestData()
  {
    final File folder = new File(dataDirectory);
    for(final File file : folder.listFiles())
    {
      file.delete();
    }

    final File opFile = Paths.get(baseDirectory, "getoperationinfo_out.tmp").toFile();

    if (opFile.exists())
    {
      opFile.delete();
    }

  }

  
  /** 
   * @throws Exception
   */
  private static void clearOutputDirectory() throws Exception
  {
    final File folder = new File(outputDirectory);
    
    if (!folder.exists())
    {
      folder.mkdirs();
    }
    
    for(final File file : folder.listFiles())
    {
      file.delete();
    }
  }

  
  /** 
   * @throws Exception
   */
  private static void resetTestData() throws Exception 
  {
    clearTestData();
    clearOutputDirectory();

    resetData_apsys_ErrorsMonitorWorker();
    resetData_apsys_TransactionsMonitorWorker();
    resetData_b250_B250LogFilesMonitorWorker();
    resetData_common_FileProgressMonitorWorker();
    resetData_common_FilePusherWorker();
    resetData_common_GenericLogFilesMonitorWorker();
    resetData_ebanking_EbankingLogFilesMonitorWorker();
    resetData_equalizer_EqconvLogFileMonitorWorker();
    
    resetData_equalizer_EqIsLogFileMonitorWorker();
    resetData_equalizer_EqPsLogFileMonitorWorker();
    resetData_equalizer_EqWsLogFileMonitorWorker();
    resetData_equalizer_EqXorLogFileMonitorWorker();
    resetData_las_EdmsLogFilesMonitorWorker();
    resetData_las_LasLogFilesMonitorWorker();
  }

  
  /** 
   * @throws Exception
   */
  private static void createTestData() throws Exception 
  {
    clearOutputDirectory();
    createData_apsys_ErrorsMonitorWorker();
    createData_apsys_TransactionsMonitorWorker();
    createData_b250_B250LogFilesMonitorWorker();
    createData_common_FileProgressMonitorWorker();
    createData_common_FilePusherWorker();
    createData_common_GenericLogFilesMonitorWorker();
    createData_ebanking_EbankingLogFilesMonitorWorker();
    createData_equalizer_EqconvLogFileMonitorWorker();
    
    createData_equalizer_EqIsLogFileMonitorWorker();
    createData_equalizer_EqPsLogFileMonitorWorker();
    createData_equalizer_EqWsLogFileMonitorWorker();
    createData_equalizer_EqXorLogFileMonitorWorker();
    createData_las_EdmsLogFilesMonitorWorker();
    createData_las_LasLogFilesMonitorWorker();
  }

  
  /** 
   * @throws Exception
   */
  private static void resetData_las_LasLogFilesMonitorWorker() throws Exception
  {
    final String error_file = "las_LasLogFilesMonitorWorker.log";
    final PrintWriter writer = new PrintWriter(Paths.get(dataDirectory, error_file).toString());
    writer.println("Test Entry");
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void resetData_las_EdmsLogFilesMonitorWorker() throws Exception
  {
    final String error_file = "las_EdmsLogFilesMonitorWorker.log";
    final PrintWriter writer = new PrintWriter(Paths.get(dataDirectory, error_file).toString());
    writer.println("Test Entry");
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void resetData_equalizer_EqIsLogFileMonitorWorker() throws Exception
  {
    final String error_file = "equalizer_EqIsLogFileMonitorWorker.log";
    final PrintWriter writer = new PrintWriter(Paths.get(dataDirectory, error_file).toString());
    writer.println("Test Entry");
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void resetData_equalizer_EqPsLogFileMonitorWorker() throws Exception
  {
    final String error_file = "equalizer_EqPsLogFileMonitorWorker.log";
    final PrintWriter writer = new PrintWriter(Paths.get(dataDirectory, error_file).toString());
    writer.println("Test Entry");
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void resetData_equalizer_EqWsLogFileMonitorWorker() throws Exception
  {
    final String error_file = "equalizer_EqWsLogFileMonitorWorker.log";
    final PrintWriter writer = new PrintWriter(Paths.get(dataDirectory, error_file).toString());
    writer.println("Test Entry");
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void resetData_equalizer_EqXorLogFileMonitorWorker() throws Exception
  {
    final String error_file = "equalizer_EqXorLogFileMonitorWorker.log";
    final PrintWriter writer = new PrintWriter(Paths.get(dataDirectory, error_file).toString());
    writer.println("Test Entry");
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void resetData_equalizer_EqconvLogFileMonitorWorker() throws Exception
  {
    final String eqz_error_file = "equalizer_EqconvLogFileMonitorWorker.log";
    final PrintWriter writer = new PrintWriter(Paths.get(dataDirectory, eqz_error_file).toString());
    writer.println("Test Entry");
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void resetData_ebanking_EbankingLogFilesMonitorWorker() throws Exception
  {
    final String ebk_error_file = "ebanking_EbankingLogFilesMonitorWorker.log";
    final PrintWriter writer = new PrintWriter(Paths.get(dataDirectory, ebk_error_file).toString());
    writer.println("Test Entry");
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void resetData_common_GenericLogFilesMonitorWorker() throws Exception
  {
    final String generic_error_file = "common_GenericLogFilesMonitorWorker.log";
    final PrintWriter writer = new PrintWriter(Paths.get(dataDirectory, generic_error_file).toString());
    writer.println("Test Entry");
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void resetData_common_FilePusherWorker() throws Exception
  {
    Files.copy(Paths.get(dataTemplatesDirectory, "check_common_FilePusherWorker.xml"), Paths.get(dataDirectory, "check_common_FilePusherWorker.xml"), StandardCopyOption.REPLACE_EXISTING);
  }

  
  /** 
   * @throws Exception
   */
  private static void resetData_common_FileProgressMonitorWorker() throws Exception
  {
    Files.copy(Paths.get(dataTemplatesDirectory, "check_common_FileProgressMonitorWorker.txt"), Paths.get(dataDirectory, "check_common_FileProgressMonitorWorker.log"), StandardCopyOption.REPLACE_EXISTING);
  }

  
  /** 
   * @throws Exception
   */
  private static void resetData_b250_B250LogFilesMonitorWorker() throws Exception
  {
    final String b250_error_file = "b250_B250LogFilesMonitorWorker.log";
    final PrintWriter writer = new PrintWriter(Paths.get(dataDirectory, b250_error_file).toString());
    writer.println("Test Entry");
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void resetData_apsys_TransactionsMonitorWorker() throws Exception
  {
    //copy the files. Worker should not handle them because they are in the past
    Files.copy(Paths.get(dataTemplatesDirectory, "check_apsys_TransactionsMonitorWorker_1.txt"), Paths.get(dataDirectory, "check_apsys_TransactionsMonitorWorker_1.log"), StandardCopyOption.REPLACE_EXISTING);
    Files.copy(Paths.get(dataTemplatesDirectory, "check_apsys_TransactionsMonitorWorker_2.txt"), Paths.get(dataDirectory, "check_apsys_TransactionsMonitorWorker_2.log"), StandardCopyOption.REPLACE_EXISTING);
    Files.copy(Paths.get(dataTemplatesDirectory, "check_apsys_TransactionsMonitorWorker_3.txt"), Paths.get(dataDirectory, "check_apsys_TransactionsMonitorWorker_3.log"), StandardCopyOption.REPLACE_EXISTING);
  }
  

  
  /** 
   * @throws Exception
   */
  private static void resetData_apsys_ErrorsMonitorWorker() throws Exception
  {
    final String apsys_error_file = "apsys_ErrorsMonitorWorker.log";
    final PrintWriter writer = new PrintWriter(Paths.get(dataDirectory, apsys_error_file).toString());
    writer.println("string1 (E) string2");
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void createData_apsys_ErrorsMonitorWorker() throws Exception
  {
    final String apsys_errors_file = "apsys_ErrorsMonitorWorker.log";
    final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(Paths.get(dataDirectory, apsys_errors_file).toString(), true)));
    final String content = new String(Files.readAllBytes(Paths.get(dataTemplatesDirectory, "check_apsys_ErrorsMonitorWorker_data.txt")), StandardCharsets.UTF_8);
    writer.println(content);
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void createData_apsys_TransactionsMonitorWorker() throws Exception
  {
    Files.copy(Paths.get(dataTemplatesDirectory, "check_apsys_TransactionsMonitorWorker_1.txt"), Paths.get(dataDirectory, "check_apsys_TransactionsMonitorWorker_1.log"), StandardCopyOption.REPLACE_EXISTING);
    Files.copy(Paths.get(dataTemplatesDirectory, "check_apsys_TransactionsMonitorWorker_2.txt"), Paths.get(dataDirectory, "check_apsys_TransactionsMonitorWorker_2.log"), StandardCopyOption.REPLACE_EXISTING);
    Files.copy(Paths.get(dataTemplatesDirectory, "check_apsys_TransactionsMonitorWorker_3.txt"), Paths.get(dataDirectory, "check_apsys_TransactionsMonitorWorker_3.log"), StandardCopyOption.REPLACE_EXISTING);

    File f1 = Paths.get(dataDirectory, "check_apsys_TransactionsMonitorWorker_1.log").toFile();
    File f2 = Paths.get(dataDirectory, "check_apsys_TransactionsMonitorWorker_2.log").toFile();
    File f3 = Paths.get(dataDirectory, "check_apsys_TransactionsMonitorWorker_3.log").toFile();

    f1.setLastModified(DateUtils.getStartupTime() + 60000);
    f2.setLastModified(DateUtils.getStartupTime() + 60000);
    f3.setLastModified(DateUtils.getStartupTime() + 60000);
  }

  
  /** 
   * @throws Exception
   */
  private static void createData_b250_B250LogFilesMonitorWorker() throws Exception
  {
    final String b250_errors_file = "b250_B250LogFilesMonitorWorker.log";
    final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(Paths.get(dataDirectory, b250_errors_file).toString(), true)));
    final String content = new String(Files.readAllBytes(Paths.get(dataTemplatesDirectory, "check_b250_B250LogFilesMonitorWorker.txt")), StandardCharsets.UTF_8);
    writer.println(content);
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void createData_common_FileProgressMonitorWorker() throws Exception
  {
    final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(Paths.get(dataDirectory, "check_common_FileProgressMonitorWorker.log").toString(), true)));
    writer.println("Test Data");
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void createData_common_FilePusherWorker() throws Exception
  {
    final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(Paths.get(dataDirectory, "check_common_FilePusherWorker.xml.ok").toString(), false)));
    writer.println("");
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void createData_common_GenericLogFilesMonitorWorker() throws Exception
  {
    final String generic_errors_file = "common_GenericLogFilesMonitorWorker.log";
    final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(Paths.get(dataDirectory, generic_errors_file).toString(), true)));
    final String content = new String(Files.readAllBytes(Paths.get(dataTemplatesDirectory, "check_common_GenericLogFilesMonitorWorker.txt")), StandardCharsets.UTF_8);
    writer.println(content);
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void createData_ebanking_EbankingLogFilesMonitorWorker() throws Exception
  {
    final String ebk_errors_file = "ebanking_EbankingLogFilesMonitorWorker.log";
    final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(Paths.get(dataDirectory, ebk_errors_file).toString(), true)));
    final String content = new String(Files.readAllBytes(Paths.get(dataTemplatesDirectory, "check_ebanking_EbankingLogFilesMonitorWorker.txt")), StandardCharsets.UTF_8);
    writer.println(content);
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void createData_equalizer_EqconvLogFileMonitorWorker() throws Exception
  {
    final String eqz_errors_file = "equalizer_EqconvLogFileMonitorWorker.log";
    final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(Paths.get(dataDirectory, eqz_errors_file).toString(), true)));
    final String content = new String(Files.readAllBytes(Paths.get(dataTemplatesDirectory, "check_equalizer_EqconvLogFileMonitorWorker.txt")), StandardCharsets.UTF_8);
    writer.println(content);
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void createData_equalizer_EqIsLogFileMonitorWorker() throws Exception
  {
    final String errors_file = "equalizer_EqIsLogFileMonitorWorker.log";
    final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(Paths.get(dataDirectory, errors_file).toString(), true)));
    final String content = new String(Files.readAllBytes(Paths.get(dataTemplatesDirectory, "check_equalizer_EqIsLogFileMonitorWorker.txt")), StandardCharsets.UTF_8);
    writer.println(content);
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void createData_equalizer_EqPsLogFileMonitorWorker() throws Exception
  {
    final String errors_file = "equalizer_EqPsLogFileMonitorWorker.log";
    final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(Paths.get(dataDirectory, errors_file).toString(), true)));
    final String content = new String(Files.readAllBytes(Paths.get(dataTemplatesDirectory, "check_equalizer_EqPsLogFileMonitorWorker.txt")), StandardCharsets.UTF_8);
    writer.println(content);
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void createData_equalizer_EqWsLogFileMonitorWorker() throws Exception
  {
    final String errors_file = "equalizer_EqWsLogFileMonitorWorker.log";
    final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(Paths.get(dataDirectory, errors_file).toString(), true)));
    final String content = new String(Files.readAllBytes(Paths.get(dataTemplatesDirectory, "check_equalizer_EqWsLogFileMonitorWorker.txt")), StandardCharsets.UTF_8);
    writer.println(content);
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void createData_equalizer_EqXorLogFileMonitorWorker() throws Exception
  {
    final String errors_file = "equalizer_EqXorLogFileMonitorWorker.log";
    final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(Paths.get(dataDirectory, errors_file).toString(), true)));
    final String content = new String(Files.readAllBytes(Paths.get(dataTemplatesDirectory, "check_equalizer_EqXorLogFileMonitorWorker.txt")), StandardCharsets.UTF_8);
    writer.println(content);
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void createData_las_EdmsLogFilesMonitorWorker() throws Exception
  {
    final String errors_file = "las_EdmsLogFilesMonitorWorker.log";
    final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(Paths.get(dataDirectory, errors_file).toString(), true)));
    final String content = new String(Files.readAllBytes(Paths.get(dataTemplatesDirectory, "check_las_EdmsLogFilesMonitorWorker.txt")), StandardCharsets.UTF_8);
    writer.println(content);
    writer.close();
  }

  
  /** 
   * @throws Exception
   */
  private static void createData_las_LasLogFilesMonitorWorker() throws Exception
  {
    final String errors_file = "las_LasLogFilesMonitorWorker.log";
    final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(Paths.get(dataDirectory, errors_file).toString(), true)));
    final String content = new String(Files.readAllBytes(Paths.get(dataTemplatesDirectory, "check_las_LasLogFilesMonitorWorker.txt")), StandardCharsets.UTF_8);
    writer.println(content);
    writer.close();
  }
}