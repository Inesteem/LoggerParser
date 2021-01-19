package src.datatree;
import src.types.*;
import static src.types.Method.*;

import java.util.Calendar;
import java.util.Vector;


public abstract class TimeUnitI<T>{
  protected Vector<T> subUnits;
  protected double sum;
  protected int num;
  protected Limits limits;

  public TimeUnitI(Limits limits){
    num = -1;
    sum = Double.NaN;
    this.limits = limits;
  }
  public void set_limits(Limits lim){
    limits = lim;
  } 

  public void reset(){
    num = -1;
    sum = Double.NaN;
  }

  void set_extrema(double val, Method method){}

  public void calc(TimeRange tr){}

  /**
   * Get the overall avg of all measurements contained in this subtree
   * @param timeRange the TimeRange object defining valid TimeUnits
   * @return the average of all measurements (NOT the average over all subUnits)
   */
  public double get_avg(TimeRange timeRange){
    if (Double.isNaN(sum) ) calc(timeRange);
    if(num!=0){
      return sum/num;
    };
    return 0;
  }

  /**
   * Get sum of all measurements contained in this subtree
   * @param timeRange the TimeRange object defining valid TimeUnits
   * @return the sum of measurements
   */
  public double get_sum(TimeRange timeRange){
    if (Double.isNaN(sum)) calc(timeRange);
    return sum;
  }

  /**
   * Get the combined value with respect to a given method of all measurements contained in this subtree
   * @param timeRange the TimeRange object defining valid TimeUnits
   * @param method the method by which to combine the measurements
   * @return the combined measurements
   */
  public double get_val(TimeRange timeRange, Method method){
    if (method == SUM) return get_sum(timeRange);
    return get_avg(timeRange);
  }

  /**
   * Get number of measurements contained in this subtree
   * @param timeRange the TimeRange object defining valid TimeUnits
   * @return number of measurements contained by the unit and/or the subUnits
   */
  public abstract int get_num(TimeRange timeRange);

  /**
   * Returns the valid index
   * TimeRange objects expect the real year not an index into subUnits
   * @param i belongs to the ith subUnit
   * @return the index needed by a TimeRange object (which is i for all but the metric.YEAR)
   */
  public int get_idx(int i){
    return i;
  }

  /**
   * Get number of valid subUnits contained in this subtree
   * @param timeRange the TimeRange object defining valid TimeUnits
   * @return number of valid subUnits
   */
  public int get_num_valid_subUnits(TimeRange timeRange) { return num;}

  public abstract double get_max(Method method, TimeRange tr, Metric metric);
  public abstract double get_min(Method method, TimeRange tr, Metric metric);

  public String identifier(int idx) {return String.valueOf(idx);}
  public abstract void print(TimeRange tr);
  public abstract void add_val(double val, Calendar cal);

  /**
   * Checks if the object implementing TimeUnitI is valid with regard to the limits object
   * @param metric for which to check for
   * @return true if the object contains enough valid subUnits to be valid
   */
  public abstract boolean is_valid(Metric metric);

}
