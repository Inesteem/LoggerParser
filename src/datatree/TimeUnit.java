
package src.datatree;
import src.types.*;

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
        minVal[i][j] = Double.MAX_VALUE;
        maxVal[i][j] = Double.MIN_VALUE;
      }
    }
    for(T unit : subUnits) {
      if (unit != null) unit.reset();
    }

  }


  public abstract void add_val(double val, Calendar cal);

  public void calc(TimeRange tr){
    num = 0;
    sum = 0;
    for(int i = 0; i < subUnits.size(); ++i){
      if (!tr.in_range(this.metric,this.get_idx(i))) {
        continue;
      }
      T unit = subUnits.get(i);
      if(unit == null || !unit.is_valid(this.metric)) continue;
      int u_num = unit.get_num(tr);
      if (u_num <= 0) continue;
      num += u_num;
      sum += unit.get_sum(tr);
      // System.out.println(this.metric + "\t " + this.get_idx(i) + " " + unit.get_sum(tr) + " " + u_num + " " + unit.get_avg(tr));
      set_extrema(unit.get_sum(tr), Method.SUM);
      set_extrema(unit.get_avg(tr), Method.AVG);

    }
  }

  void set_extrema(double val, Method method){
    int methodI = method.value();
    int metricI = metric.value();
    if(minVal[methodI][metricI] > val)
      minVal[methodI][metricI] = val;
    if(maxVal[methodI][metricI] < val)
      maxVal[methodI][metricI] = val;
  }
  // GETTER METHODS

  public double get_min(Method method, TimeRange tr, Metric metric){
    int methodI = method.value();
    int metricI = metric.value();

    if (minVal[methodI][metricI] == Double.MAX_VALUE) {
      for(int i = 0; i < subUnits.size(); ++i){
        if (!tr.in_range(this.metric,this.get_idx(i))) continue;

        T unit = subUnits.get(i);
        if(unit == null || !unit.is_valid(this.metric)) continue;

        double min  = unit.get_min(method, tr, metric);
        if(min == Double.MAX_VALUE ) continue;
        if (min < minVal[methodI][metricI]){
          minVal[methodI][metricI] = min;
        }
      }
    }
    return minVal[methodI][metricI];
  }

  public double get_max(Method method, TimeRange tr, Metric metric){

    int methodI = method.value();
    int metricI = metric.value();

    if (maxVal[methodI][metricI] == Double.MIN_VALUE) {

      for(int i = 0; i < subUnits.size(); ++i){
        if (!tr.in_range(this.metric,this.get_idx(i))) continue;

        T unit = subUnits.get(i);
        if(unit == null || !unit.is_valid(this.metric)) continue;

        double max = unit.get_max(method, tr, metric);
        if(max == -1) continue;

        if (max > maxVal[methodI][metricI]){
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
      if (!tr.in_range(this.metric,this.get_idx(i))) continue;
      T unit = subUnits.get(i);
      if(unit != null && unit.is_valid(this.metric)) unit.print(tr);

    }
  }

  public Limits get_limits(){
    return limits;
  }

  public void set_limits(Limits lim){
    limits = lim;
    for(T unit : subUnits) {
      if (unit != null) unit.set_limits(lim);
    }
  }

  /**
   * Checks if TimeUnit is valid with regard to the limits object
   * @param metric for Hour its HOUR, for Day its DAY, ...
   * @return true if the TimeUnit contains enough valid subUnits to be valid
   */
  public boolean is_valid(Metric metric){

    int num = 0;
    for(T unit : subUnits) {
      if (unit != null) {
        if (unit.is_valid(this.metric)) ++num;
      }
    }
    return limits.valid(metric, num);
  }

  public int get_num(TimeRange tr){
    if (Double.isNaN(sum)) calc(tr);
    return num;
  }

  /**
   * Removes data specified by timeRange matching cond.
   * @param timeRange
   * @param metric the metric for which to compare
   * @param cond the remove condition
   * @param cmp the compare value used by cond
   * @return number of removed entries
   */
  public int remove(TimeRange timeRange, Metric metric, Condition cond, double cmp){
    int num = 0;
    for(int i = 0; i < subUnits.size(); ++i) {
      T unit = subUnits.get(i);
      if (unit == null || !timeRange.in_range(this.metric,get_idx(i))) continue;

      if(this.metric == metric && cond != Condition.ALL ){
        int num_vsu = unit.get_num_valid_subUnits(timeRange);
        if (cond.cond.checkFor(num_vsu, cmp)){
          num += num_vsu;
          subUnits.set(i, null);
        }
      } else {
        num += unit.remove(timeRange, metric, cond, cmp);
        if (unit.get_num_valid_subUnits(TimeRange.ALL) == 0)
          subUnits.set(i, null);
      }
    }
    return num;
  }

  /**
   * Get number of valid subUnits contained in this subtree
   * @param timeRange the TimeRange object defining valid TimeUnits
   * @return number of valid subUnits
   */
  @Override
  public int get_num_valid_subUnits(TimeRange timeRange) {
    //if (valid_subUnits != -1) return valid_subUnits;
    int num = 0;
    for(int i = 0; i < subUnits.size(); ++i) {
      if (!timeRange.in_range(metric,get_idx(i))) continue;
      T unit = subUnits.get(i);
      if (unit != null) num += unit.get_num_valid_subUnits(timeRange);
    }
   // valid_subUnits = num;
    return num;
  }
}
