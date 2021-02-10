package src.datatree;
import src.types.Metric;
import src.types.Method;
/*TODO: those visitors shall replace the UGLY get_min/get_max/get_avg/get_sum functions in the data tree structure*/
/*When I have time^(TM) I will implement this shit*/

public interface TreeVisitor<R> {

    R visit(DataTree ym, Metric metric);

    R visit(Year y, Metric metric);

    R visit(Month m, Metric metric);

    R visit(Day m, Metric metric);

    R visit(Hour m, Metric metric);

    void set_limits(Limits l);

    void set_timeRange(TimeRange tr);

    void set_method(Method m);
}
