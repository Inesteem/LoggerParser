package plotting;


public enum PlotRange{
  MONTHS(0),SIZE(1);
  // constructor
  private PlotRange(final int value) {
    this.value = value;
  }

  // internal state
  private int value;

  public int value() {
    return value;
  }

}
