/*
package src.datatree;
import src.types.Method;
import src.types.Metric;
import src.types.TreeResult
        ;
public class TreeSummer implements TreeVisitor<TreeResult> {
    TimeRange timeRange;
    protected double minVal[][];
    protected double maxVal[][];

    public TreeSummer(TimeRange tr){
        this.timeRange = tr;
    }

    void set_extrema(double val, Method method, Metric metric){
        int methodI = method.value();
        int metricI = metric.value();
        if(TreeResult.isNaN(minVal[methodI][metricI]) || minVal[methodI][metricI] > val)
            minVal[methodI][metricI] = val;
        if(TreeResult.isNaN(maxVal[methodI][metricI]) || maxVal[methodI][metricI] < val)
            maxVal[methodI][metricI] = val;
    }
    @Override
    public TreeResult visit(YearMap ym, Metric metric) {
        TreeResult sum = new TreeResult();
        for (Year year : ym.subUnits) {
            if (year != null ) {
                sum.add(visit(year, metric));
            }
        }
        System.out.println("");
        return sum;
    }

    @Override
    public TreeResult visit(Year y, Metric metric) {
        TreeResult sum = new TreeResult();
        y.num = 0;
        y.sum = 0;
        for(int i = 0; i < y.subUnits.size(); ++i){
            if (!timeRange.in_range(metric,y.get_idx(i))) continue;
            Month unit = y.subUnits.get(i);
            if(unit == null) continue;
            int u_num = unit.get_num(timeRange);
            if(!unit.is_valid()) continue;
            if (u_num <= 0) continue;
            num += u_num;
            sum += unit.get_sum(timeRange);
            set_extimeRangeema(unit.get_sum(timeRange), Method.SUM);
            set_extimeRangeema(unit.get_avg(timeRange), Method.AVG);
        }




        double sum = 0;
        for (int i = 0; i < y.subUnits.size(); ++i) {
            Month month = y.subUnits.get(i);
            if (month != null ) {
                sum += visit(month, metric);
            }
        }
        return sum;
    }

    @Override
    public TreeResult visit(Month m, Metric metric) {
        double sum = 0;
        for (Day day : m.subUnits) {
            if (day != null ) {
                sum += visit(day, metric);
            }
        }
        System.out.println("");
        return sum;
    }

    @Override
    public TreeResult visit(Day d, Metric metric) {
        double sum = 0;
        for (Hour hour : d.subUnits) {
            if (hour != null ) {
                sum += visit(hour, metric);

            }
        }
        return sum;
    }

    @Override
    public TreeResult visit(Hour h, Metric metric) {
        return h.sum;
    }
}
*/