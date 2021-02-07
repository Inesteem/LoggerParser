package src.types;

public enum Condition {

    LEQ("<=", (val, cmp) -> { return val <= cmp; }),
    LES("<", (val, cmp) -> { return val < cmp; }),
    GEQ(">=", (val, cmp) -> { return val >= cmp; }),
    GRT(">", (val, cmp) -> { return val > cmp; }),
    EQL("==", (val, cmp) -> { return val == cmp; }),
    NEQ("!=", (val, cmp) -> { return val != cmp; }),
    ALL("ALL", (val, cmp) -> { return true; });

    public interface Func {
        Boolean checkFor(double val, double cmp);

    //    public static Func ALL = (val, thresh) -> {
    //        return true;
    //    };
    //    public static Func LES = (val, thresh) -> {
    //        return val < thresh;
    //    };
    //    public static Func LEQ = (val, thresh) -> {
    //        return val <= thresh;
    //    };
    //    public static Func GRT = (val, thresh) -> {
    //        return val > thresh;
    //    };
    //    public static Func GEQ = (val, thresh) -> {
    //        return val >= thresh;
    //    };
    //    public static Func EQL = (val, thresh) -> {
    //        return val == thresh;
    //    };
    //    public static Func NEQ = (val, thresh) -> {
    //        return val != thresh;
    //    };

    }
    public final String name;
    public final Func cond;
    public static final String condNames [] = { "==", "!=", ">=", ">", "<=", "<" };
    private Condition(final String name, final Func cond) {
        this.cond = cond;
        this.name = name;
    }

    public static Condition getEnum(String m){
        switch (m.toUpperCase()) {
            case "ALL": return ALL;
            case "==": return EQL;
            case "!=": return NEQ;
            case ">": return GRT;
            case ">=": return GEQ;
            case "<": return LES;
            case "<=": return LEQ;
        }
        return null;
    }
}