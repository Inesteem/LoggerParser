package timeunits;

import java.util.Calendar;
import java.util.Collection;

import java.io.FileOutputStream;
import java.io.IOException;

public abstract class TimeUnitI<T>{
  protected Collection<T> subUnits;
  protected double sum;
  protected int num;

  public TimeUnitI(){
    num=-1;
    sum = Double.NaN;
  }

  public int get_num(){
    if (num == -1) calc();
    return num;
  }

  void set_extrema(double val, Method method){}

  protected void calc(){}

  public double get_avg(){
    if (Double.isNaN(sum)) calc();
    if(num!=0){
      return sum/num;
    };

    return 0;
  }

  public double get_sum(){
    if (Double.isNaN(sum)) calc();
    return sum;
  }


  public abstract double get_min(Metric m, Method m2);
  public abstract double get_max(Metric m, Method m2);

  public String identifier(int idx) {return String.valueOf(idx);}

  public abstract void print();

  public abstract void add_val(double val, TimeRange tr, Calendar cal);
  public abstract void write_to_file(Metric metric, FileOutputStream ostream, TimeRange tr) throws IOException;

}
