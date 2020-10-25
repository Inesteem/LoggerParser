package timeunits;

import java.util.Calendar;

public interface TimeUnitI{
 

  public double get_avg(final TimeRange tr);
  public double get_sum(final TimeRange tr);

  public double get_avg();
  public double get_sum();
  public int get_num();
  public Object get_min();
  public Object get_max();


  public boolean is_valid();
  public void invalidate();
  public void validate();

  public void add_val(double val, Calendar cal);

}
