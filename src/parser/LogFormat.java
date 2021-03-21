package src.parser;
import src.gui.*;
import src.types.Data;
import src.types.Method;
import src.types.ParserType;

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

  private final ParserType type;
  protected boolean forAll = false;
  protected ArrayList<ValuePanel> valuePanels;
  protected ArrayList<Column> columns;

  public final String PREF_ALL;
  public final String PREF_STR;
  public static final String PREF_TIMEZONE = "LP_PREF_TIMEZONE";

  private final Object lock = new Object();
  protected String frame_title = "Configure Parser";
  protected Calendar calendar;
  public SimpleDateFormat date_format;
  protected JComboBox<String> timezone_select;
  String[] timezones = {"Africa/Abidjan", "Africa/Accra", "Africa/Addis_Ababa", "Africa/Algiers", "Africa/Asmara", "Africa/Bamako", "Africa/Bangui", "Africa/Banjul", "Africa/Bissau", "Africa/Blantyre", "Africa/Brazzaville", "Africa/Bujumbura", "Africa/Cairo", "Africa/Casablanca", "Africa/Ceuta", "Africa/Conakry", "Africa/Dakar", "Africa/Dar_es_Salaam", "Africa/Djibouti", "Africa/Douala", "Africa/El_Aaiun", "Africa/Freetown", "Africa/Gaborone", "Africa/Harare", "Africa/Johannesburg", "Africa/Juba", "Africa/Kampala", "Africa/Khartoum", "Africa/Kigali", "Africa/Kinshasa", "Africa/Lagos", "Africa/Libreville", "Africa/Lome", "Africa/Luanda", "Africa/Lubumbashi", "Africa/Lusaka", "Africa/Malabo", "Africa/Maputo", "Africa/Maseru", "Africa/Mbabane", "Africa/Mogadishu", "Africa/Monrovia", "Africa/Nairobi", "Africa/Ndjamena", "Africa/Niamey", "Africa/Nouakchott", "Africa/Ouagadougou", "Africa/Porto-Novo", "Africa/Sao_Tome", "Africa/Timbuktu", "Africa/Tripoli", "Africa/Tunis", "Africa/Windhoek","UTC", "UTC+1", "UTC+2", "UTC+3", "GMT", "GMT+1", "GMT+2", "GMT+3"};

  public String regex = "\\s+";
  static DecimalFormat df;


  public JFrame frame;
  public JButton OKButton;
  public JButton OKAllButton;

  public LogFormat(ParserType t, final String PREF_STR, Data data_types[] ){
    this.PREF_STR = PREF_STR;
    this.PREF_ALL = PREF_STR+"_ALL";
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


    OKButton = new JButton("Only this File");
    OKAllButton = new JButton("All Files");

  //  if(data_types == null) return;
    for(int pos = 0; pos < data_types.length; ++pos){
      Data data = data_types[pos];
      valuePanels.add(new ValuePanel(data, PREF_STR + "_" + data.name, 10, data.min,data.max));
      columns.add(new Column(pos+2, data.method == Method.AVG, calendar, data));
    }
  }

//  public LogFormat(ParserType t, final String PREF_STR) {
//    this(t, PREF_STR,null);
//  }

  public ParserType get_parser_type() { return type;}

  public String get_selected_timezone() { return (String) timezone_select.getSelectedItem();}

  public void configure(String file_name, JPanel options){
    if (forAll) return;

    frame = new JFrame(frame_title);

    ImageIcon appIcon = IOManager.loadLGIcon("icon.png");
    if(appIcon != null)
      frame.setIconImage(appIcon.getImage());


    Preferences pref = Preferences.userRoot();
    boolean pref_all = pref.getBoolean(PREF_ALL, false);

    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //TODO fix this shit; exit on close closes whole app

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

    //frame.addWindowListener(new WindowAdapter() {

    //  @Override
    //  public void windowClosing(WindowEvent arg0) {
    //    synchronized (lock) {
    //      frame.setVisible(false);
    //      IOManager.asError("Configuration aborted");
    //      lock.notify();
    //    }
    //  }
    //});

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
      pref.putBoolean(PREF_ALL, forAll);
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

  /**
   * Retrieves the date from a line of input (format : "dd.MM.yy HH:mm:ss");
   * @param data the splitted input line; the date needs to be in the first two parts.
   * @return the date
   */
  public Date getDate(String[] data) {

    Date date = null; 
    try{
      date = date_format.parse(data[0] + " " + data[1]);
    } catch (ParseException e) {
      IOManager.asError("error while parsing " + Arrays.toString(data));
    }
    return date;
  }

  /**
   * Do magic shit with data (see WithFogFormat)
   * @param data the splitted input line
   */
  abstract void preprocess(String[] data);

  /**
   * Redo magic shit with data if needed (since the input is merged) (see HoboFormat)
   * @param data the splitted input line
   */
  abstract void postprocess(String[] data);

  public abstract void configure(String file_name);
  public abstract boolean matches(String[] line);

  public boolean setValues(String[] data){
    Date date = getDate(data);
    preprocess(data);
    boolean validData = false;
    for(int i = 0; i < valuePanels.size(); ++i){
      ValuePanel panel = valuePanels.get(i);
      Column col = columns.get(i);
      // out of bounds (thresholds)
      if(!col.setValues(data, date, panel)) {
        if(data.length > col.getPos()) {
          postprocess(data);
          data[col.getPos()] = "-";
        }
      } else {
        validData = true;
      }
    }
    return validData;
  }


  void writeToFile(String filename) throws IOException{
    for(Column col : columns) {
      String tmpFileName=IOManager.addIdToFilename(filename, col.getData().name);
      if(IOManager.canWriteToFile(null, tmpFileName)) {
        FileOutputStream ostream;
        ostream = new FileOutputStream(tmpFileName);
        col.writeToFile(ostream);
        ostream.close();
      } else {
        IOManager.asWarning("The column containing " + col.getData().name + " was not saved.");
      }
    }
  }

  public void updateVisualization(DataTreeVisualization dtr) {
    dtr.update(columns);
  }
  public DataTreeVisualization doVisualize() throws InterruptedException {
    return new DataTreeVisualization(columns);
  }

}
