package workers.common;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import data.common.CheckUrlAvailabilityResult;
import workers.AgentLogger;
import workers.Worker;

public class CheckUrlAvailabilityWorker extends Worker {
	
	private String url;
	private int successHttpCode;
	
	
  /** 
   * @throws Exception
   */
  @Override
	protected void init() throws Exception {
    super.init();
    
		if (!parameters.containsKey("url"))
		{
			throw new Exception("additional parameters do not contain the url parameter: url=YOUR_FILE");
		}
		
		if (!parameters.containsKey("successhttpcode"))
		{
			throw new Exception("additional parameters do not contain the successHttpCode parameter: successHttpCode=<code>");
		}
		
		
		this.url = parameters.get("url");
		this.successHttpCode = new Integer(parameters.get("successhttpcode"));
		
		AgentLogger.logTrace("Checking if the url " + this.url + " is available");
	}
	
  /** 
   * @throws Exception
   */
  @Override
	protected void doWork() throws Exception {
		// Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
        };
 
        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
 
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
 
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        HttpsURLConnection.setFollowRedirects(true);
        HttpURLConnection.setFollowRedirects(true);
        
        URL url = new URL(this.url);
        
        CheckUrlAvailabilityResult result = new CheckUrlAvailabilityResult();
        result.setUrl(this.url);
        
        try {
	        HttpURLConnection con = (HttpURLConnection)url.openConnection();
	        
	        con.setRequestMethod("GET");
	        con.setConnectTimeout(60000);
	        con.setReadTimeout(60000);
	        
	        con.connect();
	        int responseCode = con.getResponseCode();
	        
	        result.setHttpCode(responseCode);
	        
	        if (con.getResponseCode() != this.successHttpCode)
	        {
	        	result.setStatus("PROBLEM");
	        }
	        else
	        {
	        	result.setStatus("OK");
	        }
        }
        catch(SocketTimeoutException ste)
        {
        	result.setStatus("TIMEOUT");
        }
        catch(Exception exc)
        {
        	result.setStatus("ERROR");
        }
        
        AgentLogger.logTrace(this.getName() + ": Url= " + this.url + ", Response Http Code= " + result.getHttpCode() + ", Status= " + result.getStatus());
        
        this.save(result);
	}
	
  /** 
   * @throws Exception
   */
  @Override
	protected void cleanUp() throws Exception {
		
	}
	
	
}
