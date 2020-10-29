package plotting;

import java.util.ArrayList;
import timeunits.*;
import parser.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class JRangeField extends JValField<Integer>{

  public JRangeField (int length) {
    super(length);
  }

  public Integer valueOf(String str) throws Exception{
    if(str.length()== 0 || str.toUpperCase().equals("ALL")) return 0xFFFFFFFF;
    
    String splitted[] = str.split("\\s+");
    int val = 0;
    

    for(String part : splitted) {
      String partS[] = part.split("-");
      if (partS.length > 2) throw new Exception("bullshit");
      else if(partS.length == 2) {
            int from = Integer.valueOf(partS[0]);
            int to = Integer.valueOf(partS[1]);
            if(from > to || to > 32) throw new Exception("to large num");
            val = TimeRange.set_range(val,from,to);
        //TODO
      } else {
        partS = part.split(",");
        for(String n : partS) {
            int num = Integer.valueOf(n);
            val = TimeRange.set_idx(val,num);
        }
      }
        
    }
       System.out.println("mask: " + String.format("%32s", 
                   Integer.toBinaryString(val)).replaceAll(" ", "0"));

    return val; 
  }


  public Integer errVal() {return -1;}
}
