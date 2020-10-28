package timeunits;

import java.util.Calendar;
import java.util.Vector;

public class Year extends TimeUnit<Month> {
  int y;
  public Year(int y,Limits limits){
    super(12,limits);
    metric = Metric.MONTH;
    this.y = y;
  }

   public void add_val(double val,  Calendar cal){
    int idx =  cal.get(Calendar.MONTH);
    numSubUnits |= 1 << idx;

    if(subUnits.get(idx) == null){
      subUnits.set(idx,new Month(limits));
    }
    subUnits.get(idx).add_val(val,  cal);
  }

  public String identifier(int id) {
    return String.valueOf(y); 
  };


}
