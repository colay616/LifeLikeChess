package com.ue.army_chess.ui;

import android.text.TextUtils;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.GameUtil;
import com.hyphenate.easeui.game.base.BaseInviteModeAty;
import com.ue.army_chess.R;
import com.ue.army_chess.util.ArmyChessUtil;
import com.ue.army_chess.widget.FArmyView;
import com.ue.common.util.LogUtil;
import com.ue.common.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FInviteModeAty extends BaseInviteModeAty {
    @Override
    public void initAtyRes() {
        setAtyRes(R.layout.a_aty_invite_f,R.drawable.svg_blue_first,R.drawable.svg_yellow_last,GameConstants.GAME_ACF,"军棋-翻棋");
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
                for (int i = 0,len=chessListJson.length(); i<len; i++) {
                    chessList.add(chessListJson.optInt(i, -1));
                }
                ((FArmyView)game_chessboard).setChessList(chessList, false);
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
            LogUtil.i(TAG,"playerClickStartBtn,gen chess list="+chessList+",size="+chessList.size());
            Map<String, Object> params = new HashMap<>();
            params.put(GameConstants.CHESS_LIST, chessList.toString());
            GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_START, params);
            ((FArmyView)game_chessboard).setChessList(chessList, true);
        } else {
            GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_START, null);
        }
    }
}
