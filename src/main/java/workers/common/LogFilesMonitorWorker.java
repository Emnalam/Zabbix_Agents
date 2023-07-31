package workers.common;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import config.Property;
import config.WorkerInfos;
import data.common.LogFilesMonitorResult;
import workers.AgentLogger;
import workers.Worker;
import utils.DateUtils;

public abstract class LogFilesMonitorWorker extends Worker{

  private static Map<String,Long> logFilesLength= new ConcurrentHashMap<String, Long>();
  
  private String[] includes;
  private String[] excludes;

  private String[] directories;

  private long lastCheckTime = DateUtils.getStartupTime();

  
  /** 
   * @param file
   * @param file
   * @throws Exception
   */
  protected abstract void handleLogDataBlock(File file, String[] lines) throws Exception;
  
  
  /** 
   * @param file
   * @throws Exception
   */
  protected void handleIdleEvent(final File file) throws Exception {
  }

  
  /** 
   * @throws Exception
   */
  @Override
  protected void init() throws Exception {
    super.init();

    if (this.workerInfos.getScheduling() != null) {
      if (!this.workerInfos.getScheduling().isEnabled()) {
        throw new Exception("Error: scheduling is not enabled on the worker: " + this.workerInfos.getName());
      }
    }

    if (!parameters.containsKey("directories")) {
      throw new Exception("additional parameters do not contain the directories parameter: directories=xxx");
    }

    final String _directories = parameters.get("directories");

    directories = _directories.split(",");

    for (final String directory : directories) {
      final File folder = new File(directory);
      if (folder.isDirectory() && folder.exists()) {
        AgentLogger.logTrace("Tailing and watching files for directory: " + directory);
      } else {
        throw new Exception("Directory " + directory + " is not a directory or it does not exist or access is denied");
      }
    }

    final String _includes = parameters.get("includes");

    if (_includes != null && _includes.length() > 0) {
      AgentLogger.logTrace(this.getName() + ": Using includes pattern: " + _includes);
      includes = _includes.split(",");
    }

    final String _excludes = parameters.get("excludes");

    if (_excludes != null && _excludes.length() > 0) {
      AgentLogger.logTrace(this.getName() + ": Using excludes pattern: " + _excludes);
      excludes = _excludes.split(",");
    }
  }

  
  /** 
   * @throws Exception
   */
  @Override
  protected void doWork() throws Exception {
    startMonitoringLogFiles();
  }

  
  /** 
   * @throws Exception
   */
  @Override
  protected void cleanUp() throws Exception {

  }

  
  /** 
   * @return LogFilesMonitorResult
   * @throws Exception
   */
  protected LogFilesMonitorResult getDataResultObject() throws Exception {
    return new LogFilesMonitorResult();
  }

  
  /** 
   * @param fileOrigin
   * @param line
   * @throws Exception
   */
  protected void appendFilteredErrorOutput(final File fileOrigin, final String line) throws Exception {
    final LogFilesMonitorResult result = this.getDataResultObject();
    result.setFile(fileOrigin.getAbsolutePath());
    result.setAgentDescription(line);
    result.setAgentType("FILTEREDERROR");

    this.save(result);
  }

  
  /** 
   * @param fileOrigin
   * @param line
   * @throws Exception
   */
  protected void appendErrorOutput(final File fileOrigin, final String line) throws Exception {
    final LogFilesMonitorResult result = this.getDataResultObject();
    result.setFile(fileOrigin.getAbsolutePath());
    result.setAgentDescription(line);
    result.setAgentType("ERROR");

    this.save(result);
  }

  
  /** 
   * @param fileOrigin
   * @param line
   * @throws Exception
   */
  protected void appendFatalOutput(final File fileOrigin, final String line) throws Exception {
    final LogFilesMonitorResult result = this.getDataResultObject();
    result.setFile(fileOrigin.getAbsolutePath());
    result.setAgentDescription(line);
    result.setAgentType("FATAL");

    this.save(result);
  }

  
  /** 
   * @param fileOrigin
   * @param stack
   * @throws Exception
   */
  protected void appendCrashOutput(final File fileOrigin, final String stack) throws Exception {
    final LogFilesMonitorResult result = this.getDataResultObject();
    result.setFile(fileOrigin.getAbsolutePath());
    result.setAgentDescription(stack);
    result.setAgentType("CRASH");

    this.save(result);
  }

  
  /** 
   * @param fileOrigin
   * @param line
   * @throws Exception
   */
  protected void appendReturnCodeErrorOutput(final File fileOrigin, final String line) throws Exception {
    final LogFilesMonitorResult result = this.getDataResultObject();
    result.setFile(fileOrigin.getAbsolutePath());
    result.setAgentDescription(line);
    result.setAgentType("RETURNCODEERROR");

    this.save(result);
  }

  
  /** 
   * @param fileOrigin
   * @param line
   * @param type
   * @param code
   * @throws Exception
   */
  protected void appendOutput(final File fileOrigin, final String line, final String type, final String code)
      throws Exception {
    final LogFilesMonitorResult result = this.getDataResultObject();
    result.setFile(fileOrigin.getAbsolutePath());
    result.setAgentDescription(line);
    result.setAgentType(type);
    result.setAgentCode(code);

    this.save(result);
  }

  
  /** 
   * @param file
   * @param dataBlock
   * @throws Exception
   */
  private void handleData(final File file, final String dataBlock) throws Exception {
    final String[] lines = dataBlock.split("[\\r\\n]+");

    final String fileName = file.getName();

    final Property matchedProperty = this.tryGetMatchingProperty(fileName);

    if (matchedProperty != null) {
      for (final String line : lines) {
        if (this.tryMatchPropertyValues(matchedProperty, line)) {
          this.appendFilteredErrorOutput(file, line);
        }
      }
    } else {
      this.handleLogDataBlock(file, lines);
    }
  }

  private void startMonitoringLogFiles() {
    trimCache();
    initLogFilesPositions();
    startTailers();
  }

  private void initLogFilesPositions() {
    for (final String directory : directories) {
      final File folder = new File(directory);

      final File[] listOfFiles = folder.listFiles();

      for (int i = 0; i < listOfFiles.length; i++) {

        final String fpath = listOfFiles[i].getAbsolutePath();
        if (!logFilesLength.containsKey(fpath)) {
          logFilesLength.put(fpath, listOfFiles[i].length());
        }
        else {
          long storedLength = logFilesLength.get(fpath);
          if (storedLength > listOfFiles[i].length())
          {
            logFilesLength.remove(fpath);
            logFilesLength.put(fpath, listOfFiles[i].length());
          }
        }
      }
    }
  }

  private void trimCache() {
    final Iterator<Map.Entry<String, Long>> itr = logFilesLength.entrySet().iterator();
    while (itr.hasNext()) {
      final Map.Entry<String, Long> entry = itr.next();
      final String logFile = entry.getKey();
      final File f = new File(logFile);
      if (!f.exists()) {
        itr.remove();
      }
    }
  }

  private void startTailers() {
    for (final String directory : directories) {
      final File folder = new File(directory);

      final File[] listOfFiles = folder.listFiles(new FileFilter() {
        @Override
        public boolean accept(final File f) {

          final String fname = f.getName();

          final boolean dateCheck = f.lastModified() >= lastCheckTime;
          boolean includesCheck = true;
          boolean excludesCheck = true;

          if (includes != null) {
            includesCheck = false;

            for (final String include : includes) {
              if (fname.matches(include)) {
                includesCheck = true;
                break;
              }
            }
          }

          if (excludes != null) {
            excludesCheck = false;

            for (final String exclude : excludes) {
              if (!fname.matches(exclude)) {
                excludesCheck = true;
                break;
              }
            }
          }

          return dateCheck && includesCheck && excludesCheck;
        }
      });

      lastCheckTime = System.currentTimeMillis();

      for (int i = 0; i < listOfFiles.length; i++) {
        final File file = listOfFiles[i];
        final String fpath = file.getAbsolutePath();
        if (file.isFile()) {
          final TailRunner tr = new TailRunner(file, this.workerInfos, this, logFilesLength.get(fpath));
          tr.run();
        }
      }
    }
  }

  
  /** 
   * @param file
   * @param position
   */
  private void updateFilePosition(final String file, final long position) {
    logFilesLength.put(file, position);
  }

  class TailRunner implements Runnable {
    private final LogFilesMonitorWorker worker;
    private final WorkerInfos workerInfos;
    private final File file;
    private final long seekPosition;

    public TailRunner(final File file, final WorkerInfos workerInfos, final LogFilesMonitorWorker worker,
        final long lastPosition) {
      this.worker = worker;
      this.workerInfos = workerInfos;
      this.file = file;
      this.seekPosition = lastPosition;
    }

    @Override
    public void run() {

      RandomAccessFile raf = null;

      final String fpath = file.getAbsolutePath();

      if (this.seekPosition < file.length()) {
        final StringBuffer tailOutput = new StringBuffer();

        try {
          raf = new RandomAccessFile(fpath, "r");
          raf.seek(seekPosition);

          final byte[] buffer = new byte[(int) (file.length() - seekPosition)];
          if (raf.read(buffer) > 0) {
            final String newItems = new String(buffer);
            tailOutput.append(newItems);
            this.worker.handleData(file, tailOutput.toString());
          }
        } catch (final Exception exc) {
          AgentLogger.logError(exc, workerInfos);
        } finally {
          if (raf != null) {
            try {
              raf.close();
            } catch (final IOException e) {
            }
            raf = null;
          }
        }

        worker.updateFilePosition(fpath, file.length());
      }
      try {
        this.worker.handleIdleEvent(file);
      } catch (final Exception exc)
      {
        AgentLogger.logError(exc, workerInfos);
      }
    }
  }
}

