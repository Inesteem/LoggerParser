package src.gui;

import java.awt.*;
import java.awt.Desktop;
import java.awt.event.*;

import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.text.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import src.LoggerParser;

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


  public static File[] getFileFromUser(JFrame frame, String default_path, String message, FileType f_type, boolean multiple_files, int dialogType)
          throws java.lang.NoClassDefFoundError {

    //set preferred start path
    Preferences pref = Preferences.userRoot();
    String pref_paths= pref.get(default_path, "");
    String[] sfs_str = pref_paths.split(";:,.");
    File[] sfs = new File[sfs_str.length];
    for (int i = 0; i < sfs_str.length; ++i){
      sfs[i] = new File(sfs_str[i]);
    }

    JFileChooser jfc = new JFileChooser();
    jfc.setDialogType(dialogType);
    jfc.setDialogTitle(message);

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

    if(sfs.length>0){
      jfc.setSelectedFile(sfs[0]);
      jfc.setSelectedFiles(sfs);
    }

    int returnValue;
    if (dialogType == JFileChooser.OPEN_DIALOG) {
      returnValue = jfc.showOpenDialog(frame);
    } else {
      returnValue = jfc.showSaveDialog(frame);
    }
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
      String str =String.join(";:,.",sfs_str);
      pref.put(default_path, str);
    }

    return selectedFiles;
  }

  public static File[] getFileList(JFrame frame, String default_path, String message, FileType f_type, int dialogType)
          throws java.lang.NoClassDefFoundError {
    return getFileFromUser(frame, default_path,message,f_type,true, dialogType);
  }

  public static File getFile(JFrame frame, String default_path, String message, FileType f_type, int dialogType)
          throws java.lang.NoClassDefFoundError {
    File[] file =  getFileFromUser(frame, default_path,message,f_type,false, dialogType);
    if (file == null || file.length == 0) return null;
    return file[0];

  }

  public static void createDir(String filepath){
    try {
      Path pathToFile = Paths.get(filepath);
      Files.createDirectories(pathToFile);	
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "Could not create output dir: " + e.getMessage() + "...exiting!", "IO Error", JOptionPane.ERROR_MESSAGE);
      System.exit(-1);
    }
  }

  public static void createFile(File filepath){
    try {
      filepath.createNewFile(); // if file already exists will do nothing 
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "Could not create output file: " + e.getMessage() + "...exiting!", "IO Error", JOptionPane.ERROR_MESSAGE);
      System.exit(-1);
    }
  }
  public static void createFile(String filepath){
    createFile(new File(filepath));
  }

  public static File openOutputFile(File file){
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

  public static int askTwoOptions(JFrame parent, String header, String op1, String op2, String dialogstr, String default_opt){
    Object[] options = {op1,op2};
    Preferences pref = Preferences.userRoot();
    int pref_opt = Integer.parseInt(pref.get(default_opt, "0"));
    //JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());


    //int confirmed = JOptionPane.showConfirmDialog(null, 
    //"The file " + FilenameUtils.getName(outputFile) + " already exists; are you sure you want to override it?", "Decide wisely...",
    //	JOptionPane.YES_NO_OPTION);
    int confirmed = JOptionPane.showOptionDialog(parent, dialogstr,
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
  public static int askTwoOptions(JFrame parent, String header, String op1, String op2, String dialogstr){
    return askTwoOptions(parent, header, op1, op2, dialogstr,"DEFAULT_OPTION_TWO");
  }

  public static int askNOptions(JFrame parent, String header, String[] options, String dialogstr){

    int confirmed = JOptionPane.showOptionDialog(parent, dialogstr,
        header, 
        JOptionPane.DEFAULT_OPTION, 
        JOptionPane.WARNING_MESSAGE, 
        null, options, options[0]);

    return confirmed;
  }	

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

  /**
   * Creates a temporary copy of a file in the tmp dir.
   * @param src file path
   * @param calendar a calendar object
   * @return true on success
   */
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

  public static ImageIcon loadLGIcon(String iconName) {
    URL url = LoggerParser.class.getResource( "/src/img/"+iconName);
    if(url != null)
      return new ImageIcon(url);
    return null;
  }
  //TODO: refactor; put in another file
  public static ImageIcon scale(ImageIcon imageIcon, int width, int height){
    Image image = imageIcon.getImage(); // transform it
    Image newImg = image.getScaledInstance(width,height,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
    return new ImageIcon(newImg);  // transform it back
  }

  public static String addIdToFilename(String filename, String id) {
    String newFilename = FilenameUtils.removeExtension(filename);
    newFilename += "_"+id;
    newFilename += "."+FilenameUtils.getExtension(filename);
    return newFilename;

  }

}
