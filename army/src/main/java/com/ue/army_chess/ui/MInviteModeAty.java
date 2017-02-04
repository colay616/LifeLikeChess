package com.ue.army_chess.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.GameUtil;
import com.hyphenate.easeui.game.base.BaseInviteModeAty;
import com.hyphenate.easeui.game.db.LayoutItem;
import com.ue.army_chess.R;
import com.ue.army_chess.util.ArmyChessUtil;
import com.ue.army_chess.widget.LayoutPopupWin;
import com.ue.army_chess.widget.MArmyView;
import com.ue.common.util.LogUtil;
import com.ue.common.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MInviteModeAty extends BaseInviteModeAty {
    private LayoutPopupWin mLayoutPopupWin;
    private List<Integer> myChessList;

    @Override
    public void initAtyRes() {
        setAtyRes(R.layout.a_aty_invite_m, R.drawable.svg_blue_first, R.drawable.svg_yellow_last,GameConstants.GAME_ACM,"军棋-明棋");

        findViewById(R.id.game_set_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isIStarted) {
                    ToastUtil.toast("你已选择开始游戏,现在不能更换布局");
                    return;
                }
                if (null == mLayoutPopupWin) {
                    initLayoutPopupWin();
                }
                mLayoutPopupWin.show(game_chessboard);
            }
        });
    }

    private void initLayoutPopupWin(){
        mLayoutPopupWin = new LayoutPopupWin(MInviteModeAty.this);
        mLayoutPopupWin.setLayoutItemClicker(new LayoutPopupWin.OnLayoutItemClick() {
            @Override
            public void onNameClick(LayoutItem layoutItem) {
                ToastUtil.toast("布局已设置,你可以点击开始咯");
                if (null == layoutItem) {
                    myChessList = ArmyChessUtil.genHalfChessList();
                    return;
                }
                String dataStr = layoutItem.getData();
                try {
                    JSONArray dataJson = new JSONArray(dataStr);
                    myChessList = new ArrayList<Integer>(dataJson.length());
                    for (int i = 0, len = dataJson.length(); i < len; i++) {
                        myChessList.add(dataJson.optInt(i, -1));
                    }
                } catch (JSONException e) {
                    LogUtil.i(TAG, "initAtyRes,json parse error:" + e.getMessage());
                }
            }

            @Override
            public void onEditClick(LayoutItem layoutItem) {
                Intent intent = new Intent(MInviteModeAty.this, EditLayoutAty.class).putExtra("targetItem", layoutItem);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean checkCanStart() {
        if (TextUtils.isEmpty(opponentUserName)) {
            ToastUtil.toast("*^_^* ：你要先约人或者被约才能开始");
            return false;
        }
        if (null == myChessList) {
            ToastUtil.toast("还没设置布局哦,请点击'布局'进行设置吧");
            return false;
        }
        return true;
    }

    @Override
    public boolean chckOppoStartOk(EMMessage message) {
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
            ((MArmyView)game_chessboard).setChessList(chessList, false);
        } catch (JSONException e) {
            LogUtil.i(TAG, "onStartReceived,json parse error=" + e.getMessage());
            ToastUtil.toast("T_T：数据解析出错");
            return false;
        }
        return true;
    }

    @Override
    public void prepareStart() {
        //发送布局
        Map<String, Object> params = new HashMap<>();
        params.put(GameConstants.CHESS_LIST, myChessList.toString());
        GameUtil.sendCMDMessage(opponentUserName, GameConstants.ACTION_START, params);
        ((MArmyView)game_chessboard).setChessList(myChessList, true);
    }
}
