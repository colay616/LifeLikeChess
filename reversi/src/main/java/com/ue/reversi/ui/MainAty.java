package com.ue.reversi.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.ui.LoginActivity;
import com.ue.reversi.R;


public class MainAty extends Activity {
    private boolean isInInvitationMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rv_aty_main);
    }

    public void clickBtn(View view) {
        int viewId = view.getId();
        if (viewId == R.id.reversi_double) {
            startActivity(new Intent(MainAty.this, DoubleModeAty.class));
        }else if (viewId == R.id.reversi_single) {
            startActivity(new Intent(MainAty.this, SingleModeAty.class));
        }else if (viewId == R.id.reversi_online) {
            if (!EMClient.getInstance().isLoggedInBefore()) {
                startActivity(new Intent(MainAty.this, LoginActivity.class));
                return;
            }
            startActivity(new Intent(MainAty.this, OnlineModeAty.class));
        }else if (viewId == R.id.reversi_invite) {
            if (!EMClient.getInstance().isLoggedInBefore()) {
                startActivity(new Intent(MainAty.this, LoginActivity.class));
                return;
            }
            startActivity(new Intent(MainAty.this, InviteModeAty.class));
        }
    }

//    private EMMessageListener mEMMessageListener = new EMMessageListener() {
//        @Override
//        public void onMessageReceived(List<EMMessage> list) {
//
//        }
//
//        @Override
//        public void onMessageReadAckReceived(List<EMMessage> list) {
//
//        }
//
//        @Override
//        public void onMessageDeliveryAckReceived(List<EMMessage> list) {
//
//        }
//
//        @Override
//        public void onMessageChanged(EMMessage emMessage, Object o) {
//
//        }
//
//        @Override
//        public void onCmdMessageReceived(List<EMMessage> list) {
//            for (EMMessage emMessage : list) {
//                if (!isInInvitationMode) {
//                    onCmdMsgReceived(emMessage);
//                }
//            }
//        }
//    };

//    private void onCmdMsgReceived(final EMMessage emMessage) {
//        //10秒以前的消息不做处理
//        if (System.currentTimeMillis() - emMessage.getMsgTime() >= 10000) {
//            return;
//        }
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                EMCmdMessageBody cmdMessageBody = (EMCmdMessageBody) emMessage.getBody();
//                String action = cmdMessageBody.action();
//                if (action.equals(GameConstants.ACTION_INVITE)) {//收到邀请
//                    String gameName = "";
//                    if (emMessage.getIntAttribute(GameConstants.WHICH_GAME, 2) == 2) {
//                        gameName = "黑白棋";
//                    }
//                    String timeStr = DateUtils.getFormatTime(emMessage.getMsgTime(), DateUtils.FORMAT_SHORT_DATETIME);
//                    String msg = String.format("[%s] %s 邀你一起来玩 %s", timeStr, emMessage.getFrom(), gameName);
//                    GameDialogs.showGotInvitationDialog(MainAty.this, msg, new DialogOnClickListener() {
//                        @Override
//                        public void clickLeftButton(View view) {
//                            //拒绝
//                            GameDialogs.dismissGotInvitationDialog(false);
//                        }
//
//                        @Override
//                        public void clickRightButton(View view) {
//                            //TODO 超8秒失效
//                            //接受
//                            isInInvitationMode = true;
//
//                            sendCMDMessage(GameConstants.ACTION_ACCEPT, emMessage.getFrom());
//
//                            startActivity(new Intent(MainAty.this, InviteModeAty.class).putExtra(GameConstants.OPPO_USER_NAME, emMessage.getFrom()).putExtra(GameConstants.IS_INVITED_BY_ME, false));
//                        }
//                    });
//                }
//            }
//        });
//    }

    private void sendCMDMessage(String action, String to) {
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
        cmdMsg.addBody(cmdBody);
        cmdMsg.setReceipt(to);
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isInInvitationMode = false;
//        DemoHelper.getInstance().pushActivity(this);
//        EMClient.getInstance().chatManager().addMessageListener(mEMMessageListener);
    }

    @Override
    protected void onStop() {
//        EMClient.getInstance().chatManager().removeMessageListener(mEMMessageListener);
//        DemoHelper.getInstance().popActivity(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        GameDialogs.dismissAllGameDialogs(true);
        super.onDestroy();
    }
}
