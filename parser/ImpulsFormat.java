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
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.GregorianCalendar;

import java.util.HashMap;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImpulsFormat
extends LogFormat {

  public static final String PREF_MM = "LP_PREF_IMPULS_MM";
  public static final String PREF_ALL = "LP_PREF_IMPULS_ALL";
  public static final String PREF_STR = "LP_PREF_IMPULS";
  public static final String TITLE_STR= "Millimeter";
  public static final String IMPULS_KEY = "mms";

  static String impulse_type1[] = {"Date", "Time", "Impulses", "[]"};
  static String impulse_type2[] = {"Datum", "Zeit", "Impulse", "[]"};
  static String impulse_type3[] = {"Datum", "Zeit", "1.Impulse", "[]"};
  static String impulse_type4[] = {"Date", "Time", "1.Impulses", "[]"};
  

  Double[] impuls_mms = {0.2,0.5,0.8,1.0};
  //num_elements: counts impulses > 0
  //num_measurements: counts all (even 0 values)
  public double mm;


  public ImpulsFormat(){
    super(Parser.ParserType.IMPULS, PREF_ALL);
    val_panels.add(new ValuePanel(TITLE_STR, PREF_STR, 10, 0,1000, false));

    columns.add(new Column(IMPULS_KEY, 0, 100 , 2, false, calendar));
  }

  public void configure(String file_name){

    Preferences pref = Preferences.userRoot();
    double pref_mm = pref.getDouble(PREF_MM, impuls_mms[0]);
    JComboBox<Double> mm_select= new JComboBox<Double>(impuls_mms);
    mm_select.setMaximumSize(mm_select.getPreferredSize() );
    mm_select.setSelectedItem(pref_mm);
    mm_select.setEditable(true);

    mm_select.setName(TITLE_STR + "_val");

    JPanel panelMMs = new JPanel();
    panelMMs.add(new JLabel("Millimeter per impuls: "));
    panelMMs.add(mm_select);

    super.configure(file_name, panelMMs);
    mm=(Double) mm_select.getSelectedItem();
    Column col = columns.get(0);
    col.mul = mm;
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

  public String get_value_header() {
    if(mm == 1){return "impuls \t num_meas";}
    return "mm \t num_meas";
  }


} //end class



