package src.types;


public enum Method {
  AVG(0),SUM(1),SIZE(2);
  // constructor
  private Method(final int value) {
    this.value = value;
  }

  // internal state
  private int value;

  public int value() {
    return value;
  }
}
