package timeunits;

import java.util.Calendar;
import java.util.Vector;

public class Year extends TimeUnit<Month> {
  int y;
  public Year(int y){
    super(12);
    metric = Metric.MONTH;
    this.y = y;
  }

   public void add_val(double val,  Calendar cal){
    int idx =  cal.get(Calendar.MONTH);

    if(subUnits.get(idx) == null){
      subUnits.set(idx,new Month());
    }
    subUnits.get(idx).add_val(val,  cal);
  }

  public String identifier(int id) {
    return String.valueOf(y); 
  };


}
