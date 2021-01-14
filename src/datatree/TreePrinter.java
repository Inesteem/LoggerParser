package src.datatree;
import src.types.Metric;
import static src.types.Metric.*;
public class TreePrinter implements TreeVisitor<Void> {
    TimeRange timeRange;
    Limits limits;

    public  TreePrinter(TimeRange timeRange, Limits limits) {
        this.timeRange = timeRange;
        this.limits = limits;
    }
    public  TreePrinter(Limits limits) {
        this.timeRange = TimeRange.ALL;
        this.limits = limits;
    }
    public  TreePrinter(TimeRange tr){
        this.timeRange = tr;
        this.limits = new Limits();
    }
    public  TreePrinter(){
        this.timeRange = TimeRange.ALL;
        this.limits = new Limits();
    }
    @Override
    public Void visit(YearMap ym, Metric metric) {
        System.out.println("");
        for(int i = 0; i < ym.subUnits.size(); ++i){
            if (!timeRange.in_range(YEAR,ym.get_idx(i))) continue;
            Year year = ym.subUnits.get(i);
            if (year != null && year.is_valid(YEAR)) {
                visit(year, metric);
            }
        }
        System.out.println("");
        return null;
    }

    @Override
    public Void visit(Year y, Metric metric) {
        System.out.println(y.y + ":");

        for (int i = 0; i < y.subUnits.size(); ++i) {
            if (!timeRange.in_range(MONTH,y.get_idx(i))) continue;
            Month month = y.subUnits.get(i);
            if (month != null  && month.is_valid(MONTH)) {
                System.out.println(Month.toString(i) + ":");
                visit(month, metric);
            }
        }
        return null;
    }

    @Override
    public Void visit(Month m, Metric metric) {


        for (int i = 0; i < m.subUnits.size(); ++i) {
            if (!timeRange.in_range(DAY,m.get_idx(i))) continue;
            Day day = m.subUnits.get(i);

            if (day != null  && day.is_valid(DAY)) {
                visit(day, metric);
            }
        }
        System.out.println("");
        return null;
    }

    @Override
    public Void visit(Day d, Metric metric) {
        for (int i = 0; i < d.subUnits.size(); ++i) {
        if (!timeRange.in_range(HOUR,d.get_idx(i))) continue;
            Hour hour = d.subUnits.get(i);
            if (hour != null && hour.is_valid(HOUR)) {
                visit(hour, metric);
            }
        }
        System.out.println("");
        return null;
    }

    @Override
    public Void visit(Hour h, Metric metric) {
        double avg = h.num != 0 ? h.sum/h.num : 0;
        System.out.print("(" + h.sum + " " + avg+ ")");
        return null;
    }
}
