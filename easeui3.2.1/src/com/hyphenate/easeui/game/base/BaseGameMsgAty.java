package com.hyphenate.easeui.game.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.DemoHelper;
import com.hyphenate.easeui.game.GameConstants;
import com.ue.common.util.LogUtil;

import java.util.List;

/**
 * Created by hawk on 2016/12/25.
 */

public abstract class BaseGameMsgAty extends AppCompatActivity {
    public static final String TAG = BaseGameMsgAty.class.getSimpleName();
    private long enterTime;
    public String opponentUserName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enterTime = System.currentTimeMillis();
    }

    public void enabledGameMsgListener(boolean isEnabled) {
        if (isEnabled) {
            DemoHelper.getInstance().pushActivity(this);
            EMClient.getInstance().chatManager().addMessageListener(messageListener);
        } else {
            EMClient.getInstance().chatManager().removeMessageListener(messageListener);
            DemoHelper.getInstance().popActivity(this);
        }
    }

    public abstract void onChatReceived(EMMessage message);

    public abstract void onInviteReceived(EMMessage message);//random invitation,spec invitation

    public abstract void onAcceptReceived(EMMessage message);//random accept,spec accept

    public abstract void onAdoptReceived(EMMessage message);

    public abstract void onRefuseReceived(EMMessage message);//refuse adopt

    public abstract void onReadyReceived(EMMessage message);

    public abstract void onStartReceived(EMMessage message);

    public abstract void onSurrenderReceived(EMMessage message);

    public abstract void onDataReceived(int[] data);

    public abstract void onDrawReqReceived(EMMessage message);//request draw

    public abstract void onDrawRepReceived(EMMessage message);//response draw

    public abstract void onUndoReqReceived(EMMessage message);//undo request

    public abstract void onUndoRepReceived(EMMessage message);//undo response

    public abstract void onLeaveReceived(EMMessage message);
    //发送adopt提示对方进来了，收到adopt提示准备开始吧，收到leave提示对方离开了

    private EMMessageListener messageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            for (EMMessage message : messages) {
                if (TextUtils.isEmpty(opponentUserName) || !message.getFrom().equals(opponentUserName)) {
                    continue;
                }
                if (message.getChatType() != EMMessage.ChatType.Chat || TextUtils.isEmpty(message.getStringAttribute(GameConstants.ATTR_GAME_CHAT, null))) {
                    continue;
                }
                onChatReceived(message);
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            for (final EMMessage message : messages) {
                //自己发送的消息
                if (message.getFrom().equals(EMClient.getInstance().getCurrentUser())) {
                    continue;
                }
                //消息的时间是进入页面之前的
                if (message.getMsgTime() < enterTime) {
                    continue;
                }
                //消息不是来自当前的游戏对方
                if (!TextUtils.isEmpty(opponentUserName) && !message.getFrom().equals(opponentUserName)) {
                    continue;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        EMCmdMessageBody cmdMessageBody = (EMCmdMessageBody) message.getBody();
                        String action = cmdMessageBody.action();
                        int actionFlag = 0;
                        try {
                            actionFlag = Integer.parseInt(action);
                        } catch (Exception exp) {
                            actionFlag = -10;
                        }
                        switch (actionFlag) {
                            case 10:
                                onDrawReqReceived(message);
                                break;//ACTION_DRAW_REQ
                            case 11:
                                onUndoReqReceived(message);
                                break;//ACTION_UNDO_REQ
                            case 12:
                                onDrawRepReceived(message);
                                break;//ACTION_DRAW_REP
                            case 13:
                                onUndoRepReceived(message);
                                break;//ACTION_UNDO_REP
                            case 14:
                                onLeaveReceived(message);
                                break;//ACTION_LEAVE
                            case 16://ACTION_DATA
                                //五子棋:2  中国象棋:4  国际象棋:4/5(升兵角色) 黑白棋:2
                                int dataLen = 2;
                                dataLen = message.getIntAttribute("data3", -1) == -1 ? dataLen : 4;
                                dataLen = message.getIntAttribute("data4", -1) == -1 ? dataLen : 5;
                                int[] data = new int[dataLen];
                                for (int i = 0; i < dataLen; i++) {
                                    data[i] = message.getIntAttribute(("data" + i), -1);
                                }
                                onDataReceived(data);
                                break;
                            case 17://ACTION_R_INVITE
                            case 19://ACTION_INVITE
                                onInviteReceived(message);
                                break;//ACTION_INVITE
                            case 20:
                                onAcceptReceived(message);
                                break;//ACTION_ACCEPT
                            case 21:
                                onRefuseReceived(message);
                                break;//ACTION_REFUSE
                            case 22:
                                onAdoptReceived(message);
                                break;//ACTION_ADOPT
                            case 23:
                                onReadyReceived(message);
                                break;//ACTION_READY
                            case 24:
                                onStartReceived(message);
                                break;//ACTION_START
                            case 25:
                                onSurrenderReceived(message);
                                break;//ACTION_SURRENDER
                            default:
                                LogUtil.i(BaseGameMsgAty.class.getSimpleName(), "unknown flag");
                        }
                    }
                });
            }
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {

        }
    };
}
