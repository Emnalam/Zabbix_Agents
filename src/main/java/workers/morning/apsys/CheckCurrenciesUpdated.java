package workers.morning.apsys;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import data.apsys.CheckCurrenciesUpdatedResult;

public class CheckCurrenciesUpdated extends ApsysMorningWorker {

  private final String crtFilePattern = "CRT_I002_.*";
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

    final CheckCurrenciesUpdatedResult result = new CheckCurrenciesUpdatedResult();

    if (files.length == 0) {
      result.setCheckOk(false);
      result.setCtrFilesFound(false);
    } else {
      result.setCtrFilesFound(true);
      for (final File file : files) {
        currentFile = file;
        final List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.ISO_8859_1);
        final String ratesType = this.extractRatesType(lines);
        final int items = this.extractNumberOfItems(lines);
        final int errors = this.extractNumberOfErrors(lines);
        result.addRatesInfos(ratesType, items, errors);
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
  private int extractNumberOfItems(final List<String> lines) throws Exception {
    for(final String line : lines)
    {
      String l = line.trim().toLowerCase();
      if (l.contains("nombre de lignes écrites") || l.contains("anzahl eingespeiste zeilen") || l.contains("number of lines written") || l.contains("numero di linee scritte"))
      {
        final String[] elements = line.split(":");
        final String str1 = elements[1].trim();
        final Integer count = new Integer(str1);
        return count.intValue();
      }
    }
    throw new Exception("Could not extract number of written items from " + currentFile.getAbsolutePath());
  }

  
  /** 
   * @param lines
   * @return int
   * @throws Exception
   */
  private int extractNumberOfErrors(final List<String> lines) throws Exception {
    for(final String line : lines)
    {
      String l = line.trim().toLowerCase();
      if (l.contains("nombre de lignes en erreur") || l.contains("anzahl fehlerhafte zeilen") || l.contains("number of lines with error") || l.contains("numero di linee in errore"))
      {
        final String[] elements = line.split(":");
        final String str1 = elements[1].trim();
        final Integer count = new Integer(str1);
        return count.intValue();
      }
    }
    throw new Exception("Could not extract number of errors from " + currentFile.getAbsolutePath());
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
      if (l.contains("type de cours titre") || l.contains("kursart titel") || l.contains("security rate type") || l.contains("tipo de corso titolo"))
      {
        int lastSeparator = line.lastIndexOf(":");

        return line.substring(lastSeparator+1).trim();
      }
    }
    throw new Exception("Could not extract type of rates from " + currentFile.getAbsolutePath());
  }
  
}