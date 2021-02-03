package src.gui;
import static src.types.Metric.*;
import src.types.*;
import src.datatree.YearMap;
import src.datatree.TimeRange;

import java.lang.Thread;
import java.lang.InterruptedException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.prefs.Preferences;

public class PlotWindow{
  public static final String PREF_METRICS = "LP_PLOTWINDOW_METRICS";
  public JFrame frame;
  public JButton OKButton;
  public JButton SkipButton;
  protected JComboBox<String> select_range;
  protected final Object lock;
  boolean finished = false;

  JRangeField[] rangeFields;
  TimeRange tr = new TimeRange(0xFFFFFFFF);

  public PlotWindow(){
    lock= new Object();
    String sr_str[] ={"YEARS", "MONTHS", "DAYS","HOURS"};
    String tt_str[] = {  "Valid Ranges, e.g. : ALL 1900-2022  2000,2001,2002  2000"
                        ,"Valid Ranges, e.g. : ALL 1-13  1,2,3,4 5 "
                        ,"Valid Ranges, e.g. : ALL 1-32  1,2,3,4 5"
                        ,"Valid Ranges, e.g. : ALL 0-24, 0,1,2,3 4"};

    frame = new JFrame("Wanna plot?");
    OKButton = new JButton("Plot");
    SkipButton = new JButton("Continue");
    select_range= new JComboBox<String>(sr_str);
    rangeFields=new JRangeField[Metric.SIZE.value()];
    for(Metric m : Metric.values()){
      if(m == Metric.SIZE) continue;
      int i = m.value();
      rangeFields[i] = new JRangeField(20,tr,m);
      rangeFields[i].setToolTipText(tt_str[i]);
      rangeFields[i].setName("range");
    //  rangeFields[i].setValue("ALL");
    }
  }


  public void run(JPanel options, Data pd, Method method, YearMap dataMap){
    dataMap.add_years(tr);
    Preferences pref = Preferences.userRoot();
    String pref_metric = pref.get(PREF_METRICS, "MONTHS");
    select_range.setSelectedItem(pref_metric);
    select_range.setEditable(true);
    select_range.setToolTipText("Selecting a western timezone for measurements belonging to african regions can result in 'duplicated entries' warnings, since some dates/time combis do not exist in Europa due to clock change. However, don't bother with a 100% correct timezone.");


//    boolean pref_ok = pref.getBoolean(pref_all_str, false);

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //configure panels
    JPanel panelHeader= new JPanel();
    JLabel title_l = new JLabel(String.valueOf(pd)+" - Define valid ranges:");
    title_l.setFont(title_l.getFont().deriveFont(Font.BOLD, 14f));
    panelHeader.add(title_l, BorderLayout.CENTER);

    JPanel panelThresh= new JPanel();
    panelThresh.setLayout(new BoxLayout(panelThresh, BoxLayout.Y_AXIS));

    JPanel panelMiddle = new JPanel();

    panelMiddle.setLayout(new BoxLayout(panelMiddle, BoxLayout.Y_AXIS));
    panelMiddle.add(panelThresh);
    for (int i =0; i < rangeFields.length;++i){

      JPanel range_panel = new JPanel();
      range_panel.setLayout(new BoxLayout(range_panel, BoxLayout.X_AXIS));

      range_panel.add(Box.createRigidArea(new Dimension(20,0)));
      range_panel.add(new JLabel(String.format("%1$7s",Metric.values()[i])));
      range_panel.add(Box.createRigidArea(new Dimension(10,0)));
      range_panel.add(rangeFields[i]);
      range_panel.add(Box.createRigidArea(new Dimension(20,0)));
      panelThresh.add(range_panel);
    }

    JPanel panelTZ = new JPanel();
    //panelTZ.setLayout(new BoxLayout(panelTZ, BoxLayout.X_AXIS));
    panelTZ.add(new JLabel("Select time unit: "));
    //panelTZ.add(Box.createRigidArea(new Dimension(5,0))); // a spacer
    panelTZ.add(select_range);
    panelMiddle.add(Box.createRigidArea(new Dimension(0,10))); // a spacer
    panelMiddle.add(panelTZ);
    panelMiddle.add(Box.createRigidArea(new Dimension(0,10))); // a spacer
    panelMiddle.add(new JSeparator());

    if(options != null){
      panelMiddle.add(Box.createRigidArea(new Dimension(0,10))); // a spacer
      panelMiddle.add(options);
      panelMiddle.add(Box.createRigidArea(new Dimension(0,10))); // a spacer
      panelMiddle.add(new JSeparator());
    }

    JPanel panelFooter= new JPanel();
    panelFooter.add(OKButton);
    panelFooter.add(SkipButton);

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
    finished = false;

    frame.addWindowListener(new WindowAdapter() {

        @Override
        public void windowClosing(WindowEvent arg0) {
        synchronized (lock) {
        frame.setVisible(false);
        finished = true;
        IOManager.getInstance().asError("Configuration aborted");
        lock.notify();
        }
        }

        });
    frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"clickButton");

    frame.getRootPane().getActionMap().put("clickButton",new AbstractAction(){
        public void actionPerformed(ActionEvent ae)
        {
        if (OKButton.hasFocus()){ OKButton.doClick(); }
        else if (SkipButton.hasFocus()){ SkipButton.doClick(); }
        }
        });


    //button event press
    OKButton.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e){
        synchronized (lock) {
        frame.setVisible(false);
        lock.notify();
        }
        }
        });


    SkipButton.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e){
        synchronized (lock) {
        frame.setVisible(false);
        finished = true;
        lock.notify();
        }
        }
        });

    frame.getRootPane().setDefaultButton(OKButton);
    OKButton.requestFocus();


    //run window
    while(!finished){

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

      pref.put(PREF_METRICS,String.valueOf(select_range.getSelectedItem()));

        if(!finished){
            if(select_range.getSelectedItem().equals("YEARS"))
                PlotHelper.plot_stats(dataMap, method, YEAR, "Yearly-Avg", pd, tr);
            else if(select_range.getSelectedItem().equals("MONTHS"))
                PlotHelper.plot_stats(dataMap, method, MONTH, "Monthly-Avg", pd, tr);
            else if(!finished && select_range.getSelectedItem().equals("DAYS"))
                PlotHelper.plot_stats(dataMap, method, DAY, "Daily-Avg", pd, tr);
            else if(!finished && select_range.getSelectedItem().equals("HOURS"))
                PlotHelper.plot_stats(dataMap, method, HOUR, "Hourly-Avg", pd, tr);
        }

    }
  } 


}
