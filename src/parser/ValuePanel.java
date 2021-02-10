package src.parser;
import src.gui.*;
import src.types.Data;

import java.util.prefs.Preferences;
import javax.swing.*;
import java.awt.*;

public class ValuePanel extends JPanel{
  JDoubleField min_field;
  JDoubleField max_field;

  JCheckBox use_col_box;
  String min_pref;
  String minD_pref;
  String minM_pref;
  String max_pref;
  String use_pref;
  String meas_pref;

  public ValuePanel(Data data, String pref_str, int len, double min, double max){

    Preferences pref = Preferences.userRoot();
    min_pref = pref_str + "_MIN";
    minD_pref = pref_str + "_MIND";
    minM_pref = pref_str + "_MINY";
    max_pref = pref_str + "_MAX";
    use_pref = pref_str + "_USE";
    meas_pref = pref_str + "_MEAS";

    double val_min = pref.getDouble(min_pref, min);
    min_field = new JDoubleField(len);
    min_field.setName(data.name + "_min");
    min_field.setValue(val_min);
    min_field.setToolTipText("min. allowed value for a single valid measurement point (incl.)");

    double val_max = pref.getDouble(max_pref, max);
    max_field = new JDoubleField(len);
    max_field.setName(data.name + "_max");
    max_field.setValue(val_max);
    max_field.setToolTipText("max. allowed value for a single valid measurement point (incl.)");

    boolean use = pref.getBoolean(use_pref, true);
    use_col_box = new JCheckBox("use", use);
    use_col_box.setToolTipText("use log values belonging to this column for this file(s)");

    JPanel val_panel = new JPanel();
    val_panel.setLayout(new BoxLayout(val_panel, BoxLayout.X_AXIS));

    val_panel.add(Box.createRigidArea(new Dimension(20,0)));
    val_panel.add(new JLabel("min: "));
    val_panel.add(Box.createRigidArea(new Dimension(10,0)));
    val_panel.add(min_field);
    val_panel.add(Box.createRigidArea(new Dimension(20,0)));
    val_panel.add(new JLabel("max: "));
    val_panel.add(Box.createRigidArea(new Dimension(10,0)));
    val_panel.add(max_field);
    val_panel.add(Box.createRigidArea(new Dimension(20,0)));

    val_panel.add(Box.createRigidArea(new Dimension(20,0)));

    val_panel.add(use_col_box);
    val_panel.add(Box.createRigidArea(new Dimension(20,0)));

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    JLabel title_l = new JLabel(data.description);
    title_l.setAlignmentX(Component.CENTER_ALIGNMENT);
    title_l.setAlignmentY(Component.CENTER_ALIGNMENT);
    add(title_l);
    add(Box.createRigidArea(new Dimension(0,10)));
    add(new JSeparator());
    add(Box.createRigidArea(new Dimension(0,5)));
    add(val_panel);
    add(Box.createRigidArea(new Dimension(0,5)));
    add(new JSeparator());
    add(Box.createRigidArea(new Dimension(0,5)));
  }

  public double getMin(){
    return min_field.getValue();
  }

  public double getMax(){
    return max_field.getValue();
  }

  public boolean useVal(){
    return use_col_box.isSelected();
  }

  public boolean valid(){
    return (getMin() >= 0) && (getMax() >= 0);
  }

  public void updatePrefs(){
    Preferences pref = Preferences.userRoot();
    pref.putDouble(min_pref,getMin());
    pref.putDouble(max_pref,getMax());
    pref.putBoolean(use_pref,useVal());
  }

}
