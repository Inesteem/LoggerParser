package parser;
import java.util.ArrayList;
import java.util.Arrays;
import java.text.ParseException;
import java.lang.NumberFormatException;
import java.lang.Number;


import java.util.prefs.Preferences;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import javafx.util.Pair;

import java.io.FileOutputStream;
import java.io.IOException;

public class TempRelHumFormat
extends LogFormat {
  public static final String PREF_ALL = "LP_PREF_TRH_ALL";
  public static final String PREF_STR = "LP_PREF_TRH";

  static String rel_hum1[]       = {"Datum", "Zeit", "1.Temperatur", "[°C]", "2.rel.Feuchte", "[%]"};	
  static String rel_hum2[]       = {"Date", "Time", "1.Temperature", "[DegC]", "2.rel.Humidity", "[%]"};	


  static String TEMP_KEY = "temp";
  static String RHUM_KEY = "rhum";

  public TempRelHumFormat(){
    super(Parser.ParserType.REL_HUM, PREF_ALL);
    val_panels.add(new ValuePanel("Temperature", PREF_STR+"_TEMP", 10, 0, 100, true));
    val_panels.add(new ValuePanel("Relative Humidity", PREF_STR+"_RH", 10, 0, 42, true));

    columns.add(new Column(TEMP_KEY, 0, 100 , 2, true, calendar));
    columns.add(new Column(RHUM_KEY, 0, 100 , 3, true, calendar));
  }

  public void configure(String file_name){
    configure(file_name, null); 
  }

  public static boolean matches(String[] line){
    if(Arrays.equals(rel_hum1,line) || Arrays.equals(rel_hum2,line)){
      return true;
    }
    return false;
  }

  public String get_value_header() {
    return "temp num_meas rel_hum num_meas";
  }

}

