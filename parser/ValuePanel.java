package parser;
import java.util.prefs.Preferences;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;



public class ValuePanel extends JPanel{


  JDoubleField min_field;
  JDoubleField max_field;

  JCheckBox c_box;
  String min_pref;
  String max_pref;
  String use_pref;

  public ValuePanel(String title, String pref_str, int len, double min, double max, boolean check_use){

    Preferences pref = Preferences.userRoot();
    min_pref = pref_str + "_MIN";
    max_pref = pref_str + "_MAX";
    use_pref = pref_str + "_USE";

    double val_min = pref.getDouble(min_pref, min);
    min_field = new JDoubleField(len);
    min_field.setValue(val_min);
    double val_max = pref.getDouble(max_pref, max);
    max_field = new JDoubleField(len);
    max_field.setValue(val_max);

    boolean use= pref.getBoolean(use_pref, true);
    c_box = new JCheckBox("use", use);

    JPanel val_panel = new JPanel();
    val_panel.setLayout(new BoxLayout(val_panel, BoxLayout.X_AXIS));

    val_panel.add(Box.createRigidArea(new Dimension(20,0))); // a spacer
    val_panel.add(new JLabel("min: "));
    val_panel.add(Box.createRigidArea(new Dimension(10,0))); // a spacer
    val_panel.add(min_field);
    val_panel.add(Box.createRigidArea(new Dimension(20,0))); // a spacer
    val_panel.add(new JLabel("max: "));
    val_panel.add(Box.createRigidArea(new Dimension(10,0))); // a spacer
    val_panel.add(max_field);
    val_panel.add(Box.createRigidArea(new Dimension(20,0))); // a spacer
    if (check_use) {
      val_panel.add(c_box); // a spacer
      val_panel.add(Box.createRigidArea(new Dimension(20,0))); // a spacer
    }

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

   // add(Box.createRigidArea(new Dimension(0,5))); // a spacer
    JLabel title_l = new JLabel(title);
      title_l.setAlignmentX(Component.CENTER_ALIGNMENT);
      title_l.setAlignmentY(Component.CENTER_ALIGNMENT);
    add(title_l);
    add(Box.createRigidArea(new Dimension(0,10))); // a spacer
    add(new JSeparator());
    add(Box.createRigidArea(new Dimension(0,5))); // a spacer
    add(val_panel);
    add(Box.createRigidArea(new Dimension(0,5))); // a spacer
    add(new JSeparator());
  }

  public double getMin(){
    return min_field.getValue();
  }

  public double getMax(){
    return max_field.getValue();
  }

  public boolean useVal(){
    return c_box.isSelected();
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
    pref.putBoolean(use_pref,useVal());
  }

}
