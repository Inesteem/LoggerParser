package timeunits;

import java.util.Calendar;
import java.util.Vector;

public class Year extends TimeUnit<Month> {

  public Year(){
    super(12);
    metric = Metric.MONTH;
  }

   public void add_val(double val, TimeRange tr, Calendar cal){
    int idx =  cal.get(Calendar.MONTH);
    if (!tr.in_range(metric, idx)) return;

    Vector<Month> subUnits = (Vector<Month>) this.subUnits;
    if(subUnits.get(idx) == null){
      subUnits.set(idx,new Month());
    }
    subUnits.get(idx).add_val(val, tr, cal);
  }




}
