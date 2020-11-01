package timeunits;

import java.util.Calendar;
import java.util.HashMap;


import java.io.FileOutputStream;
import java.io.IOException;

public class YearMap extends TimeUnit<Year> {
  protected HashMap<Integer,Year> hm;

  public YearMap(Limits limits){
    super(0,limits);
    hm =new HashMap<Integer,Year>();
    metric = Metric.YEAR;
  }


  public void add_val(double val,  Calendar cal){
    int idx = cal.get(Calendar.YEAR);
    Year year;
    if (!hm.containsKey(idx)) {
      year = new Year(idx,limits);
      hm.put(idx,year);	
      subUnits.add(year);
    } else {
      year = hm.get(idx);
    }
    year.add_val(val,cal);
  }

  public void add_years(TimeRange tr) {
    for (HashMap.Entry<Integer, Year> entry : hm.entrySet()) {
       tr.add_year(entry.getKey()); 
    }
  }

  public String identifier(int id) {
    return ""; 
  }

}
