
package timeunits;


public class TimeRange {
  
 
 int timeUnits[];
 public static final TimeRange ALL= new TimeRange(0xFFFFFFFF);


 public TimeRange(int arr[]){
    timeUnits=arr;
 }

 public TimeRange(int val){
  
    timeUnits= new int[Metric.SIZE.value()];
    for (int i = 0; i < timeUnits.length; ++i)
      timeUnits[i] = val;
 }

 public boolean in_range(Metric m, int idx){
    if(m == Metric.YEAR) return true;
    return (timeUnits[m.value()] & (1 << idx)) != 0;
 }


 public static int unset_idx(int val, int idx){
    return val & ~(1 << idx);
 }
 public static int set_idx(int val, int idx){
    return val | (1 << idx);
 }

 public void unset_idx(Metric m, int idx){
    timeUnits[m.value()] &= ~(1 << idx);
 }
 public void set_idx(Metric m, int idx){
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

 void print_bits(int num) {
  System.out.print(String.valueOf(num + ": "));
   int pos = 1 << 31;
   while(pos != 0){
    if((pos & num) != 0)
      System.out.print("1");
    else 
      System.out.print("0");
    pos = pos >>> 1;   
   }
   System.out.println(""); 
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
    int idx = m.value();
    timeUnits[idx] = set_range(timeUnits[idx], from, to);
 }

 public void unset_range(Metric m, int from, int to){
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

}
