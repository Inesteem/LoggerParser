package parser;
import parser.IOManager;
import parser.Data;
import parser.Metric;
import parser.Method;
import parser.TimeRange;
import parser.YearMap;

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
      python.delete();
    }
  }
  public static String copy_pyscript() {
    String tmpDir = System.getProperty("java.io.tmpdir");
    String newFileName = tmpDir + "\\PlotFiles.py";
    String pyFileName = "\\src\\plotting\\PlotFiles.py";
    File newFile = new File(newFileName);
    String classPath = new File(RainPlot.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath();
    try {
      if (!newFile.exists()){
        if (!IOManager.extractZippedFile(classPath, pyFileName, newFileName)){
            Files.copy(Paths.get(classPath+pyFileName), Paths.get(newFile.getAbsolutePath()));
        }
      }

    } catch (Exception ex) {
      IOManager.asWarning(ex.getMessage());
      ex.printStackTrace();
      return "";
    }
    return newFileName;
  }
  public static void execute_pyplot(String title, Data plotData, Metric metric, String fileName) {
    String path = copy_pyscript();
    if (path.equals("")) {
      IOManager.asWarning("Plotting not possible; The python file is not accessible.");
      return;
    }

    String line = "python " + path + " --file "+fileName + " --title "+title+" --label "+String.valueOf(plotData)+" --xTics "+String.valueOf(metric);
    System.out.println(line);
    try{
      // create a process and execute notepad.exe
      Runtime.getRuntime().exec(line);

    } catch (Exception ex) {
      IOManager.asWarning(ex.getMessage());
      ex.printStackTrace();
    }
  }

  /** Combines and plots the logged data for a specific metric and with respect to time ranges defined in timeRange
   * @param dataMap contains the logged data
   * @param method data is either retrieved AVeraGed or SUMmed from the dataMap
   * @param metric data is either plotted averaged over HOURs, DAYs or MONTHs
   * @param title the title of the plotted data graph
   * @param plotData the plotted data type (RAIN, WIND, ...)
   * @param timeRange the timeRange set by the user via the GUI restricting certain time ranges
   */
  public static void plot_stats(YearMap dataMap, Method method, Metric metric, String title, Data plotData, TimeRange tr){

    String fileName = System.getProperty("java.io.tmpdir") + "\\plot_"+String.valueOf(plotData);
    int min = metric.getMinIncl();
    int max = metric.getMaxExcl();
    if (metric == Metric.YEAR) {
      min = tr.getMinYear();
      max = tr.getMaxYear()+1;
    }
      //do not change the submitted TimeRange
    tr.print(metric);
    TimeRange timeRange = new TimeRange(tr);
    try {
      FileOutputStream plotFile = new FileOutputStream(fileName);
      long userSetTimeRange = timeRange.get_val(metric);
      timeRange.unset_all(metric);

      for(int idx = min; idx < max; ++idx){
        plotFile.write((String.valueOf(idx)).getBytes());
        if (metric == Metric.YEAR) min = 0;
        if(!timeRange.set_idx(metric,idx-min)) {
          plotFile.write(( " - \n").getBytes());
          continue;
        }
        timeRange.print(metric);
        timeRange.and_val(metric,userSetTimeRange);
        timeRange.print(metric);
        System.out.println(idx + " " + (idx-min));

        dataMap.reset();
        if (dataMap.get_num(timeRange) == 0){
          plotFile.write(( " - \n").getBytes());
          timeRange.unset_idx(metric,idx-min);
          continue;
        }

        double val;
        if(method == Method.SUM) val = dataMap.get_sum(timeRange);
        else val = dataMap.get_avg(timeRange);

        plotFile.write((" " + val + "\n").getBytes());
        timeRange.unset_idx(metric,idx-min);
      }
      plotFile.close();
    } catch(IOException e) {
      e.printStackTrace();
      IOManager.asWarning(e.getMessage());
      return;
    }
    execute_pyplot(title, plotData, metric, fileName);
  }

  


}
