package parser;
import java.util.prefs.Preferences;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;



public class ValuePanel extends JPanel{


  JIntField minD_field;
  JIntField minM_field;
  JDoubleField min_field;
  JDoubleField max_field;

  JCheckBox use_col_box;
//  JCheckBox use_meas_box;
  String min_pref;
  String minD_pref;
  String minM_pref;
  String max_pref;
  String use_pref;
  String meas_pref;

  public ValuePanel(String title, String pref_str, int len, double min, double max, boolean check_use){

    Preferences pref = Preferences.userRoot();
    min_pref = pref_str + "_MIN";
    minD_pref = pref_str + "_MIND";
    minM_pref = pref_str + "_MINY";
    max_pref = pref_str + "_MAX";
    use_pref = pref_str + "_USE";
    meas_pref = pref_str + "_MEAS";

    double val_min = pref.getDouble(min_pref, min);
    min_field = new JDoubleField(len);
    min_field.setName(title + "_min");
    min_field.setValue(val_min);
    double val_max = pref.getDouble(max_pref, max);
    max_field = new JDoubleField(len);
    max_field.setName(title + "_max");
    max_field.setValue(val_max);

    int val_minD = pref.getInt(minD_pref, 0);
    minD_field = new JIntField(len);
    minD_field.setName(title + "_minD");
    minD_field.setValue(val_minD);

    int val_minM = pref.getInt(minM_pref, 0);
    minM_field = new JIntField(len);
    minM_field.setName(title + "_minM");
    minM_field.setValue(val_minM);


    boolean use= pref.getBoolean(use_pref, true);
    use_col_box = new JCheckBox("use", use);
    use_col_box.setToolTipText("log values belonging to this column");

//    boolean meas= pref.getBoolean(meas_pref, true);
//    use_meas_box = new JCheckBox("meas", meas);
//    use_meas_box.setToolTipText("log number of measurements for this column");

    JPanel valid_panel = new JPanel();
    valid_panel.setLayout(new BoxLayout(valid_panel, BoxLayout.X_AXIS));

    valid_panel.add(Box.createRigidArea(new Dimension(20,0)));
    valid_panel.add(new JLabel("minHours: "));
    valid_panel.add(Box.createRigidArea(new Dimension(10,0)));
    valid_panel.add(minD_field);
    valid_panel.add(Box.createRigidArea(new Dimension(20,0)));
    valid_panel.add(new JLabel("minDays: "));
    valid_panel.add(Box.createRigidArea(new Dimension(10,0)));
    valid_panel.add(minM_field);
    valid_panel.add(Box.createRigidArea(new Dimension(20,0)));




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


    //val_panel.add(use_meas_box);
    val_panel.add(Box.createRigidArea(new Dimension(20,0)));

    if (check_use) {
      val_panel.add(use_col_box);
      val_panel.add(Box.createRigidArea(new Dimension(20,0)));
    }

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

   // add(Box.createRigidArea(new Dimension(0,5)));
    JLabel title_l = new JLabel(title);
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
    add(valid_panel);
    add(Box.createRigidArea(new Dimension(0,5)));
    add(new JSeparator());
  }

  public int getMinD(){
    return minD_field.getValue();
  }

  public int getMinM(){
    return minM_field.getValue();
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

  public boolean isAllowed(double val){
    System.out.println(getMin() + " <= " + val + " <= " + getMax());
    return useVal() && (getMin() <= val) && (getMax() >= val);
  }

  public void updatePrefs(){
    Preferences pref = Preferences.userRoot();
    pref.putDouble(min_pref,getMin());
    pref.putDouble(max_pref,getMax());
    pref.putInt(minD_pref,getMinD());
    pref.putInt(minM_pref,getMinM());
    pref.putBoolean(use_pref,useVal());
    ////pref.putBoolean(meas_pref,useMeas());
  }

}
