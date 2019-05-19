
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

public interface LogFormat{
	
	
	public boolean get_values(String[] data, List<Double> values);
	
	
	public Parser.ParserType get_parser_type();
	
	
	public Pair<Integer, double[]> get_month_avg(Parser.Month m);
	
	
	public String get_value_header();
	
}
