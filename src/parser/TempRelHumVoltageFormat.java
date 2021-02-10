package src.parser;
import src.gui.Column;
import src.types.*;

public class TempRelHumVoltageFormat
extends LogFormat {
    public static final String PREF_ALL = "LP_PREF_TRHV_ALL";
    public static final String PREF_STR = "LP_PREF_TRHV";

    static String TEMP_KEY = "temp";
    static String RHUM_KEY = "rhum";
    static String V2_KEY = "volt1";
    static String V3_KEY = "volt2";

    public TempRelHumVoltageFormat(){
        super(ParserType.REL_HUM_VOLT, PREF_ALL);
        valuePanels.add(new ValuePanel(Data.TEMP ,PREF_STR+"_TEMP", 11, 0, 100));
        valuePanels.add(new ValuePanel(Data.HUM  ,PREF_STR+"_RH", 11, 0, 100));
        valuePanels.add(new ValuePanel(Data.VOLT1,PREF_STR+"_V1", 10, 0, 100));
        valuePanels.add(new ValuePanel(Data.VOLT2,PREF_STR+"_V2", 10, 0, 100));

        columns.add(new Column(2, true, calendar, Data.TEMP ));
        columns.add(new Column(3, true, calendar, Data.HUM  ));
        columns.add(new Column(4, true, calendar,   Data.VOLT1));
        columns.add(new Column(5, true, calendar,   Data.VOLT2));
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
}

