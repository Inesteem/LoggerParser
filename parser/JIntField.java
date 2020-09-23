package parser;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class JIntField extends JValField<Integer>{

  public JIntField (int length) {
    super(length);
  }

  Integer valueOf(String str){
    return Integer.valueOf(str);
  }
  Integer errVal() {return -1;}
}
