package src.datatree;

import src.gui.IOManager;
import src.types.Method;
import src.types.Metric;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static src.types.Metric.*;

public class TreeETE3Stringifier implements TreeVisitor<Integer> {

    Limits limits = null;
    TimeRange timeRange = null;
    Method method;
    DecimalFormat df;
    StringBuilder sb;

    @Override
    public void set_limits(Limits l){ this.limits = l;}

    @Override
    public void set_timeRange(TimeRange tr){ this.timeRange = tr;}

    @Override
    public void set_method(Method method ) {
        this.method = method;
    }

    public TreeETE3Stringifier(StringBuilder sb, Method method){
        this.sb = sb;
        this.method = method;
        Locale locale = new Locale("en","UK");
        df = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        df.applyPattern("##.##");
    }

    @Override
    public Integer visit(YearMap ym, Metric metric){
        sb.append("(");
        for(int i = 0; i < ym.subUnits.size(); ++i){
            if (!timeRange.in_range(YEAR,ym.get_idx(i))) continue;
            Year year = ym.subUnits.get(i);
            if (year != null && year.is_valid(YEAR)) {
                sb.append("(");
                if (visit(year, metric) != 0)
                    sb.append(")"+ym.get_idx(i)+",");
                else
                    sb.setLength(sb.length() - 1);
            }
        }
        if (sb.charAt(sb.length()-1) == ',') sb.setLength(sb.length() - 1);
        else sb.append("no valid data");

        sb.append("){;");
        return null;
    }

    @Override
    public Integer visit(Year y, Metric metric){
        int num = 0;
        int len = sb.length();
        for (int i = 0; i < y.subUnits.size(); ++i) {
            if (!timeRange.in_range(MONTH,y.get_idx(i))) continue;
            Month month = y.subUnits.get(i);
            if (month != null  && month.is_valid(MONTH)) {
                sb.append("(");
                num += visit(month, metric);
                sb.append(")"+Month.toString(i)+",");
            }
        }
        if (sb.charAt(sb.length()-1) == ',') {
            if (num == 0)
                sb.setLength(len);
            else
                sb.setLength(sb.length() - 1);
        }
        return num;
    }

    @Override
    public Integer visit(Month m, Metric metric){
        int num = 0;
        int len = sb.length();
        for (int i = 0; i < m.subUnits.size(); ++i) {
            if (!timeRange.in_range(DAY,m.get_idx(i))) continue;
            Day day = m.subUnits.get(i);

            if (day != null  && day.is_valid(DAY)) {
                sb.append("(");
                num += visit(day, metric);
                sb.append(")"+(i+1)+",");
            }
        }

        if (sb.charAt(sb.length()-1) == ',') {
            if (num == 0)
                sb.setLength(len);
            else
                sb.setLength(sb.length() - 1);
        }
        return num;
    }

    @Override
    public Integer visit(Day d, Metric metric) {
        int num = 0;
        int len = sb.length();
        for (int i = 0; i < d.subUnits.size(); ++i) {
            if (!timeRange.in_range(HOUR,d.get_idx(i))) continue;
            Hour hour = d.subUnits.get(i);
            if (hour != null && hour.is_valid(HOUR)) {
                sb.append("(");
                num += visit(hour, metric);
                sb.append(")"+i+",");
            }
        }
        if (sb.charAt(sb.length()-1) == ',') {
            if (num == 0)
                sb.setLength(len);
            else
                sb.setLength(sb.length() - 1);
        }
        return num;
    }

    @Override
    public Integer visit(Hour h, Metric metric) {
        sb.append(df.format(h.get_val(timeRange, method)));
        return 1;
    }
}
