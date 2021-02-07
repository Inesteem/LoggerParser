package src.parser;
import src.gui.Column;
import src.types.*;

import java.util.prefs.Preferences;
import javax.swing.*;

public class ImpulsFormat
extends LogFormat {

  public static final String PREF_MM = "LP_PREF_IMPULS_MM";
  public static final String PREF_ALL = "LP_PREF_IMPULS_ALL";
  public static final String PREF_STR = "LP_PREF_IMPULS";
  public static final String TITLE_STR= "Millimeter";
  public static final String IMPULS_KEY = "mms";

  Double[] impuls_mms = {0.2,0.5,0.8,1.0};
  //num_elements: counts impulses > 0
  //num_measurements: counts all (even 0 values)
  public double mm;


  public ImpulsFormat(){
    super(Parser.ParserType.IMPULS, PREF_ALL);
    valuePanels.add(new ValuePanel(TITLE_STR, PREF_STR, 10, 0,1000, false));

    columns.add(new Column(IMPULS_KEY, 0, 100 , 2, false, calendar, Data.RAIN));
  }

  void preprocess(String[] data){}
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
    if(line.length != 4) return false;
    if(!line[2].contains("Impuls")) return false;
    return true;
  }

  public String get_value_header() {
    if(mm == 1){return "impuls";}
    return "mm";
  }


} //end class



