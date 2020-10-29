// javac -cp "\.;lib\*;" parser/*.java LoggerParser.java
//	java -cp "\.;lib\*;" LoggerParser
//	jar -cmvf Manifest.txt LoggerParser.jar *.class .\lib\*
//	jar -cmvf Manifest.txt LoggerParser.jar *.class .\parser\*.class .\lib\*
import timeunits.*;


import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
//String.format("%02d", myNumber)

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;


public class  Tester {

  public static void print(String msg, double d){
    System.out.println(msg + String.valueOf(d));
  }

  public static void main(String[] args) {


    try {
      Runtime.getRuntime().exec("PlotFiles.py " + filename);
    } catch (IOException ex) {// catch ex  
    }
    return;

    Calendar calendar = GregorianCalendar.getInstance();
    calendar.setTimeZone(TimeZone.getTimeZone("Africa/Nairobi"));
    SimpleDateFormat date_format;
    date_format = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
    YearMap dataMap = new YearMap(new Limits());

    String dates[] = {"01.01.01 00:00:00", "01.01.01 01:00:00","01.01.01 02:00:00","01.02.01 00:00:00", "01.03.01 01:00:00","01.04.01 02:00:00","02.04.01 02:00:00","02.04.02 03:00:00"};
    double values[] = {1,2,3,14,5,6,3,4};

    TimeRange tr = new TimeRange(0xFFFFFFFF);
//    tr.unset_range(Metric.MONTH,1,3);
    for(int i = 0; i < dates.length; ++i) {
      try {
        Date date = date_format.parse(dates[i]);
        calendar.setTime(date);  
        System.out.println(dates[i]);
        print("Day: ", calendar.get(Calendar.DAY_OF_MONTH));
        print("mon: ", calendar.get(Calendar.MONTH));
        dataMap.add_val(values[i],calendar);
      } catch(Exception e){
        System.out.println("parseexception"); 
        e.printStackTrace();
        continue;
      }
    }
    System.out.println("\n"); 
    print("AVG: ", dataMap.get_avg(tr)); 
    print("NUM: ", dataMap.get_num(tr)); 
    print("SUM: ", dataMap.get_sum(tr));
    Method meths[]={Method.AVG,Method.SUM};
    for( Method m : meths){
      System.out.println("\n"+m+"\n");
      print("MIN - HOUR: ", dataMap.get_min(m,tr,Metric.HOUR)); 
      print("MAX - HOUR: ", dataMap.get_max(m,tr,Metric.HOUR));
      System.out.println("");
      print("MIN - DAY: ", dataMap.get_min(m,tr,Metric.DAY)); 
      print("MAX - DAY: ", dataMap.get_max(m,tr,Metric.DAY)); 
      System.out.println("");
      print("MIN - MONTH: ", dataMap.get_min(m,tr,Metric.MONTH)); 
      print("MAX - MONTH: ", dataMap.get_max(m,tr,Metric.MONTH)); 
      System.out.println("");
      print("MIN - YEAR: ", dataMap.get_min(m,tr,Metric.YEAR)); 
      print("MAX - YEAR: ", dataMap.get_max(m,tr,Metric.YEAR)); 
    }
    System.out.println("");
    dataMap.print(tr);
    System.out.println("");


    File filename = new File("test.txt");  
    FileOutputStream outputStream; 
    try{
      outputStream = new FileOutputStream(filename);
      dataMap.write_to_file(Metric.HOUR,Method.AVG,outputStream, tr);
      outputStream.write("\n".getBytes());
      dataMap.write_to_file(Metric.DAY,Method.AVG,outputStream, tr);
      outputStream.write("\n".getBytes());
      dataMap.write_to_file(Metric.MONTH,Method.AVG,outputStream, tr);
      outputStream.write("\n".getBytes());
      dataMap.write_to_file(Metric.YEAR,Method.AVG,outputStream, tr);
      outputStream.close();
    } catch(IOException e) {
      e.printStackTrace();
    }

    Method method = meths[0];
    tr.unset_range(Metric.MONTH,0,13);
    for(int i = 0; i < 12; ++i){
      tr.set_idx(Metric.MONTH,i);
      dataMap.reset(); 
      System.out.print((Month.toString(i) + ": "));
      if (dataMap.get_num(tr) == 0){
      System.out.println((" - \n"));
        continue;
      }
      System.out.println(("\n num: " +String.valueOf(dataMap.get_num(tr)) + " "));
      System.out.println((" min: " +dataMap.df.format(dataMap.get_min(method)) + " "));
      System.out.println((" max: " +dataMap.df.format(dataMap.get_max(method)) + " "));
      if(method == Method.SUM)
        System.out.println((" val: " +dataMap.df.format(dataMap.get_sum(tr)) + "\n"));
      else
        System.out.println((" val: " +dataMap.df.format(dataMap.get_avg(tr)) + "\n"));
      tr.unset_idx(Metric.MONTH,i);
      } 


    System.exit(0);

  }
}
