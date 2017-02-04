package com.ue.common.util;

import android.annotation.TargetApi;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hawk on 2016/11/21.
 */

public class DateUtils {
    public static final String FORMAT_DATETIME="yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_SHORT_DATETIME="MM-dd HH:mm";
    public static final String FORMAT_TIME="HH:mm:ss";

    public static String getFormatTime(long timeMills,String format){
        return new SimpleDateFormat(format).format(new Date(timeMills));
    }
    /**
     * 指定格式返回当前系统时间
     */
    public static String getDateTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        return df.format(new Date());
    }

    /**
     * 返回当前系统时间(格式以HH:mm形式)
     */
    public static String getDateTime() {
        return getDateTime("HH:mm");
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static boolean isToday(long when) {
        android.text.format.Time time = new android.text.format.Time();
        time.set(when);

        int thenYear = time.year;
        int thenMonth = time.month;
        int thenMonthDay = time.monthDay;

        time.set(System.currentTimeMillis());
        return (thenYear == time.year)
                && (thenMonth == time.month)
                && (time.monthDay == thenMonthDay);
    }

    public static String getFriendlyTime2(Date date) {
        String showStr = "";
        if (isToday(date.getTime())) {
            showStr = "今天";
        } else {
            showStr = getFriendlyTime(date);
        }
        return showStr;
    }

    /**
     * 转换日期到指定格式方便查看的描述说明
     *
     * @return 几秒前，几分钟前，几小时前，几天前，几个月前，几年前，很久以前（10年前）,如果出现之后的时间，则提示：未知
     */
    public static String getFriendlyTime(Date date) {
        String showStr = "";
        long yearSeconds = 31536000L;//365 * 24 * 60 * 60;
        long monthSeconds = 2592000L;//30 * 24 * 60 * 60;
        long daySeconds = 86400L;//24 * 60 * 60;
        long hourSeconds = 3600L;//60 * 60;
        long minuteSeconds = 60L;

        long time = (System.currentTimeMillis() - date.getTime()) / 1000;
        if (time <= 50) {
            showStr = "刚刚";
            return showStr;
        }
        if (time / yearSeconds > 0) {
            int year = (int) (time / yearSeconds);
            if (year > 10)
                showStr = "很久以前";
            else {
                showStr = year + "年前";
            }
        } else if (time / monthSeconds > 0) {
            showStr = time / monthSeconds + "个月前";
        } else if (time / daySeconds > 7) {
            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd", Locale.getDefault());
            showStr = formatter.format(date);
        } else if (time / daySeconds > 0) {
            showStr = time / daySeconds + "天前";
        } else if (time / hourSeconds > 0) {
            showStr = time / hourSeconds + "小时前";
        } else if (time / minuteSeconds > 0) {
            showStr = time / minuteSeconds + "分钟前";
        } else if (time > 0) {
            showStr = time + "秒前";
        }
        return showStr;
    }
}
