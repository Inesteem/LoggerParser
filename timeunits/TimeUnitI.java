package timeunits;

import java.util.Calendar;
import java.util.Vector;

import java.io.FileOutputStream;
import java.io.IOException;

public abstract class TimeUnitI<T>{
  protected Vector<T> subUnits;
  protected double sum;
  protected int num;

  public TimeUnitI(){
    num = -1;
    sum = Double.NaN;
  }


  void set_extrema(double val, Method method){}

  protected void calc(TimeRange tr){}

  public double get_avg(TimeRange tr){
    if (Double.isNaN(sum)) calc(tr);
    if(num!=0){
      return sum/num;
    };

    return 0;
  }

  public double get_sum(TimeRange tr){
    if (Double.isNaN(sum)) calc(tr);
    return sum;
  }

  public int get_num(TimeRange tr){
    if (num == -1) calc(tr);
    return num;
  }
//
//  public int get_num(){
//    return get_num(TimeRange.ALL);
//  }
//
//  public double get_avg(){
//    return get_avg(TimeRange.ALL);
//  }
//
//  public double get_sum(){
//    return get_sum(TimeRange.ALL);
//  }

  public abstract double get_max(Method method, TimeRange tr, Metric metric);
  public abstract double get_min(Method method, TimeRange tr, Metric metric);

  public String identifier(int idx) {return String.valueOf(idx);}

  public abstract void print(TimeRange tr);
//  public void print() { print(TimeRange.ALL); }

  public abstract void add_val(double val, Calendar cal);
  public abstract void write_to_file(Metric metric, FileOutputStream ostream, TimeRange tr) throws IOException;

}
