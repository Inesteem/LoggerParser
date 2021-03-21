package src.parser;
import src.gui.Column;
import src.types.*;

import static src.types.Data.*;
import static src.types.Data.WIND_VEL2;

public class TempRelHumVoltageFormat
extends LogFormat {
    public static final String PREF_STR = "LP_PREF_TRHV";
    static Data data_types[] = {TEMP, HUM, VOLT1, VOLT2};

    public TempRelHumVoltageFormat(){
        super(ParserType.REL_HUM_VOLT, PREF_STR, data_types);
    }

    void preprocess(String[] data){}

    void postprocess(String[] data){}

    public void configure(String file_name){
        configure(file_name, null);
    }

    public boolean matches(String[] line){
        if(line.length != 10) return false;
        if(!line[2].contains("Temperatur")) return false;
        if(!line[4].contains("rel.Humidity") && !line[4].contains("rel.Feuchte")) return false;
        if(!line[6].contains("Voltage") || !line[8].contains("Voltage")) return false;
        return true;
    }
}

