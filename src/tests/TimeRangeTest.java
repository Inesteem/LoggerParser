package src.tests;

import src.gui.IOManager;
import src.types.Metric;
import src.datatree.TimeRange;
import java.lang.reflect.*;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TimeRangeTest {

    public static void print(long l) {
        System.out.println("val: " + String.format("%64s",
                Long.toBinaryString(l)).replaceAll(" ", "0"));
    }

    Field getField(String fieldName) {
        try {
            Field field = TimeRange.class.
                    getDeclaredField(fieldName);

            field.setAccessible(true);
            return field;

        } catch (Exception e) {
            IOManager.asWarning(e.getMessage());
            System.out.println(e.getMessage());
        }
        return null;
    }

    Method getMethod(String methodName, Class params[]) {
        try {

            Method method = TimeRange.class.
                    getDeclaredMethod(methodName, params);
            method.setAccessible(true);
            return method;

        } catch (Exception e) {
            IOManager.asWarning(e.getMessage());
            System.out.println(e.getMessage());
        }
        return null;
    }

    HashMap<Integer,Integer> getYearMap(TimeRange tr) {
        try {
            Field yearRangeField = getField("yearRange");
            return (HashMap<Integer, Integer>) yearRangeField.get(tr);
        } catch (Exception e) {
            IOManager.asWarning(e.getMessage());
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Test
    void static_un_set_idx() {
        //tests set_idx(val,idx) and unset_idx(val,idx)
        long num = 0l;
        for(int i = 0; i <= TimeRange.MAX_IDX_INCL; ++i) {
            assertFalse(TimeRange.is_set(num,i));
            num=TimeRange.set_idx(num,i);
            assertTrue(TimeRange.is_set(num,i));
            num=TimeRange.unset_idx(num,i);
            assertFalse(TimeRange.is_set(num,i));
        }
        assertEquals(0l,num);
    }

    @Test
    void static_is_set() {
        long num = 0l;
        for(int i = 0; i <= TimeRange.MAX_IDX_INCL; ++i) {
            assertFalse(TimeRange.is_set(num,i));
            num |= (1l << i);
            assertTrue(TimeRange.is_set(num,i));
        }
    }

    @Test
    void static_un_set_range() {
        //tests set_range(val,from,to) and unset_range(val,from,to)
        assertEquals(1l,TimeRange.set_range(0l,0,0));
        assertEquals(1l << 63,TimeRange.set_range(0l,63,63));
        assertEquals(0l, TimeRange.unset_range(1l<<63, 63,63));
        assertEquals(1l << 63, TimeRange.unset_range((1l<<63) | 1, 0,0));

        assertEquals(~0l, TimeRange.set_range(0l,0, 64));
        assertEquals(~0l, TimeRange.set_range(0l,0, 64));
        assertEquals(0l, TimeRange.unset_range(~0l,0, 64));

        assertEquals((~0l ^ (1l << 63l)) ^ 1l, TimeRange.set_range(0l,1,63));
        assertEquals((1l << 63l) | 1l, TimeRange.unset_range(~0l,1,63));
    }


    @Test
    void set_and_check() {

        //tests is_set(metric, idx), set_idx(metric, idx) and unset_idx(metric, idx)
        TimeRange tr = new TimeRange(0);

        for (int m = 0; m < Metric.SIZE.value(); ++m) {
            Metric metric = Metric.getEnum(m);
            for (int i = 0; i <= TimeRange.MAX_IDX_INCL; ++i) {
                int idx = metric.getMinIncl() + i;
                if (metric == Metric.YEAR){
                    tr.add_year(idx);
                }
                    assertFalse(tr.in_range(metric, idx));
                if(metric.inRange(idx)) {
                    assertTrue(tr.set_idx(metric, idx));
                    assertTrue(tr.in_range(metric, idx));
                    assertTrue(tr.unset_idx(metric, idx));
                    assertFalse(tr.in_range(metric, idx));
                }
            }
        }
    }

    @Test
    void add_year() {
        TimeRange tr = new TimeRange(0);
        for (int i = 0; i < 64; ++i) {
            int year = Metric.YEAR.getMinIncl()+ i;
            assertTrue(tr.add_year(year) != -1);
        }
        //should only allow for 64 years to be mapped
        assertTrue(tr.add_year(2021) == -1);
    }

    @Test
    void get_year_idx() {
        TimeRange tr = new TimeRange(0);

        //get private get_year_idx method
        Class params[] = new Class[1];
        params[0] = int.class;
        Method get_year_idx = getMethod( "get_year_idx", params);
        assertNotEquals(null,get_year_idx);

        //get private yearRange member
        HashMap<Integer,Integer> yearRange = getYearMap(tr);
        assertNotEquals(null,yearRange);

        int year = 2021;
        tr.add_year(year);
        assertEquals(1,yearRange.size());
        assertTrue(yearRange.containsKey(year));
        assertTrue(yearRange.containsValue(0));

        tr.add_year(year);
        assertTrue(yearRange.containsKey(year));
        assertFalse(yearRange.containsValue(1));

        try {
            assertEquals(0, get_year_idx.invoke(tr, 2021));

            for (int i = 1; i < 64; ++i) {
                year = 1950 + i;
                tr.add_year(year);
                assertEquals(i,get_year_idx.invoke(tr, year));
            }
        } catch (Exception e) {
            IOManager.asWarning(e.getMessage());
            System.out.println(e.getMessage());
            assertTrue(false);
        }

    }

    @Test
    void testUnSet_range() {
        //tests set_range(metric,from,to) and unset_range(metric, from, to)
        TimeRange tr = new TimeRange(0);
        int min_month = Metric.MONTH.getMinIncl();
        int max_month = Metric.MONTH.getMaxExcl();
        int min_year = Metric.YEAR.getMinIncl();
        int max_year = min_year+tr.MAX_IDX_INCL + 1;
        for(int i = min_year; i < max_year; ++i) {
            tr.add_year(i);
        }

        // SET SINGLE YEARS
        tr.set_range(Metric.YEAR,min_year,min_year);
        tr.set_range(Metric.YEAR,max_year-1,max_year-1);
        assertTrue(tr.in_range(Metric.YEAR, min_year));
        assertTrue(tr.in_range(Metric.YEAR, max_year-1));
        assertFalse(tr.in_range(Metric.YEAR, min_year+1));
        assertFalse(tr.in_range(Metric.YEAR, max_year-2));

        // UNSET SINGLE YEARS
        tr.unset_range(Metric.YEAR,min_year,min_year);
        tr.unset_range(Metric.YEAR,max_year-1,max_year-1);
        assertFalse(tr.in_range(Metric.YEAR, min_year));
        assertFalse(tr.in_range(Metric.YEAR, max_year-1));

        // SET YEAR RANGE
        tr.set_range(Metric.YEAR, min_year, max_year);
        for(int i = min_year; i < max_year; ++i) {
            assertTrue(tr.in_range(Metric.YEAR, i));
        }

        // UNSET YEAR RANGE
        tr.unset_range(Metric.YEAR, min_year, max_year);
        for(int i = min_year; i < max_year; ++i) {
            assertFalse(tr.in_range(Metric.YEAR, i));
        }

        // SET SINGLE MONTHS
        tr.set_range(Metric.MONTH,min_month,min_month);
        tr.set_range(Metric.MONTH,max_month-1,max_month-1);
        assertTrue(tr.in_range(Metric.MONTH, min_month));
        assertTrue(tr.in_range(Metric.MONTH, max_month-1));
        assertFalse(tr.in_range(Metric.MONTH, min_month+1));
        assertFalse(tr.in_range(Metric.MONTH, max_month-2));

        // UNSET SINGLE MONTHS
        tr.unset_range(Metric.MONTH,min_month,min_month);
        tr.unset_range(Metric.MONTH,max_month-1,max_month-1);
        assertFalse(tr.in_range(Metric.MONTH, min_month));
        assertFalse(tr.in_range(Metric.MONTH, max_month-1));

        // SET MONTH RANGE
        tr.set_range(Metric.MONTH, min_month, max_month);
        for(int i = min_month; i < max_month; ++i) {
            assertTrue(tr.in_range(Metric.MONTH, i));
        }
        // UNSET MONTH RANGE
        tr.unset_range(Metric.MONTH, min_month, max_month);
        for(int i = min_month; i < max_month; ++i) {
            assertFalse(tr.in_range(Metric.MONTH, i));
        }

        // FROM < Metric.getMinIncl() || TO > Metric.getMaxIncl()
        assertFalse(tr.set_range(Metric.MONTH, min_month-1, min_month));
        assertFalse(tr.set_range(Metric.MONTH, max_month-1, max_month+1));
        assertFalse(tr.set_range(Metric.YEAR, min_year-1, min_year));
        assertFalse(tr.set_range(Metric.YEAR, max_year-1, Metric.YEAR.getMaxExcl()+1));

        // FROM > TO
        assertFalse(tr.set_range(Metric.YEAR, min_year+1, min_year));
        assertFalse(tr.set_range(Metric.MONTH, min_month+1, min_month));
    }

    @Test
    void un_set_all() {
        //tests set_all(metric) and unset_all(metric)
        TimeRange tr = new TimeRange(0);
        int min_year = Metric.YEAR.getMinIncl();
        int max_year = min_year+tr.MAX_IDX_INCL + 1;
        for(int i = min_year; i < max_year; ++i) {
            tr.add_year(i);
        }
        tr.set_all(Metric.YEAR);
        for(int i = min_year; i < max_year; ++i) {
            assertTrue(tr.in_range(Metric.YEAR, i));
        }
        tr.unset_all(Metric.YEAR);
        for(int i = min_year; i < max_year; ++i) {
            assertFalse(tr.in_range(Metric.YEAR, i));
        }
    }

    @Test
    void testMinMaxYear() {
        TimeRange tr = new TimeRange(0);
        int min_year = Metric.YEAR.getMinIncl();
        int max_year = min_year+tr.MAX_IDX_INCL + 1;
        assertEquals(-1, tr.getMinYear());
        assertEquals(-1, tr.getMaxYear());
        for(int i = min_year; i < max_year; ++i) {
            tr.add_year(i);
            tr.set_idx(Metric.YEAR, i);
            assertEquals(min_year, tr.getMinYear());
            assertEquals(i, tr.getMaxYear());
        }
    }

    @Test
    void modify_vals() {
        //tests xor/and/or/set/get_val(metric,val)
        TimeRange tr = new TimeRange(0);
        Metric m = Metric.MONTH;
        long val1 = new java.util.Random().nextLong();
        long val2 = new java.util.Random().nextLong();
        long val3 = new java.util.Random().nextLong();
        long val4 = new java.util.Random().nextLong();
        tr.set_val(m,val1);
        assertEquals( val1, tr.get_val(m) );

        val1 = tr.get_val(m);
        tr.xor_val(m,val2);
        assertEquals( val1^val2, tr.get_val(m) );

        val1 = tr.get_val(m);
        tr.and_val(m,val3);
        assertEquals( val1&val3, tr.get_val(m) );

        val1 = tr.get_val(m);
        tr.or_val(m,val4);
        assertEquals( val1|val4, tr.get_val(m) );
    }
}