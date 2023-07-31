package data.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="sqlquerymonitorresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class SqlQueryMonitorResult extends Result {
	
	public SqlQueryMonitorResult() throws Exception {
		super();
	}
	
	protected int count;
	protected String sqlQuery;
  protected String sqlQueryResult;
  protected String status;
	
	
	
  /** 
   * @return String
   */
  public String getSqlQuery() {
		return sqlQuery;
	}
	
  /** 
   * @param sqlQuery
   */
  public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}
	
  /** 
   * @return String
   */
  public String getSqlQueryResult() {
		return sqlQueryResult;
	}
	
  /** 
   * @param sqlQueryResult
   */
  public void setSqlQueryResult(String sqlQueryResult) {
		this.sqlQueryResult = sqlQueryResult;
	}
	
  /** 
   * @return int
   */
  public int getCount() {
		return count;
	}
	
  /** 
   * @param count
   */
  public void setCount(int count) {
		this.count = count;
	}

  
  /** 
   * @return String
   */
  public String getStatus() {
    return status;
  }

  
  /** 
   * @param status
   */
  public void setStatus(String status) {
    this.status = status;
  }
	
	

}
