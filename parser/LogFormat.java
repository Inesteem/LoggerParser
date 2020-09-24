package parser;
import javafx.util.Pair;
import java.util.Arrays;
import java.lang.NumberFormatException;
import java.lang.Number;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;

import java.lang.Thread;

import java.util.prefs.Preferences;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.lang.InterruptedException;

public abstract class LogFormat{

	private Parser.ParserType type;
  protected boolean for_all = false;
  protected ArrayList<ValuePanel> val_panels;
  private final String pref_all_str;
  private Object lock = new Object();
  protected String frame_title = "Configure Parser";

	public LogFormat(Parser.ParserType t, String pas){ 
      pref_all_str = pas;
      type = t;
      val_panels= new ArrayList<ValuePanel>();
  }

	public abstract boolean get_values(String[] data, List<Double> values);

  public abstract void configure(String file_name);
	
	public Parser.ParserType get_parser_type() { return type;}
	public static boolean matches(String[] line){return false;}
	
	
//	public Pair<Integer, double[]> get_month_avg(Month m);
	public abstract Pair<int[], double[]> get_month_val(Month m);
	
	
	public abstract String get_value_header();


  public void configure(String file_name, JPanel options){
    if (for_all) return;
    Preferences pref = Preferences.userRoot();
    boolean pref_all = pref.getBoolean(pref_all_str, false);



    JFrame frame = new JFrame(frame_title);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //configure components
    JButton OKButton = new JButton("Only this File");
    JButton OKAllButton = new JButton("All Files");
  
    //configure panels
    JPanel panelHeader= new JPanel();
    JLabel title_l = new JLabel(file_name);
    title_l.setFont(title_l.getFont().deriveFont(Font.BOLD, 14f));
    panelHeader.add(title_l, BorderLayout.CENTER);

    JPanel panelThresh= new JPanel();
    panelThresh.setLayout(new BoxLayout(panelThresh, BoxLayout.Y_AXIS));
    for(ValuePanel p : val_panels){
      panelThresh.add(Box.createRigidArea(new Dimension(0,10))); // a spacer
      panelThresh.add(p);
    }

    JPanel panelMiddle = new JPanel();
    panelMiddle.setLayout(new BoxLayout(panelMiddle, BoxLayout.Y_AXIS));
    panelMiddle.add(panelThresh);
    if(options != null){
      panelMiddle.add(Box.createRigidArea(new Dimension(0,10))); // a spacer
      panelMiddle.add(options);
      panelMiddle.add(new JSeparator());
    }

    JPanel panelFooter= new JPanel();
    panelFooter.add(OKButton);
    panelFooter.add(OKAllButton);

    //configure frame layout
    Container contentPane = frame.getContentPane();
    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
    contentPane.add(Box.createRigidArea(new Dimension(0,5))); // a spacer
    contentPane.add(panelHeader);
    contentPane.add(new JSeparator());
    contentPane.add(panelMiddle);
    contentPane.add(Box.createRigidArea(new Dimension(0,10))); // a spacer
    contentPane.add(panelFooter);
    contentPane.add(Box.createRigidArea(new Dimension(0,5))); // a spacer

    //configure frame
    frame.pack();
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);

    frame.addWindowListener(new WindowAdapter() {

        @Override
        public void windowClosing(WindowEvent arg0) {
          synchronized (lock) {
            frame.setVisible(false);
            lock.notify();
          }
        }

      });
  frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"clickButton");

  frame.getRootPane().getActionMap().put("clickButton",new AbstractAction(){
      public void actionPerformed(ActionEvent ae)
      {
        if (OKButton.hasFocus()){ OKButton.doClick(); }
        else if (OKAllButton.hasFocus()){ OKAllButton.doClick(); }
      }
      });


    //button event press
     OKButton.addActionListener(new ActionListener() {

     public void actionPerformed(ActionEvent e){
       synchronized (lock) {
            frame.setVisible(false);
            for_all = false;
            lock.notify();
          }
        }
      });


     OKAllButton.addActionListener(new ActionListener() {

     public void actionPerformed(ActionEvent e){
       synchronized (lock) {
            frame.setVisible(false);
            for_all = true;
            lock.notify();
          }
        }
      });

    //set default button  
    if(pref_all) {
        frame.getRootPane().setDefaultButton(OKAllButton);
       OKAllButton.requestFocus();
    } else {
        frame.getRootPane().setDefaultButton(OKButton);
        OKButton.requestFocus();
    }


    //run window
    boolean finished = false;
    while(!finished){
      finished = true;

      frame.setVisible(true);
      Thread t = new Thread() {
        public void run() {
          synchronized(lock) {
            while (frame.isVisible())
              try {
                lock.wait();
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
          }
        }
      };
      t.start();
      try {
        t.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      
      pref.putBoolean(pref_all_str,for_all);

      for(ValuePanel p : val_panels){
         if (!p.valid()) {
          IOManager.asWarning("Wrong values entered. Please retry or exit.");
          for_all=false;
          finished=false;
          break;
        }
        p.updatePrefs();
      }

    }
  } 



	
}
