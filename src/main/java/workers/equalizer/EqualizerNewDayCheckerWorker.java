package workers.equalizer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;

import data.equalizer.EqualizerNewDayCheckerResult;
import utils.DateUtils;
import workers.AgentLogger;
import workers.Worker;

public class EqualizerNewDayCheckerWorker extends Worker { 

  protected String jdbcDriver;
  protected String dbUrl;
  protected String user;
  protected String password;
  protected final String apsysLastAccountingDateSqlQuery = "select ldtcptl from parmsgen";

  protected String equalizerPath;
  protected Path historyPath;

  
  /** 
   * @throws Exception
   */
  @Override
  protected void init() throws Exception {
    super.init();

    if (!parameters.containsKey("equalizerpath")) {
      throw new Exception("additional parameters do not contain the equalizerpath parameter: equalizerpath=xxx");
    }

    equalizerPath = parameters.get("equalizerpath");

    final File f = new File(equalizerPath);

    if (!f.isDirectory()) {
      throw new Exception("Path: " + equalizerPath + " does not exist or is not a directory or access is denied");
    }

    historyPath = Paths.get(equalizerPath, "History");

    if (!Files.exists(historyPath))
    {
      historyPath = Paths.get(equalizerPath, "Data", "History");
    }

    if (!Files.exists(historyPath)) {
      throw new Exception(
          "Path: " + equalizerPath + " does not seem to be an Equalizer path. History folder not found");
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

      this.getApsysLastAccountingDate();
    }
    else
    {
      AgentLogger.logTrace("Last Accounting date is not retrieved from Apsys. To do so, set dbConnection in configuration file");
    }
  }

  
  /** 
   * @throws Exception
   */
  @Override
  protected void doWork() throws Exception {
    String newExpectedDate = this.getNewAccountingDay();

    newExpectedDate += ".hst";
    final Path newEqualizerDay = historyPath.resolve(newExpectedDate);

    final EqualizerNewDayCheckerResult result = new EqualizerNewDayCheckerResult();
    result.setEqualizerPath(equalizerPath);
    result.setDayInfo(newExpectedDate);

    AgentLogger.logTrace(this.getName() + ": New day path to check is: " + newEqualizerDay.toString());

    if (!Files.exists(newEqualizerDay)) {
      result.setDayStatus("NotFound");
    } else {
      result.setDayStatus("Found");
    }

    this.save(result);
  }

  
  /** 
   * @throws Exception
   */
  @Override
  protected void cleanUp() throws Exception {

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
   * @return String
   * @throws Exception
   */
  protected String getNewAccountingDay() throws Exception
  {
    if(this.jdbcDriver != null)
    {
      return this.getApsysLastAccountingDate();
    }
    return DateUtils.getDateFormat(getNewEqualizerExpectedDate(), "yyyyMMdd"); 
  }

  
  /** 
   * @return Date
   */
  private Date getNewEqualizerExpectedDate() {
    final int dayOfWeek = DateUtils.getToday();
    int substract = 1;

    if (dayOfWeek == Calendar.SUNDAY) {
      substract = 2;
    } else if (dayOfWeek == Calendar.MONDAY) {
      substract = 3;
    }
    final Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.add(Calendar.DATE, -substract);
    return cal.getTime();
  }

  
  /** 
   * @return String
   * @throws Exception
   */
  private String getApsysLastAccountingDate() throws Exception {
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

}
