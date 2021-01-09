package parser;

import java.util.Calendar;
import java.util.Vector;

import java.io.FileOutputStream;
import java.io.IOException;

public abstract class TimeUnitI<T>{
  protected Vector<T> subUnits;
  protected double sum;
  protected int num;
  protected Limits limits;
  int numSubUnits;

  public TimeUnitI(Limits limits){
    num = -1;
    sum = Double.NaN;
    this.limits = limits;
    numSubUnits = 0;
  }

  public int get_num_subUnits(){ 
      return java.lang.Integer.bitCount(numSubUnits);
  }

  public void set_limits(Limits lim){
    limits = lim;
  } 

  public void reset(){
    num = -1;
    sum = Double.NaN;
    numSubUnits = 0;
  }

  void set_extrema(double val, Method method){}

  protected void calc(TimeRange tr){}

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

  public int get_num(TimeRange tr){
    if (Double.isNaN(sum)) calc(tr);
    return num;
  }

  /**
   * Returns the valid index
   * TimeRange objects expect the real year not an index into subUnits
   * @param i belongs to the ith subUnit
   * @return the index needed by a TimeRange object (which is i for all but the metric.YEAR)
   */
  public int get_idx(int i){
    return i;
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
  public abstract void write_to_file(String prefix, Metric metric, Method method, FileOutputStream ostream, TimeRange tr) throws IOException;
  public void write_to_file(Metric metric, Method method, FileOutputStream ostream, TimeRange tr) throws IOException {
    write_to_file("",metric,method,ostream,tr);
  }
  public boolean is_valid(){return true;}  
}
