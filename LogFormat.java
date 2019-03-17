
import java.util.Date;

public interface LogFormat{
	
	public boolean parse(String line[]);
	
	//public int get_hour();
	
	//public double get_val();
	
	public Date get_key();
}
