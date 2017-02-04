package com.ue.common.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.ue.common.App;
import com.ue.common.R;

/**
 * Created by hawk on 2016/11/21.
 */

public class ToastUtil {
    public static void toast(String msg) {
        Toast.makeText(App.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void toast(int msgId) {
        Toast.makeText(App.getInstance(), msgId, Toast.LENGTH_SHORT).show();
    }

    public static void toastLong(String msg) {
        Toast.makeText(App.getInstance(), msg, Toast.LENGTH_LONG).show();
    }

    public static void toastInCenter(String msg) {
        Toast toast = Toast.makeText(App.getInstance(),
                msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void toastInMainThread(Activity context, final String msg) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast(msg);
            }
        });
    }

    public static void toastInCenter(int msgId) {
        Toast toast = Toast.makeText(App.getInstance(),
                msgId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showChatToast(final Context context, final Spannable spanTxt, final boolean isMyTxt,int deviation) {
        TextView contentView = new TextView(context);
        contentView.setText(spanTxt, TextView.BufferType.SPANNABLE);
        contentView.setGravity(Gravity.CENTER_VERTICAL);
        contentView.setTextColor(Color.parseColor("#000000"));
        contentView.setTextSize(16f);
        contentView.setBackgroundResource(isMyTxt? R.mipmap.ic_left_bubble:R.mipmap.ic_right_bubble);

        Toast chatToast = new Toast(context);
        chatToast.setDuration(Toast.LENGTH_LONG);
        deviation+=20;
        chatToast.setGravity(isMyTxt ? Gravity.TOP | Gravity.LEFT : Gravity.TOP | Gravity.RIGHT, 10, deviation);
        chatToast.setView(contentView);

        chatToast.show();
    }
}