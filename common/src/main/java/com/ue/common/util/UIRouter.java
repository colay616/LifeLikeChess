package com.ue.common.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by hawk on 2016/11/21.
 */

public class UIRouter {
    public static final String REVERSI_DOUBLE_MODE_ACTIVITY="com.ue.reversi.ui.DoubleModeAty";
    public static final String REVERSI_SINGLE_MODE_ACTIVITY="com.ue.reversi.ui.SingleModeAty";
    public static final String REVERSI_ONLINE_MODE_ACTIVITY="com.ue.reversi.ui.OnlineModeAty";
    public static final String REVERSI_INVITE_MODE_ACTIVITY="com.ue.reversi.ui.InviteModeAty";

    public static final String GOBANG_DOUBLE_MODE_ACTIVITY="com.ue.gobang.ui.DoubleModeAty";
    public static final String GOBANG_SINGLE_MODE_ACTIVITY="com.ue.gobang.ui.SingleModeAty";
    public static final String GOBANG_ONLINE_MODE_ACTIVITY="com.ue.gobang.ui.OnlineModeAty";
    public static final String GOBANG_INVITE_MODE_ACTIVITY="com.ue.gobang.ui.InviteModeAty";

    public static final String CNCHESS_DOUBLE_MODE_ACTIVITY="com.ue.cnchess.ui.DoubleModeAty";
    public static final String CNCHESS_SINGLE_MODE_ACTIVITY="com.ue.cnchess.ui.SingleModeAty";
    public static final String CNCHESS_ONLINE_MODE_ACTIVITY="com.ue.cnchess.ui.OnlineModeAty";
    public static final String CNCHESS_INVITE_MODE_ACTIVITY="com.ue.cnchess.ui.InviteModeAty";

    public static final String CHESS_DOUBLE_MODE_ACTIVITY="com.ue.chess.ui.DoubleModeAty";
    public static final String CHESS_SINGLE_MODE_ACTIVITY="com.ue.chess.ui.SingleModeAty";
    public static final String CHESS_ONLINE_MODE_ACTIVITY="com.ue.chess.ui.OnlineModeAty";
    public static final String CHESS_INVITE_MODE_ACTIVITY="com.ue.chess.ui.InviteModeAty";

    public static final String MOON_DOUBLE_MODE_ACTIVITY="com.ue.moon_chess.ui.DoubleModeAty";
    public static final String MOON_SINGLE_MODE_ACTIVITY="com.ue.moon_chess.ui.SingleModeAty";
    public static final String MOON_ONLINE_MODE_ACTIVITY="com.ue.moon_chess.ui.OnlineModeAty";
    public static final String MOON_INVITE_MODE_ACTIVITY="com.ue.moon_chess.ui.InviteModeAty";

    public static final String ACF_DOUBLE_MODE_ACTIVITY="com.ue.army_chess.ui.FDoubleModeAty";
    public static final String ACF_ONLINE_MODE_ACTIVITY="com.ue.army_chess.ui.FOnlineModeAty";
    public static final String ACF_INVITE_MODE_ACTIVITY="com.ue.army_chess.ui.FInviteModeAty";

    public static final String ACA_ONLINE_MODE_ACTIVITY="com.ue.army_chess.ui.AOnlineModeAty";
    public static final String ACA_INVITE_MODE_ACTIVITY="com.ue.army_chess.ui.AInviteModeAty";

    public static final String ACM_DOUBLE_MODE_ACTIVITY="com.ue.army_chess.ui.MDoubleModeAty";
    public static final String ACM_ONLINE_MODE_ACTIVITY="com.ue.army_chess.ui.MOnlineModeAty";
    public static final String ACM_INVITE_MODE_ACTIVITY="com.ue.army_chess.ui.MInviteModeAty";

    public static void startActivityForName(Context context, String name, Bundle arguments) {
        try {
            Class clazz = Class.forName(name);
            Intent intent=new Intent(context,clazz);
            if(null!=arguments){
                intent.putExtras(arguments);
            }
            context.startActivity(intent);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Fragment getFragment(String name) {
        Fragment fragment;
        try {
            Class fragmentClass = Class.forName(name);
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return fragment;
    }
}
