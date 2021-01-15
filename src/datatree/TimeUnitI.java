package src.datatree;
import src.types.*;

import java.util.Calendar;
import java.util.Vector;

import java.io.FileOutputStream;
import java.io.IOException;

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

  public double get_avg(TimeRange tr){
    if (Double.isNaN(sum) ) calc(tr);
    if(num!=0){
      return sum/num;
    };
    return 0;
  }

  public double get_sum(TimeRange tr){
    if (Double.isNaN(sum)) calc(tr);
    return sum;
  }

  public abstract int get_num(TimeRange tr);

  /**
   * Returns the valid index
   * TimeRange objects expect the real year not an index into subUnits
   * @param i belongs to the ith subUnit
   * @return the index needed by a TimeRange object (which is i for all but the metric.YEAR)
   */
  public int get_idx(int i){
    return i;
  }
  public abstract double get_max(Method method, TimeRange tr, Metric metric);
  public abstract double get_min(Method method, TimeRange tr, Metric metric);

  public String identifier(int idx) {return String.valueOf(idx);}
  public abstract void print(TimeRange tr);
  public abstract void add_val(double val, Calendar cal);
  public abstract void write_to_file(String prefix, Metric metric, Method method, FileOutputStream ostream, TimeRange tr) throws IOException;
  public void write_to_file(Metric metric, Method method, FileOutputStream ostream, TimeRange tr) throws IOException {
    write_to_file("",metric,method,ostream,tr);
  }

  /**
   * Checks if the object implementing TimeUnitI is valid with regard to the limits object
   * @param metric for which to check for
   * @return true if the object contains enough valid subUnits to be valid
   */
  public abstract boolean is_valid(Metric metric);

}
