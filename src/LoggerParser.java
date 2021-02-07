// javac -cp "\.;lib\*;" parser/*.java LoggerParser.java
//	java -cp "\.;lib\*;" LoggerParser
//	jar -cmvf Manifest.txt LoggerParser.jar *.class .\lib\*
//	jar -cmvf Manifest.txt LoggerParser.jar *.class .\parser\*.class .\lib\*
package src;
import java.awt.*;
import javax.management.openmbean.TabularData;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import src.parser.*;
import src.gui.*;
import src.types.Condition;

public class LoggerParser {

  static File[] input_paths= null;
  static File output_path= null;

  public static final String PREF_INPUT_PATH_FILE = "LP_PREF_INPUT_PATH";
  public static final String PREF_OUTPUT_PATH_FILE = "LP_PREF_OUTPUT_PATH";

  static ArrayList <DataTreeVisualization> visualizations = new ArrayList<DataTreeVisualization>();
  static Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

private static class MainMenu extends JFrame {

  JButton inputButton = new JButton( "Input");
  JButton parseButton = new JButton( "Parse");
  JButton filterButton = new JButton( "Filter");
  JButton saveButton= new JButton("Save");
  private final Object lock = new Object();
  private final Object filterLock = new Object();
  private final Object parseLock = new Object();
  MainMenu() {
    setTitle("Logger Parser");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

    JButton buttons[] = {inputButton, parseButton, filterButton, saveButton};
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.Y_AXIS));
    JPanel lBorderPanel = new JPanel(new GridLayout(buttons.length,1));
    JPanel rBorderPanel = new JPanel(new GridLayout(buttons.length,1));
    for (JButton button : buttons){
      buttonPanel.add(Box.createRigidArea(new Dimension(0,1)));
      buttonPanel.add(button);
      buttonPanel.add(Box.createRigidArea(new Dimension(0,1)));
      button.setPreferredSize(new Dimension(120, 30));
      button.setAlignmentX(JComponent.CENTER_ALIGNMENT);
      lBorderPanel.add(Box.createRigidArea(new Dimension(3,0)));
      rBorderPanel.add(Box.createRigidArea(new Dimension(3,0)));
    }
    add(lBorderPanel);
    add(buttonPanel);
    add(rBorderPanel);
    pack();
//    setSize(100, 300);
    setLocation(dim.width/2-getSize().width/2, dim.height/2-getSize().height/2);

    parseButton.setEnabled(false);
    filterButton.setEnabled(false);
    saveButton.setEnabled(false);

    ImageIcon appIcon = IOManager.loadLGIcon("icon.png");
    if(appIcon != null) {
      setIconImage(appIcon.getImage());
    }

    final MainMenu mainFrame = this;
    setVisible(true);

    //button event press
    inputButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e){
        try {
          input_paths = IOManager.getFileList(mainFrame, PREF_INPUT_PATH_FILE, "Define input file(s)/dir(s).", IOManager.FileType.ALL, JFileChooser.OPEN_DIALOG);
          if(input_paths == null || input_paths.length == 0){
            IOManager.asWarning("No valid input specified!");
            return;
          }
          if (output_path != null ) parseButton.setEnabled(true);

        } catch (java.lang.NoClassDefFoundError e2){
          IOManager.asError("jar file missing: check if there is a 'lib' dir containing a file named 'org.apache.commons.io.FilenameUtils.jar'");
          System.exit(-1);
        }
        Parser.reset();
        parseButton.setEnabled(true);
        saveButton.setEnabled(false);
        filterButton.setEnabled(false);
        getRootPane().setDefaultButton(parseButton);
        parseButton.requestFocus();
      }
    });

    parseButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e){
        Parser.reset();

        synchronized (parseLock) {
          parseLock.notify();
        }
        //getRootPane().setDefaultButton(saveButton);
        //saveButton.requestFocus();
        getRootPane().setDefaultButton(filterButton);
        filterButton.requestFocus();

      }
    });

    filterButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e){
        synchronized(filterLock) {
          filterLock.notify();
        }
      }
    });

    saveButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e){
        try {
          output_path = IOManager.getFile(mainFrame, PREF_OUTPUT_PATH_FILE,"Select an output file/dir.", IOManager.FileType.ALL, JFileChooser.SAVE_DIALOG );
          if(output_path == null){
            IOManager.asWarning("No valid output specified! Content was not saved.");
            return;
          }
        } catch (java.lang.NoClassDefFoundError e2){
          IOManager.asError("jar file missing: check if there is a 'lib' dir containing a file named 'org.apache.commons.io.FilenameUtils.jar'");
          System.exit(-1);
        }
        saveButton.setEnabled(false);
        save_data();
        saveButton.setEnabled(true);
        IOManager.openOutputFile(output_path);
      }

    });

    getRootPane().setDefaultButton(inputButton);
    inputButton.requestFocus();

    getRootPane().getActionMap().put("clickButton",new AbstractAction(){
      public void actionPerformed(ActionEvent ae)
      {
        if (inputButton.hasFocus()){inputButton.doClick(); }
        else if (parseButton.hasFocus()){parseButton.doClick(); }
        else if (saveButton.hasFocus()){saveButton.doClick(); }
        else if (filterButton.hasFocus()){filterButton.doClick(); }
      }
    });



    Thread parseThread = new Thread() {
      public void run() {
        synchronized(parseLock) {
          while (mainFrame.isVisible()) {
            try {
              parseLock.wait();
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            parseButton.setEnabled(false);
            inputButton.setEnabled(false);
            filterButton.setEnabled(false);
            saveButton.setEnabled(false);
            boolean successfull = parse_logs(input_paths);
            parseButton.setEnabled(true);
            inputButton.setEnabled(true);
            if(successfull) {
              filterButton.setEnabled(true);
              saveButton.setEnabled(true);

              updateVisualizations();
            }

          }
        }
      }
    };
    //limit
    Thread limitThread = new Thread() {
      public void run() {
        synchronized(filterLock) {
          while (mainFrame.isVisible()) {
            try {
              filterLock.wait();
              DataTreeVisualization dtv =  Parser.doVisualize();
              visualizations.add(dtv);
              dtv.start();
            } catch (InterruptedException e) {
              e.printStackTrace();
              //TODO: what to do?
            }
          }
        }
      }
    };
    limitThread.start();
    parseThread.start();

    synchronized (lock) {
      try {
        lock.wait();
      } catch (InterruptedException ex) {
      }
    }
    try {
      limitThread.join();
      parseThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}

  static boolean parse_logs(File[] input_paths){
    Parser parser = new Parser();
    for (final File fileEntry : input_paths) {

      if (fileEntry.isFile()){
        if (!parser.parse(fileEntry)) {
          IOManager.asMessage("parsing interrupted");
          return false;
        }
      } else if ( fileEntry.isDirectory() ){
      //  new_output_path = output_path + "\\" + fileEntry.getName();
      //  IOManager.createDir(new_output_path);
        if (!parse_logs(Objects.requireNonNull(fileEntry.listFiles()))) return false;
      } else {
        IOManager.asWarning("Skipping strange file: " + fileEntry.getAbsolutePath());
      }

    }
  return true;

  }
  static void save_data() {
    String output_path = LoggerParser.output_path.getAbsolutePath();

    if(new File(output_path).isDirectory()){
      output_path = output_path + "\\" + "log.txt";
    }
    IOManager.createFile(output_path);

    // System.out.println("parsed " + cnt + " files for " + new_output_path);
    //parser.print_log_info();
    Parser.write_log_info(output_path);
  }

  static void updateVisualizations() {
    // Create an iterator from the l
    Iterator<DataTreeVisualization> itr = visualizations.iterator();

    // Find and remove all null
    while (itr.hasNext()) {

       DataTreeVisualization dtv = itr.next();

      if (!dtv.isAlive()) {
        itr.remove();
      } else {
        Parser.updateVisualization(dtv);
      }
    }

  }

  public static void main(String[] args) {
  //ImpulsFormat i_f = new ImpulsFormat();
    //i_f.configure("test");
    //System.exit(0);
    //
//    RainPlot.delete_pyscript();
//    RainPlot.copy_pyscript();
//    if(true) return;
    // Show tool tips immediately
    ToolTipManager.sharedInstance().setInitialDelay(1000);
    ToolTipManager.sharedInstance().setDismissDelay(60000);
    new MainMenu();

  }
}
