
package timeunits;


public class TimeRange {
  
 
 int timeunits[] = {0,0,0,0};
 public static final TimeRange ALL= new TimeRange(0xFFFFFFFF);


 public TimeRange(int arr[]){
    timeunits=arr;
 }


 public TimeRange(int val){
    for (int i = 0; i < timeunits.length; ++i){
      timeunits[i] = val;
      }
 }

 public boolean in_range(Metric m, int idx){
    if(m == Metric.YEAR) return true;
    return (timeunits[m.value()] & (1 << idx)) != 0;
 }

 public void unset_idx(Metric m, int idx){
    timeunits[m.value()] &= ~(1 << idx);
 }
 public void set_idx(Metric m, int idx){
    timeunits[m.value()] |= (1 << idx);
 }

 public void unset_range(Metric m, int from, int to){
    if (from > to || from > 31)
      return;

    if (from == to) {
      set_idx(m,from);
    }

    int mask =  (1 << (to-from)) - 1;
    mask <<= from;

    timeunits[m.value()] &= ~mask;

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

    timeunits[m.value()] |= mask;
   
       System.out.println("mask: " + String.format("%32s", 
                   Integer.toBinaryString(mask)).replaceAll(" ", "0"));

 }

 public void set_all(Metric m) {
    timeunits[m.value()] = 0xFFFFFFFF;
 }

}
