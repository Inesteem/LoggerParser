package src.parser;
import src.datatree.*;
import src.types.*;
import src.gui.*;
import static src.types.ParserType.*;

import java.awt.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

import java.util.Date;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;


public class Parser extends JFrame {

  static JLabel loadLabel;
  static Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

  String[] options = {"keep key", "override old key", "always keep", "always override","abort operation"};
  static ParserType parserType;
  static LogFormat logFormats[] = new LogFormat[ParserType.NONE.value];
  boolean windowClosed = false;
  //recognize duplicated entries
  HashMap<Date,String> RainPerDate;
  static DataTree dataMaps[][];
  static StringBuilder mergedInput =new StringBuilder();

  public static void reset() {
    parserType = ParserType.NONE;
    dataMaps = new DataTree[Method.SIZE.value()][Data.SIZE.value];
    mergedInput = new StringBuilder();
    logFormats[IMPULS.value] = new ImpulsFormat();
    logFormats[REL_HUM.value] =  new TempRelHumFormat();
    logFormats[REL_HUM_VOLT.value] = new TempRelHumVoltageFormat()  ;
    logFormats[REL_HUM_WIND.value] = new TempRelHumWindFormat();
    logFormats[WITH_FOG.value] = new WithFogFormat();
    logFormats[HOBO.value] = new HoboFormat();
    logFormats[WIND_DIR_SPEED_TEMP_HUM_BAR_RAIN.value] = new WindTempHumBarRain();
  }

  public Parser(){
    setTitle("Parse Log-Files");
    setVisible(false);
    RainPerDate = new HashMap<Date,String>();

    JBackgroundPanel mainPanel = new JBackgroundPanel(IOManager.loadLGIcon("test.jpg"), new ColorUIResource(0,30,30));
    add(mainPanel);

    ImageIcon loadGif = IOManager.loadLGIcon("load3.gif");
    loadLabel = new JLabel("", loadGif, JLabel.CENTER);
    loadLabel.setAlignmentX(0.5f);
    loadLabel.setAlignmentY(0.5f);
    mainPanel.add(loadLabel);

    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(400, 300);
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

  public static DataTreeVisualization doVisualize() throws InterruptedException {
    return logFormats[parserType.value].doVisualize();
  }
  public static void updateVisualization(DataTreeVisualization dtr ) {
    logFormats[parserType.value].updateVisualization(dtr);
  }

  public static DataTree getDataMap(Method method, Data data, Limits limits) {

    if(dataMaps[method.value()][data.value]== null){
      dataMaps[method.value()][data.value] = new DataTree(limits);
    } else {
      dataMaps[method.value()][data.value].set_limits(limits);
    }
    return dataMaps[method.value()][data.value];
  }

  public static void writeLogInfo(String filename){
    try{
      logFormats[parserType.value].writeToFile(filename);

      filename = IOManager.addIdToFilename(filename, "raw_data");
      if(IOManager.canWriteToFile(null, filename)) {
        FileOutputStream ostream;
        ostream = new FileOutputStream(filename);
        ostream.write(mergedInput.toString().getBytes());
        ostream.close();
      } else {
        IOManager.asWarning("The merged input data was not saved.");
      }
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
      ParserType type = NONE;
      for(int i = 0; i < 10 && type == NONE && (line = bufferedReader.readLine()) != null && !windowClosed; ++i) {
        if(parserType == NONE) {
          mergedInput.append(line + "\n");
        }
        String splitted[] = line.split("\\s+");
        for(LogFormat logFormat : logFormats) {
          if(logFormat.matches(splitted)) {
            type = logFormat.get_parser_type();
            break;
          }
        }
      }

      if ( type == NONE ) {
        IOManager.asError("Logger format unsupported : " + file.getAbsolutePath());
      } else if ( type == WIND_DIR_SPEED_TEMP_HUM_BAR_RAIN) {
        //TODO: do it smarter
        //skip two lines
        bufferedReader.readLine();
        bufferedReader.readLine();
      }

      //TODO: support merging of different formats?
      if(parserType == NONE){
        parserType = type;
      } else if(parserType != type) {
        System.out.println("File types do not match! Aborting.");
        IOManager.asWarning("File types do not match! Aborting.");
        return false;
      }

      LogFormat logFormat = logFormats[parserType.value];
      logFormat.configure(file.getName());
      setVisible(true);

      boolean keep_all = false;
      boolean override_all = false;
      boolean dub_lines = false;

      while ((line = bufferedReader.readLine()) != null && !windowClosed) {
        String splitted[] = line.split(logFormat.regex);
        //System.out.println("trying to parse: " + splitted[0] + " " + splitted[1] + " with val " + line);

        Date date = logFormat.getDate(splitted);

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

          String key_str = logFormat.date_format.format(date);

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
        if(logFormat.setValues(splitted)) {
          mergedInput.append(String.join("\t\t", splitted)+"\n");
        }
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
