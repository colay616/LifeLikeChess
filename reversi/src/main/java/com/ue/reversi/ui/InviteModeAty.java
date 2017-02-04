package com.ue.reversi.ui;

import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.base.BaseInviteModeAty;
import com.ue.reversi.R;
import com.ue.reversi.bean.Statistic;
import com.ue.reversi.util.Rule;
import com.ue.reversi.widget.ReversiView;

//查看邀请列表功能，保存最近的20条邀请记录

/**
 * 逻辑描述：
 * 进入本页面有两种方式：
 * 一、通过主页/单人模式页的邀请消息进入
 * 二、通过主页的邀请模式选项进入
 * <p>
 * 按进入方式分别处理：
 * 一、
 * 1、获取邀请人username，向邀请人回复开始通知，进入半初始化半等待状态，提示等待对方开始
 * 2、收到对方开始通知，完成页面初始化，开始游戏
 * <p>
 * 二、
 * 1、点击邀请按钮输入对方用户名进行邀请，邀请后的10秒内没有回复则提示失效，邀请后的10秒内不接受其它用户的邀请
 * 2、收到对方开始通知而我还没开始，或者我开始但还没收到对方开始通知，进入半初始化半等待状态，等到双方开始，完成页面初始化，开始游戏
 * <p>
 * 邀请模式要比随机模式简单，因为邀请是定向的，随机是不定向的
 */
public class InviteModeAty extends BaseInviteModeAty {

    public void updatePanel() {
        game_user_panel.updateFocus(game_chessboard.isMyTurn());
        Statistic statistic = Rule.analyse(((ReversiView)game_chessboard).getChessBoard(), ((ReversiView)game_chessboard).getMyColor());
        game_user_panel.setMyExtraTxt(" × " + statistic.PLAYER);
        game_user_panel.setOppoExtraTxt(" × " + statistic.AI);
    }

    @Override
    public void initAtyRes() {
        setAtyRes(R.layout.rv_aty_invite_m,R.drawable.game_black1,R.drawable.game_white1, GameConstants.GAME_AP,"黑白棋");
    }
}