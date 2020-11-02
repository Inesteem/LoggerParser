package parser;

import java.util.ArrayList;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class JRangeField extends JValField<Integer>{

  TimeRange tr;
  Metric m;

  public JRangeField (int length, TimeRange tr, Metric m) {
    super(length);
    this.m = m;
    this.tr = tr;
    setText("ALL");
    default_val = "ALL";
    tr.set_val(m,0xFFFFFFFF);
  }

  public Integer valueOf(String str) throws Exception{
    tr.print_years();
    if(str.length()== 0 || str.toUpperCase().equals("ALL")){ 
      tr.set_val(m,0xFFFFFFFF);
      return 0xFFFFFFFF;
    }
    
    String splitted[] = str.split("\\s+");
    tr.set_val(m,0);
    

    for(String part : splitted) {
      String partS[] = part.split("-");
      if (partS.length > 2) throw new Exception("bullshit");
      else if(partS.length == 2) {
            int from = Integer.valueOf(partS[0]);
            int to = Integer.valueOf(partS[1]);
            if(from > to) throw new Exception("from > to");
            tr.set_range(m,from,to+1);
        //TODO
      } else {
        partS = part.split(",");
        for(String n : partS) {
            int num = Integer.valueOf(n);
            tr.set_idx(m,num);
        }
      }
        
    }
       System.out.println("mask: " + String.format("%32s", 
                   Integer.toBinaryString(tr.get_val(m))).replaceAll(" ", "0"));

    return tr.get_val(m); 
  }


  public Integer errVal() {return -1;}
}
