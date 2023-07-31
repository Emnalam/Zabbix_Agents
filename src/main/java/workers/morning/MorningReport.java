package workers.morning;

import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class MorningReport {

  
  /** 
   * @param clientMorningFile
   * @return String
   * @throws Exception
   */
  public static String getDefaultHtml(String clientMorningFile) throws Exception
  {
    return doGenerateHtml(clientMorningFile, null);
  }

  
  /** 
   * @param clientMorningFile
   * @param xsltTransform
   * @return String
   * @throws Exception
   */
  public static String getHtml(String clientMorningFile, String xsltTransform) throws Exception
  {
    return doGenerateHtml(clientMorningFile, xsltTransform);
  }

  
  /** 
   * @param clientMorningFile
   * @param xsltTransform
   * @return String
   * @throws Exception
   */
  private static String doGenerateHtml(String clientMorningFile, String xsltTransform) throws Exception {
    TransformerFactory tFactory = TransformerFactory.newInstance();

    Source xslDoc = null;
    if (xsltTransform == null)
    {
      xslDoc = new StreamSource(MorningReport.class.getResourceAsStream("/html.xsl"));
    }
    else 
    {
      xslDoc = new StreamSource(xsltTransform); 
    }
    
    Source xmlDoc = new StreamSource(clientMorningFile);

    Transformer trasform = tFactory.newTransformer(xslDoc);
    StreamResult result = new StreamResult(new StringWriter());
    trasform.transform(xmlDoc, result);
    return result.getWriter().toString();
  }
}