/** From all the things that are disgusting in this project the data tree is the most repelling one.
 * Yes, even more awful than the gui stuff.
 * Before you can use the data tree, you need to fill it with add_data. That's ok, I guess.
 * After the tree is filled up with data, you need to specify limits (which asserts for example that
 * hours are only valid if they contain more than n measurements). This is done by set_limits and checked
 * by is_valid. Than a TimeRange needs to be applied. Since the TimeRange does not know which years are contained
 * inside the datatree (in contrast to the number of hours, days or months) they need to be added first before they
 * can be constrained. So the data tree function add_years is called upon the TimeRange object. After that, the used
 * years can be constrained for example by using set_idx. SET_IDX WILL NOT WORK BEFORE THE YEARS ARE ADDED! Brilliant.
 * Why didn't I just assume that no more than 64 years ending with the current year are valid? I mean nobody whants to add
 * 1950 log data, not using this program anyways. Yes, I guess that holds true. But I wanted the program to be more general.
 * I traded usability for better generalization.
 * For hours, days and months this is no problem by the way, since there are always max 31 days and so on. I could just hard-
 * code them.
 * Before the functions get_num, get_avg, get_sum ,get_min and get_max work calc needs to be called first with a valid
 * timeRange object. You cannot call any of this functions or even calc again using another TimeRange object.
 * First, reset needs to be called. Yes, bad design. But that's because of the DynamicProgramming Concept used to improve efficiency.
 * I traded usability for efficiency. I am sorry.
 * Why did I use a TimeUnitI interface deriving TimeUnit? Why do Hours only implement TimeUnitI while days, months and years
 * all derive from TimeUnit? I don't know any more. There is a stupid reason for this shit but I do not remember. Probably I
 * wanted to save some memory, so I traded usability for an improved memory usage.
 * Again, I am sorry. It's all crap but it works, so I wont change it. Let's just hate this peace of shit together.
 *
 * So, a valid usage would be
 * YearMap ym = ...//fill the tree
 * TimeRange tr = new TimeRange(~1l); //allow all hours, days, months and years
 * ym.add_years(tr);
 * tr.unset_idx(YEAR,2020); //unset 2020. Nobody likes 2020. Fuck you, Corona.
 * ym.calc(tr);
 * //use the tree by, e.g. getting the min (get_min(tr)), etc...
 *
 * tr.reset()
 * tr.unset_idx(YEAR,2021); //unset 2021 will suck probably too, so lets unset it
 * ym.calc(tr);
 *
 * */

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
        if (!tr.in_range(this.metric,this.get_idx(idx))) continue;
        T unit = subUnits.get(idx);
        if(unit == null || (unit.get_num(tr) < 0 || !unit.is_valid(this.metric))) continue;

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

  public void set_limits(Limits lim){
    limits = lim;
    for(T unit : subUnits) {
      if (unit != null) unit.set_limits(lim);
    }
  }

  public int get_num_subUnits(){
      return java.lang.Long.bitCount(numSubUnits);
  }
}
