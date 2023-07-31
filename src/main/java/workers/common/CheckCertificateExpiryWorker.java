
package workers.common;

import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import data.common.CheckCertificateExpiryResult;
import utils.DateUtils;
import workers.Worker;

public class CheckCertificateExpiryWorker extends Worker
{
  private String urlParameter;
  private String fileParameter;

  
  /** 
   * @throws Exception
   */
  @Override
  protected void init() throws Exception {
    super.init();

    if (!parameters.containsKey("url") && !parameters.containsKey("file"))
		{
			throw new Exception("additional parameters do not contain the url parameter or file parameter: url=xxx OR file=xxxx");
    }

    this.urlParameter = parameters.get("url"); 
    this.fileParameter = parameters.get("file");
    
    if (this.fileParameter != null && !Files.exists(Paths.get(this.fileParameter)))
    {
      throw new Exception("Certificate file: " + this.fileParameter + " does not exist or access is not possible");
    }
  }

  
  /** 
   * @throws Exception
   */
  @Override
  protected void doWork() throws Exception {
    final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

      public X509Certificate[] getAcceptedIssuers() {

        return null;
      }

      public void checkServerTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {
      }

      public void checkClientTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {

      }
    } };

    final CheckCertificateExpiryResult result = new CheckCertificateExpiryResult();

    if (this.urlParameter != null) {
      result.setUrl(this.urlParameter);
      final SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
      HttpsURLConnection.setDefaultHostnameVerifier(
        new javax.net.ssl.HostnameVerifier(){

          public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
              return true;
          }
        });

      final URL url = new URL(this.urlParameter);
      final HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
      conn.connect();
      final Certificate[] certs = conn.getServerCertificates();

      for (final Certificate c : certs) {
        final X509Certificate xc = (X509Certificate) c;
        this.handleCertificate(xc, result);
      }
    }

    if (this.fileParameter != null) {
      result.setFile(this.fileParameter);
      final CertificateFactory fact = CertificateFactory.getInstance("X.509");
      final FileInputStream is = new FileInputStream(this.fileParameter);
      final X509Certificate cert = (X509Certificate) fact.generateCertificate(is);
      this.handleCertificate(cert, result);
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
   * @param certificate
   * @param result
   */
  private void handleCertificate(final X509Certificate certificate, final CheckCertificateExpiryResult result) {
    final String dn = certificate.getSubjectDN().getName();
    final Date expiresOn = certificate.getNotAfter();
    final Date now = new Date();
    int daysLeft = 0;
    if (expiresOn.compareTo(now) > 0)
    {
      final long secondsLeft = DateUtils.getDateDiffSeconds(expiresOn, now);
      daysLeft = (int)(Math.abs(secondsLeft) / (60 * 60 * 24));
    }    
    
    result.addCertificateInfo(dn, new Integer(daysLeft).toString());
  }

}