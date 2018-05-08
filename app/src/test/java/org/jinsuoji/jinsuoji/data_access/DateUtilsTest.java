package org.jinsuoji.jinsuoji.data_access;

import org.junit.Test;

import java.text.ParseException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DateUtilsTest {
    private String testFromDateString(String str) {
        try {
            return DateUtils.toDateTimeString(DateUtils.fromDateTimeString(str));
        } catch (ParseException e) {
            return null;
        }
    }

    @Test
    public void testNormalDateString() {
        assertEquals("2018-05-01 19:00", testFromDateString("2018-05-01 19:00"));
        assertEquals("2014-07-19 00:01", testFromDateString("2014-07-19 00:01"));
    }

    @Test
    public void testMalformedDateString() {
        assertNull(testFromDateString("2018-05-01-01 19:00"));
        assertEquals("2018-05-01 18:00", testFromDateString("2018-05-01 18:00:00.000"));
        assertEquals("2018-05-01 18:00", testFromDateString("2018-5-1 18:00"));
        assertNull(testFromDateString("2018-05-01T18:00"));
    }

    @Test
    public void testOverflowedDateString() {
        assertEquals("2018-06-01 18:00", testFromDateString("2018-05-32 18:00"));
        assertEquals("2019-01-01 18:00", testFromDateString("2018-13-01 18:00"));
        assertEquals("2018-05-02 01:00", testFromDateString("2018-05-01 25:00"));
        assertEquals("2018-05-01 01:00", testFromDateString("2018-05-01 00:60"));
    }

    @Test
    public void testMakeDateString() {
        assertEquals("2018-05-01 00:00", DateUtils.makeDateString(2018, 5));
        assertEquals("2018-06-01 00:00", DateUtils.makeDateString(2018, 6));
        assertEquals("2015-05-01 00:00", DateUtils.makeDateString(2015, 5));
    }

    @Test
    public void testMakeDateInterval() {
        assertArrayEquals(new String[]{"2018-05-01 00:00", "2018-06-01 00:00"},
                DateUtils.makeDateInterval(2018, 5));
        assertArrayEquals(new String[]{"2018-06-01 00:00", "2018-07-01 00:00"},
                DateUtils.makeDateInterval(2018, 6));
        assertArrayEquals(new String[]{"2015-12-01 00:00", "2016-01-01 00:00"},
                DateUtils.makeDateInterval(2015, 12));
    }
}