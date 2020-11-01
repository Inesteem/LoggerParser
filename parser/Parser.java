package parser;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.time.YearMonth;
import javafx.util.Pair;

import java.lang.NumberFormatException;
import java.lang.Number;

import javax.swing.JLabel;
import javax.swing.JFrame;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import timeunits.*;
import plotting.*;


public class Parser {
  static DecimalFormat df;	


//  String moment_vals[]   = {"Date", "Time", "Resistance", "[Ohm]", "Current", "[mA]"};
  String[] options = {"keep key", "override old key", "always keep", "always override","abort operation"};
  ParserType p_type;
  LogFormat l_format;	
	HashMap<Date,String[]> RainPerDate;
  static YearMap dataMaps[][];


  public enum ParserType {
    NONE, IMPULS, MOMENT_VALS, REL_HUM, WITH_FOG, OTHER
  }

  public Parser(){
    p_type = ParserType.NONE;
		RainPerDate = new HashMap<Date,String[]>();
    if (dataMaps == null)
      dataMaps = new YearMap[Method.SIZE.value()][PlotData.SIZE.value()];
  }

  public void setLogFormat(LogFormat lf){
    l_format = lf;
    p_type = lf.get_parser_type();
  }
  
  public static YearMap getDataMap(Method m, PlotData pd, Limits limits) {
    
    if(dataMaps[m.value()][pd.value()]== null){
      dataMaps[m.value()][pd.value()] = new YearMap(limits);
    } else {
      dataMaps[m.value()][pd.value()].set_limits(limits);
    }
    return dataMaps[m.value()][pd.value()];
  }

  public static void plot(){
    //TODO
    for(Method m : Method.values()){
        int midx = m.value();
        if(midx >= dataMaps.length) continue;
      for(PlotData pd : PlotData.values()){
        int idx = pd.value();
        if(idx >=dataMaps[midx].length || dataMaps[midx][idx] == null) continue;
        PlotWindow pw = new PlotWindow();
        pw.run(null, pd, m, dataMaps[midx][idx]);
      }
   }
 }


  public void write_log_info(String filename){

    IOManager iom = IOManager.getInstance();
//    iom.create_temp_copy(filename, calendar);
    FileOutputStream outputStream; 
    try{
      outputStream = new FileOutputStream(filename);
      l_format.write_to_file(outputStream);
      outputStream.close();
    } catch(IOException e) {
      e.printStackTrace();
    }

  }
  public boolean parse(File file, JLabel label, JFrame frame){

    IOManager iom = IOManager.getInstance();

    String line="";

    try {

      FileReader fileReader = new FileReader(file);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      ParserType type = ParserType.NONE;
      while ((line = bufferedReader.readLine()) != null) {
        String splitted[] = line.split("\\s+");
        if(ImpulsFormat.matches(splitted)){
          type = ParserType.IMPULS;
          break;
        } else if(TempRelHumFormat.matches(splitted)){
          type = ParserType.REL_HUM;
          break;
        } else if(WithFogFormat.matches(splitted)){
          type = ParserType.WITH_FOG;
          break;
        }
      }

      //file append support; check if file types match
      if(p_type == ParserType.NONE){
        p_type = type;
        if ( p_type == ParserType.REL_HUM) {
          setLogFormat((LogFormat) new TempRelHumFormat());
        } else if ( p_type == ParserType.IMPULS) {
          setLogFormat((LogFormat) new ImpulsFormat());
        } else if ( p_type == ParserType.WITH_FOG) {
          setLogFormat((LogFormat) new WithFogFormat());
        } else {
          iom.asError("logger format unsupported : " + file.getAbsolutePath());
        }
      } else if(p_type != type) {
        System.out.println("File types do not match! Aborting.");
        iom.asWarning("File types do not match! Aborting.");
        return false;

      } 

      l_format.configure(file.getName());

      boolean keep_all = false;
      boolean override_all = false;
      boolean dub_lines = false;

      while ((line = bufferedReader.readLine()) != null) {
        String splitted[] = line.split("\\s+");
//        System.out.println("trying to parse: " + splitted[0] + " " + splitted[1] + " with val " + line);

        //long key = calendar.getTimeInMillis();


        Date date = l_format.get_date(splitted);
        String key_str = l_format.date_format.format(date);
  //      System.out.println("date: " + date + " " + key_str);
        label.setText("<html><b><center>Parsing Line:<center/><b/><br/>"+line+"</html>");

        if (RainPerDate.containsKey(date) && !override_all) {
          String old_val[] = RainPerDate.get(date);

          if(Arrays.equals(old_val, splitted)){
            System.out.println("dublicated line: " + line);
            dub_lines = true;
            continue;
          } 

          if(keep_all){
            continue;
          }


          int opt = iom.askNOptions("entry already exists", options,
              "date:\n "+key_str+"\nold:\n "+Arrays.toString(old_val)+"\nnew:\n "+line);
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

      
        l_format.set_values(splitted);

//        for(String str : splitted){
//          System.out.print(str + " ");
//          }
//          System.out.println("");

        RainPerDate.put(date,splitted);
      }
      //if (dub_lines) {
      //	iom.asWarning("some or all lines you've tried to add were already in the choosen append file");
      //}
      fileReader.close();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    } 
    return true;
  }

}
