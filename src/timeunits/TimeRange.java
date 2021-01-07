package parser;
import parser.*;

import java.util.HashMap;
import java.util.Calendar;

public class TimeRange {
  long timeUnits[];
  //since years naturally are not in the range 0-64 a HashMap is used to map
  //their non continuous values to continuous values between 0 and 64 excl
  HashMap<Integer,Integer> yearRange;

  public static final TimeRange ALL= new TimeRange(0xFFFFFFFFFFFFFFFFl);

  int current_year = Calendar.getInstance().get(Calendar.YEAR);

  public TimeRange(long arr[]){
    this.yearRange= new HashMap<Integer,Integer>();
    timeUnits=arr;
  }

  public TimeRange(long val){
    this.yearRange= new HashMap<Integer,Integer>();
    timeUnits= new long[Metric.SIZE.value()];
    for (int i = 0; i < timeUnits.length; ++i)
      timeUnits[i] = val;
  }

  public TimeRange(TimeRange other){
    //copy yearRange only shallow as they only represent a mapping that should not change
    //during one log parsing event
    this.yearRange = new HashMap<Integer,Integer>(other.yearRange);
    timeUnits= new long[Metric.SIZE.value()];
    System.arraycopy(other.timeUnits, 0, this.timeUnits, 0, other.timeUnits.length);

  }

  public static boolean is_set(long val, int idx){
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
    if (yearRange.containsKey(year)) return true;
    int year_idx = 0;
    if (!yearRange.isEmpty()) {
      year_idx = java.util.Collections.max(yearRange.entrySet(),
              new java.util.Comparator<HashMap.Entry<Integer, Integer>>() {
                public int compare(HashMap.Entry<Integer, Integer> e1, HashMap.Entry<Integer, Integer> e2) {
                  return e1.getValue().compareTo(e2.getValue());
                }
              }).getValue();
    }
    if(year_idx == 63) return false;
    yearRange.put(year,year_idx);
    set_idx(timeUnits[Metric.YEAR.value()],year_idx);
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

  public static long unset_idx(long val, int idx){
    return val & ~(1 << idx);
  }
  public static long set_idx(long val, int idx){
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

  public static long unset_range(long val, int from, int to){

    if (from > to || from > 63)
      return val;

    if (from == to) {
      return unset_idx(val,from);
    }


    long mask =  (1 << (to-from)) - 1;
    mask <<= from;

    return val & ~mask;

    //     System.out.println("mask: " + String.format("%32s", 
    //                 Integer.toBinaryString(~mask)).replaceAll(" ", "0"));
  }

  public static long set_range(long val, int from, int to){
    if (from > to || from > 63)
      return val;

    if (from == to) {
      return set_idx(val,from);
    }

    long mask =  (1 << (to-from)) -1;
    mask <<= from;

    return val | mask;

    //       System.out.println("mask: " + String.format("%32s", 
    //                   Integer.toBinaryString(mask)).replaceAll(" ", "0"));

  }
  public void set_range(Metric m, int from, int to){
    //avoid long time iterations over insane year ranges (wouldn't be so nice, though)
    if(m == Metric.YEAR){
      if(from < 1900 && to > current_year) return;
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
    timeUnits[m.value()] = 0xFFFFFFFFFFFFFFFFl;
  }
  public void unset_all(Metric m) {
    timeUnits[m.value()] = 0x0000000000000000l;
  }

  public boolean equals(TimeRange tr){
    if(this == tr) return true;
    for(int i = 0; i < timeUnits.length; ++i){
      if(this.timeUnits[i] != tr.timeUnits[i]) return false;
    }
    return true;
  }
  public void or_val(Metric m, long val){
    timeUnits[m.value()] |= val;
  }
  public void and_val(Metric m, long val){
    timeUnits[m.value()] &= val;
  }
  public void xor_val(Metric m, long val){
    timeUnits[m.value()] ^= val;
  }
  public void set_val(Metric m, long val){
    timeUnits[m.value()] = val;
  }

  public long get_val(Metric m) {
    return timeUnits[m.value()];
  }

  public void print(Metric m) {
         System.out.println("mask: " + String.format("%64s",
               Long.toBinaryString(timeUnits[m.value()])).replaceAll(" ", "0"));
  }
}
