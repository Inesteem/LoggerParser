package src.types;


public enum Data{
  TEMP(0),HUM(1),RAIN(2),FOG(3),WIND_DIR(4),WIND_VEL(5),VOLT(6),FREQ(7),SIZE(8);
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
