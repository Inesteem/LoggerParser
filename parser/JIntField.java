package parser;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class JIntField extends JValField<Integer>{

  public JIntField (int length) {
    super(length);
  }

  public Integer valueOf(String str){
    return Integer.valueOf(str);
  }
  public Integer errVal() {return -1;}
}
