package src.datatree;

import src.gui.IOManager;
import src.types.Metric;
import src.types.Method;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static src.types.Metric.*;

public class TreeWriter implements TreeVisitor<Boolean> {

    Limits limits = null;
    TimeRange timeRange = null;
    Method method;
    FileOutputStream ostream;
    DecimalFormat df;

    @Override
    public void set_limits(Limits l){ this.limits = l;}

    @Override
    public void set_timeRange(TimeRange tr){ this.timeRange = tr;}

    @Override
    public void set_method(Method method ) {
        this.method = method;
    }

    public TreeWriter(FileOutputStream ostream, Method method){
        this.ostream = ostream;
        this.method = method;
        Locale locale = new Locale("en","UK");
        df = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        df.applyPattern("##.##");
    }

    public void monthly_overview(YearMap yearMap) {
        long valid_month_range = timeRange.get_val(MONTH);
        try {
            ostream.write("\nOVERALL MONTHLY STATS (num of meas, min, max, val): \n\n".getBytes());
            timeRange.unset_range(MONTH,0,12);
            for(int i = 0; i < 12; ++i){
                yearMap.reset();
                timeRange.set_idx(Metric.MONTH,i);
                yearMap.reset();
                ostream.write((Month.toString(i) + ": ").getBytes());
                if (yearMap.get_num(timeRange) == 0){
                    ostream.write(( "- \n").getBytes());
                    continue;
                }
                ostream.write(("\t " +String.valueOf(yearMap.get_num(timeRange)) + " ").getBytes());
                ostream.write(("\t " + df.format(yearMap.get_min(method, timeRange, MONTH)) + " ").getBytes());
                ostream.write(("\t " + df.format(yearMap.get_max(method, timeRange, MONTH)) + " ").getBytes());

                double val;
                if(method == Method.SUM) val = yearMap.get_sum(timeRange);
                else val = yearMap.get_avg(timeRange);

                ostream.write(("\t " + df.format(val) + "\n").getBytes());
                timeRange.unset_idx(Metric.MONTH,i);
                yearMap.reset();
            }
            ostream.write(("\n").getBytes());

        } catch (IOException e) {
            e.printStackTrace();
            IOManager.asError("yearmap write failed");
        }
        timeRange.set_val(MONTH, valid_month_range);
        yearMap.reset();

    }
    @Override
    public Boolean visit(YearMap ym, Metric metric){
        int valid_subunits = 0;

        double min = ym.get_min(method, timeRange, YEAR);
        double max = ym.get_max(method, timeRange, YEAR);
        try {
            if (min == Double.MAX_VALUE || max == Double.MIN_VALUE)  {
                ostream.write(("no valid data\n").getBytes());
                return false;
            }
            for(int i = 0; i < ym.subUnits.size(); ++i){
                if (!timeRange.in_range(YEAR,ym.get_idx(i))) continue;
                Year year = ym.subUnits.get(i);
                if (year != null && year.is_valid(YEAR)) {
                    ostream.write(("Year " + ym.get_idx(i) + ": ").getBytes());
                    if(visit(year, metric)) valid_subunits += 1;
                }
            }
            if (valid_subunits == 0) {
                ostream.write(("no valid data\n").getBytes());
                return false;
            }
            ostream.write(("\navg years: " + df.format(ym.get_val(timeRange, method)/valid_subunits) + "\n").getBytes());
            ostream.write(("min year: " + df.format(min) + "\n").getBytes());
            ostream.write(("max year: " + df.format(max) + "\n\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            IOManager.asError("yearmap write failed");
        }
        return true;
    }

    @Override
    public Boolean visit(Year y, Metric metric){
        double min = y.get_min(method, timeRange, YEAR);
        double max = y.get_max(method, timeRange, YEAR);
        boolean hasContent = false;
        try {
            if (min == Double.MAX_VALUE || max == Double.MIN_VALUE){
                ostream.write((" no valid data\n\n").getBytes());
                return false;
            }
            ostream.write(("val: " + df.format(y.get_val(timeRange, method)) + " - ").getBytes());
            ostream.write(("min month: " + df.format(min) + " - ").getBytes());
            ostream.write(("max month: " + df.format(max) + "\n\n").getBytes());

            if(metric == YEAR) return true;

            for (int i = 0; i < y.subUnits.size(); ++i) {
                if (!timeRange.in_range(MONTH,y.get_idx(i))) continue;
                Month month = y.subUnits.get(i);
                if (month != null  && month.is_valid(MONTH)) {
                    ostream.write(("   Month " + Month.toString(i) + ": ").getBytes());
                    hasContent |= visit(month, metric);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            IOManager.asError("year write failed");
        }
        return hasContent;
    }

    @Override
    public Boolean visit(Month m, Metric metric){
        double min = m.get_min(method, timeRange, YEAR);
        double max = m.get_max(method, timeRange, YEAR);
        if (min == Double.MAX_VALUE || max == Double.MIN_VALUE)  return false;
        boolean hasContent = false;
        try {
            ostream.write(("val: " + df.format(m.get_val(timeRange, method)) + " - ").getBytes());
            ostream.write(("min day: " + df.format(min) + " - ").getBytes());
            ostream.write(("max day: " + df.format(max) + "\n\n").getBytes());

            if(metric == MONTH) return true;

            for (int i = 0; i < m.subUnits.size(); ++i) {
                if (!timeRange.in_range(DAY,m.get_idx(i))) continue;
                Day day = m.subUnits.get(i);

                if (day != null  && day.is_valid(DAY)) {
                    ostream.write(("      Day " + (i+1) + ": ").getBytes());
                    hasContent |= visit(day, metric);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            IOManager.asError("month write failed");
        }
        return hasContent;
    }

    @Override
    public Boolean visit(Day d, Metric metric) {
        double min = d.get_min(method, timeRange, YEAR);
        double max = d.get_max(method, timeRange, YEAR);
        if (min == Double.MAX_VALUE || max == Double.MIN_VALUE)  return false;
        boolean hasContent = false;
        try {
            ostream.write(("val: " + df.format(d.get_val(timeRange, method)) + " - ").getBytes());
            ostream.write(("min day: " + df.format(min) + " - ").getBytes());
            ostream.write(("max day: " + df.format(max) + "\n\n").getBytes());

            if(metric == DAY) return true;

            for (int i = 0; i < d.subUnits.size(); ++i) {
                if (!timeRange.in_range(HOUR,d.get_idx(i))) continue;
                Hour hour = d.subUnits.get(i);
                if (hour != null && hour.is_valid(HOUR)) {
                    ostream.write(("         Hour " + i + ": ").getBytes());
                    hasContent |= visit(hour, metric);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            IOManager.asError("day write failed");
        }
        return hasContent;
    }

    @Override
    public Boolean visit(Hour h, Metric metric) {
        try {
            ostream.write((df.format(h.get_val(timeRange, method)) + "\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            IOManager.asError("hour write failed");
        }
        return true;
    }
}
