package com.ue.common;

import android.app.Application;

import com.ue.common.util.LogUtil;

/**
 * Created by hawk on 2016/11/21.
 */

public class App {
    private static final Application INSTANCE;

    static {
        Application app = null;
        try {
            app = (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null);
            if (app == null)
                throw new IllegalStateException("Static initialization of Applications must be on main thread.");
        } catch (final Exception e) {
            LogUtil.e("App","Failed to get current application from AppGlobals." + e.getMessage());
            try {
                app = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null);
            } catch (final Exception ex) {
                LogUtil.e("App","Failed to get current application from ActivityThread." + e.getMessage());
            }
        } finally {
            INSTANCE = app;
        }
    }

    public static Application getInstance(){
        return INSTANCE;
    }
}
