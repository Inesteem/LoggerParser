package parser;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public abstract class JValField<T extends java.lang.Number> extends JTextField{
  private T value;
  private Font normal; 

  public JValField (int length) {
    super(length);
    normal = getFont();
    value = errVal();
    this.addKeyListener(new KeyAdapter() {
        public void keyReleased(KeyEvent ke) {
          setValue();
        }
    });
    setHorizontalAlignment(JTextField.CENTER);
  }
  abstract T valueOf(String str);
  abstract T errVal();

  void setValue(){
    String str_val= getText();
    try {
      value = valueOf(str_val);
      setBackground(Color.WHITE);
      setForeground(Color.BLACK);
      setFont(normal);
    } catch (Exception e) {
      value = errVal();
      setBackground(Color.RED);
      setForeground(Color.WHITE);
      setFont(getFont().deriveFont(Font.BOLD, 14f));
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
