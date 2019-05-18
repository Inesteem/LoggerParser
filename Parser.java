
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import javafx.util.Pair;


import java.text.DateFormatSymbols;



public class Parser {
	
	
	public static String getMonth(int month) {
		return new DateFormatSymbols().getMonths()[month];
	}

	
	String impulse_type1[] = {"Date", "Time", "Impulses", "[]"};
	String impulse_type2[] = {"Datum", "Zeit", "1.Impulse", "[]"};
	String moment_vals[]   = {"Date", "Time", "Resistance", "[Ohm]", "Current", "[mA]"};
	String rel_hum[]       = {"Datum", "Zeit", "1.Temperatur", "[°C]", "2.rel.Feuchte", "[%]"};	
	String[] options = {"ignore new key", "override old key", "always ignore", "always override","abort operation"};
	String time_zone = "UTC";
	HashMap<Date,String[]> RainPerDate;
	HashMap<Integer,Month[]> RainPerYear;
    
	SimpleDateFormat date_format;	
	
	ParserType p_type;
	LogFormat l_format;	
	
	public enum ParserType {
		NONE, IMPULS, MOMENT_VALS, REL_HUM, OTHER
	}
	
	public class Month {
		@SuppressWarnings("unchecked")
		public ArrayList<Double>[] hours = new ArrayList[24];
		public int[] measurements;
		int num_elements;
		
		public Month(){
			num_elements = 0;
			measurements = new int[24];
		}
		
		public boolean add_data(int hour, String[] data, LogFormat lf){
			if(hours[hour] == null) {
					hours[hour] = new ArrayList<Double>();
			}
			num_elements = lf.get_values(data, hours[hour]);
			if(num_elements < 1){ return false;}
			++measurements[hour];
			return true;
		}
		
		public Pair<Integer, Double> get_month_avg() {
			double avg = 0;
			int num_elements = 0;
			int num_measures = 0;
			for (int i = 0; i < 24; ++i) {
				if(hours[i] == null) { continue; }
				for (double v : hours[i]) {
					avg += v;
					++num_elements;
				}
				num_measures += measurements[i];
			}
			if(num_elements != 0) { avg /= num_elements; }
			return new Pair<Integer, Double>(num_measures, avg);
		}
		
	}
	
	public class ImpulsFormat
    implements LogFormat {
		
		public int get_values(String[] data, List<Double> values){
			if ( data.length != 3){
				return -1;
			}   
			//try {
				double val = Double.parseDouble(data[2]);
				if (val != 0.0) {
					values.add(val);
				}
			//} catch (ParseException e) {
			//	e.printStackTrace();
			//	return false;
			//}
			return 1;
		}
		
		public ParserType get_parser_type(){
			return ParserType.IMPULS;
		}
		
	}
	

	
	public Parser(String tz){
		time_zone = tz;
		RainPerDate = new HashMap<Date,String[]>();
		RainPerYear = new HashMap<Integer,Month[]>();
		date_format = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
		date_format.setTimeZone(TimeZone.getTimeZone(time_zone));
		p_type = ParserType.NONE;
	}
	
	public void setLogFormat(LogFormat lf){
		l_format = lf;
		p_type = lf.get_parser_type();
	}
	
	
	
	public boolean parse(String filename, IOManager iom){
		
		
		//identify parser type
		String line="";
		Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
					
		try {
			File file = new File(filename);
			
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			ParserType type = ParserType.NONE;
			while ((line = bufferedReader.readLine()) != null) {
					String splitted[] = line.split("\\s+");
					if(splitted.length == 4 && (Arrays.equals(impulse_type1,splitted) || Arrays.equals(impulse_type2,splitted))){
							type = ParserType.IMPULS;
							break;
					}
					else if(splitted.length == 6){
						if(Arrays.equals(moment_vals,splitted)){
							type = ParserType.MOMENT_VALS;
							break;
						}
						if(Arrays.equals(rel_hum,splitted)){
							type = ParserType.REL_HUM;
							break;
						}
					}
			}
			if(p_type != type) {
				System.out.println("File types do not match! Aborting.");
				iom.asWarning("File types do not match! Aborting.");
				return false;
					
			}
				
			boolean ignore_all = false;
			boolean override_all = false;
			boolean dub_lines = false;
			while ((line = bufferedReader.readLine()) != null) {
				String splitted[] = line.split("\\s+");
				Date date = date_format.parse(splitted[0] + " " + splitted[1]);
				calendar.setTime(date);   // assigns calendar to given date 

				//long key = calendar.getTimeInMillis();
				System.out.println("try to parse: " + splitted[0] + " " + splitted[1] + " with val " + line);
					
				String key_str = date_format.format(date);
				System.out.println("key: " + key_str + " (" + splitted[0] + " " + splitted[1]+ ")");
					
				if (RainPerDate.containsKey(date) && !override_all) {
					String old_val[] = RainPerDate.get(date);
					/*
					if(old_val.equals(line)){
						System.out.println("dublicated line: " + line);
						dub_lines = true;
						continue;
					} */
							
					if(ignore_all){
						continue;
					}
						
						
					int opt = iom.askNOptions("entry already exists", options,
					"date:\n "+key_str+"\nold:\n "+old_val+"\nnew:\n "+line);
					//int opt = iom.askTwoOptions("test1", "ignore new key", "override old key", 
					//"the entry with date >" +key +"< already exists; exit dialog to abort");
					if(opt == 0){
						System.out.println("ignore");
						continue;
					} else if (opt==1){
						System.out.println("override");
					} else if (opt==2){
						System.out.println("always ignore");
						ignore_all = true;
					} else if (opt==3){
						System.out.println("always override");
						override_all = true;
					} else {
						System.exit(0);
					}
				} 
				RainPerDate.put(date, splitted);
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				System.out.println(year + " " + month + " " + hour);
				Month[] months;
				if (!RainPerYear.containsKey(year)) {
					months = new Month[12];
					RainPerYear.put(year, months);	
					System.out.println(" adding key ...");			
				} else {
					months = RainPerYear.get(year);
					System.out.println(" contains key...");
				}
				if(months[month] == null){
					months[month] = new Month();
				}
				if(!months[month].add_data(hour, splitted, l_format )){
					System.out.println("an error occurred!");
					
				}
				
				System.out.println("");
				
			}
			if (dub_lines) {
				iom.asWarning("some or all lines you've tried to add were already in the choosen append file");
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public void print_log_info(){
		Iterator it = RainPerYear.entrySet().iterator();
		//Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			Month[] months = (Month[]) pair.getValue();
			int year  = (int) pair.getKey();
			System.out.println(year + ": ");
			for (int m = 0; m < 12; ++m){
				if(months[m] == null) { continue; }
				System.out.println(" " + getMonth(m) + ": ");
				for (ArrayList<Double> hours : months[m].hours) {
					if (hours == null) { continue; }
					for ( double v: hours) {
						System.out.print("   " + v);
					}
					
				}
				Pair avg = months[m].get_month_avg();
				System.out.println("\n avg: " + avg.getValue() + " with " + avg.getKey() + " measurements" + "\n");
					
			}
		}
	}
	
	/*
	public void print_log_info(){
		
		//List<Date> dates = new ArrayList<Date>();
		HashMap<Long,List<Double>> RainPerYear = new HashMap<Long,List<Double>>();
		
	//	Collections.sort(dates);
	//	for (long d: dates) {
	//		System.out.println(RainPerDate.get(d));
	//	} 
    // (3) create a new String using the date format we want
			//Iterator iterator = RainPerDate.keySet().iterator();  
		Iterator it = RainPerDate.entrySet().iterator();
		Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			String[] line = (String[]) pair.getValue();
			Date date = (Date) pair.getKey();
			calendar.setTime(date);   // assigns calendar to given date 
			String date_str = date_format.format(date);
			
			long year = calendar.get(Calendar.YEAR);
			long month = calendar.get(Calendar.MONTH);
			long hout = calendar.get(Calendar.HOUR_OF_DAY);
			
			
			List<Double> l_year;
			if (RainPerYear.containsKey(year)) {
				l_year = RainPerYear.get(year);
			} else {
				l_year = new ArrayList<Double>();	
				RainPerYear.put(year,  l_year);
			}
			if(!l_format.get_values(line, l_year)){
				System.out.println("error with file content");
			}
	//			System.out.println("");
	//		it.remove(); // avoids a ConcurrentModificationException
		}
		
		
		
		Iterator it2 = RainPerYear.entrySet().iterator();
		while (it2.hasNext()) {
			Map.Entry pair = (Map.Entry)it2.next();
			List<Double> line = (List<Double>) pair.getValue();
			long year = (long) pair.getKey();
			System.out.println("year: " + year + "\n" + line);
			
		}
	}*/
/*
	public void write_log_info(String filename, IOManager iom){

		
		iom.create_temp_copy(filename);
		
		Collections.sort(dates);
		FileOutputStream outputStream; 
		try{
			outputStream = new FileOutputStream(filename);
			for (long d: dates) {
				byte[] strToBytes = (RainPerDate.get(d) + "\r\n").getBytes();
				outputStream.write(strToBytes);
			}
			outputStream.close();
		} catch(IOException e) {
			e.printStackTrace();
		}	

	}*/
	
}


		

