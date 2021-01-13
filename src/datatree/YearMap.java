package src.datatree;
import src.types.*;

import java.util.Calendar;
import java.util.HashMap;

public class YearMap extends TimeUnit<Year> {
  protected HashMap<Integer,Year> hm;

  public YearMap(Limits limits){
    super(0,limits);
    hm = new HashMap<Integer,Year>();
    metric = Metric.YEAR;
  }

  public void configure(TimeRange tr){
    add_years(tr);
    calc(tr);
  }

  public void add_val(double val,  Calendar cal){
    int idx = cal.get(Calendar.YEAR);
    Year year;
    if (!hm.containsKey(idx)) {
      year = new Year(idx,limits);
      hm.put(idx,year);	
      subUnits.add(year);
    } else {
      year = hm.get(idx);
    }
    year.add_val(val,cal);
  }

  public void add_years(TimeRange tr) {
    for (HashMap.Entry<Integer, Year> entry : hm.entrySet()) {
       int idx = tr.add_year(entry.getKey());
       //TODO: check if idx == -1 really needed here ?
       tr.set_idx(Metric.YEAR, idx);
    }
  }

  public int get_idx(int i){
    if (i < 0 || i > subUnits.size()) return -1;
    return subUnits.get(i).y;
  }

  public String identifier(int id) {
    return ""; 
  }

  public <T> void accept(TreeVisitor<T> visitor, Metric m){
    visitor.visit(this,m);
  }
}
