package src.datatree;

import src.gui.IOManager;
import src.types.Metric;
import src.types.Method;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static src.types.Metric.*;

public class TreeWriter implements TreeVisitor<Void> {

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
            ostream.write("\nOVERALL MONTHLY VALS (num of meas, min, max, val: \n".getBytes());
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
    public Void visit(YearMap ym, Metric metric){
        int valid_subunits =  Math.max(1,ym.get_num_valid_subUnits(timeRange));
        try {
            ostream.write(("avg years: " + df.format(ym.get_val(timeRange, method)/valid_subunits) + " - ").getBytes());
            ostream.write(("min year: " + df.format(ym.get_min(method, timeRange, YEAR)) + " - ").getBytes());
            ostream.write(("max year: " + df.format(ym.get_max(method, timeRange, YEAR)) + ":\n\n").getBytes());
            for(int i = 0; i < ym.subUnits.size(); ++i){
                if (!timeRange.in_range(YEAR,ym.get_idx(i))) continue;
                Year year = ym.subUnits.get(i);
                if (year != null && year.is_valid(YEAR)) {
                    ostream.write(("Year " + ym.get_idx(i) + ": ").getBytes());
                    visit(year, metric);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            IOManager.asError("yearmap write failed");
        }
        return null;
    }

    @Override
    public Void visit(Year y, Metric metric){
        try {
            ostream.write(("val: " + df.format(y.get_val(timeRange, method)) + " - ").getBytes());
            ostream.write(("min month: " + df.format(y.get_min(method, timeRange, MONTH)) + " - ").getBytes());
            ostream.write(("max month: " + df.format(y.get_max(method, timeRange, MONTH)) + ":\n\n").getBytes());

            if(metric == YEAR) return null;

            for (int i = 0; i < y.subUnits.size(); ++i) {
                if (!timeRange.in_range(MONTH,y.get_idx(i))) continue;
                Month month = y.subUnits.get(i);
                if (month != null  && month.is_valid(MONTH)) {
                    ostream.write(("   Month " + Month.toString(i) + ": ").getBytes());
                    visit(month, metric);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            IOManager.asError("year write failed");
        }
        return null;
    }

    @Override
    public Void visit(Month m, Metric metric){
        try {
            ostream.write(("val: " + df.format(m.get_val(timeRange, method)) + " - ").getBytes());
            ostream.write(("min day: " + df.format(m.get_min(method, timeRange, DAY)) + " - ").getBytes());
            ostream.write(("max day: " + df.format(m.get_max(method, timeRange, DAY)) + ":\n\n").getBytes());

            if(metric == MONTH) return null;

            for (int i = 0; i < m.subUnits.size(); ++i) {
                if (!timeRange.in_range(DAY,m.get_idx(i))) continue;
                Day day = m.subUnits.get(i);

                if (day != null  && day.is_valid(DAY)) {
                    ostream.write(("      Day " + (i+1) + ": ").getBytes());
                    visit(day, metric);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            IOManager.asError("month write failed");
        }
        return null;
    }

    @Override
    public Void visit(Day d, Metric metric) {
        try {
            ostream.write(("val: " + df.format(d.get_val(timeRange, method)) + " - ").getBytes());
            ostream.write(("min day: " + df.format(d.get_min(method, timeRange, HOUR)) + " - ").getBytes());
            ostream.write(("max day: " + df.format(d.get_max(method, timeRange, HOUR)) + ":\n\n").getBytes());

            if(metric == DAY) return null;

            for (int i = 0; i < d.subUnits.size(); ++i) {
                if (!timeRange.in_range(HOUR,d.get_idx(i))) continue;
                Hour hour = d.subUnits.get(i);
                if (hour != null && hour.is_valid(HOUR)) {
                    ostream.write(("         Hour " + i + ": ").getBytes());
                    visit(hour, metric);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            IOManager.asError("day write failed");
        }
        return null;
    }

    @Override
    public Void visit(Hour h, Metric metric) {
        try {
            ostream.write((df.format(h.get_val(timeRange, method)) + ":\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            IOManager.asError("hour write failed");
        }
        return null;
    }
}
