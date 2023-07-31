package workers.equalizer;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import data.equalizer.EqualizerIntegrityCheckerResult;
import utils.FileUtils;
import workers.AgentLogger;
import workers.Worker;

public class EqualizerIntegrityCheckerWorker extends Worker{
  
	private String equalizerPath;
  private File historyDirectory;
  private final String significantDifferencesSplitExpression = "\\( difference";
  private boolean useCustomBatch;
	String integrityCommands;
	
	
  /** 
   * @throws Exception
   */
  @Override
	protected void init() throws Exception {
    super.init();
		
		if (!parameters.containsKey("equalizerpath"))
		{
			throw new Exception("additional parameters do not contain the equalizerpath parameter: equalizerpath=xxx");
		}
		
		equalizerPath = parameters.get("equalizerpath");
		
		final File f = new File(equalizerPath);

    if (!f.isDirectory()) {
      throw new Exception("Path: " + equalizerPath + " does not exist");
    }

    historyDirectory = new File(equalizerPath, "History");

    if (!Files.exists(historyDirectory.toPath()))
    {
      historyDirectory = Paths.get(equalizerPath, "Data", "History").toFile();
    }

    if (!historyDirectory.exists()) {
      throw new Exception(
          "Path: " + equalizerPath + " does not seem to be an Equalizer path. History folder not found");
    }

    integrityCommands = this.workerInfos.getCommand();

    if (integrityCommands == null || integrityCommands.trim().length() == 0) {
      throw new Exception(this.getName() + ": no eqconv commands found in the xml settings file.");
    }

    if (parameters.containsKey("usecustombatch"))
    {
      useCustomBatch = true;
    }
  }

  
  /** 
   * @throws Exception
   */
  @Override
  protected void doWork() throws Exception {

    final String[] commands = integrityCommands.split("[\\r\\n]+");

    final String lastDay = getLastEqualizerDay();

    AgentLogger.logTrace(this.getName() + ": Checking integrity for day *" + lastDay + "*");

    final EqualizerIntegrityCheckerResult result = new EqualizerIntegrityCheckerResult();
    result.setEqualizerPath(equalizerPath);
    result.setDayChecked(lastDay);
    
    boolean hasMismatches = false;

    if (equalizerPath.endsWith("/")) {
      equalizerPath = equalizerPath.substring(0, equalizerPath.length() - 1);
    }

    StringBuffer consoleOutput = new StringBuffer();

    StringBuffer fileOutput = new StringBuffer();

    for (String command : commands) {
      if (command.trim().length() > 0)
      {
        command = command.replace("${lastday}", lastDay).trim();
        final StringBuilder error = new StringBuilder();
        final String eqconvCommand = (useCustomBatch ? command : equalizerPath + File.separator + command);
        AgentLogger.logTrace("UsecustomBatch:" + useCustomBatch);
        AgentLogger.logTrace("eqconvCommand:" + eqconvCommand);
        final String output = this.executeCommand(eqconvCommand, this.workerInfos.getSuccessExitCode(), error, equalizerPath);
        consoleOutput.append(output).append("@@@");
      } 
    }

    
    if (hasSignificantDifferences(fileOutput)) {
      hasMismatches = true;
    }

    if (hasMismatches) {
      result.setIntegrityStatus("FoundErrorsOrMismatches");
    } else {
      result.setIntegrityStatus("NoErrorsOrMismatches");
    }

    result.setEqconvResult(fileOutput.toString());

    this.save(result);
  }

  
  /** 
   * @throws Exception
   */
  @Override
  protected void cleanUp() throws Exception {

  }

  
  /** 
   * @return String
   * @throws Exception
   */
  private String getLastEqualizerDay() throws Exception {
    final File[] days = historyDirectory.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(final File dir, final String name) {
        return name.matches("\\d{8}\\.hst");
      }
    });

    Arrays.sort(days, Collections.reverseOrder());

    if (days.length == 0) {
      throw new Exception(
          "History folder " + historyDirectory.getAbsoluteFile() + " does not contain any valid subfolders");
    }

    return days[0].getName().replace(".hst", "");
  }

  
  /** 
   * @param realOutput
   * @return boolean
   * @throws Exception
   */
  private boolean hasSignificantDifferences(StringBuffer realOutput) throws Exception {
    final String cashIntegrityFile = Paths.get(equalizerPath, "Audit", "Maintenance", "cash_integrity.txt").toString();
    final String securityIntegrityFile = Paths.get(equalizerPath, "Audit", "Maintenance", "security_integrity.txt").toString();

    String output = new String(Files.readAllBytes(new File(cashIntegrityFile).toPath()));
    output = FileUtils.decompose(output);
    realOutput.append(output);

    boolean cashSignificantDifferences = checkSignificantDifference(output);

    realOutput.append(System.lineSeparator());

    output = new String(Files.readAllBytes(new File(securityIntegrityFile).toPath()));
    output = FileUtils.decompose(output);
    realOutput.append(output);

    boolean securitySignificantDifferences = checkSignificantDifference(output);

    return cashSignificantDifferences || securitySignificantDifferences;
  }

  
  /** 
   * @param output
   * @return boolean
   */
  private boolean checkSignificantDifference(String output)
  {
    final String[] elements = output.split(significantDifferencesSplitExpression);
    for (int i = 1; i < elements.length; i++) {
      String difference = elements[i].trim();
      if (difference.startsWith("-")) {
        difference = difference.substring(1);
      }

      final int j = difference.indexOf(")");

      if (j > 0)
      {
        difference = difference.substring(0, j).trim();
      }

      if (difference.startsWith("0"))
      {
        continue;
      }
      AgentLogger.logTrace("Integrity significant difference found. Absolute value is: " + difference);
      return true;
    }
    return false;
  }

}
