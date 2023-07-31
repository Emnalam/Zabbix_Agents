package workers.morning.apsys;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import data.apsys.CheckMorningResult;
import workers.AgentLogger;
import workers.morning.MorningWorker;

public abstract class ApsysMorningWorker extends MorningWorker {

  protected static Boolean batchHasAborted;
  protected static String lastAccountingDate;
  protected static String accountingDate;

  protected final String getOperationInfoCommand = "getoperationinfo";

  protected String savesDirectory ="../saves";
  protected String jdbcDriver;
  protected String dbUrl;
  protected String user;
  protected String password;
  protected final String apsysLastAccountingDateSqlQuery = "select ldtcptl from parmsgen";


  
  /** 
   * @param init(
   * @throws Exception
   */
  protected abstract void doCheck() throws Exception;

  
  /** 
   * @throws Exception
   */
  @Override
  protected void init() throws Exception {
    super.init();

    if (parameters.containsKey("savesdirectory")) {
      this.savesDirectory = parameters.get("savesdirectory");
    }
    
    if (!Files.exists(Paths.get(this.savesDirectory))) {
      throw new Exception(
          "Path: " + this.savesDirectory + " does not seem to exist or access is denied");
    }

    if (parameters.containsKey("dbconnection")) {
      final String connectionString = parameters.get("dbconnection");
      final String[] elements = connectionString.split("\\|\\|");

      for (final String paramLine : elements) {
        final String[] paramPair = paramLine.split("\\|");

        if (paramPair.length != 2) {
          throw new Exception("Invalid additional parameter: " + paramLine + ". Format: key|value");
        }
        parameters.put(paramPair[0].toLowerCase(), paramPair[1]);
      }

      if (!parameters.containsKey("jdbcdriver")) {
        throw new Exception(
            "additional parameters do not contain the jdbcdriver parameter: jdbcdriver|YOUR_DRIVER_CLASS");
      }

      if (!parameters.containsKey("jdbcurl")) {
        throw new Exception("additional parameters do not contain the jdbcurl parameter: jdbcurl|YOUR_JDBC_URL");
      }

      this.jdbcDriver = parameters.get("jdbcdriver").trim();
      this.dbUrl = parameters.get("jdbcurl").trim();
      this.user = parameters.get("user");
      this.password = parameters.get("password");

      Class.forName(this.jdbcDriver);
      AgentLogger.logTrace("jdbcDriver: " + this.jdbcDriver);
      AgentLogger.logTrace("jdbcurl: " + this.dbUrl);

      this.getLastAccountingDate();
    }

    if (batchHasAborted == null)
    {
      this.acquireOperationInfos();
      lastAccountingDate = this.getLastAccountingDate();
    }

  }
  
  
  /** 
   * @throws Exception
   */
  @Override
  protected void doWork() throws Exception {
    if (!batchHasAborted)
    {
      this.doCheck();
    }
    else {
      CheckMorningResult result = new CheckMorningResult();
      result.setBatchAborted(true);
      this.save(result);
    }
  }

  
  /** 
   * @return String
   * @throws Exception
   */
  private String getLastAccountingDate() throws Exception {
    Class.forName(this.jdbcDriver);
    final Connection con = this.getConnection();
    ResultSet rs = null;

    String apsysDate = null;

    try {

      rs = con.createStatement().executeQuery(apsysLastAccountingDateSqlQuery);

      int rowCount = 0;

      while (rs.next()) {
        final Object value = rs.getObject(1);
        if (value != null)
        {
          apsysDate = value.toString();
        }
        rowCount++;
      }

      AgentLogger.logTrace("Row count: " + rowCount);
      AgentLogger.logTrace("Apsys Date: " + apsysDate);

      if (rowCount > 1)
      {
        throw new Exception("getApsysLastAccountingDate: paramsgen table contains more than one line");
      } else if (rowCount == 0)
      {
        throw new Exception("getApsysLastAccountingDate: paramsgen contains no lines");
      }

      con.close();
      rs.close();

      return apsysDate;

    } catch (final Exception exc) {
      throw exc;
    } finally {
      if (con != null) {
        con.close();
      }
      if (rs != null) {
        rs.close();
      }
    }
  }

  
  /** 
   * @return Connection
   * @throws Exception
   */
  protected Connection getConnection() throws Exception {
    Connection con = null;
    if (user != null && password != null) {
      con = DriverManager.getConnection(this.dbUrl, this.user, this.password);
    } else {
      con = DriverManager.getConnection(this.dbUrl);
    }
    return con;
  }

  
  /** 
   * @throws Exception
   */
  private void acquireOperationInfos() throws Exception
  {
    final String DEFAULT_ARG = "ApsysMorningWorkere_out.tmp";
    final String DATA_SPLIT_DELIMITOR = "#";

    this.executeCommand(getOperationInfoCommand + " " + DEFAULT_ARG, this.workerInfos.getSuccessExitCode(), null, System.getProperty("user.dir"));
    
    String content = new String(Files.readAllBytes(Paths.get(DEFAULT_ARG)));
    
    String[] parts = content.split(DATA_SPLIT_DELIMITOR);

    String acctDate = parts[1];
    String stepHasAborted = parts[5];

    AgentLogger.logTrace(this.getClass().getName() + ": GetOperationInfo output: " + content);

    File file = new File(DEFAULT_ARG);		
    file.delete();    

    batchHasAborted = stepHasAborted.equals("1");
    accountingDate = acctDate;
  }

}