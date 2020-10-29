package plotting;

import java.lang.Runtime.*;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileReader;

import java.io.IOException;

import timeunits.*;

public class RainPlot{

  public static void execute_pyplot(String file, String title, PlotData pd, Metric metric) {
      try {

        String line = "python3 " + "PlotFiles.py" + " --file "+file+" --title "+title+" --label "+String.valueOf(pd)+" --xTics "+String.valueOf(metric);
        System.out.println(line);
        // create a process and execute notepad.exe
        Process process = Runtime.getRuntime().exec(line);

      } catch (Exception ex) {
        ex.printStackTrace();
      }

    }
  
  public static void print_month_avg(YearMap dataMap, Method method, Metric metric, String file, String title, PlotData pd, TimeRange tr, int max){

    FileOutputStream ostream; 
    try{
      ostream = new FileOutputStream(file);

      tr.unset_range(metric,0,max);
      double val_avg = 0;
      double num = 0;
      for(int i = 0; i < max; ++i){

        tr.set_idx(metric,i);
        dataMap.reset(); 
        ostream.write(String.valueOf(i).getBytes());
        if (dataMap.get_num(tr) == 0){
          ostream.write(( " - \n").getBytes());
          continue;
        }
        ++num;

        double val;
        if(method == Method.SUM) val = dataMap.get_sum(tr);
        else val = dataMap.get_avg(tr);
        val_avg+=val;

        ostream.write((" " + String.valueOf(val) + "\n").getBytes());
        tr.unset_idx(metric,i);
      }
      if(method == Method.AVG && num != 0) val_avg /= num;

      ostream.close();
    } catch(IOException e) {
      e.printStackTrace();
    }
    execute_pyplot(file, title, pd, metric); 

  }

  


}
