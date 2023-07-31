package workers.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import data.common.DBCheckUsersResult;
import data.common.DBUserStatus;
import workers.AgentLogger;
import workers.Worker;

public class DBCheckUsersWorker extends Worker {
	
	private String jdbcDriver;
	private String dbUrl;
	private String dbUser;
	private String dbPwd;
	private String query;
	private static int rowCount;

	
  /** 
   * @throws Exception
   */
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

		if (!parameters.containsKey("dburl") || !parameters.containsKey("dbuser") || !parameters.containsKey("dbpwd")) {
			throw new Exception("Mandatory additional parameters missing: dburl/dbuser/dbpwd");
    }
    
    this.query = this.workerInfos.getCommand();

    if (this.query == null || this.query.trim().length() == 0)
    {
      throw new Exception("Mandatory Command parameter missing for " + this.workerInfos.getName());
    }

		this.jdbcDriver = "oracle.jdbc.driver.OracleDriver";
		
		// "jdbc:oracle:thin:@vm-psrhel01:1521:cs406p1";
		this.dbUrl      = parameters.get("dburl").trim();
		
		// zabbix/zabbix;
		this.dbUser     = parameters.get("dbuser").trim();
		this.dbPwd      = parameters.get("dbpwd").trim();
	}

	
  /** 
   * @throws Exception
   */
  @Override
	protected void doWork() throws Exception {
		Connection con = null;
		ResultSet rs = null;
		int count = 0;

		DBCheckUsersResult users = new DBCheckUsersResult();
		DBUserStatus user = new DBUserStatus();

		try {			
			Class.forName(this.jdbcDriver);
			con = DriverManager.getConnection(this.dbUrl,this.dbUser,this.dbPwd);

			rs = con.createStatement().executeQuery(this.query);
			
			while (rs.next()) {
		        String username      = rs.getString("username");
		        String accountStatus = rs.getString("account_status");

		        user.setName(username);
		        user.setStatus(accountStatus);
		        users.addUser(user);
		        
		        if (accountStatus.contains("TIMED")) { 
		        	users.setErrors(users.getErrors()+1); 
		        } else if (accountStatus.contains("GRACE")){
		        	users.setWarnings(users.getWarnings()+1);
		        } else {
		        	users.setErrors(users.getErrors()+1);
		        }
		        
				AgentLogger.logTrace("User : " + username + " - " + accountStatus);

		        count++;
		    }
			rowCount = count;
		}
		catch(Exception exc)
		{
			AgentLogger.logTrace("Failed to connect to oracle database: " + exc.getMessage());
			throw exc;
		}
		finally {
			if (con != null)
			{
				con.close();
			}
			if (rs != null)
			{
				rs.close();
			}
		}
		
		AgentLogger.logTrace("DB Account Agent: " + rowCount + " user(s) retrieved from database");
		AgentLogger.logTrace("DB Account Agent: " + users.getWarnings() + " user(s) in Warning");
		AgentLogger.logTrace("DB Account Agent: " + users.getErrors() + " user(s) in Error");
		
	    this.save(users);

	}

	
  /** 
   * @throws Exception
   */
  @Override
	protected void cleanUp() throws Exception {
		
	}

}
