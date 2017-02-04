package com.ue.army_chess.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.GameDialogs;
import com.hyphenate.easeui.game.GameUtil;
import com.hyphenate.easeui.game.base.BaseOnlineModeAty;
import com.hyphenate.easeui.game.db.LayoutItem;
import com.ue.army_chess.R;
import com.ue.army_chess.util.ArmyChessUtil;
import com.ue.army_chess.widget.AArmyView;
import com.ue.army_chess.widget.LayoutPopupWin;
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

public class AOnlineModeAty extends BaseOnlineModeAty {
    private LayoutPopupWin mLayoutPopupWin;
    private List<Integer> myChessList;

    @Override
    public void selectRoomLevel() {
        final int curRoomFlag = XSharedPref.getInt(GameConstants.ROOM_LEVEL_ACA, GameConstants.ROOM_LEVEL_PRIMARY);
        showSelectRoomDialog(curRoomFlag, new DialogOnItemClickListener() {
            @Override
            public void onItemClick(Button button, int position) {
                int newLevel = position == 0 ? GameConstants.ROOM_LEVEL_PRIMARY : GameConstants.ROOM_LEVEL_HIGH;
                if (curRoomFlag == newLevel) {
                    return;
                }
                //保存当前选择的级别
                XSharedPref.putInt(GameConstants.ROOM_LEVEL_ACA, newLevel);
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
                roomId = newLevel == GameConstants.ROOM_LEVEL_PRIMARY ? GameConstants.ROOM_ACA_1 : GameConstants.ROOM_ACA_2;

                joinGameRoom(roomId);
            }
        });
    }

    @Override
    public void initAtyRes() {
        setAtyRes(R.layout.a_aty_online_a, R.drawable.svg_blue_first, R.drawable.svg_yellow_last, GameConstants.ROOM_LEVEL_ACA, GameConstants.ROOM_ACA_1, GameConstants.ROOM_ACA_2);

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

    @Override
    public boolean checkCanStart() {
        //如果还没找到伙伴就点击开始，继续弹出寻找对手对话框
        if (TextUtils.isEmpty(opponentUserName)) {
            GameDialogs.showTipDialog(AOnlineModeAty.this, "正在寻找另一半，请稍等...");
            return false;
        }
        if (null == myChessList) {
            ToastUtil.toast("还没设置布局哦,请点击'布局'进行设置吧");
            return false;
        }
        return true;
    }

    private void initLayoutPopupWin(){
        mLayoutPopupWin = new LayoutPopupWin(AOnlineModeAty.this);
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
                Intent intent = new Intent(AOnlineModeAty.this, EditLayoutAty.class).putExtra("targetItem", layoutItem);
                startActivity(intent);
            }
        });
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
            for (int i = 0, len = chessListJson.length(); i < len; i++) {
                chessList.add(chessListJson.optInt(i, -1));
            }
            ((AArmyView)game_chessboard).setChessList(chessList, false);
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
        ((AArmyView)game_chessboard).setChessList(myChessList, true);
    }
}
