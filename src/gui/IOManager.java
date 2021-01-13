package src.gui;

import java.awt.Desktop;

import javax.swing.JOptionPane;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.JFileChooser;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.charset.Charset;

import java.io.IOException;
import java.io.File;

import java.util.List;
import java.util.prefs.Preferences;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

public class IOManager {

  private static final IOManager OBJ = new IOManager();

  private IOManager() {
    append = false;
  }

  public static IOManager getInstance() {
    return OBJ;
  }

  /** Extracts a file embedded in a Zip archive.
   * @param zipFileRoot a jar archive's path ending with .jar
   * @param zipFile a file inside the zipFileRoot starting with \\
   * @param outputFile an arbitrary file; will be created if it not exists
   * return true if the extracting was successful and false on error
   * (e.g. inputFileRoot is not a zip)
   */
  public static boolean extractZippedFile(String zipFileRoot, String zipFile, String outputFile) {
    // Wrap the file system in a try-with-resources statement
    // to auto-close it when finished and prevent a memory leak
    if(!zipFileRoot.endsWith(".jar")) return false;
    try (FileSystem fileSystem = FileSystems.newFileSystem(Paths.get(zipFileRoot), java.util.Collections.emptyMap())) {
      Path fileToExtract = fileSystem.getPath(zipFile);
      Files.copy(fileToExtract, Paths.get(outputFile));
      return true;
    } catch (IOException x) {
      return false;
    }
  }

  /** Extracts a file embedded in a Zip archive.
   * @param inputFile a file's path containing .jar
   * @param outputFile an arbitrary file; will be created if it not exists
   * return true if the extracting was successful and false on error
   * (e.g. inputFile is not inside a zip)
   */
  public static boolean extractZippedFile(String inputFile, String outputFile) throws IOException {
      String[] result = inputFile.split("[.]jar");
      if (result.length != 2) {
        return false;
      }
      return extractZippedFile(result[0] + ".jar", result[1], outputFile);
    }

  static class SelectAll extends TextAction
  {
    public SelectAll()
    {
      super("Select All");
      putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control A"));
    }

    public void actionPerformed(ActionEvent e)
    {
      JTextComponent component = getFocusedComponent();
      component.selectAll();
      component.requestFocusInWindow();
    }
  }

  private boolean append;
  public final Color ACTIVE_COLOUR = Color.BLACK;
  public final Color INACTIVE_COLOUR = Color.GRAY;

  public enum FileType {
    FILE, DIR, ALL
  }

  public double getImpulsMM(String pref_str) throws java.lang.NoClassDefFoundError {
    Object[] impuls_mms = {"0.2","0.5","1.0"};
    Preferences pref = Preferences.userRoot();
    String pref_mm = pref.get(pref_str, "");

    JFrame frame = new JFrame("Choose milli-meter per impuls:");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    String s = (String)JOptionPane.showInputDialog(
        frame,
        "Choose milli-meter per impuls:",
        "mm",
        JOptionPane.PLAIN_MESSAGE,
        null,
        impuls_mms, pref_mm);


    frame.pack();
    frame.setVisible(true);		
    if ((s != null) && (s.length() > 0)) {

      pref.put(pref_str, s);
      return Double.parseDouble(s);
    }
    asError("wrong mm return value");
    return 0;


  }

  public String getAfricanTimezone(String pref_str) throws java.lang.NoClassDefFoundError {

    Object[] timezones = {"Africa/Abidjan", "Africa/Accra", "Africa/Addis_Ababa", "Africa/Algiers", "Africa/Asmara", "Africa/Asmera", "Africa/Bamako", "Africa/Bangui", "Africa/Banjul", "Africa/Bissau", "Africa/Blantyre", "Africa/Brazzaville", "Africa/Bujumbura", "Africa/Cairo", "Africa/Casablanca", "Africa/Ceuta", "Africa/Conakry", "Africa/Dakar", "Africa/Dar_es_Salaam", "Africa/Djibouti", "Africa/Douala", "Africa/El_Aaiun", "Africa/Freetown", "Africa/Gaborone", "Africa/Harare", "Africa/Johannesburg", "Africa/Juba", "Africa/Kampala", "Africa/Khartoum", "Africa/Kigali", "Africa/Kinshasa", "Africa/Lagos", "Africa/Libreville", "Africa/Lome", "Africa/Luanda", "Africa/Lubumbashi", "Africa/Lusaka", "Africa/Malabo", "Africa/Maputo", "Africa/Maseru", "Africa/Mbabane", "Africa/Mogadishu", "Africa/Monrovia", "Africa/Nairobi", "Africa/Ndjamena", "Africa/Niamey", "Africa/Nouakchott", "Africa/Ouagadougou", "Africa/Porto-Novo", "Africa/Sao_Tome", "Africa/Timbuktu", "Africa/Tripoli", "Africa/Tunis", "Africa/Windhoek","UTC", "UTC+1", "UTC+2", "UTC+3", "GMT", "GMT+1", "GMT+2", "GMT+3"};
    //	Object[] timezones = {"UTC", "UTC+1", "UTC+2", "UTC+3", "GMT", "GMT+1", "GMT+2", "GMT+3"};
    Preferences pref = Preferences.userRoot();
    // Retrieve the selected path or use
    // an empty string if no path has
    // previously been selected
    String pref_zone = pref.get(pref_str, "Africa/Nairobi");

    JFrame frame = new JFrame("Choose a timezone");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    String s = (String)JOptionPane.showInputDialog(
        frame,
        "Choose a timezone",
        "Choose a timezone",
        JOptionPane.PLAIN_MESSAGE,
        null,
        timezones, pref_zone);


    frame.pack();
    frame.setVisible(true);		
    if ((s != null) && (s.length() > 0)) {

      pref.put(pref_str, s);
      return s;
    }
    asError("wrong timezone return value");
    return "";
  }


  public File[] getFileFromUser(String default_path, String message, FileType f_type, boolean multiple_files) throws java.lang.NoClassDefFoundError {
    //set preferred start path
    Preferences pref = Preferences.userRoot();
    String pref_paths= pref.get(default_path, "");
    String[] sfs_str = pref_paths.split(";:,.");
    File[] sfs = new File[sfs_str.length];
    for (int i = 0; i < sfs_str.length; ++i){
      sfs[i] = new File(sfs_str[i]);
    }

    JFileChooser jfc = new JFileChooser();

    //restrict selection
    if (f_type == FileType.DIR) {
      jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    } else if(f_type == FileType.FILE) {
      jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    } else {
      jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    } 
    if (multiple_files){
      jfc.setMultiSelectionEnabled(true);
    }	
    jfc.setDialogTitle(message);



    if(sfs.length>0){
      //      if (sf.isDirectory()) {
      //        jfc.setCurrentDirectory(sf);
      //      } else {
      jfc.setSelectedFile(sfs[0]);
      jfc.setSelectedFiles(sfs);
      //      }
    }


    int returnValue = jfc.showOpenDialog(null);
    if(returnValue != JFileChooser.APPROVE_OPTION) { return null ;} //CANCEL_OPTION

    File[] selectedFiles = null;

    if (multiple_files){
      selectedFiles= jfc.getSelectedFiles();
    } else {
      selectedFiles = new File[1];
      selectedFiles[0] = jfc.getSelectedFile();
    }
    sfs_str = new String[selectedFiles.length];
    for (int i = 0; i < sfs_str.length; ++i){
      sfs_str[i] = selectedFiles[i].getAbsolutePath();
    }

    if (selectedFiles != null && selectedFiles.length > 0){
     // if(selectedFiles.length == 1) {
    //    pref.put(default_path, selectedFiles[0].getAbsolutePath());
        //pref.put(default_path, FilenameUtils.getFullPath(selectedFiles[0].getAbsolutePath()));
        //  } else {
      String str =String.join(";:,.",sfs_str);
      
      pref.put(default_path, str);
      //  }
    }

    return selectedFiles;
  }

  public File[] getFileList(String default_path, String message, FileType f_type) throws java.lang.NoClassDefFoundError {
    return getFileFromUser(default_path,message,f_type,true);
  }

  public File getFile(String default_path, String message, FileType f_type) throws java.lang.NoClassDefFoundError {
    File[] file =  getFileFromUser(default_path,message,f_type,false);
    if (file == null || file.length == 0) return null;
    return file[0];

  }

  public void createDir(String filepath){		
    try {
      Path pathToFile = Paths.get(filepath);
      Files.createDirectories(pathToFile);	
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "Could not create output dir: " + e.getMessage() + "...exiting!", "IO Error", JOptionPane.ERROR_MESSAGE);
      System.exit(-1);
    }
  }

  public void createFile(File filepath){		
    try {
      filepath.createNewFile(); // if file already exists will do nothing 
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "Could not create output file: " + e.getMessage() + "...exiting!", "IO Error", JOptionPane.ERROR_MESSAGE);
      System.exit(-1);
    }
  }
  public void createFile(String filepath){		
    createFile(new File(filepath));
  }
  public boolean writeData(List<String> lines, String outputFile, boolean app){
    if(outputFile == null){
      return false;
    }
    Path file = Paths.get(outputFile);

    try {
      if(app || append){  
        Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
      } else { 
        Files.write(file, lines, Charset.forName("UTF-8"));
      }
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "Could not write to file: " + e.getMessage() + "...exiting!", "IO Error", JOptionPane.ERROR_MESSAGE);
      System.exit(-1);
    }
    return true;
  }



  public File getOutputFile(File file){	
    try {
      if(file.exists()){ 
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
          String cmd = "rundll32 url.dll,FileProtocolHandler " + file.getCanonicalPath();
          Runtime.getRuntime().exec(cmd);
        } else {
          Desktop.getDesktop().edit(file);
        }

      } else {
        JOptionPane.showMessageDialog(null, "File does not exist ... but why?", "IO Error", JOptionPane.ERROR_MESSAGE);
      }
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "Could not open output file: " + e.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
    }
    return file;
  }

  public static void asError(String msg){
    JOptionPane.showMessageDialog(null, msg , "Error", JOptionPane.ERROR_MESSAGE);
    System.exit(-1);
  }
  public static void asMessage(String msg){
    JOptionPane.showMessageDialog(null, msg);
  }
  public static void asWarning(String msg){
    JOptionPane.showMessageDialog(null, msg,"Warning",JOptionPane.WARNING_MESSAGE);
  }

  public boolean createDir(File path){
    // Create a directory; all non-existent ancestor directories are
    // automatically created
    if (path.exists()) {
      return true;
    }
    boolean success = path.mkdirs();
    if(!success){asError("could not create save dir: " + path);}
    return success;	
  }
  public static String legalFileName(String filename){
    filename = filename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

    //trim left spaces:
    filename = filename.replaceAll("^\\s+", "");

    //trim right spaces:
    filename = filename.replaceAll("\\s+$", "");


    //Remove all other spaces
    filename = filename.replaceAll("\\s+","_");
    return filename;
  }

  public int askTwoOptions(String header, String op1, String op2, String dialogstr, String default_opt){
    Object[] options = {op1,op2};
    Preferences pref = Preferences.userRoot();
    int pref_opt = Integer.parseInt(pref.get(default_opt, "0"));
    //JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());


    //int confirmed = JOptionPane.showConfirmDialog(null, 
    //"The file " + FilenameUtils.getName(outputFile) + " already exists; are you sure you want to override it?", "Decide wisely...",
    //	JOptionPane.YES_NO_OPTION);
    int confirmed = JOptionPane.showOptionDialog(null, dialogstr, 
        header, 
        JOptionPane.YES_NO_OPTION, 
        JOptionPane.WARNING_MESSAGE, 
        null, options, options[pref_opt]);
    if (confirmed == JOptionPane.YES_OPTION) {
      pref.put(default_opt, "0");
      return 0;
    }
    if (confirmed == JOptionPane.NO_OPTION) {
      pref.put(default_opt, "1");
      return 1;
    }
    return -1;
  }
  public int askTwoOptions(String header, String op1, String op2, String dialogstr){
    return askTwoOptions(header, op1, op2, dialogstr,"DEFAULT_OPTION_TWO");
  }

  public int askNOptions(String header, String[] options, String dialogstr){

    int confirmed = JOptionPane.showOptionDialog(null, dialogstr, 
        header, 
        JOptionPane.DEFAULT_OPTION, 
        JOptionPane.WARNING_MESSAGE, 
        null, options, options[0]);

    return confirmed;
  }	

  //public void copyFile(String file, String copy) 
  //	throws IOException {

  //Path copied = Paths.get("src/test/resources/copiedWithNio.txt");
  //Path originalPath = original.toPath();
  //Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

  //assertThat(copied).exists();
  //assertThat(Files.readAllLines(originalPath)
  //  .equals(Files.readAllLines(copied)));
  //}


  public boolean copy_file(String src, String dst){
    File f = new File(src);
    if (!f.exists()){ return false; }
    try{
      FileUtils.copyFile(f, new File(dst));
    } catch (IOException  e){
      e.printStackTrace();
      return false;
    }
    return true;

  }
  public boolean create_temp_copy(String src, Calendar calendar){
    String tmp_dir = System.getProperty("java.io.tmpdir");
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(calendar.getTime());
    String dst = tmp_dir + "LP_" + FilenameUtils.removeExtension(FilenameUtils.getName(src)) + "_" + timeStamp;
    boolean success = copy_file(src,dst);
    if (success) {
      System.out.println("created a tempcopy of file " + dst);
    } else {
      System.out.println("no copy of " + src);
    }
    return success;
  }
}


