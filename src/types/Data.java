package src.types;


public enum Data{
  TEMP(0, "temp", "°C", "The Temperature in °C"),
  HUM(1, "hum", "%", "The Humidity in %"),
  RAIN(2, "rain","mm", "The Amount of Rain in mm"),//TODO
  FOG(3, "fog","", "The Fog in ?"),//TODO
  WIND_DIR(4, "wind_dir", "°", "The Wind Direction in °"),//TODO
  WIND_VEL1(5, "wind_vel1", "wind_vel.png","m/s", "The Wind Velocity in m/s"), //TODO
  WIND_VEL2(6, "wind_vel2", "wind_vel.png","m/s", "The Wind Velocity in m/s"), //TODO
  VOLT1(7, "volt1","volt.png","mV", "The Voltage in mV"), //TODO
  VOLT2(8, "volt2","volt.png","mV", "The Voltage in mV"), //TODO
  FREQ(9, "freq","Hz", "The Frequency in Hz"),
  SIZE(10, "size","", "The number of different data types");

  public final String name;
  public final String description;
  public final int value;
  public final String unit;
  public final String icon;

  private Data(final int value, final String name, String unit, final String description) {
    this.value = value;
    this.name = name;
    this.icon = name + ".png";
    this.description = description;
    this.unit = unit;
  }
  private Data(final int value, final String name, final String icon, String unit, final String description) {
    this.value = value;
    this.name = name;
    this.icon = icon;
    this.description = description;
    this.unit = unit;
  }

}
