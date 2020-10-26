package timeunits;

import java.util.Calendar;
import java.util.ArrayList;


import java.io.FileOutputStream;
import java.io.IOException;

public class Hour extends TimeUnitI<Double> {
  double min;
  double max;


  public Hour(){
    sum = 0;
    min = Double.MAX_VALUE;
    max = 0;
  }

  public double get_min(Metric m,Method m2){
    return min;
  }


  public double get_max(Metric m,Method m2){
    return max;
  }

  public void add_val(double val, TimeRange tr, Calendar cal){
    sum += val;
    ++num;

    if(min > val) min = val;
    if(max < val) max = val;
  }

  public void write_to_file(Metric metric, FileOutputStream ostream, TimeRange tr) throws IOException{
    return;
  }

  public void print(){
    System.out.print(num);
    System.out.print(" - ");
    System.out.print(sum);
    System.out.print("    ");
  }
}
