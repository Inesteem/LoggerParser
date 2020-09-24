package parser;

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
import java.time.YearMonth;
import javafx.util.Pair;

import java.lang.NumberFormatException;
import java.lang.Number;

import java.text.DateFormatSymbols;
import javax.swing.JLabel;
import javax.swing.JFrame;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Parser {
  static DecimalFormat df;	
	
	public static String getMonth(int month) {
		return new DateFormatSymbols().getMonths()[month];
	}

	
	String moment_vals[]   = {"Date", "Time", "Resistance", "[Ohm]", "Current", "[mA]"};
	String[] options = {"keep key", "override old key", "always keep", "always override","abort operation"};
	String time_zone = "UTC";
	HashMap<Date,String[]> RainPerDate;
	HashMap<Integer,Month[]> RainPerYear;
	Calendar calendar;
    
	SimpleDateFormat date_format;	
	
	ParserType p_type;
	LogFormat l_format;	
	
	public enum ParserType {
		NONE, IMPULS, MOMENT_VALS, REL_HUM, OTHER
	}

	
	public Parser(String tz){
		time_zone = tz;
		RainPerDate = new HashMap<Date,String[]>();
		RainPerYear = new HashMap<Integer,Month[]>();
		date_format = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
		date_format.setTimeZone(TimeZone.getTimeZone(time_zone));


		p_type = ParserType.NONE;
		calendar = GregorianCalendar.getInstance(); 
		TimeZone pdt=TimeZone.getTimeZone(time_zone);
		
		// create a Pacific Standard Time time zone
		//SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, ids[0]);
		// set up rules for Daylight Saving Time	
		//pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
		//pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);	
		calendar.setTimeZone(pdt);

  Locale locale = new Locale("en","UK");
  df = (DecimalFormat) NumberFormat.getNumberInstance(locale);
  df.applyPattern("##.##");

	}
	
	public void setLogFormat(LogFormat lf){
		l_format = lf;
		p_type = lf.get_parser_type();
	}
	
	
	public boolean parse(File file,JLabel label,JFrame frame){
		
		IOManager iom = IOManager.getInstance();
		
		//identify parser typeCollections.sort(doublesList);
		String line="";
					
		try {
			
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			ParserType type = ParserType.NONE;
			while ((line = bufferedReader.readLine()) != null) {
					String splitted[] = line.split("\\s+");
				  if(ImpulsFormat.matches(splitted)){
							type = ParserType.IMPULS;
							break;
          }
				//		if(Arrays.equals(moment_vals,splitted)){
				//			type = ParserType.MOMENT_VALS;
				//			break;
				//		}
				  else if(TempRelHumFormat.matches(splitted)){
							type = ParserType.REL_HUM;
							break;
						}
      }

      //file append support; check if file types match
      if(p_type == ParserType.NONE){
        p_type = type;
        if ( p_type == ParserType.REL_HUM) {
          setLogFormat((LogFormat) new TempRelHumFormat());
        } else if ( p_type == ParserType.IMPULS) {
          setLogFormat((LogFormat) new ImpulsFormat());
        } else {
          iom.asError("logger format unsupported : " + file.getAbsolutePath());
        }
      } else if(p_type != type) {
        System.out.println("File types do not match! Aborting.");
        iom.asWarning("File types do not match! Aborting.");
				return false;
					
			} 
       
      l_format.configure(file.getName());
      
			boolean keep_all = false;
			boolean override_all = false;
			boolean dub_lines = false;

			while ((line = bufferedReader.readLine()) != null) {
				String splitted[] = line.split("\\s+");
				Date date = date_format.parse(splitted[0] + " " + splitted[1]);
				calendar.setTime(date);   // assigns calendar to given date 

				//long key = calendar.getTimeInMillis();
				System.out.println("trying to parse: " + splitted[0] + " " + splitted[1] + " with val " + line);
					
				String key_str = date_format.format(date);
				System.out.println("date: " + date + " " + key_str);
        label.setText("<html><b><center>Parsing Line:<center/><b/><br/>"+line+"</html>");
       
					
				if (RainPerDate.containsKey(date) && !override_all) {
					String old_val[] = RainPerDate.get(date);
					
					if(Arrays.equals(old_val, splitted)){
						System.out.println("dublicated line: " + line);
						dub_lines = true;
						continue;
					} 
							
					if(keep_all){
						continue;
					}
						
						
					int opt = iom.askNOptions("entry already exists", options,
					"date:\n "+key_str+"\nold:\n "+Arrays.toString(old_val)+"\nnew:\n "+line);
					//int opt = iom.askTwoOptions("test1", "keep new key", "override old key", 
					//"the entry with date >" +key +"< already exists; exit dialog to abort");
					if(opt == 0){
						System.out.println("keep");
						continue;
					} else if (opt==1){
						System.out.println("override");
					} else if (opt==2){
						System.out.println("always keep");
						keep_all = true;
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
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				System.out.println(year + " " + month + " " + day + " " + hour);
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
				if(!months[month].add_data(day, hour, splitted, l_format )){
					System.out.println("an error occurred!");
					
				}
				
				System.out.println("");
				
			}
			//if (dub_lines) {
			//	iom.asWarning("some or all lines you've tried to add were already in the choosen append file");
			//}
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
				int num_days = YearMonth.of(year, m+1).lengthOfMonth();
				
				Pair avg = l_format.get_month_val(months[m]);
				System.out.println("\n avg: " + Arrays.toString((double[])avg.getValue()) + " with " + avg.getKey() +  "\n");
				System.out.println( "measured " +  months[m].get_num_days() + " days of " + num_days + " = " + (100 * months[m].get_num_days())/num_days + "%");
			}
		}
	}
	public void write_log_info(String filename){

		IOManager iom = IOManager.getInstance();
		iom.create_temp_copy(filename, calendar);
		FileOutputStream outputStream; 
		Iterator it = RainPerYear.entrySet().iterator();
		String header = l_format.get_value_header();
		String line = "\r\nyear \t month \t days \t " + header + "\t measurements";
		int [] all_years_meas = new int[12];
		@SuppressWarnings("unchecked")
		ArrayList<Double>[] all_years_avg = new ArrayList[12];
		try{
			outputStream = new FileOutputStream(filename);
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry)it.next();
				Month[] months = (Month[]) pair.getValue();
				int year  = (int) pair.getKey();
				for (int m = 0; m < 12; ++m){
					if(months[m] == null) { continue; }
					line += "\r\n";
					
					int num_days = YearMonth.of(year, m+1).lengthOfMonth();
					Pair avg = l_format.get_month_val(months[m]);
					double [] avg_vals = (double[]) avg.getValue();
					int[] meas =  (int[]) avg.getKey();
					
					line += year + "\t" + getMonth(m).substring(0,3) + "\t" + months[m].get_num_days() + "/" + num_days;
					for (int pos = 0; pos < avg_vals.length; ++pos) {
						line += "\t" + df.format(avg_vals[pos]) + "\t" + meas[pos];
            
          if(all_years_avg[m] == null) {
            all_years_avg[m] = new ArrayList<>();
            }
            if(all_years_avg[m].size() <= pos){
                all_years_avg[m].add(avg_vals[pos]);
                System.out.println(m + " " + avg_vals[pos]);
            }else{
							all_years_avg[m].set(pos,all_years_avg[m].get(pos) + avg_vals[pos]);
              }
                  
					}
          all_years_meas[m] += 1;

          
//					if((100 * months[m].get_num_days())/num_days >= 80){
//						if (all_years_avg[m] == null) {
//							all_years_avg[m] = new ArrayList<Double>();
//							for(int i = 0; i < avg_vals.length; ++i){
//								all_years_avg[m].add(0.0);
//							}
//							
//						} 
//						for(int i = 0; i < avg_vals.length; ++i){
//							System.out.println("i " + i + " " + avg_vals.length +  " " + all_years_avg[m].size());
//							all_years_avg[m].set(i,all_years_avg[m].get(i) + avg_vals[i]);
//              all_years_meas[m] += 1;
//						}
//						System.out.println(meas);
					
//					}
					//System.out.println("\n avg: " + Arrays.toString((double[])avg.getValue()) + " with " + avg.getKey() + " measurements" + "\n");
					//System.out.println( "measured " +  months[m].get_num_days() + " days of " + num_days);
					
				}
			}
			line += "\r\n\r\nall years: \r\n \t month \t "+header + "\r\n";
			for(int m = 0; m < 12; ++m){
				line += "\r\n";
				if(all_years_avg[m] == null) { 
					line += "-";
					continue;
				}
				line += "\t" + getMonth(m).substring(0,3);
				for (double v : all_years_avg[m]) {
					if(all_years_meas[m] != 0){
						line += "\t" + df.format((double)v/all_years_meas[m]);
					} else {
						line += "\t" + df.format(v);
					}
				}
			}
			line += "\r\n\r\n";
			
			byte[] strToBytes = line.getBytes();
			outputStream.write(strToBytes);
			outputStream.close();
		} catch(IOException e) {
			e.printStackTrace();
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

		
		iom.create_temp_copy(filename, calendar);
		
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


		

