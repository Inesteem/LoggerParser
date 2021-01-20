// javac -cp "\.;lib\*;" parser/*.java LoggerParser.java
//	java -cp "\.;lib\*;" LoggerParser
//	jar -cmvf Manifest.txt LoggerParser.jar *.class .\lib\*
//	jar -cmvf Manifest.txt LoggerParser.jar *.class .\parser\*.class .\lib\*
package src;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Objects;

import src.parser.*;
import src.gui.*;

public class LoggerParser {

  static File[] input_paths= null;
  static File output_path= null;

  public static final String PREF_INPUT_PATH_FILE = "LP_PREF_INPUT_PATH";
  public static final String PREF_OUTPUT_PATH_FILE = "LP_PREF_OUTPUT_PATH";

  static Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

private static class MainMenu extends JFrame {

  JButton plotButton = new JButton("Plot Data");
  JButton inputButton = new JButton("Input Path");
  JButton outputButton = new JButton("Output File");
  JButton parseButton = new JButton("Parse Input");
  JButton limitButton = new JButton("Limit Data");
  JButton saveButton= new JButton("Save Data");

  MainMenu() {
    setSize(300, 200);
    setTitle("Logger Parser");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocation(dim.width/2-getSize().width/2, dim.height/2-getSize().height*2);
    setLayout(new GridLayout(6,1));

    add(inputButton);
    add(outputButton);
    add(parseButton);
    add(plotButton);
    add(limitButton);
    add(saveButton);

    parseButton.setEnabled(false);
    plotButton.setEnabled(false);
    limitButton.setEnabled(false);
    saveButton.setEnabled(false);

    ImageIcon appIcon = IOManager.loadLGIcon();
    if(appIcon != null) {
      setIconImage(appIcon.getImage());
    }

    final MainMenu main_frame = this;

    //button event press
    inputButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e){
        try {
          input_paths = IOManager.getFileList(main_frame, PREF_INPUT_PATH_FILE, "Define input file(s)/dir(s).", IOManager.FileType.ALL);
          if(input_paths == null || input_paths.length == 0){
            IOManager.asError("No valid input specified!");
            System.exit(-1);
          }
          if (output_path != null ) parseButton.setEnabled(true);

          inputButton.setBackground(Color.GREEN);

        } catch (java.lang.NoClassDefFoundError e2){
          IOManager.asError("jar file missing: check if there is a 'lib' dir containing a file named 'org.apache.commons.io.FilenameUtils.jar'");
          System.exit(-1);
        }
      }
    });

    //button event press
    outputButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e){
        try {

          output_path = IOManager.getFile(main_frame, PREF_OUTPUT_PATH_FILE,"Select an output file/dir.", IOManager.FileType.ALL);
          if(output_path == null){
            IOManager.asError("No valid output specified!");
            System.exit(-1);
          }
          if (input_paths != null ) parseButton.setEnabled(true);

          outputButton.setBackground(Color.GREEN);

        } catch (java.lang.NoClassDefFoundError e2){
          IOManager.asError("jar file missing: check if there is a 'lib' dir containing a file named 'org.apache.commons.io.FilenameUtils.jar'");
          System.exit(-1);
        }
      }
    });

    parseButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e){
        parse_logs(input_paths, output_path.getAbsolutePath());
        plotButton.setEnabled(true);
        saveButton.setEnabled(true);
        limitButton.setEnabled(true);
      }
    });

    plotButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e){
        Parser.plot();
      }
    });

    plotButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e){
        IOManager.asWarning("not implemented");
      }
    });

    limitButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e){
        IOManager.asWarning("not implemented");
      }
    });
    setVisible(true);

    synchronized (this) {
      try {
        this.wait();
      } catch (InterruptedException ex) {
      }
    }
  }
}

  static void parse_logs(File[] input_paths, String output_path){
    Parser parser = new Parser();
    String new_output_path = output_path;
    int cnt = 0;

    for (final File fileEntry : input_paths) {
  
      if (fileEntry.isFile()){
        System.out.println("parsing file " + fileEntry.getName());
        parser.parse(fileEntry);
        ++cnt;
      } else if ( fileEntry.isDirectory() ){
        new_output_path = output_path + "\\" + fileEntry.getName();
        IOManager.createDir(new_output_path);
        parse_logs(Objects.requireNonNull(fileEntry.listFiles()), new_output_path);
      } else {
        IOManager.asWarning("Skipping strange file: " + fileEntry.getAbsolutePath());
      }

    }
    if(cnt >0){
      if(new File(output_path).isDirectory()){
        new_output_path = output_path + "\\" + "log.txt";
      }
      IOManager.createFile(new_output_path);

      System.out.println("parsed " + cnt + " files for " + new_output_path);
      //parser.print_log_info();
      parser.write_log_info(new_output_path);
    }


  }


  public static void main(String[] args) {
//    ImpulsFormat i_f = new ImpulsFormat();
//    i_f.configure("test");
//    System.exit(0);
    //
//    RainPlot.delete_pyscript();
//    RainPlot.copy_pyscript();
//    if(true) return;
    // Show tool tips immediately
    ToolTipManager.sharedInstance().setInitialDelay(1000);
    ToolTipManager.sharedInstance().setDismissDelay(60000);
    new MainMenu();

    //TODO: das noch einbauen; wird übersprungen im Moment
    IOManager.asMessage("Finished!");
    IOManager.getOutputFile(output_path);
  }
}
