package src.types;

public enum ParserType {
    IMPULS(0),
    REL_HUM(1),
    REL_HUM_VOLT(2),
    REL_HUM_WIND(3),
    WITH_FOG(4),
    HOBO(5),
    NONE(6); //used as size, needs to be num of parser types

    public final int value;
    private ParserType(int value){
        this.value = value;

    }
}
