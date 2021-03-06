package src.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class JDoubleField extends JValField<Double>{

  public JDoubleField (int length) {
    super(length);
  }

  public Double valueOf(String str) throws Exception {
    return Double.valueOf(str);
  }
  public Double errVal() {return -Double.MAX_VALUE;}
}
