
package timeunits;


public class TimeRange {
  
 
 int timeunits[] = {0,0,0};
 public static final TimeRange ALL= new TimeRange(0xFFFFFFFF);


 public TimeRange(int arr[]){
    timeunits=arr;
 }


 public TimeRange(int val){
    timeunits[0] = val;
    timeunits[1] = val;
    timeunits[2] = val;
 }

 public boolean in_range(Metric m, int idx){
    return (timeunits[m.value()] & (1 << idx)) != 0;
 }

 public void set_idx(Metric m, int idx){
    timeunits[m.value()] |= (1 << idx);
 }

 public void set_range(Metric m, int from, int to){
    if (from > to || from > 31)
      return;

    if (from == to) {
      set_idx(m,from);
    }

    int mask =  (1 << (from - to)) - 1;
    mask <<= from;

    timeunits[m.value()] |= mask;

 }

 public void set_all(Metric m) {
    timeunits[m.value()] = 0xFFFFFFFF;
 }

}
