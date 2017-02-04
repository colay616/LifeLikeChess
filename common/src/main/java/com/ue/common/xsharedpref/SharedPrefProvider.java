package com.ue.common.xsharedpref;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;


public class SharedPrefProvider extends ContentProvider {

    private SharedPreferences preferences;

    public static final String SP_NAME = "xsharedPref_config";

    public static final String PARAM_KEY = "param_key";
    public static final String PARAM_VALUE = "param_value";
    public static final String PARAM_DEF_VALUE = "param_defValue";

    public static final String REPLY_VALUE = "reply_value"; //返回值


    @Override
    public boolean onCreate() {
        preferences = getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return false;
    }


    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        Bundle replyData = null;
        if (extras == null) return replyData;

        String key = extras.getString(PARAM_KEY);
        replyData = new Bundle();

        switch (method) {
            case Method.GET_STRING: {
                String defVal = extras.getString(PARAM_DEF_VALUE);
                String replyVal = preferences.getString(key, defVal);

                replyData.putString(REPLY_VALUE, replyVal);
                break;
            }

            case Method.PUT_STRING: {
                String val = extras.getString(PARAM_VALUE);
                preferences.edit().putString(key, val).commit();
                break;
            }

            case Method.GET_INT: {
                int defVal = extras.getInt(PARAM_DEF_VALUE);
                int replyVal = preferences.getInt(key, defVal);

                replyData.putInt(REPLY_VALUE, replyVal);
                break;
            }

            case Method.PUT_INT: {
                int val = extras.getInt(PARAM_VALUE);
                preferences.edit().putInt(key, val).commit();
                break;
            }

            case Method.GET_FLOAT: {
                float defVal = extras.getFloat(PARAM_DEF_VALUE);
                float replyVal = preferences.getFloat(key, defVal);

                replyData.putFloat(REPLY_VALUE, replyVal);
                break;
            }

            case Method.PUT_FLOAT: {
                float val = extras.getFloat(PARAM_VALUE);
                preferences.edit().putFloat(key, val).commit();
                break;
            }

            case Method.GET_LONG: {
                long defVal = extras.getLong(PARAM_DEF_VALUE);
                long replyVal = preferences.getLong(key, defVal);

                replyData.putLong(REPLY_VALUE, replyVal);
                break;
            }

            case Method.PUT_LONG: {
                long val = extras.getLong(PARAM_VALUE);
                preferences.edit().putLong(key, val).commit();
                break;
            }

            case Method.GET_BOOLEAN: {
                boolean defVal = extras.getBoolean(PARAM_DEF_VALUE);
                boolean replyVal = preferences.getBoolean(key, defVal);

                replyData.putBoolean(REPLY_VALUE, replyVal);
                break;
            }

            case Method.PUT_BOOLEAN: {
                boolean val = extras.getBoolean(PARAM_VALUE);
                preferences.edit().putBoolean(key, val).commit();
                break;
            }

            case Method.REMOVE: {
                preferences.edit().remove(key).commit();
                break;
            }

            case Method.CLEAR: {
                preferences.edit().clear().commit();
                break;
            }

            case Method.CONTAINS: {
                boolean replyVal = preferences.contains(key);

                replyData.putBoolean(REPLY_VALUE, replyVal);
            }

        }

        return replyData;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    public static class Method {
        public static final String PUT_STRING = "putString";
        public static final String GET_STRING = "getString";
        public static final String PUT_BOOLEAN = "putBoolean";
        public static final String GET_BOOLEAN = "getBoolean";
        public static final String PUT_FLOAT = "putFloat";
        public static final String GET_FLOAT = "getFloat";
        public static final String PUT_INT = "putInt";
        public static final String GET_INT = "getInt";
        public static final String PUT_LONG = "putLong";
        public static final String GET_LONG = "getLong";
        public static final String REMOVE = "remove";
        public static final String CLEAR = "clear";
        public static final String CONTAINS = "contains";

    }
}
