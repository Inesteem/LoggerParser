package src.parser;
import src.gui.*;

import java.util.Locale;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import java.lang.Thread;
import java.lang.InterruptedException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import java.util.prefs.Preferences;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class LogFormat {

  private final Parser.ParserType type;
  protected boolean forAll = false;
  protected ArrayList<ValuePanel> valuePanels;
  protected ArrayList<Column> columns;

  private final String pref_all_str;
  public static final String PREF_TIMEZONE = "LP_PREF_TIMEZONE";

  private final Object lock = new Object();
  protected String frame_title = "Configure Parser";
  protected Calendar calendar;
  public SimpleDateFormat date_format;
  protected JComboBox<String> timezone_select;
  String[] timezones = {"Africa/Abidjan", "Africa/Accra", "Africa/Addis_Ababa", "Africa/Algiers", "Africa/Asmara", "Africa/Bamako", "Africa/Bangui", "Africa/Banjul", "Africa/Bissau", "Africa/Blantyre", "Africa/Brazzaville", "Africa/Bujumbura", "Africa/Cairo", "Africa/Casablanca", "Africa/Ceuta", "Africa/Conakry", "Africa/Dakar", "Africa/Dar_es_Salaam", "Africa/Djibouti", "Africa/Douala", "Africa/El_Aaiun", "Africa/Freetown", "Africa/Gaborone", "Africa/Harare", "Africa/Johannesburg", "Africa/Juba", "Africa/Kampala", "Africa/Khartoum", "Africa/Kigali", "Africa/Kinshasa", "Africa/Lagos", "Africa/Libreville", "Africa/Lome", "Africa/Luanda", "Africa/Lubumbashi", "Africa/Lusaka", "Africa/Malabo", "Africa/Maputo", "Africa/Maseru", "Africa/Mbabane", "Africa/Mogadishu", "Africa/Monrovia", "Africa/Nairobi", "Africa/Ndjamena", "Africa/Niamey", "Africa/Nouakchott", "Africa/Ouagadougou", "Africa/Porto-Novo", "Africa/Sao_Tome", "Africa/Timbuktu", "Africa/Tripoli", "Africa/Tunis", "Africa/Windhoek","UTC", "UTC+1", "UTC+2", "UTC+3", "GMT", "GMT+1", "GMT+2", "GMT+3"};


  static DecimalFormat df;	


  public JFrame frame;
  public JButton OKButton;
  public JButton OKAllButton;

  public LogFormat(Parser.ParserType t, String pas){ 
    pref_all_str = pas;
    type = t;
    valuePanels = new ArrayList<ValuePanel>();
    columns= new ArrayList<Column>();

    calendar = GregorianCalendar.getInstance();
    date_format = new SimpleDateFormat("dd.MM.yy HH:mm:ss");


    timezone_select= new JComboBox<String>(timezones);
    timezone_select.setMaximumSize(timezone_select.getPreferredSize() );
    //((JLabel)timezone_select.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    //((JLabel)timezone_select.getRenderer()).setVerticalAlignment(SwingConstants.CENTER);

    Locale locale = new Locale("en","UK");
    df = (DecimalFormat) NumberFormat.getNumberInstance(locale);
    df.applyPattern("##.##");


    frame = new JFrame(frame_title);

    ImageIcon appIcon = IOManager.loadLGIcon("icon.png");
    if(appIcon != null)
      frame.setIconImage(appIcon.getImage());
    OKButton = new JButton("Only this File");
    OKAllButton = new JButton("All Files");
  }

  public JFrame get_frame() {return frame;}


  public abstract void configure(String file_name);

  public Parser.ParserType get_parser_type() { return type;}
  public static boolean matches(String[] line){return false;}

  public abstract String get_value_header();

  public String get_selected_timezone() { return (String) timezone_select.getSelectedItem();}



  public void configure(String file_name, JPanel options){
    if (forAll) return;
    Preferences pref = Preferences.userRoot();
    boolean pref_all = pref.getBoolean(pref_all_str, false);



    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //configure components

    //configure panels
    JPanel panelHeader= new JPanel();
    JLabel title_l = new JLabel(file_name);
    title_l.setFont(title_l.getFont().deriveFont(Font.BOLD, 14f));
    panelHeader.add(title_l, BorderLayout.CENTER);

    JPanel panelThresh= new JPanel();
    panelThresh.setLayout(new BoxLayout(panelThresh, BoxLayout.Y_AXIS));
    for(ValuePanel p : valuePanels){
      panelThresh.add(Box.createRigidArea(new Dimension(0,10))); // a spacer
      panelThresh.add(p);
    }

    JPanel panelMiddle = new JPanel();
    panelMiddle.setLayout(new BoxLayout(panelMiddle, BoxLayout.Y_AXIS));
    panelMiddle.add(panelThresh);

    //TIMEZONES
    String pref_zone = pref.get(PREF_TIMEZONE, "Africa/Nairobi");
    timezone_select.setSelectedItem(pref_zone);
    timezone_select.setEditable(true);
    timezone_select.setToolTipText("Selecting a western timezone for measurements belonging to african regions can result in 'duplicated entries' warnings, since some dates/time combis do not exist in Europa due to clock change. However, don't bother with a 100% correct timezone.");

    JPanel panelTZ = new JPanel();
    //panelTZ.setLayout(new BoxLayout(panelTZ, BoxLayout.X_AXIS));
    panelTZ.add(new JLabel("Select timezone: "));
    //panelTZ.add(Box.createRigidArea(new Dimension(5,0))); // a spacer
    panelTZ.add(timezone_select);
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
          IOManager.asError("Configuration aborted");
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
          forAll = false;
          lock.notify();
        }
      }
    });


    OKAllButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e){
        synchronized (lock) {
          frame.setVisible(false);
          forAll = true;
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

      String timezone = get_selected_timezone();
      pref.putBoolean(pref_all_str, forAll);
      pref.put(PREF_TIMEZONE,timezone);


      TimeZone pdt=TimeZone.getTimeZone(timezone);
      calendar.setTimeZone(pdt);
      date_format.setTimeZone(TimeZone.getTimeZone(timezone));

      for(int i = 0; i < valuePanels.size(); ++i){
        ValuePanel panel = valuePanels.get(i);

        if (!panel.valid()) {
          IOManager.asWarning("Wrong values entered. Please retry or exit.");
          forAll =false;
          finished=false;
          break;
        }

        panel.updatePrefs();
      }

    }
  } 

  public Date get_date(String[] data) {

    Date date = null; 
    try{
      date = date_format.parse(data[0] + " " + data[1]);
    } catch (ParseException e) {
      IOManager.asError("error while parsing " + Arrays.toString(data));
    }
    return date;
  }

  abstract void preprocess(String[] data);

  public boolean set_values(String[] data){
    //		calendar.setTime(date);   // assigns calendar to given date 
    Date date = get_date(data);
    preprocess(data);
    for(int i = 0; i < valuePanels.size(); ++i){
      ValuePanel panel = valuePanels.get(i);
      Column col = columns.get(i);
      // out of bounds (thresholds)
      col.set_values(data, date, panel);

    }
    return true;
  }


  void write_to_file(String filename) throws IOException{
    for(Column col : columns) {
      FileOutputStream ostream;
      ostream = new FileOutputStream(IOManager.addIdToFilename(filename, col.get_data().name));
      col.write_to_file(ostream);
      ostream.close();
    }
  }

  public void updateVisualization(DataTreeVisualization dtr) {
    dtr.update(columns);
  }
  public DataTreeVisualization doVisualize() throws InterruptedException {
    return new DataTreeVisualization(columns);
   // //Schedule a job for the event dispatch thread:
   // //creating and showing this application's GUI.
   // SwingUtilities.invokeLater(new Runnable() {
   //   public void run() {
   //     //Turn off metal's use of bold fonts
   //     UIManager.put("swing.boldMetal", Boolean.FALSE);
   //     new DataTreeVisualization(columns);
   //   }
   // });
   // final Object lock = new Object();
   // synchronized (lock) {
   //   try {
   //     lock.wait();
   //   } catch (InterruptedException ex) {
   //   }
   // }
  }

}
