package workers.apsys;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import utils.ProcessUtils;
import workers.AgentLogger;
import workers.common.SqlQueryToTextWorker;


public class ServiceInstanceCheckWithOracle extends SqlQueryToTextWorker
{
  private final String SERVICE_INSTANCE_COUNT_COLUMN_NAME = "NBSRVI";
  private final String SERVICE_INSTANCE_COLUMN_NAME="CDSRVS";

  private final String sqlOracleProcesses = "select  distinct s.process as \"pid\", s.program as \"apsys_program\", s.osuser as \"owner\" from v$process p join v$session s on (s.paddr = p.addr) order by s.program asc";

  private int checkStatus = 0;
  private boolean hasRequiredColumns = true;
  private String currentUser;

  
  /** 
   * @throws Exception
   */
  @Override
  protected void init() throws Exception {
    super.init();

    final String _currentUser = parameters.get("owner");

    if (_currentUser != null)
    {
      this.currentUser = _currentUser.trim();
    }
    else 
    {
      this.currentUser = System.getProperty("user.name"); 
    }
    AgentLogger.logTrace(this.workerInfos.getName() + ": filtering processes with user *" + this.currentUser + "*");
  }

  
  /** 
   * @param rs
   * @param resultsBuilder
   * @return int
   * @throws Exception
   */
  @Override
  protected int getFormattedResults(final ResultSet rs, final StringBuilder resultsBuilder) throws Exception {
    final ResultSetMetaData rsmd = rs.getMetaData();
    final int colCount = rsmd.getColumnCount();

    final Object[] columns = new String[colCount];
    String format = "";
    int nbsrvi_column_index = -1;
    int cdsrvs_column_index = -1;

    for (int i = 0; i < colCount; i++) {
      final String columnName = rsmd.getColumnName(i+1);
      columns[i] = columnName;
      format +="%-30s ";
      if (columnName.equals(SERVICE_INSTANCE_COUNT_COLUMN_NAME))
      {
        nbsrvi_column_index = i+1;
      }
      if (columnName.equals(SERVICE_INSTANCE_COLUMN_NAME))
      {
        cdsrvs_column_index = i+1;
      }
    }

    if (nbsrvi_column_index == -1 || cdsrvs_column_index == -1)
    {
      hasRequiredColumns = false;
    }

    if (resultsBuilder != null)
    {
      resultsBuilder.append(String.format(format, columns));
      resultsBuilder.append(System.lineSeparator());
    }

    int rowCount = 0;

    Object[] values = null;

    Map<String, ArrayList<String>> oracleProcesses = this.getApsysProcessesFromOracle();

    while (rs.next()) {
      values = new Object[colCount];

      for (int i = 0; i < colCount; i++) {
        Object value = rs.getObject(i+1);
        if (value == null) {
          value = "";
        }
        values[i] = value;
      }

      if (nbsrvi_column_index > 0 && cdsrvs_column_index > 0)
      {
        String service = values[cdsrvs_column_index-1].toString();
        int serviceInstanceCount = new Integer(values[nbsrvi_column_index-1].toString());

        int unixServiceInstanceCount = this.getUnixServiceInstanceCount(service);

        int oracleProcessesCount = this.getProcessCountFromOracle(oracleProcesses, service);

        values[nbsrvi_column_index-1] = "DB config count=" + serviceInstanceCount + ", DB processes count=" + oracleProcessesCount + ", Unix count=" + unixServiceInstanceCount + ", delta1=" + Math.abs(unixServiceInstanceCount-serviceInstanceCount)+ ", delta2=" + Math.abs(unixServiceInstanceCount-oracleProcessesCount);

        if (serviceInstanceCount != unixServiceInstanceCount)
        {
            this.checkStatus = 1;
            AgentLogger.logTrace("Service name: " + service);
            AgentLogger.logTrace("Service count from DB: " + serviceInstanceCount);
            AgentLogger.logTrace("Service count from Unix: " + unixServiceInstanceCount);
        }
      }

      if (resultsBuilder != null)
      {
        resultsBuilder.append(String.format(format, values));
        resultsBuilder.append(System.lineSeparator());
      }
      rowCount++;
    }

    return rowCount;
  }

  
  /** 
   * @param rs
   * @return int
   * @throws Exception
   */
  @Override
  protected int checkStatusColumn(ResultSet rs) throws Exception {
    if (this.hasRequiredColumns)
    {
      return this.checkStatus;
    }
    return -1;
  }

  
  /** 
   * @return Map<String, ArrayList<String>>
   * @throws Exception
   */
  private Map<String, ArrayList<String>> getApsysProcessesFromOracle() throws Exception
  {
    Class.forName(this.jdbcDriver);
    final Connection con = this.getConnection();
    ResultSet rs = null;

    try {

      rs = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(this.sqlOracleProcesses);

      Map<String, ArrayList<String>> oracleProcesses = new HashMap<String, ArrayList<String>>();

      while (rs.next()) {
        String spid = rs.getString(1);
        String programName = rs.getString(2);
        String owner = rs.getString(3);

        if (!owner.toLowerCase().equals(this.currentUser.toLowerCase()))
        {
          AgentLogger.logTrace(this.workerInfos.getName() + ", process with id " + spid + " was FILTERED because the owner " + owner + " is not the same as the configured owner:" + this.currentUser);
          continue;
        }
        else 
        {
          AgentLogger.logTrace(this.workerInfos.getName() + ", process with id " + spid + " was ADDED because the owner " + owner + " is the same as the configured owner: " + this.currentUser);
        }

        int at = programName.indexOf("@");
        if (at != -1)
        {
          programName = programName.substring(0, at);
        }

        ArrayList<String> processes = null;

        if (!oracleProcesses.containsKey(programName))
        {
          processes = new ArrayList<String>();
          oracleProcesses.put(programName, processes);
        }
        processes = oracleProcesses.get(programName);
        processes.add(spid);
      }

      con.close();
      rs.close();

      return oracleProcesses;

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
   * @param service
   * @return int
   */
  private int getUnixServiceInstanceCount(String service) {
    if (service == null)
    {
      AgentLogger.logError("getUnixServiceInstanceCount: Service parameter is NULL", this.workerInfos);
      return -1;
    }

    String unixProcess = service.toLowerCase() + "srv";
    ArrayList<String> processIds = ProcessUtils.getProcessId(unixProcess, this.currentUser);
    
    if (processIds == null)
    {
      return 0;
    }
    return processIds.size();

  }

  
  /** 
   * @param processes
   * @param service
   * @return int
   */
  private int getProcessCountFromOracle(Map<String, ArrayList<String>> processes, String service)
  {
    String key = service.toLowerCase() + "srv";

    if (processes.containsKey(key))
    {
      return processes.get(key).size();
    }
    return 0;
  }

}