package parser;
import javafx.util.Pair;
import java.util.Arrays;
import java.lang.NumberFormatException;
import java.lang.Number;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.List;

import java.lang.Thread;

import java.util.prefs.Preferences;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.lang.InterruptedException;


//import java.util.Date;
//import java.util.HashMap;
//import java.util.ArrayList;
//import java.util.List;
//import javafx.util.Pair;

public abstract class LogFormat{
	private Parser.ParserType type;
  protected boolean for_all = false;
  protected int min;
  protected int max;

  private static final String PREF_MIN = "LP_PREF_MIN";
  private static final String PREF_MAX = "LP_PREF_MAX";
  private static final String PREF_ALL = "LP_PREF_ALL";

  private Object lock = new Object();

	public LogFormat(Parser.ParserType t){ 
      type = t;
  }

	public abstract boolean get_values(String[] data, List<Double> values);
	
	
	public Parser.ParserType get_parser_type() { return type;}
	public static boolean matches(String[] line){return false;}
	
	
//	public Pair<Integer, double[]> get_month_avg(Month m);
	public abstract Pair<Integer, double[]> get_month_sum(Month m);
	
	
	public abstract String get_value_header();


  public void configure(String title, String pref_ext, JPanel options){
    if (for_all) return;
    Preferences pref = Preferences.userRoot();
    int pref_min = pref.getInt(PREF_MIN + pref_ext, 0);
    int pref_max = pref.getInt(PREF_MAX + pref_ext, Integer.MAX_VALUE);
    boolean pref_all = pref.getBoolean(PREF_ALL + pref_ext, false);

    JFrame frame = new JFrame(title);
    //JFrame frame = new JFrame("How to parse Impulses:");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //configure components
    JIntField minField = new JIntField(10);
    minField.setValue(pref_min);
    JIntField maxField = new JIntField(10);
    maxField.setValue(pref_max);

    JButton OKButton = new JButton("Only this File");
//    OKButton.setSelected(!pref_all);
    JButton OKAllButton = new JButton("All Files");
//    OKAllButton.setSelected(pref_all);
  

    //configure panels
    JPanel panelHeader= new JPanel();
    panelHeader.add(new JLabel("Thresholds for impulses (integer, incl.):"),BorderLayout.CENTER);

    JPanel panelThresh= new JPanel();
    panelThresh.setLayout(new BoxLayout(panelThresh, BoxLayout.X_AXIS));
    panelThresh.add(Box.createRigidArea(new Dimension(20,0))); // a spacer
    panelThresh.add(new JLabel("min:"));
    panelThresh.add(Box.createRigidArea(new Dimension(20,0))); // a spacer
    panelThresh.add(minField);
    panelThresh.add(Box.createRigidArea(new Dimension(20,0))); // a spacer
    panelThresh.add(new JLabel("max:"));
    panelThresh.add(Box.createRigidArea(new Dimension(20,0))); // a spacer
    panelThresh.add(maxField);
    panelThresh.add(Box.createRigidArea(new Dimension(20,0))); // a spacer



    JPanel panelMiddle = new JPanel();
    panelMiddle.setLayout(new BoxLayout(panelMiddle, BoxLayout.Y_AXIS));
    panelMiddle.add(panelThresh);
    panelMiddle.add(Box.createRigidArea(new Dimension(0,10))); // a spacer
    panelMiddle.add(options);

    JPanel panelFooter= new JPanel();
    panelFooter.add(OKButton);
    panelFooter.add(OKAllButton);

    //configure frame layout
    Container contentPane = frame.getContentPane();
    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
    contentPane.add(Box.createRigidArea(new Dimension(0,5))); // a spacer
    contentPane.add(panelHeader);
    contentPane.add(Box.createRigidArea(new Dimension(0,10))); // a spacer
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
    while(true){
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

      //process results
      min = minField.getValue();
      max = maxField.getValue();
      if (min < 0 || max < 0){
        IOManager.asWarning("Wrong threshold values set. Retry or exit");
        for_all=false;
        continue;
      }

      pref.putInt(PREF_MIN, min);
      pref.putInt(PREF_MAX, max);
      pref.putBoolean(PREF_ALL,for_all);

      break;
    }
  } 



	
}
