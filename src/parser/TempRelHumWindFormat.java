package src.parser;
import src.gui.Column;
import static src.types.Data.*;
public class TempRelHumWindFormat
        extends LogFormat {
    public static final String PREF_ALL = "LP_PREF_TRHW_ALL";
    public static final String PREF_STR = "LP_PREF_TRHW";

    static String TEMP_KEY = "temp";
    static String RHUM_KEY = "rhum";
    static String WDIR_KEY = "wdir";
    static String WVE1_KEY = "wvel1";
    static String FREQ_KEY = "freq";
    static String WVE2_KEY = "wvel2";

    public TempRelHumWindFormat(){
        super(Parser.ParserType.REL_HUM_WIND, PREF_ALL);
        valuePanels.add(new ValuePanel("Temperature", PREF_STR+"_TEMP", 11, 0, 100, true));
        valuePanels.add(new ValuePanel("Relative Humidity", PREF_STR+"_RH", 11, 0, 100, true));
        valuePanels.add(new ValuePanel("Wind Direction", PREF_STR+"_WD", 11, 0, 360, true));//degree
        valuePanels.add(new ValuePanel("Wind Velocity", PREF_STR+"_WV1", 11, 0, 10000, true));//in m per sec
        valuePanels.add(new ValuePanel("Frequency", PREF_STR+"_F", 11, 0, 1000, true));//in Hz
        valuePanels.add(new ValuePanel("Wind Velocity", PREF_STR+"_WV2", 11, 0, 10000, true));//in m per sec

        columns.add(new Column(TEMP_KEY, 1, 100,    2, true, calendar, TEMP));
        columns.add(new Column(RHUM_KEY, 1, 100,    3, true, calendar, HUM));
        columns.add(new Column(WDIR_KEY, 0, 360,    4, true, calendar, WIND_DIR));
        columns.add(new Column(WVE1_KEY, 0, 10000,  5, true, calendar, WIND_VEL));
        columns.add(new Column(FREQ_KEY, 0, 1000,   6, true, calendar, FREQ));
        columns.add(new Column(WVE2_KEY, 0, 10000,  7, true, calendar, WIND_VEL));
    }

    void preprocess(String[] data){}
    public void configure(String file_name){
        configure(file_name, null);
    }

    public static boolean matches(String[] line){
        if(line.length != 14) return false;
        if(!line[2].contains("Temperatur")) return false;
        if(!line[4].contains("rel.Humidity") && !line[4].contains("rel.Feuchte")) return false;
        if(!line[6].contains("Windricht") && !line[6].contains("inddir")) return false;
        if(!line[8].contains("Windgesch") && !line[8].contains("velocity")) return false;
        if(!line[10].contains("requen")) return false;
        if(!line[12].contains("Windgesch") && !line[12].contains("velocity")) return false;
        return true;
    }

    public String get_value_header() {
        return "temp rel_hum wind_dir wind_vel freq wind_vel";
    }

}

