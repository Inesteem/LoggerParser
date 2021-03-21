package src.parser;
import src.gui.Column;
import src.types.*;

import java.util.prefs.Preferences;
import javax.swing.*;

import static src.types.Data.RAIN;

public class ImpulsFormat
extends LogFormat {

  public static final String PREF_MM = "LP_PREF_IMPULS_MM";
  public static final String PREF_STR = "LP_PREF_IMPULS";

  static Data data_types[] = {RAIN};
  Double[] impuls_mms = {0.2,0.5,0.8,1.0};
  //num_elements: counts impulses > 0
  //num_measurements: counts all (even 0 values)
  public double mm;

  public ImpulsFormat(){
    super(ParserType.IMPULS, PREF_STR, data_types);
  }

  void preprocess(String[] data){}

  void postprocess(String[] data){}

  public void configure(String file_name){
    Preferences pref = Preferences.userRoot();
    double pref_mm = pref.getDouble(PREF_MM, impuls_mms[0]);
    JComboBox<Double> mm_select= new JComboBox<Double>(impuls_mms);
    mm_select.setMaximumSize(mm_select.getPreferredSize() );
    mm_select.setSelectedItem(pref_mm);
    mm_select.setEditable(true);

    mm_select.setName(Data.RAIN.unit + "_val");

    JPanel panelMMs = new JPanel();
    panelMMs.add(new JLabel("Millimeter per impuls: "));
    panelMMs.add(mm_select);

    super.configure(file_name, panelMMs);
    mm=(Double) mm_select.getSelectedItem();
    Column col = columns.get(0);
    col.mul = mm;
    pref.putDouble(PREF_MM,mm);
  }

  public boolean matches(String[] line){
    if(line.length != 4) return false;
    if(!line[2].contains("Impuls")) return false;
    return true;
  }

} //end class



