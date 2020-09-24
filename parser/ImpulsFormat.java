package parser;
import javafx.util.Pair;
import java.util.Arrays;
import java.lang.NumberFormatException;
import java.lang.Number;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;

import java.lang.Thread;

import java.util.prefs.Preferences;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.lang.InterruptedException;


public class ImpulsFormat
extends LogFormat {

  public static final String PREF_MM = "LP_PREF_IMPULS_MM";
  public static final String PREF_ALL = "LP_PREF_IMPULS_ALL";
  public static final String PREF_STR = "LP_PREF_IMPULS";

  static String impulse_type1[] = {"Date", "Time", "Impulses", "[]"};
  static String impulse_type2[] = {"Datum", "Zeit", "Impulse", "[]"};
  static String impulse_type3[] = {"Datum", "Zeit", "1.Impulse", "[]"};
  static String impulse_type4[] = {"Date", "Time", "1.Impulses", "[]"};


  Double[] impuls_mms = {0.2,0.5,1.0};
  //num_elements: counts impulses > 0
  //num_measurements: counts all (even 0 values)
  double mm;


  public ImpulsFormat(){
    super(Parser.ParserType.IMPULS, PREF_ALL);
    val_panels.add(new ValuePanel("Millimeter", PREF_STR, 10, 0,1000, false));
  }

  public void configure(String file_name){

    Preferences pref = Preferences.userRoot();
    double pref_mm = pref.getDouble(PREF_MM, impuls_mms[0]);
    JComboBox<Double> mm_select= new JComboBox<Double>(impuls_mms);
    mm_select.setMaximumSize(mm_select.getPreferredSize() );
    mm_select.setSelectedItem(pref_mm);
    mm_select.setEditable(true);

    JPanel panelMMs = new JPanel();
    panelMMs.add(new JLabel("Millimeter per impuls: "));
    panelMMs.add(mm_select);

    super.configure(file_name, panelMMs);
    mm=(Double) mm_select.getSelectedItem();
    pref.putDouble(PREF_MM,mm);

  } 

  public static boolean matches(String[] line){
    if(Arrays.equals(impulse_type1,line) || Arrays.equals(impulse_type2,line)){
      return true;
    }
    if(Arrays.equals(impulse_type3,line) || Arrays.equals(impulse_type4,line)){
      return true;
    }
    return false;
  }

  public boolean get_values(String[] data, List<Double> values){
    if ( data.length != 3){
      return false;
    }   
    double val = Double.parseDouble(data[2]);
    if (val != 0.0 && val_panels.get(0).isAllowed(val)){
      values.add(val);
    }
    return true;
  }


  public Pair<int[], double[]> get_month_val(Month m) {
    double avg[] = new double[1];
    int meas[] = new int[1];
    int num_elements = 0;
    for (int h = 0; h < 24; ++h) {
      if(m.hours[h] == null) { continue; }
      //for (int i = 0; i < hours[h].size(); ++i) {
      //		avg += hours[h].get(i);
      //}
      for (double v : m.hours[h]){
        double tmp = v * mm;
        avg[0] += tmp;
      }
      num_elements += m.hours[h].size();
      meas[0]+= m.measurements[h];
    }

    return new Pair<int[],double[]>(meas, avg);
  }

  public String get_value_header() {
    if(mm == 1){return "impuls num_meas";}
    return "mm num_meas";
  }

}



