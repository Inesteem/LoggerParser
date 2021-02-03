package src.gui;
import src.datatree.TimeRange;
import src.datatree.TreeETE3Stringifier;
import src.datatree.YearMap;
import src.types.*;

import java.io.FileOutputStream;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.sql.Time;

import static src.types.Metric.DAY;

public class PlotHelper {

  public static void delete_pyscripts(String scriptName) {

    String tmpDir = System.getProperty("java.io.tmpdir");
    File script = new File(tmpDir + "\\" + scriptName);
    if (script.exists()) { // true
      script.delete();
    }
  }

  public static String copy_pyscript(String scriptName) {
    String tmpDir = System.getProperty("java.io.tmpdir");
    String newFileName = tmpDir + "\\"+scriptName;
    String pyFileName = "\\src\\plotting\\"+scriptName;
    File newFile = new File(newFileName);
    String classPath = new File(PlotHelper.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath();
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
  public static void execute_dataPlot(String title, Data plotData, Metric metric, String fileName) {
    String path = copy_pyscript("PlotFiles.py");
    if (path.equals("")) {
      IOManager.asWarning("Plotting not possible; The python file '\"+fileName+\"' is not accessible.");
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
  public static void execute_treeDataPlot(String fileName) {
    String path = copy_pyscript("PlotDataTree.py");
    if (path.equals("")) {
      IOManager.asWarning("Plotting not possible; The python file '"+fileName+"' is not accessible.");
      return;
    }

    String line = "python " + path + " --file "+fileName;
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
   * @param tr the timeRange set by the user via the GUI restricting certain time ranges
   */
  public static void plot_stats(YearMap dataMap, Method method, Metric metric, String title, Data plotData, TimeRange tr){

    String fileName = System.getProperty("java.io.tmpdir") + "\\plot_"+ plotData;
    int min = metric.getUserMinIncl();
    int max = metric.getUserMaxExcl();
    if (metric == Metric.YEAR) {
      min = tr.getMinYear();
      max = tr.getMaxYear()+1;
    }
      //do not change the submitted TimeRange
    long userSetTimeRange = tr.get_val(metric);
    TimeRange timeRange = new TimeRange(tr);
    try {
      FileOutputStream plotFile = new FileOutputStream(fileName);
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
        System.out.println(idx + " " + (idx-min) + " " + dataMap.get_sum(timeRange));

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
    execute_dataPlot(title, plotData, metric, fileName);
    tr.print(metric);
  }

  public static void visualizeDataTree(YearMap dataMap, Method method, TimeRange timeRange, String fileName) {
    String filePath = System.getProperty("java.io.tmpdir") + "\\"+fileName;
    StringBuilder sb = new StringBuilder("");
    TreeETE3Stringifier tw = new TreeETE3Stringifier(sb,method);
    dataMap.add_years(timeRange);
    tw.set_timeRange(timeRange);
    dataMap.accept(tw,DAY, timeRange);
    System.out.println(sb.toString()); //TODO
    try {
      FileOutputStream plotFile = new FileOutputStream(filePath);
      plotFile.write(sb.toString().getBytes());
      plotFile.close();
      execute_treeDataPlot(filePath);
    } catch(IOException e) {
      e.printStackTrace();
      IOManager.asWarning(e.getMessage());
      return;
    }
  }

}