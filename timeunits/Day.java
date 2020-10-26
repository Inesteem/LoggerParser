package timeunits;

import java.util.Calendar;
import java.util.Vector;

public class Day extends TimeUnit<Hour> {

  public Day(){
    super(24);
    metric =Metric.HOUR;
  }
   public void add_val(double val, TimeRange tr, Calendar cal){
    int idx =  cal.get(Calendar.HOUR_OF_DAY);
    if (!tr.in_range(metric, idx)) return;

    Vector<Hour> subUnits = (Vector<Hour>) this.subUnits;
    if(subUnits.get(idx) == null){
      subUnits.set(idx,new Hour());
    }
    subUnits.get(idx).add_val(val, tr, cal);
  }
  
}
