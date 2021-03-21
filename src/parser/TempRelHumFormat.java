package src.parser;

import src.types.*;
import src.gui.*;
import static src.types.Data.*;

public class TempRelHumFormat
extends LogFormat {
  public static final String PREF_STR = "LP_PREF_TRH";
  static Data data_types[] = {TEMP, HUM};

  public TempRelHumFormat() {
    super(ParserType.REL_HUM, PREF_STR, data_types);
  }

  void preprocess(String[] data) {
  }

  void postprocess(String[] data) {
  }

  public void configure(String file_name) {
    configure(file_name, null);
  }

  public boolean matches(String[] line) {
    if (line.length != 6) return false;
    if (!line[2].contains("Temperatur")) return false;
    if (!line[4].contains("rel.Humidity") && !line[4].contains("rel.Feuchte")) return false;
    return true;
  }

}