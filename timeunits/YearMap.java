package timeunits;

import java.util.Calendar;
import java.util.HashMap;


import java.io.FileOutputStream;
import java.io.IOException;

public class YearMap extends TimeUnit<Year> {
  protected HashMap<Integer,Year> hm;

  public YearMap(){
    super(1);
    hm =new HashMap<Integer,Year>();
  }


  public void add_val(double val, TimeRange tr, Calendar cal){
    int idx = cal.get(Calendar.YEAR);
    if (!tr.in_range(metric, idx)) return;
    Year year;
    if (!hm.containsKey(idx)) {
      year = new Year();
      hm.put(idx,year);	
      subUnits.add(year);
    } else {
      year = hm.get(idx);
    }
    year.add_val(val,tr,cal);
  }


}
