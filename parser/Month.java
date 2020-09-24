package parser;
import java.util.ArrayList;


	
	public class Month {
		@SuppressWarnings("unchecked")
		public ArrayList<Double>[] hours = new ArrayList[24];
		public int[] measurements;
		int num_days; // max 31 days, set bits count as days
		
		public Month(){
			num_days = 0;
			measurements = new int[24];
		}
		
		public int get_num_days(){
			return java.lang.Integer.bitCount(num_days);
		}
		
		public boolean add_data(int day, int hour, String[] data, LogFormat lf){
			if(hours[hour] == null) {
				hours[hour] = new ArrayList<Double>();
			}
			if(!lf.get_values(data, hours[hour])){ return false; }
			++measurements[hour];
			num_days |= 1 << day;
			return true;
		}
		
		public void add_data(int day, int hour, double val){
			if(hours[hour] == null) {
				hours[hour] = new ArrayList<Double>();
			}
      hours[hour].add(val);
			++measurements[hour];
			num_days |= 1 << day;
		}
	}

