package parser;

import parser.*;
import java.util.HashMap;

public class TimeRange {
  long timeUnits[];
  //since years naturally are not in the range 0-64 a HashMap is used to map
  //their non continuous values to continuous values between 0 and 64 excl
  HashMap<Integer,Integer> yearRange;
  public static final int MAX_IDX_INCL = 63;

  public static final TimeRange ALL= new TimeRange(0xFFFFFFFFFFFFFFFFl);

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

  /**
   * Sets the ith bit in val
   * @param val the value to be checked
   * @param i the index of the bit to check
   * @return true if the ith bit is set in val and false otherwise
   */
  public static boolean is_set(long val, int i){
    return (val & (1l << i)) != 0;
  }

  /**
   * Unsets the ith bit in val
   * @param val the value to be modified
   * @param i the index of the bit to unset
   * @return the modified value
   */
  public static long unset_idx(long val, int i){
    return val & ~(1l << i);
  }

  /**
   * Sets the ith bit in val
   * @param val the value to be modified
   * @param i the index of the bit to set
   * @return the modified value
   */
  public static long set_idx(long val, int i){
    return val | (1l << i);
  }
  /**
   * Sets a range of bits in val
   * @param val the value to be modified
   * @param from the start idx incl
   * @param to the end index excl
   * @return the modified value
   */
  public static long set_range(long val, int from, int to){
    if(from == to) to += 1;
    if (from > to || from > MAX_IDX_INCL || to > MAX_IDX_INCL+1)
      return val;

    int num_bits = to-from;
    if (num_bits == MAX_IDX_INCL + 1 ) return ~0l;

    long mask =  (1l << num_bits) - 1;
    mask <<= from;

    return val | mask;
  }

  /**
   * Unsets a range of bits in val
   * @param val the value to be modified
   * @param val the value to be modified
   * @param from the start idx incl
   * @param to the end index excl
   * @return the modified value
   */
  public static long unset_range(long val, int from, int to){
    if(from == to) to += 1;

    if (from > to || from > MAX_IDX_INCL || to > MAX_IDX_INCL+1)
      return val;

    int num_bits = to-from;
    if (num_bits == MAX_IDX_INCL + 1 ) return 0l;

    long mask =  (1l << num_bits) - 1;
    mask <<= from;

    return val & ~mask;
  }

  /**
   * Checks whether a specific day/month/... is valid and enabled in this timeRange
   * @param m the metric
   * @param idx the index
   * @return true if valid idx and idx is set intimeUnits
   */
  public boolean in_range(Metric m, int idx){
    if (!m.inRange(idx)) return false;
    if(m == Metric.YEAR){
      if (!yearRange.containsKey(idx)) return false;
      idx = yearRange.get(idx);
    }
    return is_set(timeUnits[m.value()],idx);
  }

  /**
   * Registers a year in the yearMap
   * @param year number between Metric.YEAR.getMinIncl() and Metric.YEAR.getMaxIncl()
   * @return true if year was added or is already contained and false if the year is invalid or already
   *         MAX_IDX_INCL years are mapped
   */
  public int add_year(int year){
    if (yearRange.containsKey(year)) return yearRange.get(year);
    if (!Metric.YEAR.inRange(year)) return -1;
    int year_idx = 0;
    if (!yearRange.isEmpty()) {
      year_idx = java.util.Collections.max(yearRange.entrySet(),
              new java.util.Comparator<HashMap.Entry<Integer, Integer>>() {
                public int compare(HashMap.Entry<Integer, Integer> e1, HashMap.Entry<Integer, Integer> e2) {
                  return e1.getValue().compareTo(e2.getValue());
                }
              }).getValue();
      year_idx += 1;
    }
    if(year_idx == MAX_IDX_INCL + 1 ) return -1;
    yearRange.put(year,year_idx);
    return year_idx;
  }

  /**
   * Returns the intern index associated with a year
   * @param year
   * @return -1 if year not mapped
   */
  int get_year_idx(int year){

    if (!yearRange.containsKey(year)) {
      return -1;
    }
    return yearRange.get(year);
  }

  /**
   * Enables a specific day/month/... by setting the respective index in the timeUnits array
   * @param m the metric
   * @param idx the index (representing a day/month/...
   * @return true if idx is valid and mapped
   */
  public boolean set_idx(Metric m, int idx){
    if(!m.inRange(idx)) return false;
    if(m == Metric.YEAR){
      idx = get_year_idx(idx);
      if(idx == -1) return false;
    }
    timeUnits[m.value()] |= (1l << idx);
    return true;
  }

  /**
   * Disables a specific day/month/... by unsetting the respective index in the timeUnits array
   * @param m the metric
   * @param idx the index (representing a day/month/...
   * @return true if idx is valid and mapped
   */
  public boolean unset_idx(Metric m, int idx){
    if(!m.inRange(idx)) return false;
    if(m == Metric.YEAR){
      idx = get_year_idx(idx);
      if(idx == -1) return false;
    }
    timeUnits[m.value()] &= ~(1l << idx);
    return true;
  }

  /**
   * Enables a range of days/months/...
   * @param m the metric
   * @param from start index incl
   * @param to end index excl
   * @return true if the range is valid
   */
  public boolean set_range(Metric m, int from, int to){
    if(from == to) to += 1;
    if(from < m.getMinIncl() || to > m.getMaxExcl() || from > to) return false;
    if(m == Metric.YEAR){
      for(int year = from; year < to; ++year){
        int idx = get_year_idx(year);
        if(idx == -1) continue;
        timeUnits[m.value()]=set_idx(timeUnits[m.value()],idx);
      }
      return true;
    }

    int idx = m.value();
    timeUnits[idx] = set_range(timeUnits[idx], from, to);
    return true;
  }

  /**
   * Disables a range of days/months/...
   * @param m the metric
   * @param from start index incl
   * @param to end index excl
   * @return true if the range is valid
   */
  public boolean unset_range(Metric m, int from, int to){
    if(from == to) to += 1;
    if(from < m.getMinIncl() || to > m.getMaxExcl() || from > to) return false;
    if(m == Metric.YEAR){
      for(int year = from; year < to; ++year){
        int idx = get_year_idx(year);
        if(idx == -1) continue;
        timeUnits[m.value()]=unset_idx(timeUnits[m.value()],idx);
      }
      return true;
    }

    int idx = m.value();
    timeUnits[idx] = unset_range(timeUnits[idx], from, to);
    return true;
  }

  /**
   * Enables all months/days/...
   * @param m the metric
   */
  public void set_all(Metric m) {
    timeUnits[m.value()] = ~0l;
  }

  /**
   * Disables all months/days/...
   * @param m the metric
   */
  public void unset_all(Metric m) {
    timeUnits[m.value()] = 0l;
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

  /**
   * Get the smallest year mapped
   * @return -1 if yearRange is empty and the min year else
   */
  public int getMinYear() {
    if (yearRange.isEmpty()) return -1;
    return java.util.Collections.min(yearRange.keySet());
  }
  /**
   * Get the largest year mapped
   * @return -1 if yearRange is empty and the max year else
   */
  public int getMaxYear() {
    if (yearRange.isEmpty()) return -1;
    return java.util.Collections.max(yearRange.keySet());
  }

  //Debugging Functions
  public void print(Metric m) {
    System.out.println("mask: " + String.format("%64s",
            Long.toBinaryString(timeUnits[m.value()])).replaceAll(" ", "0"));
  }

  public void print_years(){
    for (HashMap.Entry<Integer,Integer> entry : yearRange.entrySet()) {
      System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());
    }
  }
}
