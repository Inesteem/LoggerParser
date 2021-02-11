package src.parser;
import src.gui.Column;
import src.types.*;

import java.util.Arrays;

public class WithFogFormat
extends LogFormat {
  public static final String PREF_ALL = "LP_PREF_TRH_ALL";
  public static final String PREF_STR = "LP_PREF_TRH";
  static String str1[] = {"Date", "Time", "1.Temperature", "[DegC]", "2.rel.Humidity", "[%]",  "Rain.Impulses", "[]", "Fog.Impulses", "[]"};

  public WithFogFormat(){
    super(ParserType.WITH_FOG, PREF_ALL);
    valuePanels.add(new ValuePanel(Data.TEMP,PREF_STR+"_TEMP", 10, 0, 100));
    valuePanels.add(new ValuePanel(Data.HUM ,PREF_STR+"_RH", 10, 0, 100));
    valuePanels.add(new ValuePanel(Data.RAIN,PREF_STR+"_RAIN", 10, 0, 100));
    valuePanels.add(new ValuePanel(Data.FOG ,PREF_STR+"_FOG", 10, 0, 100)); // todo sum or avg?

    columns.add(new Column( 2, true, calendar,Data.TEMP));
    columns.add(new Column( 3, true, calendar,Data.HUM ));
    columns.add(new Column( 4, true, calendar,Data.RAIN));
    columns.add(new Column(5, true, calendar ,Data.FOG ));
  }

  public void configure(String file_name){
    configure(file_name, null); 
    Column col = columns.get(3);
    col.mul = 1.0/16.8;//todo
  }

  void preprocess(String[] data) {
    if(!data[4].equals("0")) {
      data[5]= "0";
    }
  }
  void postprocess(String[] data) {}//TODO restore old fog value?

  public boolean matches(String[] line){
//    for(int i = 0; i < line.length; ++i) {
//      if(!str1[i].equals(line[i])){
//        System.out.println(">"+str1[i] + "< vs >" + line[i] +"<");
//      }
//    }

    if(Arrays.equals(str1,line)){
      return true;
    }
    return false;
  }


}

