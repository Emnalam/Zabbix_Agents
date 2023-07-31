package workers.integrator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import data.integrator.IntegratorCommonsLogMonitorResult;
import utils.DateUtils;
import workers.AgentLogger;
import workers.Worker;


public class IntegratorCommonsLogMonitorWorker extends Worker {

	private String jdbcDriver;
	private String dbUrl;
	private static Timestamp lastCheckDate;
	private static Timestamp previousCheckDate;
	private String sqlQuery = "select * from CommonsLog where moment >= ? and status_description <> 'Info' order by moment desc";
	
  /** 
   * @throws Exception
   */
  //private String sqlQuery = "select * from CommonsLog where moment >= ? and status_description = 'Info' order by moment desc";
	
	@Override
	protected void init() throws Exception {
    final String additionalParameter = this.workerInfos.getAdditionalParameters();
    final String[] elements = additionalParameter.split("\\|\\|");

    for (final String paramLine : elements) {
      final String[] paramPair = paramLine.split("\\|");

      if (paramPair.length != 2) {
        throw new Exception("Invalid additional parameter: " + paramLine + ". Format: key|value");
      }
      parameters.put(paramPair[0].toLowerCase(), paramPair[1]);
    }
		if (!parameters.containsKey("jdbcdriver"))
		{
			throw new Exception("additional parameters do not contain the jdbcdriver parameter: jdbcdriver|YOUR_DRIVER_CLASS");
		}
		
		if (!parameters.containsKey("jdbcurl"))
		{
			throw new Exception("additional parameters do not contain the jdbcurl parameter: jdbcurl|YOUR_JDBC_URL");
		}
		
		
		this.jdbcDriver = parameters.get("jdbcdriver").trim();
		this.dbUrl = parameters.get("jdbcurl").trim();
	}

	
  /** 
   * @throws Exception
   */
  @Override
	protected void doWork() throws Exception {
		Connection connection = this.getConnection();
		ResultSet rs = null;

		try{
			

			lastCheckDate = getLastNonInfoDate();

      if (lastCheckDate.getTime() >= DateUtils.getStartupTime())
			{
        if (lastCheckDate != null && (previousCheckDate == null || lastCheckDate.after(previousCheckDate)))
        {
          PreparedStatement pstmt = connection.prepareStatement(sqlQuery);
          pstmt.setTimestamp(1, lastCheckDate);
          rs = pstmt.executeQuery();
          
          StringBuilder sb = new StringBuilder();
          
          int count = 0;
          
          while (rs.next()) {
            
            if (count == 0)
            {
              lastCheckDate = rs.getTimestamp("moment");
            }
            
            sb.append(rs.getTimestamp("moment")).append("\t");
            sb.append(rs.getString("project")).append("\t");
            sb.append(rs.getString("job")).append("\t");
            sb.append(rs.getString("status_description")).append("\t");
            sb.append(rs.getString("description").replace("\r"," ").replace(System.lineSeparator(), " ")).append("@@@@");
            
            ++count;
          }
          
          if (count > 0)
          {
            IntegratorCommonsLogMonitorResult result = new IntegratorCommonsLogMonitorResult(); 
            result.setCommonsLogcount(count);
            result.setCommonsLogResult(sb.toString());
            
            this.save(result);
          }
          previousCheckDate = lastCheckDate;
        }
      }
		}
		catch(Exception exc)
		{
			AgentLogger.logError(exc, this.workerInfos);
		}
		finally {
			if (connection != null)
			{
				connection.close();
			}
			if (rs != null)
			{
				rs.close();
			}
		}
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
  private Connection getConnection() throws Exception
	{
		Class.forName(this.jdbcDriver);
		return DriverManager.getConnection(this.dbUrl);
	}
	
	
  /** 
   * @return Timestamp
   * @throws Exception
   */
  private Timestamp getLastNonInfoDate() throws Exception
	{
		Connection connection = this.getConnection();
		ResultSet rs = null;
		Timestamp date = null;
		
		try{
			rs = connection.createStatement().executeQuery("select top 1 moment from Commonslog where status_description <> 'Info' order by moment desc");
			while (rs.next()) {
				date = rs.getTimestamp("moment");
			}
		}
		catch(Exception exc)
		{
			AgentLogger.logError(exc, this.workerInfos);
		}
		finally {
			if (connection != null)
			{
				connection.close();
			}
			if (rs != null)
			{
				rs.close();
			}
		}
		return date;
	}

}