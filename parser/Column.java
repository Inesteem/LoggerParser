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
import plotting.*;

class Column {


  YearMap dataMap;
  public double l_thresh,u_thresh;
  int pos;
  public double mul;
  NumberFormat format;
  Calendar calendar;
  public String key;
  Method method;
  Limits limits;
  PlotData plotData;

  public Column(String key, double l_thresh, double u_thresh, int pos, boolean average, Calendar calendar, PlotData pd){
    this.plotData = pd;
    this.key=key;
    this.l_thresh = l_thresh;
    this.u_thresh = u_thresh;
    this.pos = pos;
    this.calendar = calendar;
    mul = 1.0;
    format = NumberFormat.getInstance(Locale.FRANCE);
    limits = new Limits();

    if (average) method = Method.AVG;
    else method = Method.SUM;

    dataMap = Parser.getDataMap(method,pd,limits);
  }
  
  public void set_limits(int minD, int minM){
    limits.set_limit(Metric.HOUR, minD);
    limits.set_limit(Metric.DAY, minM);
  }

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

    limits.print();
//    ostream.write("\n".getBytes());
//    dataMap.write_to_file(Metric.YEAR,method,ostream);
    ostream.write("\n".getBytes());
    dataMap.write_to_file(Metric.MONTH,method,ostream);
    ostream.write("\n".getBytes());
    dataMap.write_to_file(Metric.DAY,method,ostream);
    ostream.write("\n".getBytes());
    dataMap.write_to_file(Metric.HOUR,method,ostream);
    ostream.write("\n".getBytes());

    ostream.write("\nOVERALL MONTHLY VALS: \n".getBytes());
    TimeRange tr = new TimeRange(0xFFFFFFFF);
    dataMap.add_years(tr);
    tr.unset_range(Metric.MONTH,0,13);
    double val_avg = 0;
    double num = 0;
    for(int i = 0; i < 12; ++i){
      tr.set_idx(Metric.MONTH,i);
      dataMap.reset(); 
      ostream.write((Month.toString(i) + ": ").getBytes());
      if (dataMap.get_num(tr) == 0){
        ostream.write(( "- \n").getBytes());
        continue;
      }
      ++num;
      ostream.write(("\n num: " +String.valueOf(dataMap.get_num(tr)) + " ").getBytes());
      ostream.write((" min: " +dataMap.df.format(dataMap.get_min(method)) + " ").getBytes());
      ostream.write((" max: " +dataMap.df.format(dataMap.get_max(method)) + " ").getBytes());

      double val;
      if(method == Method.SUM) val = dataMap.get_sum(tr);
      else val = dataMap.get_avg(tr);
      val_avg+=val;

      ostream.write((" val: " +dataMap.df.format(val) + "\n").getBytes());
      tr.unset_idx(Metric.MONTH,i);
    }
    if(method == Method.AVG && num != 0) val_avg /= num;

    ostream.write(("\n overall average: "+dataMap.df.format(val_avg)+" \n").getBytes());

    

  }


};


