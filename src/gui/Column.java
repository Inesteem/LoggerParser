package src.gui;
import src.datatree.*;
import src.parser.Parser;
import src.parser.ValuePanel;
import src.types.*;
import static src.types.Metric.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Number;
import java.lang.NumberFormatException;

import java.text.NumberFormat;
import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Column {

  DataTree dataTree;
  int pos;
  public double mul;
  NumberFormat format;
  Calendar calendar;
  Limits limits;
  Data data;
  TimeRange timeRange;

  public Column(int pos, boolean average, Calendar calendar, Data data){
    this.data = data;
    this.pos = pos;
    this.calendar = calendar;
    mul = 1.0;
    format = NumberFormat.getInstance(Locale.FRANCE);
    limits = new Limits();
    timeRange = new TimeRange(~0l);

    dataTree = Parser.getDataMap(data,limits);
  }
  public Data getData() {
    return data;
  }

  public Method getMethod() {
    return data.method;
  }

  public int getPos() {
    return pos;
  }

  public DataTree getDataTree() {
    return dataTree;
  }

  public TimeRange getTimeRange() {
    return  timeRange;
  }

  public double parseVal(String[] data_line) {
    double val = -Double.MAX_VALUE;
    try {
      val= Double.parseDouble(data_line[pos]);
    } catch (NumberFormatException e){
      try { // number has , instead of .
        Number number = format.parse(data_line[pos]);
        val = number.doubleValue();
      } catch (ParseException e2){
        e2.printStackTrace();
      }
    }
    return val;
  }

  public boolean setValues(String[] data, Date date, ValuePanel panel){
    if (!panel.useVal() || data.length <= pos || data[pos].length() == 0){
      return false;
    }

    double val = parseVal(data);
    if (val == -Double.MAX_VALUE) return false;

    if(panel.getMin() > val || val > panel.getMax()) return false;
    val *= mul;

    calendar.setTime(date);  
    dataTree.add_val(val, calendar);
    return true;
  }

  public void writeToFile(FileOutputStream ostream) throws IOException {
    ostream.write(("Data: "+data.description + "\n").getBytes());

    TreeWriter tw = new TreeWriter(ostream,data.method);
    dataTree.add_years(timeRange);
    tw.set_timeRange(timeRange);
    tw.monthly_overview(dataTree);
    tw.hourly_overview(dataTree);
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


