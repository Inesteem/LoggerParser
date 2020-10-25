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
import java.util.HashMap;
import java.util.Map;
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

class Column {


  HashMap<Integer,Month[]> ValPerYear;
  public double l_thresh,u_thresh;
  int pos;
  public double mul;
  NumberFormat format;
  Calendar calendar;
  public String key;
  public boolean log_meas;
  public boolean average;

  public Column(String key, double l_thresh, double u_thresh, int pos, boolean average, Calendar calendar){
    this.key=key;
    this.l_thresh = l_thresh;
    this.u_thresh = u_thresh;
    this.pos = pos;
    this.calendar = calendar;
    this.log_meas= true;
    this.average = average;
    mul = 1.0;
    format = NumberFormat.getInstance(Locale.FRANCE);
    ValPerYear = new HashMap<Integer,Month[]>();
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
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int hour = calendar.get(Calendar.HOUR_OF_DAY);

    Month[] months;
    if (!ValPerYear.containsKey(year)) {
      months = new Month[12];
      ValPerYear.put(year, months);	
    } else {
      months = ValPerYear.get(year);
    }

    if(months[month] == null){
      months[month] = new Month();
    }
//    System.out.println("column: parsed " + val);
    months[month].add_data(day, hour, val);

    return true;
  }


  public void collect_values(HashMap<Integer, HashMap<String, Month.MonthSum > > summedVals, HashMap<String,Month.MonthSum[]> summedPerMonth){
    Iterator it = ValPerYear.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry)it.next();
      Month[] months = (Month[]) pair.getValue();
      int year  = (int) pair.getKey();

      for (int m = 0; m < 12; ++m){
        if(months[m] == null) { continue; }

        //GET VALUE
        int num_days = YearMonth.of(year, m+1).lengthOfMonth();
        Month.MonthSum sum = months[m].get_month_sum();
        if(average && sum.num != 0) sum.sum /= sum.num;

        //PER YEAR
        int key = (year << 4) | m;
        System.out.println(year + " " + m + " " +key + " " + sum.sum);
        HashMap<String, Month.MonthSum > svs;
        if (!summedVals.containsKey(key)) {
          svs = new HashMap<String, Month.MonthSum >();
          summedVals.put(key,svs);	
        } else {
          svs = summedVals.get(key);
        }
        svs.put(this.key, sum);

        //PER MONTH
        if (!summedPerMonth.containsKey(this.key)) {
          summedPerMonth.put(this.key,new Month.MonthSum[12]);

        }
        if (summedPerMonth.get(this.key)[m] == null) {
          summedPerMonth.get(this.key)[m] = new Month.MonthSum();
        }
        summedPerMonth.get(this.key)[m].add(sum);

      }
    }

  }

};


