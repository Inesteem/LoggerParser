package src.datatree;
import src.types.*;
/**
 * The Limits class restricts the elements in a TimeUnit object
 * that are used to calculate the average or sum.
 * For example, calculating the temperature average of a month using only
 * 1 day of logger data is not really significant.
 * Setting the monthly limit to at least half of the days will ensure that
 * averaged values are meaningful - or absent.
 */

public class Limits {
  private int limits[];

  public Limits(){
    limits = new int[Metric.SIZE.value()];
    for(int i = 0; i < limits.length; ++i) limits[i] = 1;
  }

  /**
   * Checks if the number of measurements for a specific metric is sufficient
   * @param m the metric
   * @param num the number of measurements (incl)
   * @return true if the number of measurements is significant
   */
  public boolean valid(Metric m, int num) {
    return (num >= limits[m.value()]);
  }

  /**
   * Defines the minimal number of measurements needed for a metric to calculate
   * a significant average or sum
   * @param m the metric
   * @param lim the minimal number if measurements required for a valid calculation
   */
  public void set_limit(Metric m, int lim) {
    limits[m.value()]=lim;
  }

//debugging
  public void print(){
    for(int i = 0; i < limits.length; ++i) System.out.print(String.valueOf(limits[i]) + " ");
    System.out.println("");
  }
}
