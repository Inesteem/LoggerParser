// javac -cp "\.;lib\*;" parser/*.java LoggerParser.java
//	java -cp "\.;lib\*;" LoggerParser
//	jar -cmvf Manifest.txt LoggerParser.jar *.class .\lib\*
//	jar -cmvf Manifest.txt LoggerParser.jar *.class .\parser\*.class .\lib\*
package src;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

import src.parser.*;
import src.gui.*;

public class LoggerParser {

  static File[] input_paths= null;
  static File output_path= null;

  public static final String PREF_INPUT_PATH_FILE = "LP_PREF_INPUT_PATH";
  public static final String PREF_OUTPUT_PATH_FILE = "LP_PREF_OUTPUT_PATH";

  static ArrayList <DataTreeVisualization> visualizations = new ArrayList<DataTreeVisualization>();
  static Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

private static class MainMenu extends JFrame {
  ColoredButton inputButton = ColoredButton.newMenuButton( "Input");
  ColoredButton parseButton = ColoredButton.newMenuButton( "Parse");
  ColoredButton filterButton = ColoredButton.newMenuButton( "Filter");
  ColoredButton saveButton= ColoredButton.newMenuButton("Save");
  private final Object lock = new Object();
  private final Object filterLock = new Object();
  private final Object parseLock = new Object();
  MainMenu() {
    setTitle("LParser");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

    LinkedList<Object> a=new LinkedList<Object>();
    a.add(0.3);
    a.add(0.3);

    //JMenuBar menuBar= new JMenuBar();
    //JMenuItem debugItem;
    //setJMenuBar(menuBar);
    //JMenu debugMenu = new JMenu("Debug");
    //debugMenu.setForeground(Color.GRAY);
    //debugItem = debugMenu.add("Reload Scripts");
    //debugItem.setForeground(Color.GRAY);
    //debugItem.setToolTipText("This is useless shit if you are an user.");
    //menuBar.add(debugMenu);
    JBackgroundPanel mainPanel = JBackgroundPanel.newMainMenuPanel();
    add(mainPanel);

    ColoredButton buttons[] = {inputButton, parseButton, filterButton, saveButton};
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.Y_AXIS));
    buttonPanel.add(Box.createRigidArea(new Dimension(0,15)));
    for (JButton button : buttons){
      buttonPanel.add(Box.createRigidArea(new Dimension(0,5)));
      buttonPanel.add(button);
      buttonPanel.add(Box.createRigidArea(new Dimension(0,5)));
//      button.setPreferredSize(new Dimension(120, 30));
      button.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    }
    buttonPanel.add(Box.createRigidArea(new Dimension(0,15)));
    mainPanel.add(buttonPanel);
    pack();
    setSize(225,240);
    //setSize(getSize().width+100,getSize().height);
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
          Parser.reset();
          inputButton.setEnabled(false);
          parseButton.setEnabled(false);
          saveButton.setEnabled(false);
          filterButton.setEnabled(false);
          input_paths = IOManager.getFileList(mainFrame, PREF_INPUT_PATH_FILE, "Define input file(s)/dir(s).", IOManager.FileType.ALL, JFileChooser.OPEN_DIALOG);
          if(input_paths == null || input_paths.length == 0){
            IOManager.asWarning("No valid input specified!");
            inputButton.setEnabled(true);
            return;
          }
          if (output_path != null ) parseButton.setEnabled(true);

        } catch (java.lang.NoClassDefFoundError e2){
          IOManager.asError("jar file missing: check if there is a 'lib' dir containing a file named 'org.apache.commons.io.FilenameUtils.jar'");
          System.exit(-1);
        }
        inputButton.setEnabled(true);
        parseButton.setEnabled(true);
        mainFrame.getRootPane().setDefaultButton(parseButton);
        parseButton.requestFocus();
      }
    });

    parseButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e){
        Parser.reset();

        synchronized (parseLock) {
          parseLock.notify();
        }
        parseButton.setEnabled(false);
        //getRootPane().setDefaultButton(saveButton);
        //saveButton.requestFocus();
        mainFrame.getRootPane().setDefaultButton(filterButton);
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
        saveData();
        saveButton.setEnabled(true);
        IOManager.openOutputDir(output_path);
      }

    });

    //debugItem.addActionListener(new ActionListener() {
    //  @Override
    //  public void actionPerformed(ActionEvent e) {
    //    PlotHelper.delete_pyscripts(PlotHelper.PlotScript1);
    //    PlotHelper.delete_pyscripts(PlotHelper.PlotScript2);
    //  }
    //});


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
            inputButton.setEnabled(false);
            filterButton.setEnabled(false);
            saveButton.setEnabled(false);
            boolean successful = parseLogs(input_paths);
            parseButton.setEnabled(true);
            inputButton.setEnabled(true);
            if(successful) {
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

  static boolean parseLogs(File[] input_paths){
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
        if (!parseLogs(Objects.requireNonNull(fileEntry.listFiles()))) return false;
      } else {
        IOManager.asWarning("Skipping strange file: " + fileEntry.getAbsolutePath());
      }

    }
  return true;

  }
  static void saveData() {
    String output_path = LoggerParser.output_path.getAbsolutePath();
    if(new File(output_path).isDirectory()){
      output_path = output_path + File.separator + "log.txt";
    }
    Parser.writeLogInfo(output_path);
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
