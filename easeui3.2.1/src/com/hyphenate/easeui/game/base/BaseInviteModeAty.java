package com.hyphenate.easeui.game.base;

import android.os.Bundle;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

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
import com.ue.common.util.DateUtils;
import com.ue.common.util.ToastUtil;
import com.ue.common.widget.dialog.MDEditDialog;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hawk on 2016/12/11.
 */

public abstract class BaseInviteModeAty extends BaseGameMsgAty {
    public UserPanelView game_user_panel;
    protected BaseGameView game_chessboard;
    public Button game_start_game;
    public Button game_invite;
    private SendMsgFragment mSendGameMsgFragment;

    public boolean isInvitedByMe;
    public boolean isOppoStarted = false;
    public boolean isIStarted = false;
    public int myImage;
    public int oppoImage;
    public long lastInviteTime;//10秒内只能发一次邀请
    private int inviteTimeCount = 10;//邀请发出后10秒内没有回应则提醒邀请失效
    private Timer countDownTimer;

    public int whichGame;
    public String gameName;
    public int firstPlayImage;
    public int lastPlayImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initAtyRes();
        game_chessboard.setGameMode(GameConstants.MODE_ONLINE);
        game_chessboard.setPlayListener(new OnPlayListener() {
            @Override
            public void onIPlayed(int[] data) {
                EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
                EMCmdMessageBody cmdBody = new EMCmdMessageBody(GameConstants.ACTION_DATA);
                cmdMsg.addBody(cmdBody);
                cmdMsg.setReceipt(opponentUserName);
                for (int i = 0, len = data.length; i < len; i++) {
                    cmdMsg.setAttribute("data" + i, data[i]);
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
        game_invite = (Button) findViewById(R.id.game_invite);
        game_start_game = (Button) findViewById(R.id.game_start_game);
        game_user_panel = (UserPanelView) findViewById(R.id.game_user_panel);
        game_user_panel.setMyName(EMClient.getInstance().getCurrentUser());

        setListeners();

        Bundle arguments = getIntent().getExtras();
        if (null != arguments) {
            opponentUserName = arguments.getString(GameConstants.OPPO_USER_NAME, null);
        }

        if (!TextUtils.isEmpty(opponentUserName)) {
            GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_READY, null);
        }
    }

    public boolean checkCanStart(){
        if (TextUtils.isEmpty(opponentUserName)) {
            ToastUtil.toast("*^_^* ：你要先约人或者被约才能开始");
            return false;
        }
        return true;
    }

    public void prepareStart(){
        GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_START, null);
    }

    public void updatePanel() {
        game_user_panel.updateFocus(game_chessboard.isMyTurn());
    }

    private void setListeners() {
        findViewById(R.id.game_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameDialogs.showOnlineExitDialog(BaseInviteModeAty.this, new View.OnClickListener() {
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
        findViewById(R.id.game_chat).setOnClickListener(new View.OnClickListener() {
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
        findViewById(R.id.game_surrender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (game_chessboard.isGameStarted()) {
                    game_chessboard.surrender(true);
                    GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_SURRENDER, null);
                    gameOver(OnPlayListener.FLAG_OPPO_WIN);
                }
            }
        });
        findViewById(R.id.game_draw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (game_chessboard.isGameStarted()) {
                    GameDialogs.showTipDialog(BaseInviteModeAty.this, "正在发送和棋请求,请稍等...");
                    GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_DRAW_REQ, null);
                }
            }
        });
        findViewById(R.id.game_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (game_chessboard.isGameStarted() && game_chessboard.isAllowedToUndo()) {
                    GameDialogs.showTipDialog(BaseInviteModeAty.this, "正在发送悔棋请求,请稍等...");
                    GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_UNDO_REQ, null);
                }
            }
        });
        game_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (game_chessboard.isGameStarted()) {
                    return;
                }
                if (System.currentTimeMillis() - lastInviteTime < 10000) {
                    ToastUtil.toast("10秒内只能邀请一次,请稍等再试");
                    return;
                }
                GameDialogs.showInviteManDialog(BaseInviteModeAty.this, new MDEditDialog.OnClickEditDialogListener() {
                    @Override
                    public void onClick(View view, String editText) {
                        if (TextUtils.isEmpty(editText.trim())) {
                            ToastUtil.toast("用户名不能为空哦");
                            return;
                        }
                        game_start_game.setVisibility(View.VISIBLE);
                        GameDialogs.showTipDialog(BaseInviteModeAty.this, "正在发送邀请,请稍等...");
                        opponentUserName = editText.trim();
                        Map<String, Object> attrs = new HashMap<>();
                        attrs.put(GameConstants.WHICH_GAME, whichGame);
                        GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_INVITE, attrs);
                        lastInviteTime = System.currentTimeMillis();
                        isInvitedByMe = true;

                        startInviteCountDown();
                    }
                });
            }
        });
        game_start_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkCanStart()){
                    playerClickStartBtn(true);
                }
            }
        });
    }

    private void gameOver(int resultFlag) {
        String msg = "";
        if (resultFlag == OnPlayListener.FLAG_I_WIN) {
            msg = "我赢了";
        } else if (resultFlag == OnPlayListener.FLAG_DRAW) {
            msg = "平局";
        } else if (resultFlag == OnPlayListener.FLAG_OPPO_WIN) {
            msg = "我输了";
        }
        GameDialogs.showResultDialog(BaseInviteModeAty.this, msg);

        game_start_game.setVisibility(View.VISIBLE);

        isOppoStarted = false;
        isIStarted = false;
    }


    public void startInviteCountDown() {
        inviteTimeCount = 10;
        countDownTimer = new Timer();
        countDownTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                inviteTimeCount--;
                if (inviteTimeCount <= 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            opponentUserName = null;
                            GameDialogs.showTipDialog(BaseInviteModeAty.this, "提示", "邀请超时,已失效.可以尝试重新邀请.");
                        }
                    });
                    cancel();
                }
            }
        }, 0, 1000);
    }

    public void cancelInviteCountDown() {
        if (null != countDownTimer) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    public void startGame() {
        game_chessboard.startGame(isInvitedByMe);
        updatePanel();
        isInvitedByMe = !isInvitedByMe;
    }

    //我收到邀请
    private void showInvitationDialog(final EMMessage replyMessage) {
        String timeStr = DateUtils.getFormatTime(replyMessage.getMsgTime(), DateUtils.FORMAT_SHORT_DATETIME);
        String msg = String.format("[%s] %s 邀你一起来玩 %s", timeStr, replyMessage.getFrom(), gameName);
        GameDialogs.showGotInvitationDialog(BaseInviteModeAty.this, msg, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //接受
                if (System.currentTimeMillis() - replyMessage.getMsgTime() > 8000) {
                    game_start_game.setVisibility(View.VISIBLE);
                    opponentUserName = null;
                    ToastUtil.toast("邀请已失效");
                    return;
                }
                isInvitedByMe = false;
                opponentUserName = replyMessage.getFrom();

                GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_READY, null);
            }
        });
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

    //*************************消息处理start**********
    @Override
    public void onChatReceived(final EMMessage message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Spannable spanTxt = EaseSmileUtils.getSmiledText(BaseInviteModeAty.this, ((EMTextMessageBody) message.getBody()).getMessage());
                ToastUtil.showChatToast(BaseInviteModeAty.this, spanTxt, false, game_user_panel.getHeight());
            }
        });
    }

    @Override
    public void onInviteReceived(EMMessage message) {
        if (message.getIntAttribute(GameConstants.WHICH_GAME, -1) != whichGame) {
            return;
        }
        if (message.getMsgTime() - lastInviteTime < 10000) {
            return;
        }
        showInvitationDialog(message);
    }

    @Override
    public void onAcceptReceived(EMMessage message) {

    }

    @Override
    public void onAdoptReceived(EMMessage message) {

    }

    @Override
    public void onRefuseReceived(EMMessage message) {
        cancelInviteCountDown();
        GameDialogs.showTipDialog(BaseInviteModeAty.this, "提示", "对方拒绝了邀请");
        opponentUserName = null;
    }

    @Override
    public void onReadyReceived(EMMessage message) {
        cancelInviteCountDown();
        GameDialogs.dismissTipAlertDialog();
        game_user_panel.setOppoName(opponentUserName);
        ToastUtil.toast("对方接受了邀请");
    }

    @Override
    public void onStartReceived(EMMessage message) {
        cancelInviteCountDown();
        GameDialogs.dismissTipAlertDialog();
        game_user_panel.setOppoName(opponentUserName);
        //
        if(chckOppoStartOk(message)){
            playerClickStartBtn(false);
        }
    }

    public boolean chckOppoStartOk(EMMessage message){return true;}

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
        game_chessboard.playChess(data);
    }

    @Override
    public void onDrawReqReceived(EMMessage message) {
        if (!game_chessboard.isGameStarted()) {
            return;
        }
        GameDialogs.showDrawDialog(BaseInviteModeAty.this,
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
                        game_start_game.setVisibility(View.VISIBLE);
                        Map<String, Object> attrs = new HashMap<>();
                        attrs.put(GameConstants.ATTR_CODE, GameConstants.CODE_ACCEPT);
                        GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_DRAW_REP, attrs);
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
            GameDialogs.showUndoDialog(BaseInviteModeAty.this,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //不同意悔棋
                            Map<String, Object> undoAttrs = new HashMap<>();
                            undoAttrs.put(GameConstants.ATTR_CODE, GameConstants.CODE_REFUSE);
                            GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_UNDO_REP, undoAttrs);
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //同意悔棋
                            Map<String, Object> undoAttrs = new HashMap<>();
                            undoAttrs.put(GameConstants.ATTR_CODE, GameConstants.CODE_ACCEPT);
                            GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_UNDO_REP, undoAttrs);
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
        game_chessboard.escape();
        opponentUserName = null;
        game_user_panel.setOppoName("...");
        GameDialogs.dismissTipAlertDialog();//可能我请求悔棋的时候对方离开了
        GameDialogs.showOppoExitDialog(BaseInviteModeAty.this, null);
    }
    //*************************消息处理end**********

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            GameDialogs.showOnlineExitDialog(BaseInviteModeAty.this, new View.OnClickListener() {
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
            ToastUtil.toast("登录后才能进入邀请模式");
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
        GameDbManager.getInstance().closeDB();
        super.onDestroy();
    }

    public void setAtyRes(int layoutRes,int firstImgRes,int lastImgRes,int gameFlag,String gameName){
        setContentView(layoutRes);
        firstPlayImage=firstImgRes;
        lastPlayImage=lastImgRes;
        whichGame=gameFlag;
        this.gameName=gameName;
        game_chessboard= (BaseGameView) findViewById(R.id.game_chessboard);
    }

    public abstract void initAtyRes();
}