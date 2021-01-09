package parser;

import java.util.Calendar;

public enum Metric {
  YEAR(0),MONTH(1),DAY(2),HOUR(3),SIZE(4);
  // constructor
  private Metric(final int value) {
    this.value = value;
  }
  static final Integer[] minVals = {1950,0,0,0};
  static final Integer[] maxVals = {Calendar.getInstance().get(Calendar.YEAR),11,30,23};
  static final Integer[] minValsUser = {1950,1,1,0};
  static final Integer[] maxValsUser = {Calendar.getInstance().get(Calendar.YEAR),12,31,23};
  // internal state
  private int value;

  public int value() {
    return value;
  }

  public String getPrev() {
    if(this == Metric.MONTH){return "Year";}
    if(this == Metric.DAY){return "Month";}
    if(this == Metric.HOUR){return "Day";}
    return "Summary";
  }

  public boolean inRange(int val) {
    return val>= minVals[value] && val <= maxVals[value];
  }

  public static Metric getEnum(int m){
    return Metric.values()[m];
  }

  public int getMinIncl() {
    return minVals[value];
  }

  public int getMaxIncl() {
    return maxVals[value];
  }

  public int getMaxExcl() {
    return maxVals[value] + 1;
  }


  public int getUserMinIncl() {
    return minValsUser[value];
  }

  public int getUserMaxIncl() {
    return maxValsUser[value];
  }

  public int getUserMaxExcl() {
    return maxValsUser[value] + 1;
  }


}
