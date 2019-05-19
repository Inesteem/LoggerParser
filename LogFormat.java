
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public interface LogFormat{
	
	
	public int get_values(String[] data, List<Double> values);
	
	
	public Parser.ParserType get_parser_type();
	
	
	public String get_value_names();
	
}
