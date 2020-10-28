package timeunits;

import java.util.Calendar;
import java.util.ArrayList;


import java.io.FileOutputStream;
import java.io.IOException;

public class Hour extends TimeUnitI<Double> {
  double min;
  double max;


  public Hour(){
    super();
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
}
