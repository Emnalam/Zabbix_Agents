package workers.common;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import data.common.SqlQueryMonitorResult;
import data.common.SqlQueryToTextResult;

public class SqlQueryToTextWorker extends SqlQueryMonitorWorker {

  
  /** 
   * @return SqlQueryMonitorResult
   * @throws Exception
   */
  @Override
  protected SqlQueryMonitorResult getResultObject() throws Exception
  {
    return new SqlQueryToTextResult();
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

    for (int i = 0; i < colCount; i++) {
      final String columnName = rsmd.getColumnName(i+1);
      columns[i] = columnName;
      format +="%-30s ";
    }

    if (resultsBuilder != null)
    {
      resultsBuilder.append(String.format(format, columns));
      resultsBuilder.append(System.lineSeparator());
    }

    int rowCount = 0;

    Object[] values = null;

    while (rs.next()) {
      values = new Object[colCount];

      for (int i = 0; i < colCount; i++) {
        Object value = rs.getObject(i+1);
        if (value == null) {
          value = "";
        }
        values[i] = value;
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
}
