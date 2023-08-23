package workers.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import data.common.IPLabelGlobalData;
import data.common.IPLabelintegrationData;
import workers.AgentLogger;
import workers.Worker;

public class IPLabelIntegration extends Worker{

	// API URL
    String apiUrl = "http://localhost/rest/api/instances";

    // Basic Authentication credentials
    String username = "sysadmin";
    String password = "sa";
	
    @Override
	protected void init(){
    	
    	
    }
	
	@Override
	protected void doWork() throws Exception {
		
            
		try {
            // Create a URL object
            URL url = new URL(apiUrl);

            // Create an HttpURLConnection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set request method to GET
            conn.setRequestMethod("GET");

            // Set Basic Authentication header
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

            // Check the response code
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                // Parse and extract the required fields from the JSON
                String rawData = response.toString();
                AgentLogger.logTrace("Entering extractanddisplaydata method");
                extractAndDisplayData(rawData);
                AgentLogger.logTrace("method executed");
            } else {
                System.out.println("Error: Unable to fetch data from the API. Response Code: " + responseCode);
            }

            // Close the connection
            conn.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
           
        
		
	}
	
	
	// Helper method to extract values from JSON
    private void extractAndDisplayData(String rawData) throws Exception {
        int startIndex = rawData.indexOf("{");
        int endIndex;
        AgentLogger.logTrace("in extract method");
        IPLabelGlobalData GlobalData=new IPLabelGlobalData();
        List<IPLabelintegrationData> rowdata=new ArrayList<IPLabelintegrationData>();
        while (startIndex >= 0) {
        	IPLabelintegrationData data=new IPLabelintegrationData();
            endIndex = findMatchingClosingBrace(rawData, startIndex);
            String object = rawData.substring(startIndex, endIndex + 1);

            // Extract robot name
            String robotName = extractValue(object, "\"Name\":\"", "\"");

            // Extract scenario name
            String scenarioName = extractScenarioName(object);

            // Extract current status value
            String currentStatusValue = extractValue(object, "\"Value\":\"", "\"");

            // Extract current status last message
            String currentStatusLastMessage = extractValue(object, "\"LastMessage\":\"", "\"");

            data.setRobotName(robotName);
            AgentLogger.logTrace(robotName);
            data.setScenarioName(scenarioName);
            AgentLogger.logTrace(scenarioName);
            data.setCurrentStatusValue(currentStatusValue);
            AgentLogger.logTrace(currentStatusValue);
            data.setLastMessage(currentStatusLastMessage);
            AgentLogger.logTrace(currentStatusLastMessage);
            
            
           // this.save(data);
            AgentLogger.logTrace(data.toString());
            rowdata.add(data);

            // Find the next JSON object
            startIndex = rawData.indexOf("{", endIndex + 1);
        }
        AgentLogger.logTrace(rowdata.toString());
        GlobalData.setDataList(rowdata);
        AgentLogger.logTrace(GlobalData.toString());
        this.save(GlobalData);
    }

    // Helper method to find the matching closing brace for a given starting brace
    private int findMatchingClosingBrace(String data, int startIndex) {
        int openBraceCount = 0;

        for (int i = startIndex; i < data.length(); i++) {
            char c = data.charAt(i);

            if (c == '{') {
                openBraceCount++;
            } else if (c == '}') {
                openBraceCount--;
                if (openBraceCount == 0) {
                    return i;
                }
            }
        }

        return -1; // Indicates an error (no matching closing brace found)
    }

    // Helper method to extract robot name and current status value
    private String extractValue(String data, String key, String endTag) {
        int startIndex = data.indexOf(key) + key.length();
        int endIndex = data.indexOf(endTag, startIndex);
        return data.substring(startIndex, endIndex);
    }

    // Helper method to extract scenario name
    private String extractScenarioName(String data) {
        int startIndex = data.indexOf("\"Scenario\":{\"Id\":");
        int endIndex = data.indexOf("\"Name\":\"", startIndex) + "\"Name\":\"".length();
        int scenarioNameEndIndex = data.indexOf("\"", endIndex);
        return data.substring(endIndex, scenarioNameEndIndex);
    }
	
	

	@Override
	protected void cleanUp() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	

	

}
