package src.types;


public enum Data{
  TEMP(0, "temp", "The Temperature in Degree Celsius"),
  HUM(1, "hum", "The Humidity in Percent"),
  RAIN(2, "rain", "The Amount of Rain in mm (?)"),//TODO
  FOG(3, "fog", "The Fog in ?"),//TODO
  WIND_DIR(4, "wind_dir", "The Wind Direction in ?"),//TODO
  WIND_VEL(5, "wind_vel", "The Wind Velocity in ?"), //TODO
  VOLT(6, "volt", "The Voltage in ?"), //TODO
  FREQ(7, "freq", "The Frequency in Hertz"),
  SIZE(8, "size", "The number of different data types");

  private String name;
  private String description;
  private int value;

  private Data(final int value, final String name, final String description) {
    this.value = value;
    this.name = name;
    this.description = description;
  }
  public int value() {
    return value;
  }

  public String getDescription() {
    return description;
  }

}
