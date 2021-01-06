package parser;


public enum Data{
  TEMP(0),HUM(1),RAIN(2),FOG(3),WIND(4),VOLT(5),SIZE(6);
  // constructor
  private Data(final int value) {
    this.value = value;
  }

  // internal state
  private int value;

  public int value() {
    return value;
  }

}
