<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:template match="/Client">
      <html>
         <head>
          <title>Morning Check Results</title>
          <style>
            table td, table td * {
              vertical-align: top;
            }
          </style>
         </head>
         <body>
            <h1>Morning Check Results</h1>
            <table border="0" width="500">
              <tr>
                <td>
                  <b>Customer</b>
                </td>
                <td>
                  <xsl:value-of select="@name" />
                </td>
              </tr>
              <tr>
                <td>
                  <b>Environment</b>
                </td>
                <td>
                  <xsl:value-of select="@environment" />
                </td>
              </tr>
              <tr>
                <td>
                  <b>Timestamp</b>
                </td>
                <td>
                  <xsl:value-of select="descendant::data/@timestamp" />
                </td>
              </tr>
              <tr>
                <td>
                  <b>Batch Aborted</b>
                </td>
                <td>
                  <xsl:choose>
                    <xsl:when test="descendant::batchAborted/@ok='false'">
                     <font color="RED">YES</font>
                    </xsl:when>
                    <xsl:otherwise>
                      <font color="GREEN">NO</font>
                   </xsl:otherwise>
                  </xsl:choose>
                </td>
              </tr>
              <tr>
                <td>
                  <b>General Status</b>
                </td>
                <td>
                  <xsl:choose>
                    <xsl:when test="descendant::data/@ok='false'">
                     <font color="RED">FAILED</font>
                    </xsl:when>
                    <xsl:otherwise>
                      <font color="GREEN">PASS</font>
                   </xsl:otherwise>
                  </xsl:choose>
                </td>
              </tr>
            </table>
            <br/>
            <table width="1300" border="1">
              <xsl:for-each select="descendant::data/product">
                <tr>
                  <td style="min-width:250px">
                    <b>Product:</b> <xsl:value-of select="@name"/>
                  </td>
                  <td>
                    <xsl:choose>
                      <xsl:when test="descendant::checkResults/@missing">
                        <font color="RED">
                          Missing checks: <xsl:value-of select="descendant::checkResults/@missing"/>
                        </font>
                      </xsl:when>
                      <xsl:otherwise>
                          All checks have been executed
                      </xsl:otherwise>
                    </xsl:choose>
                  </td>
                </tr>
               
                <xsl:for-each select="descendant::result">
                  <tr>
                    <xsl:choose>
                      <xsl:when test="descendant::currentaccountingdateresult">
                          <td>Accounting Date Check</td>
                          <td>
                            <table border="0">
                              <tr>
                                 <td style="min-width:250px">
                                  <b>Status</b>
                                </td>
                                <td>
                                  <xsl:choose>
                                    <xsl:when test="descendant::currentaccountingdateresult/isCheckOk='false'">
                                      <font color="RED">FAIL</font>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <font color="GREEN">PASS</font>
                                    </xsl:otherwise>
                                  </xsl:choose>
                                </td>
                              </tr>
                              <tr>
                                <td>
                                  Current Accounting Date:
                                </td>
                                <td>
                                  <xsl:value-of select="descendant::currentaccountingdateresult/accountingDate"/>
                                </td>
                              </tr>
                            </table>
                          </td>
                      </xsl:when>
                      <xsl:when test="descendant::databaseoperationalstatusresult">
                        <td>Database Operational Infos</td>
                        <td>
                          <table border="0">
                            <tr>
                               <td style="min-width:250px">
                                <b>Status</b>
                              </td>
                              <td>
                                <xsl:choose>
                                    <xsl:when test="descendant::databaseoperationalstatusresult/isCheckOk='false'">
                                      <font color="RED">FAIL</font>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <font color="GREEN">PASS</font>
                                    </xsl:otherwise>
                                  </xsl:choose>
                              </td>
                            </tr>
                            <tr>
                              <td>
                                Operating Mode:
                              </td>
                              <td>
                                <xsl:value-of select="descendant::databaseoperationalstatusresult/operatingMode"/>
                              </td>
                            </tr>
                            <tr>
                              <td>
                                Current Stage:
                              </td>
                              <td>
                                <xsl:value-of select="descendant::databaseoperationalstatusresult/currentStage"/>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </xsl:when>
                      <xsl:when test="descendant::runconfigurationresult">
                        <td>Run Check</td>
                        <td>
                          <table border="0">
                            <tr>
                               <td style="min-width:250px">
                                <b>Status</b>
                              </td>
                              <td>
                                <xsl:choose>
                                    <xsl:when test="descendant::runconfigurationresult/isCheckOk='false'">
                                      <font color="RED">FAIL</font>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <font color="GREEN">PASS</font>
                                    </xsl:otherwise>
                                </xsl:choose>
                              </td>
                            </tr>
                            <tr>
                              <td>
                                Number of defined runs:
                              </td>
                              <td>
                                <xsl:value-of select="descendant::runconfigurationresult/runCount"/>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </xsl:when>
                      <xsl:when test="descendant::checkcurrenciesupdatedresult">
                        <td>Currencies Check</td>
                        <td>
                          <table border="0">
                            <tr>
                               <td style="min-width:250px">
                                <b>Status</b>
                              </td>
                              <td>
                                <xsl:choose>
                                    <xsl:when test="descendant::checkcurrenciesupdatedresult/isCheckOk='false'">
                                      <font color="RED">FAIL</font>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <font color="GREEN">PASS</font>
                                    </xsl:otherwise>
                                </xsl:choose>
                              </td>
                            </tr>
                            <tr>
                              <td>
                                CTR Files found
                              </td>
                              <td>
                                <xsl:value-of select="descendant::checkcurrenciesupdatedresult/ctrFilesFound"/>
                              </td>
                            </tr>
                            <xsl:for-each select="descendant::currenciesinfo">
                              <tr>
                                <td>
                                  <xsl:value-of select="descendant::ratesType"/>
                                </td>
                                <td>
                                  Count: <xsl:value-of select="descendant::count"/>, Errors: <xsl:value-of select="descendant::errorsCount"/>
                                </td>
                              </tr>
                            </xsl:for-each>
                          </table>
                        </td> 
                      </xsl:when>
                      <xsl:when test="descendant::checksecuritiesratesresult">
                        <td>Securities Check</td>
                        <td>
                          <table border="0">
                            <tr>
                               <td style="min-width:250px">
                                <b>Status</b>
                              </td>
                              <td>
                                 <xsl:choose>
                                    <xsl:when test="descendant::checksecuritiesratesresult/isCheckOk='false'">
                                      <font color="RED">FAIL</font>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <font color="GREEN">PASS</font>
                                    </xsl:otherwise>
                                </xsl:choose>
                              </td>
                            </tr>
                            <tr>
                              <td>
                                CTR Files found
                              </td>
                              <td>
                                <xsl:value-of select="descendant::checksecuritiesratesresult/ctrFilesFound"/>
                              </td>
                            </tr>
                            <xsl:for-each select="descendant::securityratesinfo">
                              <tr>
                                <td>
                                  <xsl:value-of select="descendant::ratesType"/>
                                </td>
                                <td>
                                  Count: <xsl:value-of select="descendant::count"/>
                                </td>
                              </tr>
                            </xsl:for-each>
                          </table>
                        </td> 
                      </xsl:when>
                      <xsl:when test="descendant::checksavesbackup">
                        <td>Saves Backup Check</td>
                        <td>
                          <table border="0">
                            <tr>
                               <td style="min-width:250px">
                                <b>Status</b>
                              </td>
                              <td>
                                <xsl:choose>
                                    <xsl:when test="descendant::checksavesbackup/isCheckOk='false'">
                                      <font color="RED">FAIL</font>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <font color="GREEN">PASS</font>
                                    </xsl:otherwise>
                                </xsl:choose>
                              </td>
                            </tr>
                          </table>
                        </td> 
                      </xsl:when>
                      <xsl:when test="descendant::checkprogramabortresult">
                        <td>Programs Aborts Check</td>
                        <td>
                          <table border="0">
                            <tr>
                               <td style="min-width:250px">
                                <b>Status</b>
                              </td>
                              <td>
                                 <xsl:choose>
                                    <xsl:when test="descendant::checkprogramabortresult/isCheckOk='false'">
                                      <font color="RED">FAIL</font>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <font color="GREEN">PASS</font>
                                    </xsl:otherwise>
                                </xsl:choose>
                              </td>
                            </tr>
                            <tr>
                              <td>
                               Aborts
                              </td>
                              <td>
                                <xsl:value-of select="descendant::checkprogramabortresult/batchProgramAborts"/>
                              </td>
                            </tr>
                          </table>
                        </td> 
                      </xsl:when>
                      <xsl:when test="descendant::checktransactionabortresult">
                        <td>Transactions Aborts Check</td>
                        <td>
                          <table border="0">
                            <tr>
                               <td style="min-width:250px">
                                <b>Status</b>
                              </td>
                              <td>
                                 <xsl:choose>
                                    <xsl:when test="descendant::checktransactionabortresult/isCheckOk='false'">
                                      <font color="RED">FAIL</font>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <font color="GREEN">PASS</font>
                                    </xsl:otherwise>
                                </xsl:choose>
                              </td>
                            </tr>
                            <xsl:for-each select="descendant::error">
                              <tr>
                                <td>
                                  <xsl:value-of select="descendant::transactionnumber"/>
                                </td>
                                <td>
                                  Message: <xsl:value-of select="descendant::message"/>, CdApps: <xsl:value-of select="descendant::cdApps"/>
                                </td>
                              </tr>
                            </xsl:for-each>
                          </table>
                        </td> 
                      </xsl:when>
                      <xsl:when test="descendant::checkbatchlogresult">
                        <td>Batch Errors Check</td>
                        <td>
                          <table border="0">
                            <tr>
                               <td style="min-width:250px">
                                <b>Status</b>
                              </td>
                              <td>
                                <xsl:choose>
                                    <xsl:when test="descendant::checkbatchlogresult/isCheckOk='false'">
                                      <font color="RED">FAIL</font>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <font color="GREEN">PASS</font>
                                    </xsl:otherwise>
                                </xsl:choose>
                              </td>
                            </tr>
                            <tr>
                              <td>
                               Errors
                              </td>
                              <td>
                                <pre>
                                  <xsl:value-of select="descendant::checkbatchlogresult/batchErrors"/>
                                </pre>
                              </td>
                            </tr>
                          </table>
                        </td> 
                      </xsl:when>
                      
                      <xsl:otherwise>Unknown tag received</xsl:otherwise>
                    </xsl:choose>
                  </tr>
                </xsl:for-each>
              </xsl:for-each>
            </table>
         </body>
      </html>
   </xsl:template>
</xsl:stylesheet>