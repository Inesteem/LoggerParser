package parser;


public enum Metric {
  YEAR(0),MONTH(1),DAY(2),HOUR(3),SIZE(4);
  // constructor
  private Metric(final int value) {
    this.value = value;
  }
  static final Integer[] minVals = {0,1,1,0};
  static final Integer[] maxVals = {0,12,31,24};
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

  public int getMinIncl() {
    return minVals[value];
  }

  public int getMaxIncl() {
    return maxVals[value];
  }

  public int getMaxExcl() {
    return maxVals[value] + 1;
  }
}
