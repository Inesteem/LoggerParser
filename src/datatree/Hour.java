package src.datatree;
import src.types.*;

import java.util.Calendar;
import java.io.FileOutputStream;
import java.io.IOException;

import static src.types.Metric.HOUR;

public class Hour extends TimeUnitI<Double> {
  double min;
  double max;


  public Hour(Limits limits){
    super(limits);
    sum = 0;
    min = Double.MAX_VALUE;
    max = 0;
    num = 0;
  }

  public double get_max(Method method, TimeRange tr, Metric metric){
    return max;
    }
  
  public double get_min(Method method, TimeRange tr, Metric metric){
    return min;
  }

  public void add_val(double val,  Calendar cal){
    sum += val;
    ++num;

    if(min > val) min = val;
    if(max < val) max = val;
  }

  public void write_to_file(String pfx, Metric metric, Method method, FileOutputStream ostream, TimeRange tr) throws IOException{
    return;
  }

  public int get_num(TimeRange timeRange) {
    return num;
  }

  /**
   * Checks if the Hour object is valid with regard to the limits object
   * @param metric not used here since it clearly is HOUR
   * @return true if the object contains enough valid subUnits to be valid
   */
  public boolean is_valid(Metric metric) {
    return limits.valid(HOUR, num);
  }
  public void invalidate(Limits lim){}
  public void reset() {}
  public void print(TimeRange tr){
    System.out.print(num);
    System.out.print(", ");
    System.out.print(sum);
    System.out.print(" --- ");
    //System.out.print(min);
    //System.out.print("    ");
    //System.out.print(max);
    //System.out.print("    ");
  }

  /**
   * Removes data specified by timeRange matching cond.
   * @param timeRange
   * @param metric the metric for which to compare
   * @param cond the remove condition
   * @param cmp the compare value used by cond
   * @return number of removed entries
   */
  public int remove(TimeRange timeRange, Metric metric, Condition cond, double cmp){
    sum = 0;
    num = 0;
    return 1;
  }
  @Override
  public boolean matches(Metric metric){
    return metric == HOUR;
  }

  @Override
  public int get_num_valid_subUnits(TimeRange timeRange, Metric metric){
    assert(false);
    return 0;
  }
}
