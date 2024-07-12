package com.longx.intelligent.android.ichat2.util;

import android.annotation.SuppressLint;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by LONG on 2023/4/20 at 10:42 AM.
 */
public class TimeUtil {
    private static final String TIME_FORMAT_PATTERN_TODAY_SIMPLE = "a h:mm";
    private static final String TIME_FORMAT_PATTERN_YESTERDAY_SIMPLE = "昨天 a h:mm";
    private static final String TIME_FORMAT_PATTERN_BEFORE_YESTERDAY_SIMPLE = "前天 a h:mm";
    private static final String TIME_FORMAT_PATTERN_TOMORROW_SIMPLE = "明天 a h:mm";
    private static final String TIME_FORMAT_PATTERN_AFTER_TOMORROW_SIMPLE = "后天 a h:mm";
    private static final String TIME_FORMAT_PATTERN_WEEK_SIMPLE = "E";
    private static final String TIME_FORMAT_PATTERN_MONTH_SIMPLE = "M 月 d 日";
    private static final String TIME_FORMAT_PATTERN_YEAR_SIMPLE = "y 年 M 月 d 日";

    private static final String TIME_FORMAT_PATTERN_TODAY = "a h:mm";
    private static final String TIME_FORMAT_PATTERN_YESTERDAY = "昨天 a h:mm";
    private static final String TIME_FORMAT_PATTERN_BEFORE_YESTERDAY = "前天 a h:mm";
    private static final String TIME_FORMAT_PATTERN_TOMORROW = "明天 a h:mm";
    private static final String TIME_FORMAT_PATTERN_AFTER_TOMORROW = "后天 a h:mm";
    private static final String TIME_FORMAT_PATTERN_WEEK = "EEEE a h:mm";
    private static final String TIME_FORMAT_PATTERN_MONTH = "M 月 d 日 EEEE a h:mm";
    private static final String TIME_FORMAT_PATTERN_YEAR = "y 年 M 月 d 日 EEEE a h:mm";

    private static final String TIME_FORMAT_PATTERN_TODAY_DETAILED = "a h:mm:ss";
    private static final String TIME_FORMAT_PATTERN_YESTERDAY_DETAILED = "昨天 a h:mm:ss";
    private static final String TIME_FORMAT_PATTERN_BEFORE_YESTERDAY_DETAILED = "前天 a h:mm:ss";
    private static final String TIME_FORMAT_PATTERN_TOMORROW_DETAILED = "明天 a h:mm:ss";
    private static final String TIME_FORMAT_PATTERN_AFTER_TOMORROW_DETAILED = "后天 a h:mm:ss";
    private static final String TIME_FORMAT_PATTERN_WEEK_DETAILED = "EEEE a h:mm:ss";
    private static final String TIME_FORMAT_PATTERN_MONTH_DETAILED = "M 月 d 日 EEEE a h:mm:ss";
    private static final String TIME_FORMAT_PATTERN_YEAR_DETAILED = "y 年 M 月 d 日 EEEE a h:mm:ss";

    public static boolean isToday(Date date){
        return DateUtils.isToday(date.getTime());
    }

    public static boolean isYesterday(Date date) {
        return DateUtils.isToday(date.getTime() + DateUtils.DAY_IN_MILLIS);
    }

    public static boolean isTomorrow(Date date) {
        return DateUtils.isToday(date.getTime() - DateUtils.DAY_IN_MILLIS);
    }

    public static boolean isTheDayBeforeYesterday(Date date){
        return DateUtils.isToday(date.getTime() + DateUtils.DAY_IN_MILLIS * 2);
    }

    public static boolean isTheDayAfterTomorrow(Date date){
        return DateUtils.isToday(date.getTime() - DateUtils.DAY_IN_MILLIS * 2);
    }

    public static boolean isThisWeek(Date date){
        return (System.currentTimeMillis() - date.getTime()) <= DateUtils.WEEK_IN_MILLIS;
    }

    public static boolean isThisYear(Date date){
        return (System.currentTimeMillis() - date.getTime()) <= DateUtils.YEAR_IN_MILLIS;
    }

    public static String formatSimpleRelativeTime(Date date){
        SimpleDateFormat simpleDateFormat;
        if(isToday(date)){
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_TODAY_SIMPLE, Locale.CHINA);
        }else if(isYesterday(date)){
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_YESTERDAY_SIMPLE, Locale.CHINA);
        }else if(isTheDayBeforeYesterday(date)){
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_BEFORE_YESTERDAY_SIMPLE, Locale.CHINA);
        }else if(isTomorrow(date)){
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_TOMORROW_SIMPLE, Locale.CHINA);
        }else if(isTheDayAfterTomorrow(date)){
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_AFTER_TOMORROW_SIMPLE, Locale.CHINA);
        }else if(isThisWeek(date)){
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_WEEK_SIMPLE, Locale.CHINA);
        }else if(isThisYear(date)){
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_MONTH_SIMPLE, Locale.CHINA);
        }else {
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_YEAR_SIMPLE, Locale.CHINA);
        }
        return simpleDateFormat.format(date);
    }

    public static String formatRelativeTime(Date date){
        SimpleDateFormat simpleDateFormat;
        if(isToday(date)){
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_TODAY, Locale.CHINA);
        }else if(isYesterday(date)){
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_YESTERDAY, Locale.CHINA);
        }else if(isTheDayBeforeYesterday(date)){
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_BEFORE_YESTERDAY, Locale.CHINA);
        }else if(isTomorrow(date)){
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_TOMORROW, Locale.CHINA);
        }else if(isTheDayAfterTomorrow(date)){
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_AFTER_TOMORROW, Locale.CHINA);
        }else if(isThisWeek(date)){
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_WEEK, Locale.CHINA);
        }else if(isThisYear(date)){
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_MONTH, Locale.CHINA);
        }else {
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_YEAR, Locale.CHINA);
        }
        return simpleDateFormat.format(date);
    }

    public static String formatDetailedRelativeTime(Date date){
        SimpleDateFormat simpleDateFormat;
        if(isToday(date)){
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_TODAY_DETAILED, Locale.CHINA);
        }else if(isYesterday(date)){
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_YESTERDAY_DETAILED, Locale.CHINA);
        }else if(isTheDayBeforeYesterday(date)){
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_BEFORE_YESTERDAY_DETAILED, Locale.CHINA);
        }else if(isTomorrow(date)){
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_TOMORROW_DETAILED, Locale.CHINA);
        }else if(isTheDayAfterTomorrow(date)){
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_AFTER_TOMORROW_DETAILED, Locale.CHINA);
        }else if(isThisWeek(date)){
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_WEEK_DETAILED, Locale.CHINA);
        }else if(isThisYear(date)){
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_MONTH_DETAILED, Locale.CHINA);
        }else {
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN_YEAR_DETAILED, Locale.CHINA);
        }
        return simpleDateFormat.format(date);
    }

    public static boolean isDateAfter(long less, long greater, long thresholdMillis) {
        return isDateAfter(new Date(less), new Date(greater), thresholdMillis);
    }

    public static boolean isDateAfter(Date less, Date greater, long thresholdMillis) {
        long referenceTime = less.getTime();
        long targetTime = greater.getTime();
        return targetTime - referenceTime > thresholdMillis;
    }

    public static Date findNearestPastDate(Set<Date> dateList, Date date){
        List<Long> biggerThanList = new ArrayList<>();
        Map<Long, Date> map = new HashMap<>();
        for (Date date1 : dateList) {
            long biggerThan = date.getTime() - date1.getTime();
            biggerThanList.add(biggerThan);
            map.put(biggerThan, date1);
        }
        biggerThanList.sort((o1, o2) -> {
            if(o1 > o2){
                return 1;
            }else if(o1 < o2) {
                return -1;
            }
            return 0;
        });
        for (int i = 0; i < biggerThanList.size(); i++) {
            if(biggerThanList.get(i) > 0L){
                return map.get(biggerThanList.get(i));
            }
        }
        return null;
    }

    public static boolean isInSameYear(long millis1, long millis2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(millis1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(millis2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    public static boolean isInSameMonth(long millis1, long millis2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(millis1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(millis2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }

    public static boolean isInSameDay(long millis1, long millis2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(millis1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(millis2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    @SuppressLint("DefaultLocale")
    public static String formatTime(long timeInMillis) {
        long seconds = timeInMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        seconds %= 60;
        minutes %= 60;
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(String.format("%02d:", hours));
        }
        sb.append(String.format("%02d:", minutes));
        sb.append(String.format("%02d", seconds));
        return sb.toString();
    }

    @SuppressLint("DefaultLocale")
    public static String formatMillisecondsToMinSec(long milliseconds) {
        long totalSeconds = Math.round(milliseconds / 1000.0);
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        if (minutes > 0) {
            if(seconds > 0) {
                return String.format("%d' %02d''", minutes, seconds);
            }else {
                return String.format("%d'", minutes);
            }
        } else {
            return String.format("%d''", seconds);
        }
    }
}
