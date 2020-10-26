package timeunits;

import java.util.Calendar;
import java.util.ArrayList;

import java.io.FileOutputStream;
import java.io.IOException;


public abstract class TimeUnit<T extends TimeUnitI> implements TimeUnitI {
  protected double minVal[][];
  protected double maxVal[][];
  protected double avg;
  protected double sum;
  protected int num;
  protected boolean valid;
  @SuppressWarnings("unchecked")
  protected T subUnits[];
  protected Metric metric;

  public TimeUnit(){
    minVal = new double[Method.SIZE.value()][Metric.SIZE.value()];
    maxVal = new double[Method.SIZE.value()][Metric.SIZE.value()];
    for(int i = 0; i< minVal.length; ++i){
      for(int j = 0; j< minVal[0].length; ++j){
        minVal[i][j] = Double.NaN;
        maxVal[i][j] = Double.NaN;
      }
    }

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
    
//    if(metric != this.metric) {
//      for(int idx = 0; idx < subUnits.length; ++idx){
//          if (!tr.in_range(metric, idx)) continue;
//          T unit = subUnits[idx];
//          if(unit == null || !unit.is_valid()) continue;
//          ostream.write((identifier(idx) + ":\n").getBytes());
//          unit.write_to_file(metric,ostream,tr);
//      }
//   
//      return;
//    }
//
//    ostream.write(" num min max ".getBytes());
//    if(Double.isNaN(avg))
//      ostream.write("avg\n".getBytes());
//    else
//      ostream.write("sum\n".getBytes());
//
//      ostream.write((String.valueOf(get_num()) + " ").getBytes());
//      ostream.write((String.valueOf(get_min()) + " ").getBytes());
//      ostream.write((String.valueOf(get_max()) + " ").getBytes());
//      if(Double.isNaN(avg))
//        ostream.write((String.valueOf(get_sum()) + "\n").getBytes());
//      else
//        ostream.write((String.valueOf(get_avg()) + "\n").getBytes());
//
  }
  public void write_to_file(Metric metric, FileOutputStream ostream) throws IOException{
    write_to_file(metric,ostream,TimeRange.ALL);
  }


  // GETTER METHODS
  public double get_sum(final TimeRange tr){
    int methodI = Method.SUM.value();
    int metricI = metric.value();
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

          if(Double.isNaN(minVal[methodI][metricI]) || minVal[methodI][metricI] > val) 
            minVal[methodI][metricI] = val;
          if(Double.isNaN(maxVal[methodI][metricI]) || maxVal[methodI][metricI] < val) 
            maxVal[methodI][metricI] = val;
        }

      }
      return sum;
  }

  public double get_avg(final TimeRange tr){
    int methodI = Method.AVG.value();
    int metricI = metric.value();

    if (Double.isNaN(avg)) {
     avg = 0;
     num = 0;
     for(int idx = 0; idx < subUnits.length; ++idx){
          if (!tr.in_range(metric, idx)) continue;
          T unit = subUnits[idx];
          if(unit == null || !unit.is_valid()) continue;

          avg += unit.get_sum();
          num += unit.get_num();
          double val = unit.get_avg();

          if(Double.isNaN(minVal[methodI][metricI]) || minVal[methodI][metricI] > val) 
            minVal[methodI][metricI] = val;
          if(Double.isNaN(maxVal[methodI][metricI]) || maxVal[methodI][metricI] < val) 
            maxVal[methodI][metricI] = val;
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

  public double get_min(Metric metric,Method method){

    int methodI = method.value();
    int metricI = metric.value();

    if (Double.isNaN(minVal[methodI][metricI])) {

      for (T unit : subUnits){
        if(unit == null) continue;

        if (Double.isNaN(minVal[methodI][metricI]) ||
          unit.get_min(metric,method) < minVal[methodI][metricI]){
            minVal[methodI][metricI] = unit.get_min(metric,method);
        }
      }
    }
    return minVal[methodI][metricI];
  }


  public double get_max(Metric metric,Method method){

    int methodI = method.value();
    int metricI = metric.value();

    if (Double.isNaN(maxVal[methodI][metricI])) {

      for (T unit : subUnits){
        if(unit == null) continue;

        if (Double.isNaN(maxVal[methodI][metricI]) ||
          unit.get_max(metric,method) > maxVal[methodI][metricI]){
            maxVal[methodI][metricI] = unit.get_max(metric,method);
        }
      }
    }
    return maxVal[methodI][metricI];
  }

  public int get_num(){
      return num;
  }


  public double get_min(Method method){
    return minVal[method.value()][metric.value()];
  }


  public double get_max(Method method){
    return maxVal[method.value()][metric.value()];
  }

}
