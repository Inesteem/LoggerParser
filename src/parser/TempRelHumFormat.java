package src.parser;

import src.types.*;
import src.gui.*;

public class TempRelHumFormat
extends LogFormat {
  public static final String PREF_ALL = "LP_PREF_TRH_ALL";
  public static final String PREF_STR = "LP_PREF_TRH";

  static String TEMP_KEY = "temp";
  static String RHUM_KEY = "rhum";

  public TempRelHumFormat(){
    super(Parser.ParserType.REL_HUM, PREF_ALL);
    valuePanels.add(new ValuePanel(Data.TEMP,PREF_STR+"_TEMP", 10, 0, 100));
    valuePanels.add(new ValuePanel(Data.HUM, PREF_STR+"_RH", 10, 0, 100));

    columns.add(new Column(TEMP_KEY,  2, true, calendar, Data.TEMP));
    columns.add(new Column(RHUM_KEY,  3, true, calendar, Data.HUM ));
  }

  void preprocess(String[] data){}
  public void configure(String file_name){
    configure(file_name, null); 
  }

  public static boolean matches(String[] line){
    if(line.length != 6) return false;
    if(!line[2].contains("Temperatur")) return false;
    if(!line[4].contains("rel.Humidity") && !line[4].contains("rel.Feuchte")) return false;
    return true;
  }

  public String get_value_header() {
    return "temp rel_hum";
  }

}

