package timeunits;

import java.util.Calendar;
import java.util.Vector;

import java.io.FileOutputStream;
import java.io.IOException;


public abstract class TimeUnit<T extends TimeUnitI> extends TimeUnitI<T> {
  protected double minVal[][];
  protected double maxVal[][];
  @SuppressWarnings("unchecked")
    protected Metric metric;

  public TimeUnit(int num){
    if(num != 0){
      subUnits = new Vector<T>(num);
      for(int i = 0; i < num; ++i)
        subUnits.add(null);
    }

    minVal = new double[Method.SIZE.value()][Metric.SIZE.value()];
    maxVal = new double[Method.SIZE.value()][Metric.SIZE.value()];
    for(int i = 0; i< minVal.length; ++i){
      for(int j = 0; j< minVal[0].length; ++j){
        minVal[i][j] = Double.NaN;
        maxVal[i][j] = Double.NaN;
      }
    }

    num = 0;
  }

  public abstract void add_val(double val, TimeRange tr, Calendar cal);

  protected void calc(){
    if( !Double.isNaN(sum)) return;

    num = 0;
    sum = 0;
    for(T unit : subUnits){
      if(unit == null) continue;

      sum += unit.get_sum();
      num += unit.get_num();
      set_extrema(unit.get_sum(), Method.SUM);
      set_extrema(unit.get_avg(), Method.AVG);

    }

  }

  public void write_to_file(Metric metric, FileOutputStream ostream, TimeRange tr) throws IOException{

    //    if(metric != this.metric) {
    //      for(int idx = 0; idx < subUnits.length; ++idx){
    //          if (!tr.in_range(metric, idx)) continue;
    //          T unit = subUnits.get(idx);
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

  void set_extrema(double val, Method method){
    int methodI = method.value();
    int metricI = metric.value();
    if(Double.isNaN(minVal[methodI][metricI]) || minVal[methodI][metricI] > val) 
      minVal[methodI][metricI] = val;
    if(Double.isNaN(maxVal[methodI][metricI]) || maxVal[methodI][metricI] < val) 
      maxVal[methodI][metricI] = val; 
  }
  // GETTER METHODS

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


  public double get_min(Method method){
    return minVal[method.value()][metric.value()];
  }


  public double get_max(Method method){
    return maxVal[method.value()][metric.value()];
  }

  public void print(){
    for(T unit : subUnits) if(unit != null) unit.print();
  }
}
