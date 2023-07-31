package utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.Normalizer;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import workers.AgentLogger;

public class FileUtils {
  
  /** 
   * @param prefix
   * @param data
   * @param append
   * @throws Exception
   */
  public static void writeTempFile(final String prefix, final String data, final boolean append) throws Exception {
    String path = getTempFile(prefix).toString();
    writeFile(path, data, append);
  }

  
  /** 
   * @param path
   * @param data
   * @param append
   * @throws Exception
   */
  public static void writeFile(String path, final String data, final boolean append) throws Exception {
    BufferedWriter writer = null;
    try { 
      writer = new BufferedWriter(new FileWriter(path, append));
      writer.append(data);
    } catch (final Exception exc) {
      throw exc;
    } finally {
      writer.close();
    }
  }

  
  /** 
   * @param prefix
   * @return String
   * @throws Exception
   */
  public static String readTempFile(final String prefix) throws Exception {
    final Path tempFile = getTempFile(prefix);
    if (!Files.exists(tempFile)) {
      return null;
    }
    return readFile(tempFile.toFile());
  }

  
  /** 
   * @param file
   * @return String
   * @throws Exception
   */
  public static String readFile(final File file) throws Exception {
    return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(Files.readAllBytes(file.toPath()))).toString();
  }

  
  /** 
   * @param directoryFilePath
   * @param fileExtension
   * @return File
   */
  public static File getMostRecentFile(String directoryFilePath, String fileExtension) {
    File directory = new File(directoryFilePath);
    File[] files = directory.listFiles(File::isFile);
    long lastModifiedTime = Long.MIN_VALUE;
    File chosenFile = null;

    if (files != null) {
      for (File file : files) {
        if (file.lastModified() > lastModifiedTime && getFileExtension(file).toLowerCase().equals(fileExtension.toLowerCase())) {
          chosenFile = file;
          lastModifiedTime = file.lastModified();
        }
      }
    }
    
    return chosenFile;
  }

  
  /** 
   * @param directoryFilePath
   * @param regexPattern
   * @return File
   */
  public static File getMostRecentFileWithPattern(String directoryFilePath, String regexPattern) {
    File directory = new File(directoryFilePath);
    File[] files = directory.listFiles(File::isFile);
    long lastModifiedTime = Long.MIN_VALUE;
    File chosenFile = null;

    if (files != null) {
      for (File file : files) {
        if (file.lastModified() > lastModifiedTime && file.getName().matches(regexPattern)) {
          chosenFile = file;
          lastModifiedTime = file.lastModified();
        }
      }
    }
    
    return chosenFile;
  }

  
  /** 
   * @param directoryFilePath
   * @param regexPattern
   * @return File[]
   */
  public static File[] getFilesWithPattern(String directoryFilePath, String regexPattern) {
    File directory = new File(directoryFilePath);
    File [] files = directory.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
          return name.matches(regexPattern);
      }
    });

    return files;
  }

  
  /** 
   * @param file
   * @return String
   */
  public static String getFileExtension(File file) {
    String name = file.getName();
    int lastIndexOf = name.lastIndexOf(".");
    if (lastIndexOf == -1) {
        return ""; // empty extension
    }
    return name.substring(lastIndexOf);
  }

  
  /** 
   * @param file
   * @return String
   * @throws Exception
   */
  public static String getFileChecksum(File file) throws Exception
  {
      MessageDigest digest = MessageDigest.getInstance("MD5");

      //Get file input stream for reading the file content
      FileInputStream fis = new FileInputStream(file);
      
      //Create byte array to read data in chunks
      byte[] byteArray = new byte[1024];
      int bytesCount = 0; 
        
      //Read file data and update in message digest
      while ((bytesCount = fis.read(byteArray)) != -1) {
          digest.update(byteArray, 0, bytesCount);
      };
      
      //close the stream; We don't need it now.
      fis.close();
      
      //Get the hash's bytes
      byte[] bytes = digest.digest();
      
      //This bytes[] has bytes in decimal format;
      //Convert it to hexadecimal format
      StringBuilder sb = new StringBuilder();
      for(int i=0; i< bytes.length ;i++)
      {
          sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
      }
      
      //return complete hash
    return sb.toString();
  }

  
  /** 
   * @param zipFile
   * @param extractFolder
   * @return String
   * @throws Exception
   */
  public static String extractFolder(String zipFile, String extractFolder) throws Exception 
  {
    int BUFFER = 2048;
    File file = new File(zipFile);

    ZipFile zip = new ZipFile(file);

    String newPath = extractFolder;

    new File(newPath).mkdir();

    Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();

    String extractedPath = null;

    // Process each entry
    while (zipFileEntries.hasMoreElements())
    {
        // grab a zip file entry
        ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
        String currentEntry = entry.getName();

        File destFile = new File(newPath, currentEntry);
        //destFile = new File(newPath, destFile.getName());
        File destinationParent = destFile.getParentFile();

        // create the parent directory structure if needed
        destinationParent.mkdirs();

        if (extractedPath == null)
        {
          String path = destinationParent.getAbsolutePath();
          
          if (!Files.isSameFile(Paths.get(path), Paths.get(extractFolder)))
          {
              extractedPath = path;
          }
        }

        if (!entry.isDirectory())
        {
            BufferedInputStream is = new BufferedInputStream(zip
            .getInputStream(entry));
            int currentByte;
            // establish buffer for writing file
            byte data[] = new byte[BUFFER];

            // write the current file to disk
            FileOutputStream fos = new FileOutputStream(destFile);
            BufferedOutputStream dest = new BufferedOutputStream(fos,
            BUFFER);

            // read and write until last byte is encountered
            while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                dest.write(data, 0, currentByte);
            }
            dest.flush();
            dest.close();
            is.close();
        }
      }

      zip.close();
      return extractedPath;
  }

  
  /** 
   * @param s
   * @return String
   */
  public static String decompose(String s) {
    return java.text.Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+","");
  }
  

  
  /** 
   * @return String
   */
  public static String getTempDir() 
  {
    final String property = "java.io.tmpdir";

    final String tempDir = System.getProperty(property);

    AgentLogger.logTrace("getTempDir(), temporary directory is: " + tempDir);

    return tempDir;
  }

  
  /** 
   * @param prefix
   * @return Path
   */
  private static Path getTempFile(final String prefix)
  {
    String user = System.getProperty("user.name");
    return Paths.get(getTempDir(), prefix + "." + user + ".nwamon");
  }
}