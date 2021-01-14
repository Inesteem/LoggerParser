package src.datatree;
import src.types.*;

import java.util.Calendar;

public class Day extends TimeUnit<Hour> {

  public Day(Limits limits){
    super(24,limits);
    metric =Metric.HOUR;
  }
   public void add_val(double val,  Calendar cal){
    int idx =  cal.get(Calendar.HOUR_OF_DAY);
    numSubUnits |= 1l << idx;

    if(subUnits.get(idx) == null){
      subUnits.set(idx,new Hour(limits));
    }
    subUnits.get(idx).add_val(val,  cal);
  }
  
  public String identifier(int id) {
    return "day " + String.valueOf(id+1); 
  };
}
