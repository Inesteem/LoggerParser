package src.types;

public enum ParserType {
    IMPULS(0),
    MOMENT_VALS(1),
    REL_HUM(2),
    REL_HUM_VOLT(3),
    WITH_FOG(4),
    REL_HUM_WIND(5),
    HOBO(6),
    NONE(7);

    public final int value;
    private ParserType(int value){
        this.value = value;

    }
}
