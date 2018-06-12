package org.jinsuoji.jinsuoji.data_access;

import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期和日期字符串之间的一些操作.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class DateUtils {
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

    public static DateFormat getDateFormat() {
        return dateFormat;
    }

    public static @Nullable Date fromDateString(String str) {
        try {
            return dateFormat.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    public static @Nullable Date fromDateTimeString(@Nullable String str) {
        try {
            return dateTimeFormat.parse(str);
        } catch (NullPointerException | ParseException e) {
            return null;
        }
    }

    public static Date makeDate(int year, int month, int date, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month - 1, date, hour, minute);
        return calendar.getTime();
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
        return dateFormat.format(makeDate(year, month));
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
        try {
            return dateTimeFormat.format(date);
        } catch (NullPointerException e) {
            return null;
        }
    }
}
