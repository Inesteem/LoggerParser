package parser;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class JDoubleField extends JValField<Double>{

  public JDoubleField (int length) {
    super(length);
  }

  public Double valueOf(String str){
    return Double.valueOf(str);
  }
  public Double errVal() {return -1.0;}
}
