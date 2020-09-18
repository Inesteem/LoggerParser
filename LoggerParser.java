// javac -cp "\.;lib\*;" parser/*.java LoggerParser.java
//	java -cp "\.;lib\*;" LoggerParser
//	jar -cmvf Manifest.txt LoggerParser.jar *.class .\lib\*
//	jar -cmvf Manifest.txt LoggerParser.jar *.class .\parser\*.class .\lib\*
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
//from w  w w  . j ava2  s.co  m
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;


import java.beans.*;
import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import parser.*;

public class LoggerParser {

  static IOManager iom = IOManager.getInstance();
  static String timezone="";

  static File[] input_paths= null;
  static File output_path= null;

  public static final String PREF_INPUT_PATH_FILE = "LP_PREF_INPUT_PATH";
  public static final String PREF_OUTPUT_PATH_FILE = "LP_PREF_OUTPUT_PATH";

  static JPanel statusBar;
  static final JFrame frame = new JFrame("LoggerParser"); 
  static final JLabel status = new JLabel(); 
  static JLabel content = new JLabel("stuff");

  static void set_frame(){ 

    statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
    statusBar.setBorder(new CompoundBorder(new LineBorder(Color.DARK_GRAY),
          new EmptyBorder(4, 4, 4, 4)));
    statusBar.add(status);

    content.setHorizontalAlignment(JLabel.CENTER);

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());
    frame.add(content);
    frame.add(statusBar, BorderLayout.SOUTH);

    frame.addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
        status.setText(frame.getWidth() + "x" + frame.getHeight());
        }
        });

    frame.setBounds(20,20,200,200);
    frame.setVisible(true);
  }


  static	void parse_logs(File[] input_paths, String output_path){
    Parser parser = new Parser(timezone);
    String new_output_path = output_path;
    int cnt = 0;

    for (final File fileEntry : input_paths) {

      if (fileEntry.isFile()){		
        System.out.println("parsing file " + fileEntry.getName());
        parser.parse(fileEntry);
        ++cnt;
      } else if ( fileEntry.isDirectory() ){
        new_output_path = output_path + "\\" + fileEntry.getName();
        iom.createDir(new_output_path);
        parse_logs(fileEntry.listFiles(), new_output_path);
      } else {
        iom.asWarning("Skipping strange file: " + fileEntry.getAbsolutePath());
      }

    }
    if(cnt >0){
      if(new File(output_path).isDirectory()){
        new_output_path = output_path + "\\" + "log.txt";
      }
      iom.createFile(new_output_path);

      System.out.println("parsed " + cnt + " files for " + new_output_path);
      parser.print_log_info();
    }
    //parser.write_log_info();


  }


  static void set_io_files(){
    try { 
      input_paths = iom.getFileList(PREF_INPUT_PATH_FILE, "Define input file(s)/dir(s).", IOManager.FileType.ALL);

      if(input_paths == null || input_paths.length == 0){
        iom.asError("No valid input specified!");
        System.exit(-1);
      } 

      output_path = iom.getFile(PREF_OUTPUT_PATH_FILE,"Select an output file/dir.", IOManager.FileType.ALL);
      if(output_path == null){
        iom.asError("No valid output specified!");
        System.exit(-1);
      }


    } catch (java.lang.NoClassDefFoundError e){
      iom.asError("jar file missing: check if there is a 'lib' dir containing a file named 'org.apache.commons.io.FilenameUtils.jar'");
      System.exit(-1);
    }

    System.out.println("INPUTs:");
    for(int i = 0; i < input_paths.length; ++i){
      System.out.println(input_paths[i].getAbsolutePath());	
    }
    System.out.println("\nOUTPUT: " + output_path.getAbsolutePath());

  }


  public static void main(String[] args) {
    set_frame();
    set_io_files();	
    timezone = iom.getAfricanTimezone();

    parse_logs(input_paths, output_path.getAbsolutePath());
    iom.asMessage("Finished!");
    iom.getOutputFile(output_path);
    System.exit(0);

  }
}
