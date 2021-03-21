package src.parser;

import src.types.*;
import src.gui.*;

import static src.types.Data.*;

public class WindTempHumBarRain
extends LogFormat {
    public static final String PREF_STR = "LP_PREF_WTHBR";
    static Data data_types[] = {WIND_DIR, WIND_VEL1, TEMP, HUM, BAR, RAIN, VOLT1, VOLT1, VOLT3, VOLT4, TEMP_PT100, TEMP_PT1000};

    public WindTempHumBarRain(){
        super(ParserType.WIND_DIR_SPEED_TEMP_HUM_BAR_RAIN, PREF_STR, data_types);
    }

    void preprocess(String[] data){}

    void postprocess(String[] data){}

    public void configure(String file_name){
        configure(file_name, null);
    }

    public boolean matches(String[] line){//
        if(line.length != 26) return false;
        if(!line[2].contains("Wind") || !line[4].contains("Wind") ) return false;
        if(!line[6].contains("Temp") || !line[24].contains("Temp") || !line[22].contains("Temp")) return false;
        return true;
    }

}
