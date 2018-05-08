package org.jinsuoji.jinsuoji.data_access;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期和日期字符串之间的一些操作.
 */
public class DateUtils {
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

    public static DateFormat getDateFormat() {
        return dateFormat;
    }

    public static Date fromDateString(String str) throws ParseException {
        return dateFormat.parse(str);
    }

    public static Date fromDateTimeString(String str) throws ParseException {
        return dateTimeFormat.parse(str);
    }

    public static Date makeDate(int year, int month, int date) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month - 1, date);
        return calendar.getTime();
    }

    public static Date makeDate(int year, int month) {
        return makeDate(year, month, 1);
    }

    public static String makeDateString(int year, int month) {
        return dateTimeFormat.format(makeDate(year, month));
    }

    public static String[] makeDateInterval(int year, int month) {
        return new String[]{
                makeDateString(year, month),
                month == 12 ? makeDateString(year + 1, 1) : makeDateString(year, month + 1),
        };
    }

    public static String toDateString(Date date) {
        return dateFormat.format(date);
    }

    public static String toDateTimeString(Date date) {
        return dateTimeFormat.format(date);
    }
}
