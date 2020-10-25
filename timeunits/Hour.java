package timeunits;

import java.util.Calendar;
import java.util.ArrayList;


import java.io.FileOutputStream;
import java.io.IOException;

public class Hour implements TimeUnitI {
  double sum;
  double min;
  double max;

  int num;
  boolean valid;

  public Hour(){
    sum = 0;
    num = 0;
    min = Double.MAX_VALUE;
    max = 0;
    valid=true;
  }

  public double get_avg(){
    if(num == 0) return 0;
    return sum/num;  
  }

  public double get_sum(){
    return sum;
  }
  public int get_num(){
    return num;
  }

  public Hour get_min(){
    return this;
  }


  public Hour get_max(){
    return this;
  }
  public boolean is_valid(){return valid;}
  public void invalidate(){valid=false;}
  public void validate(){valid=true;}

  public void add_val(double val, Calendar cal){
     sum += val;
     ++num;

     if(min > val) min = val;
     if(max < val) max = val;
  }


  public double get_avg(final TimeRange tr){
    return get_avg();
  }
  public double get_sum(final TimeRange tr){
    return get_sum();
  }


  public String identifier(int idx) {return String.valueOf(idx);}
 

  public void write_to_file(Metric metric, FileOutputStream ostream, TimeRange tr) throws IOException{
    return;
  }

  }
