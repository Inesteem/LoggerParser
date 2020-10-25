package timeunits;

import java.util.Calendar;
import java.util.ArrayList;

public abstract class TimeUnit<T extends TimeUnitI> implements TimeUnitI {
  protected T minVal;
  protected T maxVal;
  protected double avg;
  protected double sum;
  protected int num;
  protected boolean valid;
  @SuppressWarnings("unchecked")
  protected ArrayList<T> subUnits;
  protected Metric metric;

  public TimeUnit(){
 //   @SuppressWarnings("unchecked")
//    subUnits = new ArrayList<T>(num);
    sum = 0;
    avg= 0;
    num = 0;
    valid=true;
  }

  public abstract void add_val(double val, Calendar cal);


  public void invalidate(){valid=false;}
  public void validate(){valid=true;}
  public boolean is_valid(){return valid;}

  // GETTER METHODS
  public double get_sum(final TimeRange tr){

    if (sum == -1) {
     for(int idx = 0; idx < subUnits.size(); ++idx){
          if (!tr.in_range(metric, idx)) continue;
          T unit = subUnits.get(idx);
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

    if (avg == -1) {
     for(int idx = 0; idx < subUnits.size(); ++idx){
          if (!tr.in_range(metric, idx)) continue;
          T unit = subUnits.get(idx);
          if(unit == null || !unit.is_valid()) continue;

          double val = unit.get_avg();
          if(val==-1) continue;
          if (avg < 0) {
            avg = 0; 
            num = 0;
          }
          avg += val;
          num += unit.get_num();

          if(minVal == null || minVal.get_avg() > val) 
            minVal = unit;
          if(maxVal == null || maxVal.get_avg() < val) 
            maxVal = unit;
        }

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
    if (subUnits.size() >= idx || idx < 0 || subUnits.get(idx) == null) 
      return -1;
    
    return subUnits.get(idx).get_num();

  }


  public double get_avg(int idx){
    if (subUnits.size() >= idx || idx < 0 || subUnits.get(idx) == null) 
      return -1;
    
    return subUnits.get(idx).get_avg();

  }

  public boolean has_idx(int idx){
    if (subUnits.size() >= idx || idx < 0 || subUnits.get(idx) == null) 
      return false;
    return true;
  }

  public double get_sum(int idx){
 //   if (subUnits.size() >= idx || idx < 0 || subUnits.get(idx) == null) 
 //     return -1;
    
    return subUnits.get(idx).get_sum();

  }

  public Object get_max(int idx){
  //  if (subUnits.size() >= idx || idx < 0 || subUnits.get(idx) == null) 
 //     return -1;
    
    return subUnits.get(idx).get_max();

  }

  public Object get_min(int idx){
 //   if (subUnits.size() >= idx || idx < 0 || subUnits.get(idx) == null) 
//      return -1;
    
    return subUnits.get(idx).get_min();

  }



}
