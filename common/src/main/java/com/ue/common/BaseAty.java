package com.ue.common;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by hawk on 2017/1/4.
 */

public class BaseAty extends AppCompatActivity {
    public <T> T $(int viewID) {
        return (T) findViewById(viewID);
    }
}
