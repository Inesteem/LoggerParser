package src.parser;
import src.datatree.*;
import src.types.*;
import src.gui.*;

import java.awt.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.Date;
import java.util.HashMap;

import javax.swing.*;


public class Parser extends JFrame {

  static JLabel loadLabel;
  static Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

  String[] options = {"keep key", "override old key", "always keep", "always override","abort operation"};
  static ParserType p_type = ParserType.NONE;
  static LogFormat l_format;
  boolean windowClosed = false;
  //recognize duplicated entries
  HashMap<Date,String> RainPerDate;
  static DataTree dataMaps[][] = new DataTree[Method.SIZE.value()][Data.SIZE.value];

  public enum ParserType {
    NONE, IMPULS, MOMENT_VALS, REL_HUM, REL_HUM_VOLT, WITH_FOG, REL_HUM_WIND, OTHER
  }

  public static void reset() {
    p_type = ParserType.NONE;
    dataMaps = new DataTree[Method.SIZE.value()][Data.SIZE.value];
  }
  public Parser(){
    setTitle("Parse Log-Files");
    setVisible(false);
    RainPerDate = new HashMap<Date,String>();

    JPanel mainPanel = new JPanel(){
      @Override
      public boolean isOptimizedDrawingEnabled() {
        return false;
      }
    };
    mainPanel.setLayout(new OverlayLayout(mainPanel));
    add(mainPanel);

    ImageIcon bg= IOManager.loadLGIcon("test.jpg");
    JLabel background= new JLabel("",bg, JLabel.CENTER);

    background.setAlignmentX(0.5f);
    background.setAlignmentY(0.5f);

    ImageIcon loadGif = IOManager.loadLGIcon("load3.gif");
    assert(loadGif != null);
    loadLabel = new JLabel("", loadGif, JLabel.CENTER);
//    loadGif.setImageObserver(loadLabel);
    //loadLabel.setOpaque(true);
    Font f = loadLabel.getFont();
    loadLabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
    loadLabel.setAlignmentX(0.5f);
    loadLabel.setAlignmentY(0.5f);

    mainPanel.add(loadLabel);
    mainPanel.add(background);

    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    setSize(400, 300);
    //setBounds(20,20,600,100);
    setLocation(dim.width/2-getSize().width/2, dim.height/2-getSize().height/2);

    ImageIcon appIcon = IOManager.loadLGIcon("icon.png");
    if(appIcon != null) {
      setIconImage(appIcon.getImage());
    }

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e)
      {
        windowClosed = true;
      }
    });
  }

  public void setLogFormat(LogFormat lf){
    l_format = lf;
    //p_type = lf.get_parser_type();
  }

  public static DataTreeVisualization doVisualize() throws InterruptedException {
    return l_format.doVisualize();
  }
  public static void updateVisualization(DataTreeVisualization dtr ) {
    l_format.updateVisualization(dtr);
  }

  public static DataTree getDataMap(Method method, Data data, Limits limits) {

    if(dataMaps[method.value()][data.value]== null){
      dataMaps[method.value()][data.value] = new DataTree(limits);
    } else {
      dataMaps[method.value()][data.value].set_limits(limits);
    }
    return dataMaps[method.value()][data.value];
  }

  public static void write_log_info(String filename){
    try{
      l_format.write_to_file(filename);
    } catch(IOException e) {
      e.printStackTrace();
    }
  }

  public boolean parse(File file){
    //content.setText("<html><b><center>Parsing File:<center/><b/><br/>"+file.getName()+"</html>");
    String line="";

    try {

      FileReader fileReader = new FileReader(file);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      ParserType type = ParserType.NONE;
      while ((line = bufferedReader.readLine()) != null && !windowClosed) {
        String splitted[] = line.split("\\s+");
        if(ImpulsFormat.matches(splitted)){
          setLogFormat((LogFormat) new ImpulsFormat());
          type = ParserType.IMPULS;
          break;
        } else if(TempRelHumFormat.matches(splitted)){
          setLogFormat((LogFormat) new TempRelHumFormat());
          type = ParserType.REL_HUM;
          break;
        } else if(WithFogFormat.matches(splitted)){
          setLogFormat((LogFormat) new WithFogFormat());
          type = ParserType.WITH_FOG;
          break;
        } else if(TempRelHumVoltageFormat.matches(splitted)){
          setLogFormat((LogFormat) new TempRelHumVoltageFormat()  );
          type = ParserType.REL_HUM_VOLT;
          break;
        } else if(TempRelHumWindFormat.matches(splitted)){
          setLogFormat((LogFormat) new TempRelHumWindFormat());
          type = ParserType.REL_HUM_WIND;
          break;
        }
      }
      if ( type == ParserType.NONE ) {
        IOManager.asError("logger format unsupported : " + file.getAbsolutePath());
      }
      //file append support; check if file types match
      //TODO: support merging of different formats?
      if(p_type == ParserType.NONE){
        p_type = type;
      } else if(p_type != type) {
        System.out.println("File types do not match! Aborting.");
        IOManager.asWarning("File types do not match! Aborting.");
        return false;
      }

      l_format.configure(file.getName());
      setVisible(true);

      boolean keep_all = false;
      boolean override_all = false;
      boolean dub_lines = false;

      while ((line = bufferedReader.readLine()) != null && !windowClosed) {
        String splitted[] = line.split("\\s+");
        //System.out.println("trying to parse: " + splitted[0] + " " + splitted[1] + " with val " + line);

        Date date = l_format.get_date(splitted);

        //Dublicated Entry Handling
        if (RainPerDate.containsKey(date) && !override_all) {
          String old_val = RainPerDate.get(date);

          if(line.equals(old_val)){
            System.out.println("dublicated line: " + line);
            dub_lines = true;
            continue;
          }

          if(keep_all){
            continue;
          }

          String key_str = l_format.date_format.format(date);

          int opt = IOManager.askNOptions(this, "entry already exists", options,
                  "date:\n "+key_str+"\nold:\n "+old_val+"\nnew:\n "+line);
          //int opt = iom.askTwoOptions("test1", "keep new key", "override old key", 
          //"the entry with date >" +key +"< already exists; exit dialog to abort");
          if(opt == 0){
            System.out.println("keep");
            continue;
          } else if (opt==1){
            System.out.println("override");
          } else if (opt==2){
            System.out.println("always keep");
            keep_all = true;
          } else if (opt==3){
            System.out.println("always override");
            override_all = true;
          } else {
            System.exit(0);
          }
        }

        //      System.out.println("date: " + date + " " + key_str);
        //content.setText("<html><b><center>Parsing Line:<center/><b/><br/>"+line+"</html>");
        l_format.set_values(splitted);
        RainPerDate.put(date,line);
      }
      //if (dub_lines) {
      //	iom.asWarning("some or all lines you've tried to add were already in the choosen append file");
      //}
      fileReader.close();
    } catch (IOException e) {
      setVisible(false);
      e.printStackTrace();
      return false;
    }
    setVisible(false);
    return !windowClosed;
  }

}
