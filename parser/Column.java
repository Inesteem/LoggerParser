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
  double l_thresh,u_thresh,min,max;
  int pos;
  NumberFormat format;
  Calendar calendar;
  String key;

  public Column(String key, double l_thresh, double u_thresh, int pos, Calendar calendar){
    this.key=key;
    this.l_thresh = l_thresh;
    this.u_thresh = u_thresh;
    max = l_thresh;
    min = u_thresh;
    this.pos = pos;
    this.calendar = calendar;
    NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
  }

  public boolean set_values(String[] data, Date date){
    if (data.length <= pos){
      return false;
    } 

    double val = -1;
    try {
      val= Double.parseDouble(data[pos]);
    } catch (NumberFormatException e){
      try {
        Number number = format.parse(data[pos]);
        val = number.doubleValue();
      } catch (ParseException e2){
        e2.printStackTrace();
        IOManager.getInstance().asError("Parse exception with " + Arrays.toString(data));
      }
    }

    if(l_thresh <= val && val <= u_thresh) return false;
    if(min > val) min = val;
    if(max < val) max = val;

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

    months[month].add_data(day, hour, val);

    return true;
  }

  public Pair<Integer, Double> get_month_sum(Month m) {
    double sum = 0;
    int num = 0;
    for (int h = 0; h < 24; ++h) {
      if(m.hours[h] == null) { continue; }
      for (int e = 0; e < m.hours[h].size(); ++e){
        sum += m.hours[h].get(e);
        ++num;
      }
    }
    return new Pair<Integer,Double>(num, sum);
  }

  public void collect_values(HashMap<Integer, HashMap<String, Pair<Integer,Double> > > summedVals, HashMap<Integer, HashMap<String, Pair<Integer,Double> > > summedValsMonth, boolean avg){
    Iterator it = ValPerYear.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry)it.next();
      Month[] months = (Month[]) pair.getValue();
      int year  = (int) pair.getKey();

      for (int m = 0; m < 12; ++m){
        if(months[m] == null) { continue; }

        int num_days = YearMonth.of(year, m+1).lengthOfMonth();
        Pair<Integer,Double> sum = get_month_sum(months[m]);
        

        int key = (year << 2) & m;
        HashMap<String, Pair<Integer,Double> > svs;
        if (!summedVals.containsKey(key)) {
          svs = new HashMap<String, Pair<Integer,Double> >();
          summedVals.put(key,svs);	
        } else {
          svs = summedVals.get(key);
        }
        svs.put(this.key, sum);
      }
    }
  }

};


