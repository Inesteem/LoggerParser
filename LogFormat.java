
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public interface LogFormat{
	
	
	public int get_values(String[] data, List<Double> values);
	
	
	public Parser.ParserType get_parser_type();
	
	
				//hour = calendar.get(Calendar.HOUR_OF_DAY); 
			
			//calendar.set(Calendar.MINUTE,0); 
			//calendar.set(Calendar.SECOND,0); 
			//calendar.set(Calendar.HOUR_OF_DAY,0); 
}
