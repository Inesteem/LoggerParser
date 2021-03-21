package src.parser;
import src.gui.Column;
import src.types.Data;
import src.types.ParserType;

import static src.types.Data.*;
public class TempRelHumWindFormat
        extends LogFormat {
    public static final String PREF_STR = "LP_PREF_TRHW";
    static Data data_types[] = {TEMP, HUM, WIND_DIR, WIND_VEL1, FREQ, WIND_VEL2};

    public TempRelHumWindFormat(){
        super(ParserType.REL_HUM_WIND, PREF_STR, data_types);
    }

    void preprocess(String[] data){}

    void postprocess(String[] data){}

    public void configure(String file_name){
        configure(file_name, null);
    }

    public boolean matches(String[] line){
        if(line.length != 14) return false;
        if(!line[2].contains("Temperatur")) return false;
        if(!line[4].contains("rel.Humidity") && !line[4].contains("rel.Feuchte")) return false;
        if(!line[6].contains("Windricht") && !line[6].contains("inddir")) return false;
        if(!line[8].contains("Windgesch") && !line[8].contains("velocity")) return false;
        if(!line[10].contains("requen")) return false;
        if(!line[12].contains("Windgesch") && !line[12].contains("velocity")) return false;
        return true;
    }

}

