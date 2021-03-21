package src.types;
import static src.types.Method.AVG;
import static src.types.Method.SUM;

public enum Data{
  TEMP(0, AVG,  -200,200, "temp", "°C", "The Temperature in °C"),
  TEMP_PT100(1, AVG,  -200,200, "tempPT100", "°C", "The Temperature in °C (PT100)"), //TODO
  TEMP_PT1000(2, AVG,  -200,200, "tempPT1000", "°C", "The Temperature in °C (PT1000)"), //TODO
  HUM(3, AVG, 0,100, "hum", "%", "The Humidity in %"),
  RAIN(4, SUM, 0,10000, "rain","mm", "The Amount of Rain in mm"),//TODO
  FOG(5, SUM, 0,10000, "fog","", "The Fog in ?"),//TODO
  WIND_DIR(6, AVG, 0, 360, "wind_dir", "°", "The Wind Direction in °"),//TODO
  WIND_VEL1(7, AVG, 0,500000, "wind_vel1", "wind_vel.png","m/s", "The Wind Velocity in m/s"), //TODO
  WIND_VEL2(8, AVG, 0,500000,"wind_vel2", "wind_vel.png","m/s", "The Wind Velocity in m/s"), //TODO
  VOLT1(9, AVG, 0,1000000,"volt1","volt.png","mV", "The Voltage in mV"), //TODO
  VOLT2(10, AVG, 0,1000000, "volt2","volt.png","mV", "The Voltage in mV"), //TODO
  VOLT3(11, AVG, 0,1000000, "volt3","volt.png","mV", "The Voltage in mV (DVM)"), //TODO
  VOLT4(12, AVG, 0,1000000, "volt4","volt.png","mV", "The Voltage in mV (DVM)"), //TODO
  FREQ(13, AVG, 0,100000000,"freq","Hz", "The Frequency in Hz"),
  BAR(14, AVG, 0,100000000,"bar_press","hPa", "The barometric pressure in hPa"),
  SIZE(15,Method.SIZE, 0,0,"", "","The number of different data types");

  public final String name;
  public final String description;
  public final int value;
  public final String unit;
  public final String icon;
  public final int min;
  public final int max;
  public final Method method;

  private Data(final int value, Method method, final int min, final int max, final String name, String unit, final String description) {
    this.value = value;
    this.name = name;
    this.icon = name + ".png";
    this.description = description;
    this.unit = unit;
    this.min = min;
    this.max = max;
    this.method = method;
  }
  private Data(final int value, Method method,  final int min, final int max, final String name, final String icon, String unit, final String description) {
    this.value = value;
    this.name = name;
    this.icon = icon;
    this.description = description;
    this.unit = unit;
    this.min = min;
    this.max = max;
    this.method = method;
  }

}
