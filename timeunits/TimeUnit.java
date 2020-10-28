package timeunits;

import java.util.Calendar;
import java.util.Vector;

import java.io.FileOutputStream;
import java.io.IOException;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public abstract class TimeUnit<T extends TimeUnitI> extends TimeUnitI<T> {
  protected double minVal[][];
  protected double maxVal[][];
  public Metric metric;
  public DecimalFormat df;	

  public TimeUnit(int num, Limits limits){
    super(limits);
    subUnits = new Vector<T>(num);
    for(int i = 0; i < num; ++i)
      subUnits.add(null);

    minVal = new double[Method.SIZE.value()][Metric.SIZE.value()];
    maxVal = new double[Method.SIZE.value()][Metric.SIZE.value()];

    Locale locale = new Locale("en","UK");
    df = (DecimalFormat) NumberFormat.getNumberInstance(locale);
    df.applyPattern("##.##");
    reset();
  }

  public void reset(){
    super.reset();
    for(int i = 0; i < minVal.length; ++i){
      for(int j = 0; j < minVal[0].length; ++j){
        minVal[i][j] = Double.NaN;
        maxVal[i][j] = Double.NaN;
      }
    }
    for(T unit : subUnits) {
      if (unit != null) unit.reset();
    }

  }

  public abstract void add_val(double val, Calendar cal);

  protected void calc(TimeRange tr){
    //    if( !Double.isNaN(sum)) return;

    num = 0;
    sum = 0;
    for(int i = 0; i < subUnits.size(); ++i){
      if (!tr.in_range(this.metric, i)) continue;
      T unit = subUnits.get(i);
      if(unit == null) continue;
      int u_num = unit.get_num(tr);
      if(!unit.is_valid()) continue;
      if (u_num <= 0) continue;
      num += u_num;
      sum += unit.get_sum(tr);
      set_extrema(unit.get_sum(tr), Method.SUM);
      set_extrema(unit.get_avg(tr), Method.AVG);

    }

  }

  public void write_to_file(String prefix, Metric metric, Method method, FileOutputStream ostream, TimeRange tr) throws IOException{
    if(this.metric == Metric.YEAR) {
      ostream.write(("Values at " + String.valueOf(metric.getPrev()) + " level:\n\n").getBytes());
    } else {
      prefix += " ";
    }
    if(metric != this.metric) {
      for(int idx = 0; idx < subUnits.size(); ++idx){
        if (!tr.in_range(this.metric, idx)) continue;
        T unit = subUnits.get(idx);
        if(unit == null || (unit.get_num(tr) < 0 || !unit.is_valid())) continue;

        ostream.write((prefix + unit.identifier(idx) + ":\n").getBytes());
        unit.write_to_file(prefix,metric,method,ostream,tr);
      }

      return;
    }

    ostream.write((prefix+"num: " +String.valueOf(get_num(tr)) + " ").getBytes());
    ostream.write((prefix+"min: " +df.format(get_min(method)) + " ").getBytes());
    ostream.write((prefix+"max: " +df.format(get_max(method)) + " ").getBytes());
    if(method == Method.SUM)
      ostream.write((prefix+"val: " +df.format(get_sum(tr)) + "\n").getBytes());
    else
      ostream.write((prefix+"val: " +df.format(get_avg(tr)) + "\n").getBytes());
    ostream.write("\n".getBytes());
  }


  public void write_to_file(Metric metric, Method method, FileOutputStream ostream) throws IOException{
    write_to_file(" ", metric,method, ostream,TimeRange.ALL);
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

  public double get_min(Method method, TimeRange tr, Metric metric){

    int methodI = method.value();
    int metricI = metric.value();

    if (Double.isNaN(minVal[methodI][metricI])) {
      minVal[methodI][metricI] = Double.MAX_VALUE;


      for(int i = 0; i < subUnits.size(); ++i){
        if (!tr.in_range(this.metric, i)) continue; 
        T unit = subUnits.get(i);
        if(unit == null) continue;

        double min  = unit.get_min(method, tr, metric); 
        if(!unit.is_valid()) continue;

        if (Double.isNaN(minVal[methodI][metricI]) || min < minVal[methodI][metricI]){
          minVal[methodI][metricI] = min;
        }
      }
    }
    return minVal[methodI][metricI];
  }


  public double get_max(Method method, TimeRange tr, Metric metric){

    int methodI = method.value();
    int metricI = metric.value();

    if (Double.isNaN(maxVal[methodI][metricI])) {
      maxVal[methodI][metricI] = -1;

      for(int i = 0; i < subUnits.size(); ++i){
        if (!tr.in_range(this.metric, i)) continue; 
        T unit = subUnits.get(i);
        if(unit == null) continue;

        double max  = unit.get_max(method, tr, metric);
        if(!unit.is_valid()) continue;

        if (Double.isNaN(maxVal[methodI][metricI]) 
            || max > maxVal[methodI][metricI]){
          maxVal[methodI][metricI] = max;
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

  public void print(TimeRange tr){

    for(int i = 0; i < subUnits.size(); ++i){
      if (!tr.in_range(this.metric, i)) continue;
      T unit = subUnits.get(i);
      if(unit != null && unit.is_valid()) unit.print(tr);

    }
  }
  public boolean is_valid(){
    return limits.valid(this.metric, num);
  }

  public void set_limits(Limits lim){
    limits = lim;
    for(T unit : subUnits) {
      if (unit != null) unit.set_limits(lim);
    }
  }
}
