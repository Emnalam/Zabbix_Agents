package workers.morning.apsys;

import java.sql.Connection;
import java.sql.ResultSet;

import data.apsys.CheckTransactionAbortResult;

public class CheckTransactionAbort extends ApsysMorningWorker {

  private final String sysmonSql ="select t.cdapps, t.dtcptl, t.nusesi, t.fiches, " + 
                                  "flagdoperi*16 + flagdoctai*2 + flagdomesi*8 + flagdoprti*4 as " +
                                  "flagori, " +
                                  "CASE WHEN flagdonel  = 0 THEN 'proc' "+
                                  "WHEN flagdonel  >16384  THEN 'MES' "+
                                  "WHEN flagdonel  >2048  THEN 'PRT' "+
                                  "WHEN flagdonel  >1024  THEN 'PER' "+
                                  "WHEN flagdonel  >512  THEN 'CTA' "+
                                  "ELSE 'Unknown' END AS cause, "+
                                  "flagdonel "+
                                  "from tdstds t, parmsgen p, tbldet d "+
                                  "where t.dtcptl = p.dtcptl "+
                                  "and t.cdapps <> 'CTA' "+
                                  "and t.flagetati = 2 "+
                                  "and t.cdapps = d.cdapps "+
                                  "and t.flagdonel <> flagdoperi*16 + flagdoctai*2 + flagdomesi*8 + flagdoprti*4";

  
  /** 
   * @throws Exception
   */
  @Override
  protected void doCheck() throws Exception {
    Class.forName(this.jdbcDriver);
    final Connection con = this.getConnection();
    ResultSet rs = null;

    try {

      rs = con.createStatement().executeQuery(sysmonSql);

      int rowCount = 0;

      CheckTransactionAbortResult result = new CheckTransactionAbortResult();

      while (rs.next()) {          
        rowCount++;
        final int fiches = rs.getInt("fiches");
        final int dtcptl = rs.getInt("dtcptl");
        final int nusesi = rs.getInt("nusesi");
        final String cdapps = rs.getString("cdapps");
        final String cause = rs.getString("cause");
        final String trId = dtcptl + "_" + nusesi + "_" + fiches;

        result.addError(trId, cdapps, cause);
      }

      result.setCheckOk(rowCount == 0);


      this.save(result);

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
   * @throws Exception
   */
  @Override
  protected void cleanUp() throws Exception {
   

  } 
}