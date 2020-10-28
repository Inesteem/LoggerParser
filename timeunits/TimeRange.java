
package timeunits;


public class TimeRange {
  
 
 int timeUnits[] = {0,0,0,0};
 public static final TimeRange ALL= new TimeRange(0xFFFFFFFF);


 public TimeRange(int arr[]){
    timeUnits=arr;
 }


 public TimeRange(int val){
    for (int i = 0; i < timeUnits.length; ++i){
      timeUnits[i] = val;
      }
 }

 public boolean in_range(Metric m, int idx){
    if(m == Metric.YEAR) return true;
    return (timeUnits[m.value()] & (1 << idx)) != 0;
 }

 public void unset_idx(Metric m, int idx){
    timeUnits[m.value()] &= ~(1 << idx);
 }
 public void set_idx(Metric m, int idx){
    timeUnits[m.value()] |= (1 << idx);
 }

 public void unset_range(Metric m, int from, int to){
    if (from > to || from > 31)
      return;

    if (from == to) {
      set_idx(m,from);
    }

    int mask =  (1 << (to-from)) - 1;
    mask <<= from;

    timeUnits[m.value()] &= ~mask;

       System.out.println("mask: " + String.format("%32s", 
                   Integer.toBinaryString(~mask)).replaceAll(" ", "0"));
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

 public void set_range(Metric m, int from, int to){
    if (from > to || from > 31)
      return;

    if (from == to) {
      set_idx(m,from);
    }

    int mask =  (1 << (to-from)) -1;
    mask <<= from;

    timeUnits[m.value()] |= mask;
   
       System.out.println("mask: " + String.format("%32s", 
                   Integer.toBinaryString(mask)).replaceAll(" ", "0"));

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
}
