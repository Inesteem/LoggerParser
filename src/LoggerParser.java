// javac -cp "\.;lib\*;" parser/*.java LoggerParser.java
//	java -cp "\.;lib\*;" LoggerParser
//	jar -cmvf Manifest.txt LoggerParser.jar *.class .\lib\*
//	jar -cmvf Manifest.txt LoggerParser.jar *.class .\parser\*.class .\lib\*
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.io.File;
import javax.swing.ToolTipManager;

import src.parser.*;
import src.gui.*;

public class LoggerParser {

  static IOManager iom = IOManager.getInstance();

  static File[] input_paths= null;
  static File output_path= null;

  public static final String PREF_INPUT_PATH_FILE = "LP_PREF_INPUT_PATH";
  public static final String PREF_OUTPUT_PATH_FILE = "LP_PREF_OUTPUT_PATH";

  static JFrame frame = new JFrame("Parse Log-Files");
  static JLabel content = new JLabel("stuff");

  static Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
  static void set_frame(){ 

    Font f = content.getFont();
    content.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
    content.setHorizontalAlignment(JLabel.CENTER);


    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());
    frame.add(content);
    frame.setBounds(20,20,600,100);
    frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height*2);
//    frame.addComponentListener(new ComponentAdapter() {
//        @Override
//        public void componentResized(ComponentEvent e) {
//        status.setText(frame.getWidth() + "x" + frame.getHeight());
//        content.setText(frame.getWidth() + "x" + frame.getHeight());
//        }
//        });
  }


  static void parse_logs(File[] input_paths, String output_path){
    Parser parser = new Parser();
    String new_output_path = output_path;
    int cnt = 0;

    for (final File fileEntry : input_paths) {
  

      content.setText("<html><b><center>Parsing File:<center/><b/><br/>"+fileEntry.getName()+"</html>");

      //frame.pack();
      if (fileEntry.isFile()){		
        System.out.println("parsing file " + fileEntry.getName());
        parser.parse(fileEntry, content,frame);
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
      //parser.print_log_info();
      parser.write_log_info(new_output_path);
    }


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
    //DEBUG
    System.out.println("INPUTs:");
    for(int i = 0; i < input_paths.length; ++i){
      System.out.println(input_paths[i].getAbsolutePath());	
    }
    System.out.println("\nOUTPUT: " + output_path.getAbsolutePath());

  }


  public static void main(String[] args) {
//    ImpulsFormat i_f = new ImpulsFormat();
//    i_f.configure("test");
//    System.exit(0);
    //
    RainPlot.delete_pyscript();
//    RainPlot.copy_pyscript();
//    if(true) return;
    // Show tool tips immediately
    ToolTipManager.sharedInstance().setInitialDelay(1000);
    ToolTipManager.sharedInstance().setDismissDelay(60000);
    set_frame();
    set_io_files();	

    frame.setVisible(true);
    parse_logs(input_paths, output_path.getAbsolutePath());
    frame.setVisible(false);
    
    Parser.plot();

    iom.asMessage("Finished!");
    iom.getOutputFile(output_path);

    
    System.exit(0);

  }
}
