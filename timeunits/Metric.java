package timeunits;


public enum Metric {
  MONTH(0),DAY(1),HOUR(2);
  // constructor
  private Metric(final int value) {
    this.value = value;
  }

  // internal state
  private int value;

  public int value() {
    return value;
  }
}
