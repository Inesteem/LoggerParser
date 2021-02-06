package src.gui;
import src.datatree.*;
import src.parser.Parser;
import src.types.*;
import static src.types.Metric.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Number;
import java.lang.NumberFormatException;

import java.text.NumberFormat;
import java.text.ParseException;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Column {

  YearMap dataTree;
  public double l_thresh,u_thresh;
  int pos;
  public double mul;
  NumberFormat format;
  Calendar calendar;
  public String key;
  Method method;
  Limits limits;
  Data data;
  TimeRange timeRange;

  public Column(String key, double l_thresh, double u_thresh, int pos, boolean average, Calendar calendar, Data pd){
    this.data = pd;
    this.key=key;
    this.l_thresh = l_thresh;
    this.u_thresh = u_thresh;
    this.pos = pos;
    this.calendar = calendar;
    mul = 1.0;
    format = NumberFormat.getInstance(Locale.FRANCE);
    limits = new Limits();
    timeRange = new TimeRange(~0l);

    if (average) method = Method.AVG;
    else method = Method.SUM;

    dataTree = Parser.getDataMap(method,pd,limits);
  }
  public Data get_data() {
    return data;
  }

  public Method get_method() {
    return method;
  }

  public YearMap get_data_tree() {
    return dataTree;

  }

  public TimeRange get_timeRange() {
    return  timeRange;
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
    dataTree.add_val(val, calendar);

    return true;
  }

  public void write_to_file(FileOutputStream ostream) throws IOException {
    ostream.write(("\nData: "+data.toString().toLowerCase()+" in "+key+" \n").getBytes());

    TreeWriter tw = new TreeWriter(ostream,method);
    dataTree.add_years(timeRange);
    tw.set_timeRange(timeRange);
    tw.monthly_overview(dataTree);
    dataTree.accept(tw,DAY, timeRange);
    return;
    /*
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
    timeRange.unset_range(Metric.MONTH,0,13);
    double val_avg = 0;
    double num = 0;
    for(int i = 0; i < 12; ++i){
      timeRange.set_idx(Metric.MONTH,i);
      dataMap.reset(); 
      ostream.write((Month.toString(i) + ": ").getBytes());
      if (dataMap.get_num(timeRange) == 0){
        ostream.write(( "- \n").getBytes());
        continue;
      }
      ++num;
      ostream.write(("\n num: " +String.valueOf(dataMap.get_num(timeRange)) + " ").getBytes());
      ostream.write((" min: " +dataMap.df.format(dataMap.get_min(method)) + " ").getBytes());
      ostream.write((" max: " +dataMap.df.format(dataMap.get_max(method)) + " ").getBytes());

      double val;
      if(method == Method.SUM) val = dataMap.get_sum(timeRange);
      else val = dataMap.get_avg(timeRange);
      val_avg+=val;

      ostream.write((" val: " +dataMap.df.format(val) + "\n").getBytes());
      timeRange.unset_idx(Metric.MONTH,i);
    }
    if(method == Method.AVG && num != 0) val_avg /= num;

    ostream.write(("\n overall average: "+dataMap.df.format(val_avg)+" \n").getBytes());

  */

  }


};


