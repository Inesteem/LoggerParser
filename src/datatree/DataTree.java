/** From all the things that are disgusting in this project the data tree is the most repelling one.
 * Yes, even more awful than the gui stuff.
 * Before you can use the data tree, you need to fill it with add_data. That's ok, I guess.
 * After the tree is filled up with data, you need to specify limits (which asserts for example that
 * hours are only valid if they contain more than n measurements). This is done by set_limits and checked
 * by is_valid. Than a TimeRange needs to be applied. Since the TimeRange does not know which years are contained
 * inside the data tree (in contrast to the number of hours, days or months) they need to be added first before they
 * can be constrained. So the data tree function add_years is called upon the TimeRange object. After that, the used
 * years can be constrained for example by using set_idx. SET_IDX WILL NOT WORK BEFORE THE YEARS ARE ADDED! Brilliant.
 * Why didn't I just assume that no more than 64 years ending with the current year are valid? I mean nobody wants to add
 * 1950 log data, not using this program anyways. Yes, I guess that holds true. But I wanted the program to be more general.
 * I traded usability for better generalization.
 * For hours, days and months this is no problem by the way, since there are always max 31 days and so on. I could just hard-
 * code them.
 * Before the functions get_num, get_avg, get_sum ,get_min and get_max work calc needs to be called first with a valid
 * timeRange object. You cannot call any of this functions or even calc again using another TimeRange object.
 * First, reset needs to be called. Yes, bad design. But that's because of the DynamicProgramming Concept used to improve efficiency.
 * I traded usability for efficiency. I am sorry.
 * Why did I use a TimeUnitI interface deriving TimeUnit? Why do Hours only implement TimeUnitI while days, months and years
 * all derive from TimeUnit? I don't know any more. There is a stupid reason for this shit but I do not remember. Probably I
 * wanted to save some memory, so I traded usability for an improved memory usage.
 * Again, I am sorry. It's all crap but it works, so I wont change it. Let's just hate this peace of shit together.
 *
 * So, a valid usage would be
 * DataTree ym = ...//fill the tree
 * TimeRange tr = new TimeRange(~0l); //allow all hours, days, months and years
 * //days, hours and months can be unset in advance
 * tr.unset_idx(DAY,0); //everybody hates mondays, so let's disable them. Remember to subtract one for the nth day
 * ym.add_years(tr);
 * //years can only be disabled AFTER add_years is called
 * tr.unset_idx(YEAR,2020); //unset 2020. Nobody likes 2020. Fuck you, Corona.
 * ym.calc(tr);
 * //use the tree by, e.g. getting the min (get_min(tr)), etc...
 *
 * //adding new TimeRanges requires a reset
 * tr.reset()
 * //the data inside the tree and thus the valid years do not change; there is no need to call add_years again.
 * tr.unset_idx(YEAR,2021); //unset 2021. It will probably suck, too.
 * ym.calc(tr);
 *
 * //adding new Limits requires a reset
 * tr.reset()
 * Limits limits = new Limits();
 * //its equally valid to add a limit to limits BEFORE its added to the DataTree...
 * limits.set_limit(HOUR, 3); // hours are only valid if they contain at least 3 measurements
 * limits.set_limit(DAY, 12); // days are only valid if they contain at least 12 valid hours
 * ym.set_limit(limits);
 * //as it is valid to add it AFTER. It doesn't matter since it is the same element.
 * //this means that changing the limits element outside the DataTree changes the limits inside it.
 * limits.set_limit(MONTH, 15); // months are only valid if they contain at least 15 valid days
 * ym.calc(tr);
 * */


package src.datatree;
import src.types.*;

import java.util.Calendar;
import java.util.HashMap;

public class DataTree extends TimeUnit<Year> {
  protected HashMap<Integer,Year> hm;

  public DataTree(Limits limits){
    super(0,limits);
    hm = new HashMap<Integer,Year>();
    metric = Metric.YEAR;
  }

  public void add_val(double val,  Calendar cal){
    int idx = cal.get(Calendar.YEAR);
    Year year;
    if (!hm.containsKey(idx)) {
      year = new Year(idx,limits);
      hm.put(idx,year);	
      subUnits.add(year);
    } else {
      year = hm.get(idx);
    }
    year.add_val(val,cal);
  }

  public void add_years(TimeRange tr) {
    for (HashMap.Entry<Integer, Year> entry : hm.entrySet()) {
       int idx = tr.add_year(entry.getKey());
       //TODO: check if idx == -1 really needed here ?
       tr.set_idx(Metric.YEAR, idx);
    }
  }

  public int get_idx(int i){
    if (i < 0 || i > subUnits.size()) return -1;
    Year year = subUnits.get(i);
    if (year == null) return -1;
    return year.y;
  }

  public String identifier(int id) {
    return ""; 
  }

  public <T> void accept(TreeVisitor<T> visitor, Metric m, TimeRange timeRange){
    visitor.set_limits(this.limits);
    visitor.set_timeRange(timeRange);
    visitor.visit(this,m);
  }
}
