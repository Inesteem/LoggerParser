package timeunits;

import java.util.Calendar;
import java.util.ArrayList;

public class Month extends TimeUnit<Day> {

  public Month(){
    super();
    subUnits = new ArrayList<Day>(31);
    metric = Metric.DAY;
  }
 public void add_val(double val, Calendar cal){
    int idx =  cal.get(Calendar.DAY_OF_MONTH);
      if(subUnits.get(idx) == null){
      subUnits.set(idx,new Day());
    }
    subUnits.get(idx).add_val(val, cal);
  }
  
  
}
