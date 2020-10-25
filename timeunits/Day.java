package timeunits;

import java.util.Calendar;
import java.util.ArrayList;

public class Day extends TimeUnit<Hour> {

  public Day(){
    super();
    subUnits = new ArrayList<Hour>(24);
    metric =Metric.HOUR;
  }
 public void add_val(double val, Calendar cal){
    int idx =  cal.get(Calendar.HOUR_OF_DAY);
      if(subUnits.get(idx) == null){
      subUnits.set(idx,new Hour());
    }
    subUnits.get(idx).add_val(val, cal);
  }
  
}