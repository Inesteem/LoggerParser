package src.parser;
import src.gui.Column;
import src.types.*;

import java.util.Arrays;

import static src.types.Data.*;
import static src.types.Data.TEMP_PT1000;

public class WithFogFormat
extends LogFormat {
  public static final String PREF_STR = "LP_PREF_TRH";
  static String str1[] = {"Date", "Time", "1.Temperature", "[DegC]", "2.rel.Humidity", "[%]",  "Rain.Impulses", "[]", "Fog.Impulses", "[]"};
  static Data data_types[] = {TEMP,HUM,RAIN,FOG};

  public WithFogFormat(){
    super(ParserType.WITH_FOG, PREF_STR, data_types);
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

