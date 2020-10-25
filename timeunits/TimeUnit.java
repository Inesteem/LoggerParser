package timeunits;

import java.util.Calendar;
import java.util.ArrayList;

import java.io.FileOutputStream;
import java.io.IOException;


public abstract class TimeUnit<T extends TimeUnitI> implements TimeUnitI {
  protected T minVal;
  protected T maxVal;
  protected double avg;
  protected double sum;
  protected int num;
  protected boolean valid;
  @SuppressWarnings("unchecked")
  protected T subUnits[];
  protected Metric metric;

  public TimeUnit(){
 //   @SuppressWarnings("unchecked")
//    subUnits = new ArrayList<T>(num);
    sum = Double.NaN;
    avg= Double.NaN;
    num = 0;
    valid=true;
  }

  public abstract void add_val(double val, Calendar cal);


  public void invalidate(){valid=false;}
  public void validate(){valid=true;}
  public boolean is_valid(){return valid;}

  public String identifier(int idx) {return String.valueOf(idx);}


  public void write_to_file(Metric metric, FileOutputStream ostream, TimeRange tr) throws IOException{
    
    if(metric != this.metric) {
      for(int idx = 0; idx < subUnits.length; ++idx){
          if (!tr.in_range(metric, idx)) continue;
          T unit = subUnits[idx];
          if(unit == null || !unit.is_valid()) continue;
          ostream.write((identifier(idx) + ":\n").getBytes());
          unit.write_to_file(metric,ostream,tr);
      }
   
      return;
    }

    ostream.write(" num min max ".getBytes());
    if(avg != Double.NaN)
      ostream.write("avg\n".getBytes());
    else
      ostream.write("sum\n".getBytes());

      ostream.write((String.valueOf(get_num()) + " ").getBytes());
      ostream.write((String.valueOf(get_min()) + " ").getBytes());
      ostream.write((String.valueOf(get_max()) + " ").getBytes());
      if(avg != Double.NaN)
        ostream.write((String.valueOf(get_avg()) + "\n").getBytes());
      else
        ostream.write((String.valueOf(get_sum()) + "\n").getBytes());

  }
  public void write_to_file(Metric metric, FileOutputStream ostream) throws IOException{
    write_to_file(metric,ostream,TimeRange.ALL);
  }


  // GETTER METHODS
  public double get_sum(final TimeRange tr){

    if (Double.isNaN(sum)) {
    sum = 0;
    num = 0;
     for(int idx = 0; idx < subUnits.length; ++idx){
          if (!tr.in_range(metric, idx)) continue;
          T unit = subUnits[idx];
          if(unit == null || !unit.is_valid()) continue;

          double val = unit.get_sum();
          sum += val;
          num += unit.get_num();

          if(minVal == null || minVal.get_sum() > val) 
            minVal = unit;
          if(maxVal == null || maxVal.get_sum() < val) 
            maxVal = unit;
        }

      }
      return sum;
  }

  public double get_avg(final TimeRange tr){

    if (Double.isNaN(avg)) {
     avg = 0;
    num = 0;
     for(int idx = 0; idx < subUnits.length; ++idx){
          if (!tr.in_range(metric, idx)) continue;
          T unit = subUnits[idx];
          if(unit == null || !unit.is_valid()) continue;

          double val = unit.get_avg();
          avg += unit.get_sum();
          num += unit.get_num();

          if(minVal == null || minVal.get_avg() > val) 
            minVal = unit;
          if(maxVal == null || maxVal.get_avg() < val) 
            maxVal = unit;
        }

      if(num!=0)avg/=num;
      }
      return avg;
  }


  public double get_avg(){
    return get_avg(TimeRange.ALL);
  }
  public double get_sum(){
    return get_sum(TimeRange.ALL);
  }


  public int get_num(){
      return num;
  }


  public T get_min(){
    return minVal;
  }


  public T get_max(){
    return maxVal;
  }


  public int get_num(int idx){
    if (subUnits.length >= idx || idx < 0 || subUnits[idx] == null) 
      return -1;
    
    return subUnits[idx].get_num();

  }


  public double get_avg(int idx){
    if (subUnits.length >= idx || idx < 0 || subUnits[idx] == null) 
      return -1;
    
    return subUnits[idx].get_avg();

  }

  public boolean has_idx(int idx){
    if (subUnits.length >= idx || idx < 0 || subUnits[idx] == null) 
      return false;
    return true;
  }

  public double get_sum(int idx){
 //   if (subUnits.size() >= idx || idx < 0 || subUnits[idx] == null) 
 //     return -1;
    
    return subUnits[idx].get_sum();

  }

  public Object get_max(int idx){
  //  if (subUnits.size() >= idx || idx < 0 || subUnits[idx] == null) 
 //     return -1;
    
    return subUnits[idx].get_max();

  }

  public Object get_min(int idx){
 //   if (subUnits.size() >= idx || idx < 0 || subUnits[idx] == null) 
//      return -1;
    
    return subUnits[idx].get_min();

  }



}
