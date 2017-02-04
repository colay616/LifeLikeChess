package com.ue.common.util;

/**
 * Created by hawk on 2016/11/21.
 */

import android.util.Log;

/**
 * 日志打印类
 */
public class LogUtil {

    //开发期间：isLogEnabled=true, 输出全部调试日志
    //开发结束：isLogEnabled=false, 关闭全部调试日志
    private static boolean isLogEnabled=true;

    public static void v(String TAG, String msg) {
        if (isLogEnabled) {
            Log.v(TAG, msg);
        }
    }

    public static void d(String TAG, String msg) {
        if (isLogEnabled) {
            Log.d(TAG, msg);
        }
    }

    public static void i(String TAG, String msg) {
        if (isLogEnabled) {
            Log.i(TAG, msg);
        }
    }

    public static void w(String TAG, String msg) {
        if (isLogEnabled) {
            Log.w(TAG, msg);
        }
    }

    public static void e(String TAG, String msg) {
        if (isLogEnabled) {
            Log.e(TAG, msg);
        }
    }
}