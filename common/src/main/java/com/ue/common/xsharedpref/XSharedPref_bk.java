//package com.ue.common.xsharedpref;
//
//import android.content.Context;
//import android.net.Uri;
//import android.os.Bundle;
//
//
//public class XSharedPref_bk {
//
//    private static final Uri spUri = Uri.parse("content://cn.droidlover.xsharedpref.sp");
//
//    public static String getString(Context context, String key, String defValue) {
//        Bundle reqData = new Bundle();
//        reqData.putString(SharedPrefProvider.PARAM_KEY, key);
//        reqData.putString(SharedPrefProvider.PARAM_DEF_VALUE, defValue);
//        Bundle replyData = context.getContentResolver().call(spUri, SharedPrefProvider.Method.GET_STRING, null, reqData);
//        return replyData.getString(SharedPrefProvider.REPLY_VALUE);
//    }
//
//
//    public static int getInt(Context context, String key, int defValue) {
//        Bundle reqData = new Bundle();
//        reqData.putString(SharedPrefProvider.PARAM_KEY, key);
//        reqData.putInt(SharedPrefProvider.PARAM_DEF_VALUE, defValue);
//
//        Bundle replyData = context.getContentResolver().call(spUri, SharedPrefProvider.Method.GET_INT, null, reqData);
//        return replyData.getInt(SharedPrefProvider.REPLY_VALUE);
//    }
//
//    public static long getLong(Context context, String key, long defValue) {
//        Bundle reqData = new Bundle();
//        reqData.putString(SharedPrefProvider.PARAM_KEY, key);
//        reqData.putLong(SharedPrefProvider.PARAM_DEF_VALUE, defValue);
//
//        Bundle replyData = context.getContentResolver().call(spUri, SharedPrefProvider.Method.GET_LONG, null, reqData);
//        return replyData.getLong(SharedPrefProvider.REPLY_VALUE);
//    }
//
//    public static float getFloat(Context context, String key, float defValue) {
//        Bundle reqData = new Bundle();
//        reqData.putString(SharedPrefProvider.PARAM_KEY, key);
//        reqData.putFloat(SharedPrefProvider.PARAM_DEF_VALUE, defValue);
//
//        Bundle replyData = context.getContentResolver().call(spUri, SharedPrefProvider.Method.GET_FLOAT, null, reqData);
//        return replyData.getFloat(SharedPrefProvider.REPLY_VALUE);
//    }
//
//    public static boolean getBoolean(Context context, String key, boolean defValue) {
//        Bundle reqData = new Bundle();
//        reqData.putString(SharedPrefProvider.PARAM_KEY, key);
//        reqData.putBoolean(SharedPrefProvider.PARAM_DEF_VALUE, defValue);
//
//        Bundle replyData = context.getContentResolver().call(spUri, SharedPrefProvider.Method.GET_BOOLEAN, null, reqData);
//        return replyData.getBoolean(SharedPrefProvider.REPLY_VALUE);
//    }
//
//
//    public static void putString(Context context, String key, String value) {
//        Bundle reqData = new Bundle();
//        reqData.putString(SharedPrefProvider.PARAM_KEY, key);
//        reqData.putString(SharedPrefProvider.PARAM_VALUE, value);
//
//        Bundle replyData = context.getContentResolver().call(spUri, SharedPrefProvider.Method.PUT_STRING, null, reqData);
//    }
//
//
//    public static void putInt(Context context, String key, int value) {
//        Bundle reqData = new Bundle();
//        reqData.putString(SharedPrefProvider.PARAM_KEY, key);
//        reqData.putInt(SharedPrefProvider.PARAM_VALUE, value);
//
//        context.getContentResolver().call(spUri, SharedPrefProvider.Method.PUT_INT, null, reqData);
//    }
//
//    public static void putLong(Context context, String key, long value) {
//        Bundle reqData = new Bundle();
//        reqData.putString(SharedPrefProvider.PARAM_KEY, key);
//        reqData.putLong(SharedPrefProvider.PARAM_VALUE, value);
//
//        context.getContentResolver().call(spUri, SharedPrefProvider.Method.PUT_LONG, null, reqData);
//    }
//
//    public static void putFloat(Context context, String key, float value) {
//        Bundle reqData = new Bundle();
//        reqData.putString(SharedPrefProvider.PARAM_KEY, key);
//        reqData.putFloat(SharedPrefProvider.PARAM_VALUE, value);
//
//        context.getContentResolver().call(spUri, SharedPrefProvider.Method.PUT_FLOAT, null, reqData);
//    }
//
//    public static void putBoolean(Context context, String key, boolean value) {
//        Bundle reqData = new Bundle();
//        reqData.putString(SharedPrefProvider.PARAM_KEY, key);
//        reqData.putBoolean(SharedPrefProvider.PARAM_VALUE, value);
//
//        context.getContentResolver().call(spUri, SharedPrefProvider.Method.PUT_BOOLEAN, null, reqData);
//    }
//
//    public static void remove(Context context, String key) {
//        Bundle reqData = new Bundle();
//        reqData.putString(SharedPrefProvider.PARAM_KEY, key);
//
//        context.getContentResolver().call(spUri, SharedPrefProvider.Method.REMOVE, null, reqData);
//    }
//
//    public static void clear(Context context) {
//        Bundle reqData = new Bundle();
//
//        context.getContentResolver().call(spUri, SharedPrefProvider.Method.PUT_STRING, null, reqData);
//    }
//
//    public static boolean contains(Context context, String key) {
//        Bundle reqData = new Bundle();
//        reqData.putString(SharedPrefProvider.PARAM_KEY, key);
//
//        Bundle replyData = context.getContentResolver().call(spUri, SharedPrefProvider.Method.CONTAINS, null, reqData);
//        return replyData.getBoolean(SharedPrefProvider.REPLY_VALUE);
//    }
//
//}
