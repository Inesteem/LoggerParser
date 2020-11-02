package parser;


public class Limits {
  private int limits[];

  public Limits(){
    limits = new int[Metric.SIZE.value()];
    for(int i = 0; i < limits.length; ++i) limits[i] = 0; 
  }

  public boolean valid(Metric m, int num) {
    return (num >= limits[m.value()]);
  } 

  public void set_limit(Metric m, int lim) {
    limits[m.value()]=lim;
  }

  public void print(){
    for(int i = 0; i < limits.length; ++i) System.out.print(String.valueOf(limits[i]) + " ");
    System.out.println("");
  }
}
