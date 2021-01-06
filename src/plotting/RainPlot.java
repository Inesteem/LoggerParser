package parser;

import java.io.FileOutputStream;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;

public class RainPlot{

  public static void delete_pyscript() {

    String tmpDir = System.getProperty("java.io.tmpdir");
    File python = new File(tmpDir + "\\PlotFiles.py");
    if (python.exists()) { // true

    }
  }

  public static void execute_pyplot(String file, String title, Data pd, Metric metric) {
    try {

      String tmpDir = System.getProperty("java.io.tmpdir");
      File python = new File(tmpDir + "\\PlotFiles.py");
      System.out.println(python.getAbsolutePath()); // true
      //FileUtils.copyDirectory(new File(path), python);
      if (!python.exists()){ // true
        String path = new File(RainPlot.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath();
        path += "\\src\\plotting\\PlotFiles.py";
        System.out.println("new path: " + path);
    //    path="C:\\Users\\bt121073\\IdeaProjects\\LoggerParser\\out\\production\\LoggerParser\\src\\plotting\\PlotFiles.py";
        //System.out.println(RainPlot.class.getResourceAsStream());
        Files.copy(Paths.get(path),Paths.get(python.getAbsolutePath()));
      }

      String line = "python " + python.getAbsolutePath()+ " --file "+file+" --title "+title+" --label "+String.valueOf(pd)+" --xTics "+String.valueOf(metric);
      System.out.println(line);

      // create a process and execute notepad.exe
      Runtime.getRuntime().exec(line);

    } catch (Exception ex) {
      ex.printStackTrace();
    }

  }

  public static void plot_stats(YearMap dataMap, Method method, Metric metric, String file, String title, Data pd, TimeRange tr, int min, int max){

    FileOutputStream ostream; 
    try{
      ostream = new FileOutputStream(file);

      tr.unset_all(metric);
      double val_avg = 0;
      double num = 0;
      for(int i = min; i < max; ++i){

        tr.set_idx(metric,i);

        System.out.println("");
        tr.print(Metric.YEAR);
        tr.print(Metric.MONTH);
        tr.print(Metric.DAY);
        tr.print(Metric.HOUR);


        dataMap.reset();
        ostream.write(String.valueOf(i).getBytes());
        if (dataMap.get_num(tr) == 0){
          ostream.write(( " - \n").getBytes());
          tr.unset_idx(metric,i);
          continue;
        }
        ++num;

        double val;
        if(method == Method.SUM) val = dataMap.get_sum(tr);
        else val = dataMap.get_avg(tr);
        val_avg+=val;

        System.out.println(String.valueOf(i) + " " + String.valueOf(val) );
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
