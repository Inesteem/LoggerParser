package parser;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public abstract class JValField<T extends java.lang.Number> extends JTextField{
  private T value;

  public JValField (int length) {
    super(length);
    value = errVal();
    this.addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent ke) {
          setValue();
        }
    });
  }
  abstract T valueOf(String str);
  abstract T errVal();

  void setValue(){
    String str_val= getText();
    try {
      value = valueOf(str_val);
      setBackground(Color.WHITE);
    } catch (Exception e) {
      value = errVal();
      setBackground(Color.RED);
    }
  }

  void setValue(T v){
    setText(String.valueOf(v));
    value = v;
  }

  T getValue(){
    return value;
  }

}
