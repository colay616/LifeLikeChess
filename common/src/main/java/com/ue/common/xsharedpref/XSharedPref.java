package com.ue.common.xsharedpref;

import android.net.Uri;
import android.os.Bundle;

import com.ue.common.App;


public class XSharedPref {

    private static final Uri spUri = Uri.parse("content://com.ue.chess_life.sp");

    public static String getString(String key, String defValue) {
        Bundle reqData = new Bundle();
        reqData.putString(SharedPrefProvider.PARAM_KEY, key);
        reqData.putString(SharedPrefProvider.PARAM_DEF_VALUE, defValue);
        Bundle replyData = App.getInstance().getContentResolver().call(spUri, SharedPrefProvider.Method.GET_STRING, null, reqData);
        return replyData.getString(SharedPrefProvider.REPLY_VALUE);
    }


    public static int getInt(String key, int defValue) {
        Bundle reqData = new Bundle();
        reqData.putString(SharedPrefProvider.PARAM_KEY, key);
        reqData.putInt(SharedPrefProvider.PARAM_DEF_VALUE, defValue);

        Bundle replyData = App.getInstance().getContentResolver().call(spUri, SharedPrefProvider.Method.GET_INT, null, reqData);
        return replyData.getInt(SharedPrefProvider.REPLY_VALUE);
    }

    public static long getLong(String key, long defValue) {
        Bundle reqData = new Bundle();
        reqData.putString(SharedPrefProvider.PARAM_KEY, key);
        reqData.putLong(SharedPrefProvider.PARAM_DEF_VALUE, defValue);

        Bundle replyData = App.getInstance().getContentResolver().call(spUri, SharedPrefProvider.Method.GET_LONG, null, reqData);
        return replyData.getLong(SharedPrefProvider.REPLY_VALUE);
    }

    public static float getFloat(String key, float defValue) {
        Bundle reqData = new Bundle();
        reqData.putString(SharedPrefProvider.PARAM_KEY, key);
        reqData.putFloat(SharedPrefProvider.PARAM_DEF_VALUE, defValue);

        Bundle replyData = App.getInstance().getContentResolver().call(spUri, SharedPrefProvider.Method.GET_FLOAT, null, reqData);
        return replyData.getFloat(SharedPrefProvider.REPLY_VALUE);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        Bundle reqData = new Bundle();
        reqData.putString(SharedPrefProvider.PARAM_KEY, key);
        reqData.putBoolean(SharedPrefProvider.PARAM_DEF_VALUE, defValue);

        Bundle replyData = App.getInstance().getContentResolver().call(spUri, SharedPrefProvider.Method.GET_BOOLEAN, null, reqData);
        return replyData.getBoolean(SharedPrefProvider.REPLY_VALUE);
    }


    public static void putString(String key, String value) {
        Bundle reqData = new Bundle();
        reqData.putString(SharedPrefProvider.PARAM_KEY, key);
        reqData.putString(SharedPrefProvider.PARAM_VALUE, value);

        Bundle replyData = App.getInstance().getContentResolver().call(spUri, SharedPrefProvider.Method.PUT_STRING, null, reqData);
    }


    public static void putInt(String key, int value) {
        Bundle reqData = new Bundle();
        reqData.putString(SharedPrefProvider.PARAM_KEY, key);
        reqData.putInt(SharedPrefProvider.PARAM_VALUE, value);

        App.getInstance().getContentResolver().call(spUri, SharedPrefProvider.Method.PUT_INT, null, reqData);
    }

    public static void putLong(String key, long value) {
        Bundle reqData = new Bundle();
        reqData.putString(SharedPrefProvider.PARAM_KEY, key);
        reqData.putLong(SharedPrefProvider.PARAM_VALUE, value);

        App.getInstance().getContentResolver().call(spUri, SharedPrefProvider.Method.PUT_LONG, null, reqData);
    }

    public static void putFloat(String key, float value) {
        Bundle reqData = new Bundle();
        reqData.putString(SharedPrefProvider.PARAM_KEY, key);
        reqData.putFloat(SharedPrefProvider.PARAM_VALUE, value);

        App.getInstance().getContentResolver().call(spUri, SharedPrefProvider.Method.PUT_FLOAT, null, reqData);
    }

    public static void putBoolean(String key, boolean value) {
        Bundle reqData = new Bundle();
        reqData.putString(SharedPrefProvider.PARAM_KEY, key);
        reqData.putBoolean(SharedPrefProvider.PARAM_VALUE, value);

        App.getInstance().getContentResolver().call(spUri, SharedPrefProvider.Method.PUT_BOOLEAN, null, reqData);
    }

    public static void remove(String key) {
        Bundle reqData = new Bundle();
        reqData.putString(SharedPrefProvider.PARAM_KEY, key);

        App.getInstance().getContentResolver().call(spUri, SharedPrefProvider.Method.REMOVE, null, reqData);
    }

    public static void clear() {
        Bundle reqData = new Bundle();
        App.getInstance().getContentResolver().call(spUri, SharedPrefProvider.Method.PUT_STRING, null, reqData);
    }

    public static boolean contains(String key) {
        Bundle reqData = new Bundle();
        reqData.putString(SharedPrefProvider.PARAM_KEY, key);

        Bundle replyData = App.getInstance().getContentResolver().call(spUri, SharedPrefProvider.Method.CONTAINS, null, reqData);
        return replyData.getBoolean(SharedPrefProvider.REPLY_VALUE);
    }

}
