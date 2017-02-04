package com.ue.chess.ui;

import android.widget.Button;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.GameUtil;
import com.hyphenate.easeui.game.base.BaseOnlineModeAty;
import com.ue.chess.R;
import com.ue.common.widget.dialog.DialogOnItemClickListener;
import com.ue.common.xsharedpref.XSharedPref;

public class OnlineModeAty extends BaseOnlineModeAty {
    @Override
    public void selectRoomLevel() {
        final int curRoomLevel = XSharedPref.getInt(GameConstants.ROOM_LEVEL_IC, GameConstants.ROOM_LEVEL_PRIMARY);
        showSelectRoomDialog(curRoomLevel, new DialogOnItemClickListener() {
            @Override
            public void onItemClick(Button button, int position) {
                int newLevel = position == 0 ? GameConstants.ROOM_LEVEL_PRIMARY : GameConstants.ROOM_LEVEL_HIGH;
                if (curRoomLevel == newLevel) {
                    return;
                }
                //保存当前选择的级别
                XSharedPref.putInt(GameConstants.ROOM_LEVEL_IC, newLevel);
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
                //加入新房间
                roomId = newLevel == GameConstants.ROOM_LEVEL_PRIMARY ? GameConstants.ROOM_IC_1 : GameConstants.ROOM_IC_2;
                joinGameRoom(roomId);
            }
        });
    }

    @Override
    public void initAtyRes() {
        setAtyRes(R.layout.ic_aty_online_m, R.drawable.chess_wking, R.drawable.chess_bking, GameConstants.ROOM_LEVEL_IC, GameConstants.ROOM_IC_1, GameConstants.ROOM_IC_2);
    }
}
