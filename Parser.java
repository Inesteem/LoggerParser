
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Parser{
	
	public class A_Parser implements LogFormat{
		SimpleDateFormat input_format;	
		String line[];
		Date date;
		Date key;
		public A_Parser(){
			input_format = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
		}
		
		public boolean parse(String line[]){
			this.line = line;
			try {
				System.out.println(date);
				date = input_format.parse(line[0] + " " + line[1]);
				
				Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
				calendar.setTime(date);   // assigns calendar to given date 
				//hour = calendar.get(Calendar.HOUR_OF_DAY); 
				
				//calendar.set(Calendar.MINUTE,0); 
				//calendar.set(Calendar.SECOND,0); 
				//calendar.set(Calendar.HOUR_OF_DAY,0); 
				key = calendar.getTime();
				
				System.out.println(calendar.get(Calendar.HOUR) +  " / " + calendar.get(Calendar.HOUR_OF_DAY) +  " " + calendar.get(Calendar.MINUTE) +  " " + calendar.get(Calendar.SECOND));
			} catch (java.text.ParseException pe) {
				return false;
			}
			return true;
		}
		
		public Date get_key(){
			return key;
		}
		
	};
	
	public enum ParserType {
		IMPULS, MOMENT_VALS, OTHER
	}
	
	
	
	HashMap<Date,String> RainPerDate;
	SimpleDateFormat output_format = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
    List<Date> dates = new ArrayList<Date>();

	public Parser(){
		RainPerDate = new HashMap<Date,String>();
	}
	
	public boolean parse(String log_file, String append_file){
		
		A_Parser a_parser = new A_Parser();
		String line="";
		
		try {
			File file = new File(append_file);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			while ((line = bufferedReader.readLine()) != null) {
				String splitted[] = line.split("\\s+");
				
				if(a_parser.parse(splitted)){
					
					Date key = a_parser.get_key();
					RainPerDate.put(key, line);
					dates.add(key);

				}
			}
			fileReader.close();
		} catch (IOException e) {
			System.out.println("something is wrong with your append file; failed at line: " + line);
		}	
		
		
		//identify parser type
		String impulse_type1[] = {"Date", "Time", "Impulses", "[]"};
		String impulse_type2[] = {"Datum", "Zeit", "1.Impulse", "[]"};
		String moment_vals[]   = {"Date", "Time", "Resistance", "[Ohm]", "Current", "[mA]"};
		
		
		
		
		try {
			File file = new File(log_file);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			ParserType p_type = ParserType.OTHER;
			while ((line = bufferedReader.readLine()) != null) {
				String splitted[] = line.split("\\s+");
				if(splitted.length == 4 && (Arrays.equals(impulse_type1,splitted) || Arrays.equals(impulse_type2,splitted))){
					p_type = ParserType.IMPULS;
					break;
				}
				else if(splitted.length == 6 && Arrays.equals(moment_vals,splitted)){
					p_type = ParserType.MOMENT_VALS;
					break;
				}
			}
			System.out.println("found parser type: " + p_type);
			if(p_type != ParserType.IMPULS){
				return false;
			}
			while ((line = bufferedReader.readLine()) != null) {
				String splitted[] = line.split("\\s+");
				
				if(a_parser.parse(splitted)){
					System.out.println("try to parse: " + splitted[0] + " " + splitted[1] + " with val " + line);
					
					Date key = a_parser.get_key();
					
					String key_str = output_format.format(key);
					System.out.println("key: " + key_str);
					
					if (RainPerDate.containsKey(key)) {
						System.out.println("double entry for date: " + key +"; going to ignore it");
					} else {
						RainPerDate.put(key, line);
						dates.add(key);
					}
					
					System.out.println("worked!");
					

				}
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return true;
	}
	
	public void print_log_info(){
		Collections.sort(dates);
		for (Date d: dates) {
			System.out.println(RainPerDate.get(d));
		}
    // (3) create a new String using the date format we want
			//Iterator iterator = RainPerDate.keySet().iterator();  
	//	Iterator it = RainPerDate.entrySet().iterator();
	//	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
	//	while (it.hasNext()) {
	//		Map.Entry pair = (Map.Entry)it.next();
	//		String li = (String) pair.getValue();
	//		Date date = (Date) pair.getKey();
	//		calendar.setTime(date);   // assigns calendar to given date 
	//		String date_str = output_format.format(date);
	//		System.out.println(date_str + ": " + li);

	//			System.out.println("");
	//		it.remove(); // avoids a ConcurrentModificationException
	//	}
	}

	public void write_log_info(String filename){
		Collections.sort(dates);
		FileOutputStream outputStream; 
		try{
			outputStream = new FileOutputStream(filename);
			for (Date d: dates) {
				byte[] strToBytes = (RainPerDate.get(d) + "\r\n").getBytes();
				outputStream.write(strToBytes);
			}
			outputStream.close();
		} catch(IOException e) {
			e.printStackTrace();
		}	

	}
	
}


		

