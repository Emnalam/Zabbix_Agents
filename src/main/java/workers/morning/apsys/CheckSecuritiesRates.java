package workers.morning.apsys;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import data.apsys.CheckSecuritiesRatesResult;

public class CheckSecuritiesRates extends ApsysMorningWorker {

  private final String crtFilePattern = "CRT_R002_.*";
  
  private File currentFile;

  
  /** 
   * @throws Exception
   */
  @Override
  protected void doCheck() throws Exception {
    final Path cFolder = Paths.get(this.savesDirectory, String.format("run%s_1c", lastAccountingDate));
    //
    final File[] files = cFolder.toFile().listFiles(new FilenameFilter() {
      @Override
      public boolean accept(final File dir, final String name) {
        return name.matches(crtFilePattern);
      }
    });

    final CheckSecuritiesRatesResult result = new CheckSecuritiesRatesResult();

    if (files.length == 0) {
      result.setCheckOk(false);
      result.setCtrFilesFound(false);
    } else {
      result.setCtrFilesFound(true);
      for (final File file : files) {
        currentFile = file;
        final List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.ISO_8859_1);
        final String ratesType = this.extractRatesType(lines);
        final int historicalItems = this.extractNumberOfHistoricalItems(lines);
        result.addRatesInfos(ratesType, historicalItems);
      }
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
   * @param lines
   * @return int
   * @throws Exception
   */
  private int extractNumberOfHistoricalItems(final List<String> lines) throws Exception {
    for(final String line : lines)
    {
      String l = line.trim().toLowerCase();
      if (l.contains("nombre de records historiques insérés") || l.contains("anzahl der historischen geschriebenen records") || l.contains("number of historical records inserted")  || l.contains("numero di records storicizzati inseriti"))
      {
        final String[] elements = line.split(":");
        final String element = elements[1].trim();
        final String str1 = element.substring(0, element.indexOf(" ")).trim();
        final Integer count = new Integer(str1);
        return count.intValue();
      }
    }
    throw new Exception("Could not extract number of historical records of security rates from " + currentFile.getAbsolutePath());
  }

  
  /** 
   * @param lines
   * @return String
   * @throws Exception
   */
  private String extractRatesType(final List<String> lines) throws Exception
  {
    for(final String line : lines)
    {
      String l = line.trim().toLowerCase();
      if (l.contains("type de cours") || l.contains("kurs-art") || l.contains("rate type") || l.contains("tipo di corso"))
      {
        final String[] elements = line.split(":");
        final String str1 = elements[1].trim();
        final String str2 = str1.substring(0, str1.indexOf(" ")).trim();
        return str2;
      }
    }
    throw new Exception("Could not extract Security Rates Type from " + currentFile.getAbsolutePath());
  }
  
}