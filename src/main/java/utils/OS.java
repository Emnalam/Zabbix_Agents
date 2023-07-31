package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import workers.AgentLogger;

public class OS {
  private static String os;

  
  /** 
   * @return String
   */
  public static String getOsName() {
    if (os == null) {
      os = System.getProperty("os.name");
    }
    return os;
  }

  
  /** 
   * @return boolean
   */
  public static boolean isWindows() {
    return getOsName().startsWith("Windows");
  }

  
  /** 
   * @return String
   * @throws Exception
   */
  public static String getSystemUptime() throws Exception {
    long uptime = -1;

    if (isWindows()) {
      Process uptimeProc =  Runtime.getRuntime().exec(new String[] { "cmd", "/c", "wmic os get lastbootuptime" });
      BufferedReader in = new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
      String line;
      while ((line = in.readLine()) != null) {
        AgentLogger.logTrace("getSystemUptime: line: " + line);
          if (line.toLowerCase().contains("lastbootuptime") || line.trim().length() == 0)
          {
            continue;
          }
          AgentLogger.logTrace("getSystemUptime, wmic command: " + line);
          SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

          String str = line.substring(0, line.indexOf("."));
          Date boottime = format.parse(str);
          uptime = System.currentTimeMillis() - boottime.getTime();
          break;
      }
      if (uptime == -1) {
        return "N/A";
      }

      uptime = uptime / 1000;

      int day = (int) TimeUnit.SECONDS.toDays(uptime);
      long hours = TimeUnit.SECONDS.toHours(uptime) - (day * 24);
      long minute = TimeUnit.SECONDS.toMinutes(uptime) - (TimeUnit.SECONDS.toHours(uptime) * 60);

      return day + " day(s), " + hours + " hour(s), " + minute + " minute(s)";

    } else {
      Process uptimeProc = Runtime.getRuntime().exec(new String[] { "sh", "-c", "uptime" });
      BufferedReader in = new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
      String line = in.readLine();
      AgentLogger.logTrace("GetSystemUpTime, uptime: " + line);
      if (line != null) {
        String[] elements = line.trim().split(",");
        for (int i = 0; i < elements.length; i++) {
          AgentLogger.logTrace("getSystemUpTime: " + i + ": " + elements[i]);
        }
        String _days = elements[0].trim().split("up")[1].trim();
        String time = elements[1].trim();

        String dLabel = " day(s)";
        String sep = ", ";

        if (_days.contains("day"))
        {
          dLabel = "";
        }

        if (time.contains(","))
        {
          time = time.split(",")[0].trim();
        }

        if (time.contains("user"))
        {
          time = "";
          sep = "";
        }

        return _days + dLabel + sep + time;
      }
    }
    return "N/A";
  }
}
