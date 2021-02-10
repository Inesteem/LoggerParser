package src.datatree;

import src.types.Data;
import src.types.Method;
import src.types.Metric;

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
    Data data;

    @Override
    public void set_limits(Limits l){ this.limits = l;}

    @Override
    public void set_timeRange(TimeRange tr){ this.timeRange = tr;}

    @Override
    public void set_method(Method method ) {
        this.method = method;
    }

    public void set_data(Data data) { this.data = data;}

    public TreeETE3Stringifier(StringBuilder sb, Method method){
        this.sb = sb;
        this.method = method;
        Locale locale = new Locale("en","UK");
        df = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        df.applyPattern("##.##");
    }

    @Override
    public Integer visit(DataTree ym, Metric metric){
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

        sb.append(")-;");
        return null;
    }

    @Override
    public Integer visit(Year y, Metric metric){

        int num = y.get_num_valid_subUnits(timeRange);
        System.out.println(y + " " + num);
        if (num == 0) return 0;

        if (metric == YEAR) {
            sb.append(num + "|" + df.format(y.get_val(timeRange, method))+data.unit);
            //sb.append(df.format(y.get_val(timeRange, method)));
            return num;
        }
        for (int i = 0; i < y.subUnits.size(); ++i) {
            if (!timeRange.in_range(MONTH,y.get_idx(i))) continue;
            Month month = y.subUnits.get(i);
            if (month != null  && month.is_valid(MONTH)) {
                sb.append("(");
                visit(month, metric);
                sb.append(")"+Month.toString(i)+",");
            }
        }
        if (sb.charAt(sb.length()-1) == ',') {
            sb.setLength(sb.length() - 1);
        }
        return num;
    }

    @Override
    public Integer visit(Month m, Metric metric){
        int num = m.get_num_valid_subUnits(timeRange);
        if (num == 0) return 0;

        if (metric == MONTH) {
            sb.append(num + "|" + df.format(m.get_val(timeRange, method))+data.unit);
            //sb.append(df.format(m.get_val(timeRange, method)));
            return num;
        }
        for (int i = 0; i < m.subUnits.size(); ++i) {
            if (!timeRange.in_range(DAY,m.get_idx(i))) continue;
            Day day = m.subUnits.get(i);

            if (day != null  && day.is_valid(DAY)) {
                sb.append("(");
                visit(day, metric);
                sb.append(")"+(i+1)+",");
            }
        }

        if (sb.charAt(sb.length()-1) == ',') {
                sb.setLength(sb.length() - 1);
        }
        return num;
    }

    @Override
    public Integer visit(Day d, Metric metric) {
        int num = d.get_num_valid_subUnits(timeRange);
        if (num == 0) return 0;

        if (metric == DAY) {
            sb.append(num + "|" + df.format(d.get_val(timeRange, method))+data.unit);
            //sb.append(df.format(d.get_val(timeRange, method)));
            return num;
        }
        for (int i = 0; i < d.subUnits.size(); ++i) {
            if (!timeRange.in_range(HOUR,d.get_idx(i))) continue;
            Hour hour = d.subUnits.get(i);
            if (hour != null && hour.is_valid(HOUR)) {
                sb.append("(");
                visit(hour, metric);
                sb.append(")"+i+",");
            }
        }
        if (sb.charAt(sb.length()-1) == ',') {
            sb.setLength(sb.length() - 1);
        }
        return num;
    }

    @Override
    public Integer visit(Hour h, Metric metric) {
        sb.append(h.get_num(timeRange) + "|" + df.format(h.get_val(timeRange, method))+data.unit);
        //sb.append(df.format(h.get_val(timeRange, method)));
        return 1;
    }
}
