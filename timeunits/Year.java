package timeunits;

import java.util.Calendar;
import java.util.ArrayList;

public class Year extends TimeUnit<Month> {

  public Year(){
    super();
    subUnits = new Month[12];
    metric = Metric.MONTH;
  }

   public void add_val(double val, Calendar cal){
    int idx =  cal.get(Calendar.MONTH);
      if(subUnits[idx] == null){
      subUnits[idx] = new Month();
    }
    subUnits[idx].add_val(val, cal);
  }




}
