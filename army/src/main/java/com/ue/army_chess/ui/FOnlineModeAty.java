package com.ue.army_chess.ui;

import android.text.TextUtils;
import android.widget.Button;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.GameUtil;
import com.hyphenate.easeui.game.base.BaseOnlineModeAty;
import com.ue.army_chess.R;
import com.ue.army_chess.util.ArmyChessUtil;
import com.ue.army_chess.widget.FArmyView;
import com.ue.common.util.LogUtil;
import com.ue.common.util.ToastUtil;
import com.ue.common.widget.dialog.DialogOnItemClickListener;
import com.ue.common.xsharedpref.XSharedPref;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FOnlineModeAty extends BaseOnlineModeAty {
    @Override
    public void selectRoomLevel() {
        final int curRoomFlag = XSharedPref.getInt(GameConstants.ROOM_LEVEL_ACF, GameConstants.ROOM_LEVEL_PRIMARY);
        showSelectRoomDialog(curRoomFlag, new DialogOnItemClickListener() {
            @Override
            public void onItemClick(Button button, int position) {
                int newLevel = position == 0 ? GameConstants.ROOM_LEVEL_PRIMARY : GameConstants.ROOM_LEVEL_HIGH;
                if (curRoomFlag == newLevel) {
                    return;
                }
                //保存当前选择的级别
                XSharedPref.putInt(GameConstants.ROOM_LEVEL_ACF, newLevel);
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
                roomId = newLevel == GameConstants.ROOM_LEVEL_PRIMARY ? GameConstants.ROOM_ACF_1 : GameConstants.ROOM_ACF_2;

                joinGameRoom(roomId);
            }
        });
    }

    @Override
    public void initAtyRes() {
        setAtyRes(R.layout.a_aty_online_f, R.drawable.svg_blue_first, R.drawable.svg_yellow_last, GameConstants.ROOM_LEVEL_ACF, GameConstants.ROOM_ACF_1, GameConstants.ROOM_ACF_2);
    }

    @Override
    public boolean chckOppoStartOk(EMMessage message) {
        if (!isInvitedByMe) {
            String chessListStr = message.getStringAttribute(GameConstants.CHESS_LIST, null);
            if (TextUtils.isEmpty(chessListStr)) {
                ToastUtil.toast("出错啦：没有布局数据");
                return false;
            }
            try {
                JSONArray chessListJson = new JSONArray(chessListStr);
                List<Integer> chessList = new ArrayList<>();
                for (int i = 0, len = chessListJson.length(); i < len; i++) {
                    chessList.add(chessListJson.optInt(i, -1));
                }
                LogUtil.i(TAG, "onStartReceived,chessList=" + chessList);
                ((FArmyView) game_chessboard).setChessList(chessList, false);
            } catch (JSONException e) {
                LogUtil.i(TAG, "onStartReceived,json parse error=" + e.getMessage());
                ToastUtil.toast("T_T：数据解析出错");
                return false;
            }
        }
        return true;
    }

    @Override
    public void prepareStart() {
        if (isInvitedByMe) {
            //发送布局
            List<Integer> chessList = ArmyChessUtil.genChessList();
            LogUtil.i(TAG, "playerClickStartBtn,gen chess list=" + chessList + ",size=" + chessList.size());
            Map<String, Object> params = new HashMap<>();
            params.put(GameConstants.CHESS_LIST, chessList.toString());
            GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_START, params);
            ((FArmyView) game_chessboard).setChessList(chessList, true);
        } else {
            GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_START, null);
        }
    }
}
