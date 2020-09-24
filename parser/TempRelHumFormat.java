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


public class TempRelHumFormat
extends LogFormat {
  public static final String PREF_ALL = "LP_PREF_TRH_ALL";
  public static final String PREF_STR = "LP_PREF_TRH";

  static String rel_hum1[]       = {"Datum", "Zeit", "1.Temperatur", "[°C]", "2.rel.Feuchte", "[%]"};	
  static String rel_hum2[]       = {"Date", "Time", "1.Temperature", "[DegC]", "2.rel.Humidity", "[%]"};	
  public TempRelHumFormat(){
    super(Parser.ParserType.REL_HUM, PREF_ALL);
    val_panels.add(new ValuePanel("Temperature", PREF_STR+"_TEMP", 10, 0, 100, true));
    val_panels.add(new ValuePanel("Relative Humidity", PREF_STR+"_RH", 10, 0, 42, true));
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

  public boolean get_values(String[] data, List<Double> values){
    if ( data.length != 4){
      return false;
    }

    double temp = -1;
    double relH = -1;
    try {
      temp= Double.parseDouble(data[2]);
      relH = Double.parseDouble(data[3]);
    } catch (NumberFormatException e){
      try {
        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
        Number number = format.parse(data[2]);
        temp= number.doubleValue();
        number = format.parse(data[3]);
        relH= number.doubleValue();
      } catch (ParseException e2){
        e2.printStackTrace();
        IOManager.getInstance().asError("double parse exception with " + data[2]+ " or " + data[3]);
      }
    }

    if (val_panels.get(0).isAllowed(temp)){
      values.add(temp);

    } else {
      System.out.println("not allowed TEMP: " + temp);
      values.add(-1.0);
    }
    if (val_panels.get(1).isAllowed(relH)){
      values.add(relH);
    } else {
      System.out.println("not allowed RELH: "+ relH);
      values.add(-1.0);
      }
    return true;
  }


  public Pair<int[], double[]> get_month_val(Month m) {
    double sum[] = new double[2];
    int num_measures[]= {0,0};
    for (int h = 0; h < 24; ++h) {
      if(m.hours[h] == null) { continue; }
      //for (int i = 0; i < hours[h].size(); ++i) {
      //		avg += hours[h].get(i);
      //}
      for (int e = 0; e < m.hours[h].size(); ++e){
        double tmp = m.hours[h].get(e);
        if(tmp == -1) continue;
        sum[e%2] += tmp;
        ++num_measures[e%2];
      }
    }
    if(num_measures[0] != 0){
      sum[0] /= num_measures[0];
    }
    if(num_measures[1] != 0){
      sum[1] /= num_measures[1];
    }
    return new Pair<int[],double[]>(num_measures, sum);
  }

  public String get_value_header() {
    return "temp num_meas rel_hum num_meas";
  }

}

