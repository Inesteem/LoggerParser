package src.types;

import static src.types.Method.AVG;
import static src.types.Method.SUM;

public class Data{

  public static final Data TEMP = new Data(0, AVG,  -200,200, "temp", "°C", "The Temperature in °C");
  public static final Data TEMP_PT100 = new Data(1, AVG,  -200,200, "temp_PT100", "temp.png","°C", "The Temperature in °C (PT100)"); //TODO
  public static final Data TEMP_PT1000 = new Data(2, AVG,  -200,200, "temp_PT1000", "temp.png", "°C", "The Temperature in °C (PT1000)"); //TODO
  public static final Data HUM = new Data(3, AVG, 0,100, "hum", "%", "The Humidity in %");
  public static final Data RAIN = new Data(4, SUM, 0,10000, "rain","mm", "The Amount of Rain in mm");//TODO
  public static final Data RAIN_SUM = new Data(5, SUM, 0,10000, "rain","mm", "The Amount of Rain in mm");//TODO
  public static final Data FOG = new Data(6, SUM, 0,10000, "fog","", "The Fog in ?");//TODO
  public static final Data WIND_DIR = new Data(7, AVG, 0, 360, "wind_dir", "°", "The Wind Direction in °");//TODO
  public static final Data WIND_VEL1 = new Data(8, AVG, 0,500000, "wind_vel1", "wind_vel.png","m/s", "The Wind Velocity in m/s"); //TODO
  public static final Data WIND_VEL2 = new Data(9, AVG, 0,500000,"wind_vel2", "wind_vel.png","m/s", "The Wind Velocity in m/s"); //TODO
  public static final Data VOLT1 = new Data(10, AVG, 0,1000000,"volt1","volt.png","mV", "The Voltage in mV"); //TODO
  public static final Data VOLT2 = new Data(11, AVG, 0,1000000, "volt2","volt.png","mV", "The Voltage in mV"); //TODO
  public static final Data VOLT3 = new Data(12, AVG, 0,1000000, "volt3","volt.png","mV", "The Voltage in mV (DVM)"); //TODO
  public static final Data VOLT4 = new Data(13, AVG, 0,1000000, "volt4","volt.png","mV", "The Voltage in mV (DVM)"); //TODO
  public static final Data FREQ = new Data(14, AVG, 0,100000000,"freq","Hz", "The Frequency in Hz");
  public static final Data BAR = new Data(15, AVG, 0,100000000,"bar_press","hPa", "The barometric pressure in hPa");
  public static final Data SIZE = new Data(16,Method.SIZE, 0,0,"", "","The number of different data types");


  public final String name;
  public final String description;
  public final int value;
  public final String unit;
  public final String icon;
  public final int min;
  public final int max;
  public final Method method;


  private Data(final int value, Method method,  final int min, final int max, final String name, final String icon, String unit, final String description) {
    this.value = value;
    this.name = name.toUpperCase();
    this.icon = icon;
    this.description = description;
    this.unit = unit;
    this.min = min;
    this.max = max;
    this.method = method;
  }

  private Data(final int value, Method method, final int min, final int max, final String name, String unit, final String description) {
    this(value,method, min, max, name, name+".png", unit, description);
  }

  @Override
  public String toString() {
    return name;
  }
}
