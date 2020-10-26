package timeunits;

import java.util.Calendar;
import java.util.Vector;

public class Month extends TimeUnit<Day> {
  public static final String ids[] = {"Jan", "Feb", "Mae", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
  public Month(){
    super(31);
    
    metric = Metric.DAY;
  }
  public void add_val(double val, TimeRange tr, Calendar cal){
    int idx =  cal.get(Calendar.DAY_OF_MONTH)-1;
    if (!tr.in_range(metric, idx)) return;

    Vector<Day> subUnits = (Vector<Day>) this.subUnits;
    if(subUnits.get(idx) == null){
      subUnits.set(idx,new Day());
    }
    subUnits.get(idx).add_val(val, tr, cal);
  }

  public String identifier(int id) {

    return ids[id]; 
  };

}
