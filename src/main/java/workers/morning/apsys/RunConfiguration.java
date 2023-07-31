package workers.morning.apsys;

import java.sql.Connection;
import java.sql.ResultSet;

import data.apsys.RunConfigurationResult;
import workers.AgentLogger;

public class RunConfiguration extends ApsysMorningWorker {

  private final String tblRunQuery = "select count(*) from tblrun where dtcptl > %s";
  private int warningThreshold = 5;

  
  /** 
   * @throws Exception
   */
  @Override
  protected void init() throws Exception {
    super.init();

    if (parameters.containsKey("warningthreshold"))
    {
      warningThreshold = new Integer(parameters.get("warningthreshold"));
    }
  }

  
  /** 
   * @throws Exception
   */
  @Override
  protected void doCheck() throws Exception {
    final int currentNumberOfRuns = this.getNumberOfFutureRuns();
    final RunConfigurationResult result = new RunConfigurationResult();
    result.setRunCount(currentNumberOfRuns);

    if (currentNumberOfRuns == 0) {
      result.setCheckOk(false);
    } else if (currentNumberOfRuns <= this.warningThreshold) {
      result.setCheckOk(true);
      result.setIsWarning(true);
    } else {
      result.setIsWarning(false);
      result.setCheckOk(true);
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
   * @return int
   * @throws Exception
   */
  private int getNumberOfFutureRuns() throws Exception {
    Class.forName(this.jdbcDriver);
    final Connection con = this.getConnection();
    ResultSet rs = null;

    int runCount = -1;

    try {
      final String query = String.format(tblRunQuery, accountingDate);
      AgentLogger.logTrace(this.workerInfos.getName() + ", tblrun Query: " + query);
      rs = con.createStatement().executeQuery(query);

      int rowCount = 0;

      while (rs.next()) {
        final Object value = rs.getObject(1);
        if (value != null)
        {
          runCount = new Integer(value.toString());
        }
        rowCount++;
      }

      AgentLogger.logTrace("Run Count: " + rowCount);
      AgentLogger.logTrace("Apsys Accounting Date: " + accountingDate);

      con.close();
      rs.close();

      return runCount;

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