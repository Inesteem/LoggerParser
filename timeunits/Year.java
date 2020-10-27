package timeunits;

import java.util.Calendar;
import java.util.Vector;

public class Year extends TimeUnit<Month> {

  public Year(){
    super(12);
    metric = Metric.MONTH;
  }

   public void add_val(double val,  Calendar cal){
    int idx =  cal.get(Calendar.MONTH);

    if(subUnits.get(idx) == null){
      subUnits.set(idx,new Month());
    }
    subUnits.get(idx).add_val(val,  cal);
  }




}
