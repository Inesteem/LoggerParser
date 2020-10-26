package timeunits;

import java.util.Calendar;

import java.io.FileOutputStream;
import java.io.IOException;

public interface TimeUnitI{
 

  public double get_avg(final TimeRange tr);
  public double get_sum(final TimeRange tr);

  public double get_avg();
  public double get_sum();
  public int get_num();
  public double get_min(Metric m, Method m2);
  public double get_max(Metric m, Method m2);


  public boolean is_valid();
  public void invalidate();
  public void validate();

  public void add_val(double val, Calendar cal);
  public void write_to_file(Metric metric, FileOutputStream ostream, TimeRange tr) throws IOException;
  
  public String identifier(int idx);
}
