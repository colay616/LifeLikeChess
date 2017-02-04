package com.ue.reversi.ui;

import android.widget.Button;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.GameUtil;
import com.hyphenate.easeui.game.base.BaseOnlineModeAty;
import com.ue.common.widget.dialog.DialogOnItemClickListener;
import com.ue.common.xsharedpref.XSharedPref;
import com.ue.reversi.R;
import com.ue.reversi.bean.Statistic;
import com.ue.reversi.util.Rule;
import com.ue.reversi.widget.ReversiView;

/**
 * 逻辑描述：
 * 1、进入本页面先获取之前保存的等级数据，根据等级参数选择加入对应的聊天室
 * 2、加入聊天室成功则发送一条邀请消息，第一个回复的用户通过邀请，进入inAdoptStatus状态，
 * 期间不接受邀请消息和接受邀请消息，等待对方的Ready通知，最后双方都点击开始才能正式开始
 */
public class OnlineModeAty extends BaseOnlineModeAty {

    public void updatePanel() {
        game_user_panel.updateFocus(game_chessboard.isMyTurn());
        Statistic statistic = Rule.analyse(((ReversiView) game_chessboard).getChessBoard(), ((ReversiView) game_chessboard).getMyColor());
        game_user_panel.setMyExtraTxt(" × " + statistic.PLAYER);
        game_user_panel.setOppoExtraTxt(" × " + statistic.AI);
    }

    @Override
    public void selectRoomLevel() {
        final int curRoomLevel = XSharedPref.getInt(GameConstants.ROOM_LEVEL_AP, GameConstants.ROOM_LEVEL_PRIMARY);
        showSelectRoomDialog(curRoomLevel, new DialogOnItemClickListener() {
            @Override
            public void onItemClick(Button button, int position) {
                int newLevel = position == 0 ? GameConstants.ROOM_LEVEL_PRIMARY : GameConstants.ROOM_LEVEL_HIGH;
                if (curRoomLevel == newLevel) {
                    return;
                }
                //保存当前选择的级别
                XSharedPref.putInt(GameConstants.ROOM_LEVEL_AP, newLevel);
                //退出当前房间
                EMClient.getInstance().chatroomManager().leaveChatRoom(roomId);

                if (isInAdoptStatus) {//当前准备开始游戏
                    GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_REFUSE, null);
                    isInAdoptStatus = false;
                    opponentUserName = null;
                } else if (game_chessboard.isGameStarted()) {//当前正在游戏
                    GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_LEAVE, null);
                    game_chessboard.escape();
                    opponentUserName = null;
                }
                roomId = newLevel == GameConstants.ROOM_LEVEL_PRIMARY ? GameConstants.ROOM_AP_1 : GameConstants.ROOM_AP_2;
                //加入新房间
                joinGameRoom(roomId);
            }
        });
    }

    @Override
    public void initAtyRes() {
        setAtyRes(R.layout.rv_aty_online_m, R.drawable.game_black1, R.drawable.game_white1, GameConstants.ROOM_LEVEL_AP, GameConstants.ROOM_AP_1, GameConstants.ROOM_AP_2);
    }
}