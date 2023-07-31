package workers.common;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import data.common.SqlQueryMonitorResult;
import workers.AgentLogger;
import workers.Worker;

public class SqlQueryMonitorWorker extends Worker {

  protected String jdbcDriver;
  protected String dbUrl;
  protected String user;
  protected String password;

  private String sqlQuery;
  private int status;

  private final String STATUS_COLUMN_NAME = "nwamon_status";
  private final String STATUS_ERROR = "nok";
  private final String STATUS_OK = "ok";

  
  /** 
   * @throws Exception
   */
  @Override
  protected void init() throws Exception {
    final String additionalParameter = this.workerInfos.getAdditionalParameters();
    this.sqlQuery = this.workerInfos.getCommand();

    if (additionalParameter == null || additionalParameter.length() == 0) {
      throw new Exception("additional parameters were not set for " + this.getClass().getName());
    }

    if (this.sqlQuery == null || this.sqlQuery.trim().length() == 0) {
      throw new Exception("Command parameter with Sql query reference, was not set for " + this.getClass().getName());
    }

    final String[] elements = additionalParameter.split("\\|\\|");

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

    
    Connection con = this.getConnection();

    if (con != null)
    {
      con.close();
    }

  }

  
  /** 
   * @return Connection
   * @throws Exception
   */
  protected Connection getConnection() throws Exception
  {
    Connection con = null;
    if (user != null && password != null)
    {
      con = DriverManager.getConnection(this.dbUrl, this.user, this.password);
    }
    else {
      con = DriverManager.getConnection(this.dbUrl);
    }
    return con;
  }

  
  /** 
   * @throws Exception
   */
  @Override
  protected void doWork() throws Exception {

    Class.forName(this.jdbcDriver);
    final Connection con = this.getConnection();
    ResultSet rs = null;

    try {

      rs = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(this.workerInfos.getCommand());

      final StringBuilder results = new StringBuilder();

      final int rowCount = this.getFormattedResults(rs, results);

      if (!rs.isBeforeFirst())
      {
        rs.beforeFirst();
      }

      this.status = this.checkStatusColumn(rs);

      final String result = results.toString();

      AgentLogger.logTrace("Row count: " + rowCount);
      AgentLogger.logTrace(result);

      con.close();
      rs.close();

      final SqlQueryMonitorResult res = this.getResultObject();

      switch(status)
      {
        case -1:
          res.setStatus("INFO");
        break;
        case 0:
          res.setStatus("OK");
        break;
        case 1:
          res.setStatus("NOK");
        break;
        case 2:
          res.setStatus("UNKNOWN");
        break;
      }

      res.setSqlQuery(sqlQuery);
      res.setSqlQueryResult(result);
      res.setCount(rowCount);

      this.save(res);
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
   * @return SqlQueryMonitorResult
   * @throws Exception
   */
  protected SqlQueryMonitorResult getResultObject() throws Exception
  {
    return new SqlQueryMonitorResult();
  }

  
  /** 
   * @param rs
   * @param resultsBuilder
   * @return int
   * @throws Exception
   */
  protected int getFormattedResults(final ResultSet rs, final StringBuilder resultsBuilder) throws Exception {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder builder = factory.newDocumentBuilder();
    final Document doc = builder.newDocument();
    final Element results = doc.createElement("Results");
    doc.appendChild(results);

    final ResultSetMetaData rsmd = rs.getMetaData();
    final int colCount = rsmd.getColumnCount();

    int rowCount = 0;

    while (rs.next()) {
      final Element row = doc.createElement("Row");
      results.appendChild(row);
      for (int i = 1; i <= colCount; i++) {
        final String columnName = rsmd.getColumnName(i);
        Object value = rs.getObject(i);
        if (value == null) {
          value = "";
        }
        final Element node = doc.createElement(columnName);
        node.appendChild(doc.createTextNode(value.toString()));
        row.appendChild(node);
      }
      rowCount++;
    }

    final DOMSource domSource = new DOMSource(doc);
    final TransformerFactory tf = TransformerFactory.newInstance();
    final Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
    final StringWriter sw = new StringWriter();
    final StreamResult sr = new StreamResult(sw);
    transformer.transform(domSource, sr);

    final String result = sw.toString();

    if (resultsBuilder != null)
    {
      resultsBuilder.append(result);
    }

    return rowCount;
  }

	
  /** 
   * @throws Exception
   */
  @Override
	protected void cleanUp() throws Exception {
		
  }
  
  
  /** 
   * @param rs
   * @return int
   * @throws Exception
   */
  protected int checkStatusColumn(ResultSet rs) throws Exception
  {
    final ResultSetMetaData rsmd = rs.getMetaData();
    final int colCount = rsmd.getColumnCount();

    int statusColumnIndex = -1;

    int status = -1;

    for (int i = 1; i <= colCount; i++) {
      String columnName = rsmd.getColumnName(i);
      if (columnName.toLowerCase().equals(STATUS_COLUMN_NAME))
      {
        statusColumnIndex = i;
      }
    }

    if (statusColumnIndex > -1)
    {
      status = 2;

      while (rs.next()) {
        Object value = rs.getObject(statusColumnIndex);
        if (value != null)
        {
          String valueToLower = value.toString().toLowerCase();

          if (valueToLower.equals(STATUS_ERROR))
          {
            status = 1;
            break;
          }

          if (valueToLower.equals(STATUS_OK))
          {
              status = 0;
          }
        }
        
      }
    }

    if (!rs.isBeforeFirst())
    {
      rs.beforeFirst();
    }

    return status;
  }

}
