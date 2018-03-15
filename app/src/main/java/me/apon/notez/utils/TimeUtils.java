package me.apon.notez.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/13.
 */

public class TimeUtils {
    public static long toTimestamp(String serverTime) {
        return DateTime.parse(serverTime).getMillis();
    }

    public static String toServerTime(long timeInMills) {
        return DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").print(timeInMills);
    }

    public static String toTimeFormat(long timeInMills) {
        return DateTimeFormat.forPattern("H:mm:ss").print(timeInMills);
    }

    public static String toDateFormat(long timeInMills) {
        return DateTimeFormat.forPattern("M-dd H:mm:ss").print(timeInMills);
    }

    public static String toYearFormat(long timeInMills) {
        return DateTimeFormat.forPattern("yyyy-M-dd H:mm:ss").print(timeInMills);
    }
}
