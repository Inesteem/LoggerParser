package parser;
import parser.JValField;
import parser.Metric;
import parser.TimeRange;

public class JRangeField extends JValField<Long>{
  TimeRange timeRange;
  Metric metric;

  public JRangeField (int length, TimeRange tr, Metric m) {
    super(length);
    this.metric = m;
    this.timeRange = tr;
    default_val = String.valueOf(m.getMinIncl()) + "-" + String.valueOf(m.getMaxIncl());
    setText(default_val);
    tr.set_all(m);
  }

  public Long valueOf(String str) throws Exception{
    int min = metric.getMinIncl();
    //Non-informaticians find max inclusive ranges more intuitive
    int max = metric.getMaxIncl();

    if(str.length()== 0 || str.toUpperCase().equals("ALL")){
      timeRange.set_all(metric);
      return 0xFFFFFFFFFFFFFFFFl;
    }
    
    String splitted[] = str.split("\\s+");
    timeRange.set_val(metric,0);
    

    for(String part : splitted) {
      String partS[] = part.split("-");
      if (partS.length > 2) throw new Exception("bullshit");
      else if(partS.length == 2) {
            int from = Integer.valueOf(partS[0]);
            int to = Integer.valueOf(partS[1]);
            if(from > to || from < min || to > max) throw new Exception("from > to");
            timeRange.set_range(metric,from-min,to+1-min);
        //TODO
      } else {
        partS = part.split(",");
        for(String n : partS) {
            int num = Integer.valueOf(n);
          if(num < min || num > max) throw new Exception("not in range");
          timeRange.set_idx(metric,num-min);
        }
      }
        
    }

    return timeRange.get_val(metric);
  }


  public Long errVal() {return -1l;}
}
