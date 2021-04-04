package src.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.gui.DataTreeVisualization;
import src.parser.ImpulsFormat;
import src.parser.LogFormat;
import src.parser.Parser;
import src.parser.TempRelHumFormat;
import src.plotting.PlotHelper;
import src.types.Data;
import src.types.Metric;
import src.types.ParserType;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

class ParserTest {
    final static String PREFIX = "logLP_TEST";
    final static String TMP_DIR = System.getProperty("java.io.tmpdir");
    Parser parser = new Parser();
    String base_path = ParserTest.class.getProtectionDomain().getCodeSource().getLocation().getPath() + File.separator + "data";
    @BeforeEach
    void setUp() {
         parser.reset();
         delete_test_data(new File(TMP_DIR));
    }

    public static boolean fileContentEquals(File f1, File f2, boolean invalidData) {
        try {
            FileReader fileReader1 = new FileReader(f1);
            BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
            FileReader fileReader2 = new FileReader(f2);
            BufferedReader bufferedReader2 = new BufferedReader(fileReader2);
            String l1 = null;
            String l2 = null;
            while( true) {
                l1= bufferedReader1.readLine();
                l2= bufferedReader2.readLine();
                if(l1 == null || l2 == null) break;
                String splitted1[] = l1.replaceAll("[\t\n]", " ").split("\\s+");
                String splitted2[] = l2.replaceAll("[\t\n]", " ").split("\\s+");
                if(splitted1.length != splitted2.length){
                    System.out.println(l1 + "  NOT EQUALS ");
                    System.out.println(l2 + "\n");
                    return false;
                }
                for(int i = 0; i < splitted1.length; ++i){
                    if(!(invalidData && (splitted1[i].equals("-") || splitted2[i].equals("-"))) &&
                        !splitted1[i].equals(splitted2[i])){
                        System.out.println(">"+splitted1[i] + "<  NOT EQUALS ");
                        System.out.println(">"+splitted2[i] + "<\n");
                        return false;
                    }

                }
            }
            while( l1 != null || (l1= bufferedReader1.readLine()) != null){
                l1 = l1.replaceAll("[\n\t\\s+]","");
                if(l1.length() != 0) return false;
                l1 = null;
            }
            while( l2 != null || (l2= bufferedReader2.readLine()) != null){
                l2 = l2.replaceAll("[\n\t\\s+]","");
                if(l2.length() != 0) return false;
                l2 = null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    void delete_test_data(File directory) {
        for (File f : directory.listFiles()) {
            if (f.getName().startsWith(PREFIX)) {
                System.out.println("delete " + f.getName());
                f.delete();
            }
        }
    }

    public static void pressEnter(int delay) {
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            assert(false);
        }
        robot.delay(delay);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

    private void parse(ParserType parserType, String input_path, boolean invalidData){
        final File input_file = new File(input_path);
        try {
            FileReader fileReader = new FileReader(input_file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            assert(parser.set_log_format(bufferedReader));
            assert(parser.getParserType() == parserType);
            LogFormat logFormat = parser.getLogFormat();
            Runnable runnable =
                    () -> { pressEnter(1000); };
            Thread thread = new Thread(runnable);
            thread.start();
            logFormat.configure("");
            thread.join();
            parser.parse(bufferedReader, logFormat);
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String output_path = TMP_DIR + File.separator + PREFIX;
        File output_file = new File(output_path + "_raw_data");
        Parser.writeLogInfo(output_path);

        assert(output_file.exists());
        assert(fileContentEquals(input_file, output_file, invalidData));
    }

    public void visualize() {
        try {
            DataTreeVisualization dtv = parser.doVisualize();
            dtv.start();
            dtv.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkFileContains(String[] data, File file) {
        try {
            FileReader fileReader1 = new FileReader(file);
            BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
            String line = null;
            while( (line = bufferedReader1.readLine()) != null) {

                line = line.replaceAll("[\t\\s+]","");
                for(int i = 0; i < data.length; ++i){
                    if(data[i].replaceAll(" ", "").equals(line.replaceAll(" ", ""))){
                        data[i] = "";
                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String s : data) {
            if (s.length() > 0) {
                System.out.println(s);
                return false;
            }
        }
        return true;
    }

    @Test
    public void parseImpuls2() throws IOException {
        String file_name = "test.ASC";
        //String file_name = "kidiamnarachini_rain DK311P-27029 26.11.20 17-13-19.ASC";
        String input_path = base_path + File.separator + "impuls" + File.separator + file_name;
        Preferences pref = Preferences.userRoot();
        pref.putDouble(ImpulsFormat.PREF_STR+"_" + Data.RAIN.name + "_MIN", Data.RAIN.min);
        pref.putDouble(ImpulsFormat.PREF_STR+"_" + Data.RAIN.name + "_MAX", Data.RAIN.max);
        pref.putDouble(ImpulsFormat.PREF_STR+ "_MM", 0.2);
        parse(ParserType.IMPULS, input_path, false);
        String results[] = {
                "Jan:-", "Feb:-", "Mar:-", "Apr:-",
                "May:-", "Jun:-", "Jul:-", "Aug:-",
                "Sep: 6 1 4.2 4.2 4.2", "Oct:-",
                "Nov: 10 2 1.6 2.2 1.9", "Dec:-",
                "0:-","1:-", "2:-", "3:-", "4:-", "5:-", "6:-", "7:-", "8:-", "9:-", "10:-",
                "11: 3 1 2", "12: 3 2 0.6",
                "13: 3 1 0", "14: 4 2 0.9",
                "15: 3 1 3", "16:-", "17:-", "18:-", "19:-", "20:-", "21:-",
                "22:-", "23:-",
                "Year 2020: val: 6.4 - min month: 2.2 - max month: 4.2",
                "Year 2021: val: 1.6 - min month: 1.6 - max month: 1.6",
                "avg all: 4","min year: 1.6","max year: 6.4"
        };
        String output_path = TMP_DIR + File.separator + PREFIX;
        File output_file = new File(output_path + "_RAIN");
        assert(output_file.exists());
        assert(checkFileContains(results, output_file));
    }
    @Test
    public void parseImpuls() throws IOException {
        String file_name = "test.ASC";
        //String file_name = "kidiamnarachini_rain DK311P-27029 26.11.20 17-13-19.ASC";
        String input_path = base_path + File.separator + "impuls" + File.separator + file_name;
        Preferences pref = Preferences.userRoot();
        pref.putDouble(ImpulsFormat.PREF_STR+"_" + Data.RAIN.name + "_MIN", Data.RAIN.min);
        pref.putDouble(ImpulsFormat.PREF_STR+"_" + Data.RAIN.name + "_MAX", Data.RAIN.max);
        pref.putDouble(ImpulsFormat.PREF_STR+ "_MM", 1.0);
        parse(ParserType.IMPULS, input_path, false);
        String results[] = {
                "Jan:-", "Feb:-", "Mar:-", "Apr:-",
                "May:-", "Jun:-", "Jul:-", "Aug:-",
                "Sep: 6 1 21 21 21", "Oct:-",
                "Nov: 10 2 8 11 9.5", "Dec:-",
                "0:-","1:-", "2:-", "3:-", "4:-", "5:-", "6:-", "7:-", "8:-", "9:-", "10:-",
                "11: 3 1 10", "12: 3 2 3",
                "13: 3 1 0", "14: 4 2 4.5",
                "15: 3 1 15", "16:-", "17:-", "18:-", "19:-", "20:-", "21:-",
                "22:-", "23:-",
                "Year 2020: val: 32 - min month: 11 - max month: 21",
                "Year 2021: val: 8 - min month: 8 - max month: 8",
                "avg all: 20","min year: 8","max year: 32"
        };
        String output_path = TMP_DIR + File.separator + PREFIX;
        File output_file = new File(output_path + "_RAIN");
        assert(output_file.exists());
        assert(checkFileContains(results, output_file));
    }

   @Test
   public void parseTempRelHum() throws IOException {
       String file_name = "test.ASC";
       String input_path = base_path + File.separator + "temp_rel_hum" + File.separator + file_name;
       Preferences pref = Preferences.userRoot();
       pref.putDouble(TempRelHumFormat.PREF_STR+"_" + Data.TEMP.name + "_MIN", Data.TEMP.min);
       pref.putDouble(TempRelHumFormat.PREF_STR+"_" + Data.TEMP.name + "_MAX", Data.TEMP.max);
       pref.putDouble(TempRelHumFormat.PREF_STR+"_" + Data.HUM.name + "_MIN",  Data.HUM.min);
       pref.putDouble(TempRelHumFormat.PREF_STR+"_" + Data.HUM.name + "_MAX",  Data.HUM.max);
       parse(ParserType.REL_HUM, input_path, false);
       String results[] = {
               "Jan:-", "Feb:  13 1  -0.48  -0.4  -0.46".replaceAll(" ", ""),
               "Mar:  3 1  -0.4  -0.4  -0.4",
               "Apr:  4 2  -0.87  -0.44  -0.66",
               "May:  3 1  -0.69  -0.69  -0.69",
               "Jun:  3 1  -0.02  -0.02  -0.02",
               "Jul:-", "Aug:-","Sep:-", "Oct:-", "Nov:-", "Dec:-",
               "0:  11  -0.32", "1: 33 -0.66", "2: 11 -0.85", "3: 42 -0.24", "4: 33 -0.81", "5: 32 -0.67",
               "6: 11 -0.56", "7: 11 -0.56", "8: 11 -0",  "9: 11 0.49", "10: -",  "11: -",
               "12: -", "13: -", "14: -", "15: -", "16: -", "17: -", "18: -", "19: -", "20: -",
               "21: 11 0.33", "22: 53 -0.34", "23: 11 -1.37",
               "Year 2015: val: -0.46 - min month: -0.46 - max month: -0.46",
               "Year 2016: val: -0.42 - min month: -0.44 - max month: -0.4",
               "Year 2017: val: -0.49 - min month: -0.87 - max month: -0.02",
               "avg all: -0.46","min year: -0.49","max year: -0.42"
       };

       String output_path = TMP_DIR + File.separator + PREFIX;
       File output_file_hum = new File(output_path + "_HUM");
       File output_file_temp = new File(output_path + "_TEMP");

       assert(output_file_hum.exists());
       assert(output_file_temp.exists());
       assert(checkFileContains(results, output_file_temp));
   }
}