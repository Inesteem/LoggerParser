package parser;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.time.YearMonth;
import javafx.util.Pair;

import java.lang.NumberFormatException;
import java.lang.Number;

import java.text.DateFormatSymbols;
import javax.swing.JLabel;
import javax.swing.JFrame;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import timeunits.*;

class Column {


  YearMap dataMap;
  public double l_thresh,u_thresh;
  int pos;
  public double mul;
  NumberFormat format;
  Calendar calendar;
  public String key;
  public boolean log_meas;
  Method method;

  public Column(String key, double l_thresh, double u_thresh, int pos, boolean average, Calendar calendar){
    this.key=key;
    this.l_thresh = l_thresh;
    this.u_thresh = u_thresh;
    this.pos = pos;
    this.calendar = calendar;
    this.log_meas= true;
    mul = 1.0;
    format = NumberFormat.getInstance(Locale.FRANCE);
    dataMap = new YearMap();
    if (average) method = Method.AVG;
    else method = Method.SUM;
  }

  public boolean logMeas() {return log_meas; }

  public boolean set_values(String[] data, Date date){
    if (data.length <= pos){
      return false;
    } 

    double val = -1;
    try {
      val= Double.parseDouble(data[pos]);
    } catch (NumberFormatException e){
      try { // number has , instead of .
        Number number = format.parse(data[pos]);
        val = number.doubleValue();
      } catch (ParseException e2){
        e2.printStackTrace();
        IOManager.getInstance().asError("Parse exception with " + Arrays.toString(data));
      }
    }
    val *= mul;
    if(l_thresh > val || val > u_thresh) return false;

    calendar.setTime(date);  
    dataMap.add_val(val, calendar);

    return true;
  }

  public void write_to_file(FileOutputStream ostream) throws IOException{
//    ostream.write("\n".getBytes());
//    dataMap.write_to_file(Metric.YEAR,method,ostream);
    ostream.write("\n".getBytes());
    dataMap.write_to_file(Metric.MONTH,method,ostream);
    ostream.write("\n".getBytes());
    dataMap.write_to_file(Metric.DAY,method,ostream);
    ostream.write("\n".getBytes());
    dataMap.write_to_file(Metric.HOUR,method,ostream);
    ostream.write("\n".getBytes());

    ostream.write("\n OVERALL MONTHLY AVG: \n".getBytes());
    TimeRange tr = new TimeRange(0xFFFFFFFF);
    tr.unset_range(Metric.MONTH,0,13);
    for(int i = 0; i < 12; ++i){
      tr.set_idx(Metric.MONTH,i);
      dataMap.reset(); 
      ostream.write((Month.toString(i) + ": ").getBytes());
      if (dataMap.get_num(tr) == 0){
        ostream.write(( "- \n").getBytes());
        continue;
      }
      ostream.write(("\n num: " +String.valueOf(dataMap.get_num(tr)) + " ").getBytes());
      ostream.write((" min: " +dataMap.df.format(dataMap.get_min(method)) + " ").getBytes());
      ostream.write((" max: " +dataMap.df.format(dataMap.get_max(method)) + " ").getBytes());
      if(method == Method.SUM)
        ostream.write((" val: " +dataMap.df.format(dataMap.get_sum(tr)) + "\n").getBytes());
      else
        ostream.write((" val: " +dataMap.df.format(dataMap.get_avg(tr)) + "\n").getBytes());
      tr.unset_idx(Metric.MONTH,i);
    } 

  }


};


