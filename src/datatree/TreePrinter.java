package src.datatree;
import src.types.Metric;
public class TreePrinter implements TreeVisitor<Void> {

    @Override
    public Void visit(YearMap ym, Metric metric) {
        System.out.println("");
        for (Year year : ym.subUnits) {
            if (year != null ) {
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
            Month month = y.subUnits.get(i);
            if (month != null ) {
                System.out.println(Month.toString(i) + ":");
                visit(month, metric);
            }
        }
        return null;
    }

    @Override
    public Void visit(Month m, Metric metric) {
        for (Day day : m.subUnits) {
            if (day != null ) {
                visit(day, metric);
            }
        }
        System.out.println("");
        return null;
    }

    @Override
    public Void visit(Day d, Metric metric) {
        for (Hour hour : d.subUnits) {
            if (hour != null ) {
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
