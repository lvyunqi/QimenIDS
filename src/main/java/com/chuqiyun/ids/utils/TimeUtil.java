package com.chuqiyun.ids.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author mryunqi
 * @date 2023/3/14
 */
public class TimeUtil {
    private static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER =
            ThreadLocal.withInitial(() -> new SimpleDateFormat(DATE_FORMAT));

    public static long dateToTimestamp(String dateString) {
        return TimeUtil.parse(dateString);

    }

    public static long parse(String dateString) {
        LocalDateTime date4 = ZonedDateTime
                .parse(dateString, DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH))
                .toLocalDateTime();

        return date4.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }
/*    public static void main(String[] args) {
        String dateString = "Tue Mar 14 11:23:38 CST 2023";
        long timestamp = TimeUtil.parse(dateString);
        System.out.println(timestamp);
    }*/
}
