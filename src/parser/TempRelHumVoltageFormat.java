package src.parser;
import src.gui.Column;
import src.types.*;

public class TempRelHumVoltageFormat
extends LogFormat {
    public static final String PREF_ALL = "LP_PREF_TRHV_ALL";
    public static final String PREF_STR = "LP_PREF_TRHV";

    static String TEMP_KEY = "temp";
    static String RHUM_KEY = "rhum";
    static String V1_KEY = "volt1";
    static String V2_KEY = "volt2";

    public TempRelHumVoltageFormat(){
        super(Parser.ParserType.REL_HUM_VOLT, PREF_ALL);
        val_panels.add(new ValuePanel("Temperature", PREF_STR+"_TEMP", 10, 0, 100, true));
        val_panels.add(new ValuePanel("Relative Humidity", PREF_STR+"_RH", 10, 0, 42, true));
        val_panels.add(new ValuePanel("Voltage 1", PREF_STR+"_V1", 10, 0, 100, true));
        val_panels.add(new ValuePanel("Voltage 2", PREF_STR+"_V2", 10, 0, 100, true));

        columns.add(new Column(TEMP_KEY, 0, 100 , 2, true, calendar, Data.TEMP));
        columns.add(new Column(RHUM_KEY, 0, 100 , 3, true, calendar, Data.HUM));
        columns.add(new Column(V1_KEY, 0, 100 , 3, true, calendar, Data.VOLT));
        columns.add(new Column(V2_KEY, 0, 100 , 3, true, calendar, Data.VOLT));
    }

    void preprocess(String[] data){}
    public void configure(String file_name){
        configure(file_name, null);
    }

    public static boolean matches(String[] line){
        if(line.length != 10) return false;
        if(!line[2].contains("Temperatur")) return false;
        if(!line[4].contains("rel.Humidity") && !line[4].contains("rel.Feuchte")) return false;
        if(!line[6].contains("Voltage") || !line[8].contains("Voltage")) return false;
        return true;
    }

    public String get_value_header() {
        return "temp rel_hum volt1 volt2";
    }

}

