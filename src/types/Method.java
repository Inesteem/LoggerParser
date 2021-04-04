package src.types;


public enum Method {
  AVG(0),SUM(1),SIZE(2);

  public final int value;

  private Method(final int value) {
    this.value = value;
  }
}
