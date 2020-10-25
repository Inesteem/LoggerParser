package timeunits;

import java.util.Calendar;
import java.util.ArrayList;

public class Month extends TimeUnit<Day> {
  public static final String ids[] = {"Jan", "Feb", "Mae", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
  public Month(){
    super();
    subUnits = new Day[31];
    metric = Metric.DAY;
  }
 public void add_val(double val, Calendar cal){
    int idx =  cal.get(Calendar.DAY_OF_MONTH);
      if(subUnits[idx] == null){
      subUnits[idx]=new Day();
    }
    subUnits[idx].add_val(val, cal);
  }
  
  public String identifier(int id) {

      return ids[id]; 
  };
  
}
