package parser;
import java.util.ArrayList;


	
	public class Month {
    public static class MonthSum{
      public double sum=0;
      public double min=Double.MAX_VALUE;
      public double max=-1;
      public int num = 0;
      public int num_days= 0;
      int num_added = 0;

      void add(MonthSum other){
        sum += other.sum;
        min += other.min;
        max += other.max;
        num += other.num;
        num_days += other.num_days;
      }

      void div(){
        if(num_added== 0) return;
        sum /= num_added;
        min /= num_added;
        max /= num_added;
        num /= num_added;
        num_days /= num_added;
        num_added = 0;
      }

    };


		@SuppressWarnings("unchecked")
		public ArrayList<Double>[] hours = new ArrayList[24];
		public int[] measurements;
		int num_days; // max 31 days, set bits count as days
	  double min,max;

		public Month(){
			num_days = 0;
			measurements = new int[24];
      max = -1;
      min = Double.MAX_VALUE;
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
      
      if(min > val) min = val;
      if(max < val) max = val;
      hours[hour].add(val);
			++measurements[hour];
			num_days |= 1 << day;
		}

  public MonthSum get_month_sum() {
    MonthSum ms = new MonthSum();
    for (int h = 0; h < 24; ++h) {
      if(hours[h] == null) { continue; }
      for (int e = 0; e < hours[h].size(); ++e){
        ms.sum += hours[h].get(e);
        ++ms.num;
      }
    }
    ms.min = min;
    ms.max = max;
    ms.num_days = get_num_days();
    return ms;
  }


	}

