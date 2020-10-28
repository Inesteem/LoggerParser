package timeunits;

import java.util.Calendar;
import java.util.Vector;

public class Month extends TimeUnit<Day> {
  public static final String ids[] = {"Jan", "Feb", "Mae", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
  public Month(Limits limits){
    super(31,limits);
    metric = Metric.DAY;
  }
  public void add_val(double val,  Calendar cal){
    int idx =  cal.get(Calendar.DAY_OF_MONTH)-1;

    if(subUnits.get(idx) == null){
      subUnits.set(idx,new Day(limits));
    }
    subUnits.get(idx).add_val(val,  cal);
  }

  public static String toString(int id) {
    return ids[id]; 
  };

  public String identifier(int id) {
    return toString(id); 
  };

}
