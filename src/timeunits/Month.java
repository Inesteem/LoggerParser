package parser;

import java.util.Calendar;
import java.text.DateFormatSymbols;

public class Month extends TimeUnit<Day> {
  public Month(Limits limits){
    super(31,limits);
    metric = Metric.DAY;
  }
  public void add_val(double val,  Calendar cal){
    int idx =  cal.get(Calendar.DAY_OF_MONTH)-1;
    numSubUnits |= 1 << idx;

    if(subUnits.get(idx) == null){
      subUnits.set(idx,new Day(limits));
    }
    subUnits.get(idx).add_val(val,  cal);
  }

  public static String toString(int id) {
    return new DateFormatSymbols().getMonths()[id];
  };

  public String identifier(int id) {
    return toString(id); 
  };

}
