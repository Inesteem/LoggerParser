package timeunits;


public enum Metric {
  YEAR(0),MONTH(1),DAY(2),HOUR(3),SIZE(4);
  // constructor
  private Metric(final int value) {
    this.value = value;
  }

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

}
