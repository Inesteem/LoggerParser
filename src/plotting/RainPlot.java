package parser;

import java.lang.Runtime.*;
import java.lang.Process.*;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.FileReader;
import java.io.IOException;


import org.apache.commons.io.FileUtils;


public class RainPlot{

  public static void execute_pyplot(String file, String title, Data pd, Metric metric) {
    try {

      String tmpDir = System.getProperty("java.io.tmpdir");
      File python = new File(tmpDir + "\\PlotFiles.py");
      System.out.println(python.getAbsolutePath()); // true
      //FileUtils.copyDirectory(new File(path), python);
      if (!python.exists()){ // true
        Files.copy(RainPlot.class.getResourceAsStream("PlotFiles.py"),Paths.get(python.getAbsolutePath()));
      }

      String line = "python3 " + python.getAbsolutePath()+ " --file "+file+" --title "+title+" --label "+String.valueOf(pd)+" --xTics "+String.valueOf(metric);
      System.out.println(line);

      // create a process and execute notepad.exe
      Process process = Runtime.getRuntime().exec(line);

    } catch (Exception ex) {
      ex.printStackTrace();
    }

  }

  public static void plot_stats(YearMap dataMap, Method method, Metric metric, String file, String title, Data pd, TimeRange tr, int max){

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
