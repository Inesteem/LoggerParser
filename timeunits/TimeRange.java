package parser;

import java.util.HashMap;
import java.util.Calendar;

public class TimeRange {


  int timeUnits[];
  HashMap<Integer,Integer> yearRange= new HashMap<Integer,Integer>();
  public static final TimeRange ALL= new TimeRange(0xFFFFFFFF);

  int year_idx = 0;
  int current_year = Calendar.getInstance().get(Calendar.YEAR);

  public TimeRange(int arr[]){
    timeUnits=arr;
  }

  public TimeRange(int val){

    timeUnits= new int[Metric.SIZE.value()];
    for (int i = 0; i < timeUnits.length; ++i)
      timeUnits[i] = val;
  }


  public static boolean is_set(int val, int idx){
    return (val & (1 << idx)) != 0;
  }

  public boolean in_range(Metric m, int idx){
    if(m == Metric.YEAR){ 
      //thats because of the  JRangeField implementation, which auto checks input ranges after each character
      //just removing years avoids overfilling the hashmap 
      if (!yearRange.containsKey(idx)) return true; 
      idx = yearRange.get(idx);
    }
    return is_set(timeUnits[m.value()],idx);
  }


  public boolean add_year(int year){
    if (!yearRange.containsKey(year)) {
      if(year_idx == 31) return false;
      yearRange.put(year,year_idx);
      set_idx(timeUnits[Metric.YEAR.value()],year_idx);
      ++year_idx;
    }
    return true;
  }

  public void print_years(){
    for (HashMap.Entry<Integer,Integer> entry : yearRange.entrySet()) {
      System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());
    }
  }

  int get_year_idx(int year){

    if (!yearRange.containsKey(year)) {
      return -1;
    }
    return yearRange.get(year);
  }

  public static int unset_idx(int val, int idx){
    return val & ~(1 << idx);
  }
  public static int set_idx(int val, int idx){
    return val | (1 << idx);
  }

  public void unset_idx(Metric m, int idx){
    if(m == Metric.YEAR){ 
      idx = get_year_idx(idx);
      if(idx == -1) return; 
    }
    timeUnits[m.value()] &= ~(1 << idx);
  }

  public void set_idx(Metric m, int idx){
    if(m == Metric.YEAR){ 
      idx = get_year_idx(idx);
      if(idx == -1) return;
    }
    timeUnits[m.value()] |= (1 << idx);
  }

  public static int unset_range(int val, int from, int to){

    if (from > to || from > 31)
      return val;

    if (from == to) {
      return unset_idx(val,from);
    }


    int mask =  (1 << (to-from)) - 1;
    mask <<= from;

    return val & ~mask;

    //     System.out.println("mask: " + String.format("%32s", 
    //                 Integer.toBinaryString(~mask)).replaceAll(" ", "0"));
  }

  public static int set_range(int val, int from, int to){
    if (from > to || from > 31)
      return val;

    if (from == to) {
      return set_idx(val,from);
    }

    int mask =  (1 << (to-from)) -1;
    mask <<= from;

    return val | mask;

    //       System.out.println("mask: " + String.format("%32s", 
    //                   Integer.toBinaryString(mask)).replaceAll(" ", "0"));

  }
  public void set_range(Metric m, int from, int to){
    //avoid long time iterations over insane year ranges (wouldn't be so nice, though)
    if(m == Metric.YEAR){
      if(from < 1990 && to > current_year) return;
      for(int year = from; year < to; ++year){
        int idx = get_year_idx(year);
        System.out.println(String.valueOf(year) + " " + String.valueOf(idx));
        if(idx == -1) continue;
        timeUnits[m.value()]=set_idx(timeUnits[m.value()],idx); 
      }
      return;
    }

    int idx = m.value();
    timeUnits[idx] = set_range(timeUnits[idx], from, to);
  }

  public void unset_range(Metric m, int from, int to){

    //avoid long time iterations over insane year ranges (same, same)
    if(m == Metric.YEAR){
      if(from < 1990 && to > current_year) return;
      for(int year = from; year < to; ++year){
        int idx = get_year_idx(year);
        if(idx == -1) continue;
        timeUnits[m.value()]=unset_idx(timeUnits[m.value()],idx); 
      }
      return;
    }

    int idx = m.value();
    timeUnits[idx] = unset_range(timeUnits[idx], from, to);
  }
  public void set_all(Metric m) {
    timeUnits[m.value()] = 0xFFFFFFFF;
  }

  public boolean equals(TimeRange tr){
    if(this == tr) return true;
    for(int i = 0; i < timeUnits.length; ++i){
      if(this.timeUnits[i] != tr.timeUnits[i]) return false;
    }
    return true;
  }

  public void set_val(Metric m, int val){
    timeUnits[m.value()] = val;
  }

  public int get_val(Metric m) {
    return timeUnits[m.value()];
  }
}
