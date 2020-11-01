package parser;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public abstract class JValField<T extends java.lang.Number> extends JTextField{
  private T value;
  private Font normal;
  protected String default_val = "";

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
  public abstract T valueOf(String str) throws Exception;
  public abstract T errVal();

  public void setValue(String str_val){
    try {
      value = valueOf(str_val);
      setBackground(Color.WHITE);
      if(str_val == default_val)
        setForeground(Color.GRAY);
      else
        setForeground(Color.BLACK);
      
      setFont(normal);
    } catch (Exception e) {
      value = errVal();
      setBackground(Color.RED);
      setForeground(Color.WHITE);
      setFont(getFont().deriveFont(Font.BOLD, 14f));
      e.printStackTrace();
    }
  }
  public void setValue(){
    String str_val = getText();
    setValue(str_val);
  }

  public void setValue(T v){
    setText(String.valueOf(v));
    value = v;
  }

  public T getValue(){
    return value;
  }

  public void setDefaultVal(String str) {default_val = str;}
}
