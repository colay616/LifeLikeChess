package com.ue.common.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * create by Weavey
 * on date 2016-01-06
 * TODO
 */

public class DisplayUtil {
    private static DisplayMetrics dm;

    public static DisplayMetrics getDisplayMetrics(Context context){
        if(null==dm){
            dm = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        }
        return dm;
    }

    //获取屏幕宽度
    public static int getScreenWidth(Context context) {
        if(null==dm){
            dm=getDisplayMetrics(context);
        }
        return dm.widthPixels;
    }

    //获取屏幕高度
    public static int getScreenHeight(Context context) {
        if(null==dm){
            dm=getDisplayMetrics(context);
        }
        return dm.heightPixels;
    }

    /**
     * dp转换px
     */
    public static int dp2px(Context context,int dip) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * px转换dp
     */

    public static int px2dip(Context context,int px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public static int px2sp(Context context,float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics()
                .scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context,float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics()
                .scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
