package src.parser;

import src.gui.Column;
import src.gui.IOManager;
import src.types.Data;
import src.types.ParserType;

import javax.swing.*;
import java.util.Date;
import java.util.prefs.Preferences;

import static src.types.Data.TEMP;

public class HoboFormat
extends LogFormat {

  public static final String PREF_MM = "LP_PREF_HOBO_MM";
  public static final String PREF_ALL = "LP_PREF_HOBO_ALL";
  public static final String PREF_STR = "LP_PREF_HOBO";
  double last = 0;
  Double[] impuls_mms = {0.2,0.5,0.8,1.0};
  //num_elements: counts impulses > 0
  //num_measurements: counts all (even 0 values)
  public double mm;


  public HoboFormat(){
    super(ParserType.HOBO, PREF_ALL);
    valuePanels.add(new ValuePanel(TEMP     ,PREF_STR+"_TEMP", 11, 0, 100));
    valuePanels.add(new ValuePanel(Data.RAIN, PREF_STR, 10, 0,1000));

    columns.add(new Column(2, true, calendar, Data.TEMP));
    columns.add(new Column(3, false, calendar, Data.RAIN));
    regex = ";";
  }

  void preprocess(String[] data){
    if(data.length == 4) {
      Column col = columns.get(1);
      double val = col.parse_val(data);
      data[col.get_pos()] = ""+(val-last);
      last = val;
    }
  }
  public void configure(String file_name){
    Preferences pref = Preferences.userRoot();
    double pref_mm = pref.getDouble(PREF_MM, impuls_mms[0]);
    JComboBox<Double> mm_select= new JComboBox<Double>(impuls_mms);
    mm_select.setMaximumSize(mm_select.getPreferredSize() );
    mm_select.setSelectedItem(pref_mm);
    mm_select.setEditable(true);

    mm_select.setName(Data.RAIN.unit + "_hobo_val");

    JPanel panelMMs = new JPanel();
    panelMMs.add(new JLabel("Millimeter per impuls: "));
    panelMMs.add(mm_select);

    super.configure(file_name, panelMMs);
    mm=(Double) mm_select.getSelectedItem();
    Column col = columns.get(1);
    col.mul = mm;
    pref.putDouble(PREF_MM,mm);
  }

  public static boolean matches(String[] line){
    if (line.length != 4) {
      IOManager.asError("Either your hobo format is wrong or not supported. Chose as delimiter ; , only use temperature and event in this order and use the data format : dd.MM.yy HH.mm.ss, both separated by ;" );

    }
    return line[0].contains("\"");
  }

} //end class



