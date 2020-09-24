package parser;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class JDoubleField extends JValField<Double>{

  public JDoubleField (int length) {
    super(length);
  }

  Double valueOf(String str){
    return Double.valueOf(str);
  }
  Double errVal() {return -1.0;}
}
