package com.hyphenate.easeui.game.base;

import android.os.Bundle;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.GameDialogs;
import com.hyphenate.easeui.game.GameUtil;
import com.hyphenate.easeui.game.SendMsgFragment;
import com.hyphenate.easeui.game.UserPanelView;
import com.hyphenate.easeui.game.db.GameDbManager;
import com.hyphenate.easeui.game.iterface.OnPlayListener;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.ue.common.util.ToastUtil;
import com.ue.common.widget.dialog.DialogOnItemClickListener;
import com.ue.common.widget.dialog.NormalSelectionDialog;
import com.ue.common.xsharedpref.XSharedPref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseOnlineModeAty extends BaseGameMsgAty {
    public UserPanelView game_user_panel;
    protected BaseGameView game_chessboard;
    public View game_start_game;
    public View game_exit;
    public View game_level;
    public View game_chat;
    public View game_change;
    public View game_surrender;
    public View game_draw;
    public View game_undo;

    public String roomId;
    public boolean isInAdoptStatus = false;//是否发送/接收了通过消息,是的话不再发送/接收通过消息
    public boolean isInvitedByMe;
    public int myImage;
    public int oppoImage;
    public int firstPlayImage;
    public int lastPlayImage;
    public boolean isOppoStarted = false;
    public boolean isIStarted = false;
    ////在发送邀请后的短时间内，不接受用户的邀请
    private long mySentInvitationTime;
    ////
    private SendMsgFragment mSendGameMsgFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initAtyRes();

        game_user_panel = (UserPanelView) findViewById(R.id.game_user_panel);
        game_exit = findViewById(R.id.game_exit);
        game_level = findViewById(R.id.game_level);
        game_chat = findViewById(R.id.game_chat);
        game_change = findViewById(R.id.game_change);
        game_surrender = findViewById(R.id.game_surrender);
        game_draw = findViewById(R.id.game_draw);
        game_undo = findViewById(R.id.game_undo);

        game_user_panel.setMyName(EMClient.getInstance().getCurrentUser());
        game_start_game = findViewById(R.id.game_start_game);
        game_chessboard.setGameMode(GameConstants.MODE_ONLINE);
        game_chessboard.setPlayListener(new OnPlayListener() {
            @Override
            public void onIPlayed(int[] data) {
                EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
                EMCmdMessageBody cmdBody = new EMCmdMessageBody(GameConstants.ACTION_DATA);
                cmdMsg.addBody(cmdBody);
                cmdMsg.setReceipt(opponentUserName);
                for (int i = 0, len = data.length; i < len; i++) {
                    cmdMsg.setAttribute(("data" + i), data[i]);
                }
                EMClient.getInstance().chatManager().sendMessage(cmdMsg);
                updatePanel();
            }

            @Override
            public void onOppoPlayed(int[] data) {
                updatePanel();
            }

            @Override
            public void onGameOver(int resultFlag) {
                gameOver(resultFlag);
            }
        });

        setListeners();

        joinGameRoom(roomId);
    }

    public void updatePanel() {
        game_user_panel.updateFocus(game_chessboard.isMyTurn());
    }

    private void setListeners() {
        game_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameDialogs.showOnlineExitDialog(BaseOnlineModeAty.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //确定退出
                        if (!TextUtils.isEmpty(opponentUserName)) {
                            GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_LEAVE, null);
                        }
                        finish();
                    }
                });
            }
        });

        game_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRoomLevel();
            }
        });
        game_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //在游戏中才能聊天
                if (!TextUtils.isEmpty(opponentUserName)) {
                    if (null == mSendGameMsgFragment) {
                        mSendGameMsgFragment = new SendMsgFragment();
                    }
                    mSendGameMsgFragment.setOpponentUserName(opponentUserName);
                    mSendGameMsgFragment.setDeviation(game_user_panel.getHeight());
                    mSendGameMsgFragment.show(getSupportFragmentManager(), null);
                }
            }
        });
        game_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (game_chessboard.isGameStarted()) {
                    //提醒，当前正在游戏中，确定换桌吗
                    GameDialogs.showChangeDeskDialog(BaseOnlineModeAty.this, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //换桌
                            changeDesk();
                        }
                    });
                    return;
                }
                changeDesk();
            }
        });
        game_surrender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (game_chessboard.isGameStarted()) {
                    game_chessboard.surrender(true);
                    GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_SURRENDER, null);
                    gameOver(OnPlayListener.FLAG_OPPO_WIN);
                }
            }
        });
        game_draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (game_chessboard.isGameStarted()) {
                    GameDialogs.showTipDialog(BaseOnlineModeAty.this, "正在发送和棋请求,请稍等...");
                    GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_DRAW_REQ, null);
                }
            }
        });
        game_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (game_chessboard.isGameStarted() && game_chessboard.isAllowedToUndo()) {
                    GameDialogs.showTipDialog(BaseOnlineModeAty.this, "正在发送悔棋请求,请稍等...");
                    GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_UNDO_REQ, null);
                }
            }
        });
        game_start_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果还没找到伙伴就点击开始，继续弹出寻找对手对话框
                if (checkCanStart()) {
                    playerClickStartBtn(true);
                }
            }
        });
    }

    public boolean checkCanStart() {
        if (TextUtils.isEmpty(opponentUserName)) {
            ToastUtil.toast("*^_^* ：正在寻找另一半，请稍等...");
            return false;
        }
        return true;
    }

    public void showSelectRoomDialog(int curRoomFlag, DialogOnItemClickListener itemClicker) {
        ArrayList<String> rooms = new ArrayList<>(2);
        rooms.add("小仙区");
        rooms.add("大神区");
        NormalSelectionDialog selectionsDialog = new NormalSelectionDialog.Builder(BaseOnlineModeAty.this)
                .setCanceledOnTouchOutside(true)  //设置是否可点击其他地方取消dialog
                .setCancleButtonText("取消")  //设置最底部“取消”按钮文本
                .setOnItemListener(itemClicker)
                .build();
        selectionsDialog.setDataList(rooms);
        selectionsDialog.show();
    }

    public void joinGameRoom(String theRoomId) {
        roomId = theRoomId;
        GameDialogs.showTipDialog(BaseOnlineModeAty.this, "正在传送至房间，请稍等...");
        EMClient.getInstance().chatroomManager().joinChatRoom(roomId, new EMValueCallBack<EMChatRoom>() {
            @Override
            public void onSuccess(EMChatRoom emChatRoom) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GameDialogs.showTipDialog(BaseOnlineModeAty.this, "正在寻找另一半，请稍等...");
                    }
                });
                sendRandomInvitation(roomId);
            }

            @Override
            public void onError(int i, final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GameDialogs.dismissTipAlertDialog();
                        //dialog,传送失败，重试/退出
                        ToastUtil.toast("加入房间失败:" + s);
                    }
                });
            }
        });
    }

    protected void sendRandomInvitation(String roomId) {
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(GameConstants.ACTION_R_INVITE);
        cmdMsg.addBody(cmdBody);
        cmdMsg.setReceipt(roomId);
        cmdMsg.setChatType(EMMessage.ChatType.ChatRoom);
        mySentInvitationTime = System.currentTimeMillis();
//        LogUtil.i(TAG,"begin="+System.currentTimeMillis());
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
//        LogUtil.i(TAG,"end="+System.currentTimeMillis());
    }

    public void gameOver(int resultFlag) {
        String msg = "";
        if (resultFlag == OnPlayListener.FLAG_I_WIN) {
            msg = "我赢了";
        } else if (resultFlag == OnPlayListener.FLAG_DRAW) {
            msg = "平局";
        } else if (resultFlag == OnPlayListener.FLAG_OPPO_WIN) {
            msg = "我输了";
        }
        GameDialogs.showResultDialog(BaseOnlineModeAty.this, msg);

        game_start_game.setVisibility(View.VISIBLE);
    }

    public void startGame() {
        game_chessboard.startGame(isInvitedByMe);
        isOppoStarted = false;
        isIStarted = false;
        updatePanel();
        isInAdoptStatus = false;
        isInvitedByMe = !isInvitedByMe;
    }

    public void prepareStart() {
        GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_START, null);
    }

    private void playerClickStartBtn(boolean isMyStart) {
        if (isMyStart) {
            prepareStart();
            isIStarted = true;
            game_start_game.setVisibility(View.GONE);

            if (isOppoStarted) {
                game_user_panel.setMyImage(myImage);
                startGame();
            } else {
                if (isInvitedByMe) {
                    myImage = firstPlayImage;
                    oppoImage = lastPlayImage;
                } else {
                    myImage = lastPlayImage;
                    oppoImage = firstPlayImage;
                }
                game_user_panel.setMyImage(myImage);
                game_user_panel.setOppoImage(0);
                game_user_panel.setMyExtraTxt(" × 2");
                game_user_panel.setOppoExtraTxt(" × 2");
                game_chessboard.initChessBoard(true);
            }
        } else {
            isOppoStarted = true;
            if (isIStarted) {
                game_user_panel.setOppoImage(oppoImage);
                startGame();
            } else {
                if (isInvitedByMe) {
                    myImage = firstPlayImage;
                    oppoImage = lastPlayImage;
                } else {
                    myImage = lastPlayImage;
                    oppoImage = firstPlayImage;
                }
                game_user_panel.setMyImage(0);
                game_user_panel.setOppoImage(oppoImage);
                game_user_panel.setMyExtraTxt(" × 2");
                game_user_panel.setOppoExtraTxt(" × 2");
                game_chessboard.initChessBoard(false);
            }
        }
    }

    private void changeDesk() {
        if (isInAdoptStatus) {//当前准备开始游戏
            if (!TextUtils.isEmpty(opponentUserName)) {
                GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_REFUSE, null);
            }
            isInAdoptStatus = false;
            opponentUserName = null;
            game_user_panel.setOppoName("...");
        } else if (game_chessboard.isGameStarted()) {//当前正在游戏
            isInAdoptStatus = false;
            if (!TextUtils.isEmpty(opponentUserName)) {
                GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_LEAVE, null);
            }
            game_chessboard.escape();
            opponentUserName = null;
            game_user_panel.setOppoName("...");
        }
        sendRandomInvitation(roomId);
    }

    //*************************msg process start**********

    @Override
    public void onChatReceived(final EMMessage message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Spannable spanTxt = EaseSmileUtils.getSmiledText(BaseOnlineModeAty.this, ((EMTextMessageBody) message.getBody()).getMessage());
                ToastUtil.showChatToast(BaseOnlineModeAty.this, spanTxt, false, game_user_panel.getHeight());
            }
        });
    }

    @Override
    public void onInviteReceived(EMMessage message) {
        if (isInAdoptStatus || game_chessboard.isGameStarted()) {
            return;
        }
        if (System.currentTimeMillis() - mySentInvitationTime <= 2000) {//在发送邀请两秒内，不接受其它邀请
            return;
        }
        GameUtil.sendCMDMessage(message.getFrom(), GameConstants.ACTION_ACCEPT, null);
    }

    @Override
    public void onAcceptReceived(EMMessage message) {
        if (isInAdoptStatus || game_chessboard.isGameStarted()) {
            return;
        }
        isInAdoptStatus = true;//不再对其它用户发送通过消息，只发送一个通过消息
        GameUtil.sendCMDMessage(message.getFrom(), GameConstants.ACTION_ADOPT, null);
    }

    @Override
    public void onAdoptReceived(EMMessage message) {
        if (isInAdoptStatus || game_chessboard.isGameStarted()) {
            GameUtil.sendCMDMessage(message.getFrom(), GameConstants.ACTION_ADOPT, null);
            return;
        }
        isInAdoptStatus = true;
        isInvitedByMe = false;
        opponentUserName = message.getFrom();
        game_user_panel.setOppoName(opponentUserName);
        GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_READY, null);

        //隐藏提示框，显示开始按钮
        GameDialogs.dismissTipAlertDialog();
        game_start_game.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRefuseReceived(EMMessage message) {
        isInAdoptStatus = false;
        sendRandomInvitation(roomId);
    }

    @Override
    public void onReadyReceived(EMMessage message) {
        if (game_chessboard.isGameStarted()) {
            GameUtil.sendCMDMessage(message.getFrom(), GameConstants.ACTION_REFUSE, null);
            return;
        }
        //隐藏提示框，显示开始按钮
        GameDialogs.dismissTipAlertDialog();
        game_start_game.setVisibility(View.VISIBLE);
        isInvitedByMe = true;
        opponentUserName = message.getFrom();
        game_user_panel.setOppoName(opponentUserName);
        ToastUtil.toast(opponentUserName + " 进入了房间");
    }

    @Override
    public void onStartReceived(EMMessage message) {
        if (chckOppoStartOk(message)) {
            playerClickStartBtn(false);
        }
    }

    public boolean chckOppoStartOk(EMMessage message) {
        return true;
    }

    @Override
    public void onSurrenderReceived(EMMessage message) {
        if (!game_chessboard.isGameStarted()) {
            return;
        }
        GameDialogs.dismissTipAlertDialog();
        game_chessboard.surrender(false);
        game_start_game.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDataReceived(int[] data) {
        String dataStr="";
        for(int i=0,len=data.length;i<len;i++){
            dataStr=dataStr+data[0]+",";
        }
        if (!game_chessboard.isGameStarted()) {
            return;
        }
        //可能我请求悔棋，对方取消了继续下棋
        GameDialogs.dismissTipAlertDialog();

        game_chessboard.playChess(data);
    }

    @Override
    public void onDrawReqReceived(EMMessage message) {
        if (!game_chessboard.isGameStarted()) {
            return;
        }
        GameDialogs.showDrawDialog(BaseOnlineModeAty.this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //不同意和棋
                        Map<String, Object> attrs = new HashMap<>();
                        attrs.put(GameConstants.ATTR_CODE, GameConstants.CODE_REFUSE);
                        GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_DRAW_REP, attrs);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //同意和棋
                        game_chessboard.drawChess(false);
                        Map<String, Object> attrs = new HashMap<>();
                        attrs.put(GameConstants.ATTR_CODE, GameConstants.CODE_ACCEPT);
                        GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_DRAW_REP, attrs);
                        game_start_game.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onDrawRepReceived(EMMessage message) {
        if (!game_chessboard.isGameStarted()) {
            return;
        }
        ////
        GameDialogs.dismissTipAlertDialog();
        ////
        int resultCode = message.getIntAttribute(GameConstants.ATTR_CODE, -1);
        if (resultCode == GameConstants.CODE_ACCEPT) {
            game_chessboard.drawChess(true);
        } else {
            ToastUtil.toast("对方不同意和棋");
        }
    }

    @Override
    public void onUndoReqReceived(EMMessage message) {
        if (!game_chessboard.isGameStarted()) {
            return;
        }
        if (game_chessboard.isAllowedToUndo()) {
            GameDialogs.showUndoDialog(BaseOnlineModeAty.this,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //不同意悔棋
                            Map<String, Object> attrs = new HashMap<>();
                            attrs.put(GameConstants.ATTR_CODE, GameConstants.CODE_REFUSE);
                            GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_UNDO_REP, attrs);
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //同意悔棋
                            Map<String, Object> attrs = new HashMap<>();
                            attrs.put(GameConstants.ATTR_CODE, GameConstants.CODE_ACCEPT);
                            GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_UNDO_REP, attrs);
                            game_chessboard.undoChess(false);
                            updatePanel();
                        }
                    });
        }
    }

    @Override
    public void onUndoRepReceived(EMMessage message) {
        if (!game_chessboard.isGameStarted()) {
            return;
        }
        ////
        GameDialogs.dismissTipAlertDialog();
        ////
        int resultCode = message.getIntAttribute(GameConstants.ATTR_CODE, -1);
        if (resultCode == GameConstants.CODE_ACCEPT) {
            game_chessboard.undoChess(true);
            updatePanel();
        } else {
            ToastUtil.toast("对方不同意悔棋");
        }
    }

    @Override
    public void onLeaveReceived(EMMessage message) {
        game_user_panel.setOppoName("...");
        ToastUtil.toast(opponentUserName + " 离开了房间");
        GameDialogs.dismissTipAlertDialog();//可能我请求悔棋的时候对方离开了
        if (game_chessboard.isGameStarted()) {
            GameDialogs.showOppoExitDialog(BaseOnlineModeAty.this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    game_chessboard.escape();
                    opponentUserName = null;
                    //换桌
                    changeDesk();
                }
            });
//            showCommonDialog(GameDialogs.DIALOG_OPPO_EXIT);
        } else if (isInAdoptStatus) {
            isInAdoptStatus = false;
            changeDesk();
        }
    }
    //*************************msg process end**********

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            GameDialogs.showOnlineExitDialog(BaseOnlineModeAty.this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //确定退出
                    if (!TextUtils.isEmpty(opponentUserName)) {
                        GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_LEAVE, null);
                    }
                    finish();
                }
            });
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!EMClient.getInstance().isLoggedInBefore()) {
            ToastUtil.toast("登录后才能进入所选模式");
            finish();
            return;
        }
        enabledGameMsgListener(true);
    }

    @Override
    protected void onStop() {
        enabledGameMsgListener(false);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        EMClient.getInstance().chatroomManager().leaveChatRoom(roomId);//退出聊天室
        GameDbManager.getInstance().closeDB();
        super.onDestroy();
    }

    public void setAtyRes(int layoutRes, int firstImgRes, int lastImgRes, String roomPref, String roomOne, String roomTwo) {
        setContentView(layoutRes);
        firstPlayImage = firstImgRes;
        lastPlayImage = lastImgRes;
        int roomLevel = XSharedPref.getInt(roomPref, GameConstants.ROOM_LEVEL_PRIMARY);
        roomId = roomLevel == GameConstants.ROOM_LEVEL_PRIMARY ? roomOne : roomTwo;
        game_chessboard= (BaseGameView) findViewById(R.id.game_chessboard);
    }

    public abstract void selectRoomLevel();

    public abstract void initAtyRes();
}
