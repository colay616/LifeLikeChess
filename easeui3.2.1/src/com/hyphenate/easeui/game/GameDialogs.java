package com.hyphenate.easeui.game;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.ue.common.widget.dialog.MDEditDialog;
import com.ue.common.widget.dialog.NormalAlertDialog;
import com.ue.common.widget.dialog.TipAlertDialog;

/**
 * Created by hawk on 2016/11/27.
 */

public class GameDialogs {
    private static NormalAlertDialog doubleBtnDialog;
    private static NormalAlertDialog singleBtnDialog;
    private static TipAlertDialog tipAlertDialog;
    private static MDEditDialog editDialog;

    private static void showDoubleBtnDialog(Context context, String title, String content, String leftTxt, View.OnClickListener leftClicker, String rightTxt, View.OnClickListener rightClicker) {
        if (null == doubleBtnDialog|| context != doubleBtnDialog.getContext()) {
            doubleBtnDialog = new NormalAlertDialog.Builder(context).setCanceledOnTouchOutside(false).build();
        }
        doubleBtnDialog.setTitle(title);
        doubleBtnDialog.setContentText(content);
        doubleBtnDialog.setLeftBtn(leftTxt,leftClicker);
        doubleBtnDialog.setRightBtn(rightTxt,rightClicker);
        doubleBtnDialog.show();
    }

    private static void showSingleBtnDialog(Context context, String title, String content, String btnTxt, View.OnClickListener btnClicker) {
        if(null==singleBtnDialog|| context != singleBtnDialog.getContext()){
            singleBtnDialog=new NormalAlertDialog.Builder(context)
                    .setSingleMode(true)
                    .setCanceledOnTouchOutside(false)
                    .build();
        }
        singleBtnDialog.setTitle(title);
        singleBtnDialog.setContentText(content);
        singleBtnDialog.setSingleBtn(btnTxt,btnClicker);
        singleBtnDialog.show();
    }

    public static void showTipDialog(Context context, String msg) {
        if (null == tipAlertDialog || context != tipAlertDialog.getContext()) {
            tipAlertDialog = new TipAlertDialog.Builder(context).setCanceledOnTouchOutside(false).build();
        }
        tipAlertDialog.setContentText(msg);
        tipAlertDialog.show();
    }

    private static void showEditDialog(Context context, String title, String leftBtnTxt, MDEditDialog.OnClickEditDialogListener leftClicker, String rightBtnTxt, MDEditDialog.OnClickEditDialogListener rightClicker) {
        if(null==editDialog||context!=editDialog.getContext()){
            editDialog=new MDEditDialog.Builder(context).build();
        }
        editDialog.setTitle(title);
        editDialog.setLeftButton(leftBtnTxt, leftClicker);
        editDialog.setRightButton(rightBtnTxt, rightClicker);
        editDialog.show();
    }

    public static void showUpdateApkDialog(final Context context, String content, View.OnClickListener updateClicker){
        showDoubleBtnDialog(context, "版本更新", content, "取消", null, "更新",updateClicker);
    }

    public static void showGotInvitationDialog(Context context, String content, View.OnClickListener rightClicker) {
        showDoubleBtnDialog(context, "游戏邀请", content, "拒绝", null, "接受", rightClicker);
    }

    public static void showDrawDialog(Context context, View.OnClickListener leftClicker, View.OnClickListener rightClicker) {
        showDoubleBtnDialog(context, "和棋请求", "对方请求和棋,是否同意", "不同意", leftClicker, "同意", rightClicker);
    }

    public static void showUndoDialog(Context context, View.OnClickListener leftClicker, View.OnClickListener rightClicker) {
        showDoubleBtnDialog(context, "悔棋请求", "对方请求悔棋,是否同意", "不同意", leftClicker, "同意", rightClicker);
    }

    public static void showOnlineExitDialog(Context context, View.OnClickListener leftClicker) {
        showDoubleBtnDialog(context, "温馨提示：", "确认退出游戏吗？", "确定", leftClicker, "取消", null);
    }

    public static void showOppoExitDialog(final Context context, View.OnClickListener rightClicker) {
        showDoubleBtnDialog(context, "温馨提示：", "对方退出了游戏", "退出", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) context).finish();
            }
        }, "继续", rightClicker);
    }

    public static void showChangeDeskDialog(Context context, View.OnClickListener clickListener) {
        showDoubleBtnDialog(context, "温馨提示：", "当前正在游戏中,确定换桌吗?", "确定", clickListener, "取消", null);
    }

    public static void showOfflineExitDialog(final Context context) {
        showDoubleBtnDialog(context, "温馨提示", "确认退出游戏吗？", "确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) context).finish();
            }
        }, "取消", null);
    }

    public static void showResultDialog(Context context, String content) {
        showSingleBtnDialog(context, "游戏结果", content, "知道了", null);
    }

    public static void showTipDialog(Context context, String title, String content) {
        showSingleBtnDialog(context, title, content, "知道了", null);
    }

    public static void dismissTipAlertDialog() {
        if (null != tipAlertDialog && tipAlertDialog.isShowing()) {
            tipAlertDialog.dismiss();
        }
    }

    public static void showInviteManDialog(Context context, MDEditDialog.OnClickEditDialogListener clicker) {
        showEditDialog(context, "对方用户名：", "取消", null, "确定", clicker);
    }

    public static void showReviseNickDialog(Context context, MDEditDialog.OnClickEditDialogListener clicker) {
        showEditDialog(context, "新昵称:", "取消", null, "确定", clicker);
    }
}