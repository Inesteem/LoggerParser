package src.tests;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.BeforeEach;
import static src.types.Metric.*;
import static src.types.Method.*;
import static src.datatree.TimeRange.ALL;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import src.datatree.Limits;
import src.datatree.TimeRange;
import src.datatree.TreePrinter;
import src.datatree.YearMap;

import static org.junit.jupiter.api.Assertions.*;

class YearMapTest {
    public static DateFormat date_format;
    public static Calendar calendar;
    public static YearMap yearMap;
    public static YearMap yearMap2;

    final static String[] dates2 = {
            "01.01.10", "02.01.10", "03.02.10", "04.02.10",
            "04.02.11", "05.02.11", "08.06.11", "07.08.11",
            "01.01.20", "02.01.20", "09.06.20", "10.06.20"
    };
    final static String[][] times2 = {
            {"12:00:00","12:30:00","13:00:00","13:30:00","14:00:00"},
            {"00:30:00","01:00:00","01:30:00","02:00:00","02:30:00"},
            {"13:00:00","14:00:00","15:00:00","16:20:00","16:30:00","16:40:00"},
            {"18:00:00","19:00:00","20:00:00","21:00:00"},
            {"18:00:00","18:30:00"},
            {"18:20:00","18:30:00"},
            {"16:00:00","16:30:00"},
            {"17:00:00","18:00:00"},
            {"11:00:00","11:30:00","12:00:00"},
            {"01:20:00","01:40:00","02:00:00"},
            {"12:00:00","13:00:00"},
            {"00:10:00","00:20:00"}
    };
    final static double[] data2 = {
            1,2,3,4,5,
            5,6,7,8,9,
            12,0,0,1,2,1,
            27,1,2,3,
            1,4,
            3,5,
            4,5,
            3,4,
            3,4,1,
            7,8,1,
            0,0,
            2,3
    };

    final static String[] dates = {
            "07.06.11", "08.06.11",
            "01.09.20", "02.09.20", "03.09.20",
            "27.01.21", "28.01.21", "31.01.21", "01.06.21"
    };

    final static String[][] times = {
            {"18:00:00", "18:30:00"}, {"18:20:00", "18:40:00"},
            {"14:00:00"}, {"14:20:00"}, {"15:30:00", "14:40:00"},
            {"15:00:00"}, {"15:20:00"}, {"00:40:00"}, {"20:40:00"}
    };
    final static double[] data = {
            1, 4, 3, 5, // 13\
            6, 3, 4, 7, // 20 } 45
            2, 2, 3, 5  // 12/
    };
    public static YearMap getFilledYearMap(String dates[], String times[][], double data[]) throws java.text.ParseException {
         Limits limits = new Limits();
         YearMap yp = new YearMap(limits);
         assertTrue(dates.length == times.length);
         int d = 0;
         for(int i = 0; i < dates.length; ++i) {
             for(int j = 0; j < times[i].length; ++j) {
                 Date date = date_format.parse(dates[i] + " " + times[i][j]);
                 calendar.setTime(date);   // assigns calendar to given date
                 yp.add_val(data[d],calendar);
                 ++d;
             }
         }
        return yp;
    }
    public static YearMap getYearMap1(){

        try {
            return getFilledYearMap(dates, times, data);
        } catch (Exception e ){
            assertTrue(false);
        }
        return null;

    }
    public static YearMap getYearMap2(){

        try {
            return getFilledYearMap(dates2, times2, data2);
        } catch (Exception e ){
            assertTrue(false);
        }
        return null;

    }
    @BeforeAll
    static void beforeAll() {
        date_format = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
        calendar = GregorianCalendar.getInstance();
        yearMap = getYearMap1();
        yearMap2 = getYearMap2();
    }

    @BeforeEach
    void beforeEach() {
        yearMap.reset();
        yearMap2.reset();
    }

    @Test
    void test_stats() {
        yearMap.configure(ALL);
        int num = data.length;
        double sum = java.util.stream.DoubleStream.of(data).sum();
        assertEquals(num, yearMap.get_num(ALL));
        assertEquals(sum, yearMap.get_sum(ALL));
        assertEquals(sum/(double)num, yearMap.get_avg(ALL));

        yearMap2.configure(ALL);
        num = data2.length;
        sum = java.util.stream.DoubleStream.of(data2).sum();
        assertEquals(num, yearMap2.get_num(ALL));
        assertEquals(sum, yearMap2.get_sum(ALL));
        assertEquals(sum/(double)num, yearMap2.get_avg(ALL));

//        TreePrinter tp = new TreePrinter();
//        yearMap.accept(tp,null);
    }

    @Test
    void test_hours() {
        yearMap.configure(ALL);

        //min hour avg
        assertEquals(2,yearMap.get_min(SUM, ALL, HOUR));
        assertEquals(2,yearMap.get_min(AVG, ALL, HOUR));
        //max hour avg
        assertEquals(8,yearMap.get_max(SUM, ALL, HOUR));
        assertEquals(7,yearMap.get_max(AVG, ALL, HOUR));


        yearMap2.configure(ALL);

        TreePrinter tp = new TreePrinter();
        yearMap2.accept(tp,null);
        //min hour avg
        assertEquals(0,yearMap2.get_min(SUM, ALL, HOUR));
        assertEquals(0,yearMap2.get_min(AVG, ALL, HOUR));
        //max hour avg
        assertEquals(27,yearMap2.get_max(SUM, ALL, HOUR));
        assertEquals(27,yearMap2.get_max(AVG, ALL, HOUR));
    }
    @Test
    void test_days() {
        yearMap.configure(ALL);

        //min day avg
        assertEquals(2,yearMap.get_min(SUM, ALL, DAY));
        assertEquals(2,yearMap.get_min(AVG, ALL, DAY));
        //max day avg
        assertEquals(11,yearMap.get_max(SUM, ALL, DAY));
        assertEquals(6,yearMap.get_max(AVG, ALL, DAY));


        yearMap2.configure(ALL);

        //min day avg
        assertEquals(0,yearMap2.get_min(SUM, ALL, DAY));
        assertEquals(0,yearMap2.get_min(AVG, ALL, DAY));
        //max day avg
        assertEquals(35,yearMap2.get_max(SUM, ALL, DAY));
        assertEquals(8.25,yearMap2.get_max(AVG, ALL, DAY));
    }

    @Test
    void test_months() {
        yearMap.configure(ALL);
        //min month avg
        assertEquals(5,yearMap.get_min(SUM, ALL, MONTH));
        assertEquals(7.0/3.0,yearMap.get_min(AVG, ALL, MONTH));
        //max month avg
        assertEquals(20,yearMap.get_max(SUM, ALL, MONTH));
        assertEquals(5.0,yearMap.get_max(AVG, ALL, MONTH));

        yearMap2.configure(ALL);
        TreePrinter tp = new TreePrinter();
        yearMap2.accept(tp,null);

        //min month avg
        assertEquals(5,yearMap2.get_min(SUM, ALL, MONTH));
        assertEquals(1.25,yearMap2.get_min(AVG,ALL, MONTH));
        //max month avg
        assertEquals(50,yearMap2.get_max(SUM, ALL, MONTH));
        assertEquals(5,yearMap2.get_max(AVG, ALL, MONTH));



    }


    @Test
    void test_years() {
        yearMap.configure(ALL);
        //min year avg
        assertEquals(12,yearMap.get_min(SUM, ALL, YEAR));
        assertEquals(3.0,yearMap.get_min(AVG, ALL, YEAR));
        //max year avg
        assertEquals(20,yearMap.get_max(SUM, ALL, YEAR));
        assertEquals(5.0,yearMap.get_max(AVG, ALL, YEAR));


        yearMap2.configure(ALL);
        TreePrinter tp = new TreePrinter();
        yearMap2.accept(tp,null);

        //min year avg
        assertEquals(29,yearMap2.get_min(SUM, ALL, YEAR));
        assertEquals(2.9,yearMap2.get_min(AVG, ALL, YEAR));
        //max year avg
        assertEquals(99,yearMap2.get_max(SUM, ALL, YEAR));
        assertEquals(99./20.,yearMap2.get_max(AVG, ALL, YEAR));

    }

}