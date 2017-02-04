package com.ue.army_chess.ui;

import android.content.Intent;
import android.view.View;

import com.hyphenate.easeui.game.base.BaseDoubleModeAty;
import com.hyphenate.easeui.game.db.LayoutItem;
import com.ue.army_chess.R;
import com.ue.army_chess.util.ArmyChessUtil;
import com.ue.army_chess.widget.DLayoutPopupWin;
import com.ue.army_chess.widget.MArmyView;
import com.ue.common.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MDoubleModeAty extends BaseDoubleModeAty {
    private List<Integer>userOneList;
    private List<Integer>userTwoList;
    private DLayoutPopupWin mDLayoutPopupWin;

    @Override
    public void initAtyRes() {
        setAtyRes(R.layout.a_aty_double_m, R.drawable.svg_blue_first, R.drawable.svg_yellow_last);

        findViewById(R.id.game_set_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game_chessboard.escape();//更换布局结束游戏
                if(null==mDLayoutPopupWin){
                    initLayoutPopupWin();
                }
                mDLayoutPopupWin.show(game_chessboard);
            }
        });
    }

    private void initLayoutPopupWin(){
        mDLayoutPopupWin=new DLayoutPopupWin(MDoubleModeAty.this);
        mDLayoutPopupWin.setLayoutItemClicker(new DLayoutPopupWin.OnLayoutItemClick() {
            @Override
            public void onNameClick(int tag, LayoutItem layoutItem) {
                if (null == layoutItem) {
                    if(tag==0){
                        userOneList=ArmyChessUtil.genHalfChessList();
                    }else{
                        userTwoList=ArmyChessUtil.genHalfChessList();
                    }
                    return;
                }
                String dataStr = layoutItem.getData();
                try {
                    JSONArray dataJson = new JSONArray(dataStr);
                    List<Integer>chessList = new ArrayList<Integer>(dataJson.length());
                    for (int i = 0, len = dataJson.length(); i < len; i++) {
                        chessList.add(dataJson.optInt(i, -1));
                    }
                    if(tag==0){
                        userOneList=chessList;
                    }else{
                        userTwoList=chessList;
                    }
                } catch (JSONException e) {
                    LogUtil.i(TAG, "initAtyRes,json parse error:" + e.getMessage());
                }
                findViewById(R.id.game_new).performClick();
            }
            @Override
            public void onEditClick(int tag, LayoutItem layoutItem) {
                Intent intent = new Intent(MDoubleModeAty.this, EditLayoutAty.class).putExtra("targetItem", layoutItem);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initUserInfo() {
        super.initUserInfo();
        if(null==userOneList){
            userOneList=ArmyChessUtil.genHalfChessList();
        }
        if(null==userTwoList){
            userTwoList=ArmyChessUtil.genHalfChessList();
        }
        ((MArmyView)game_chessboard).setChessList(userOneList,true);
        ((MArmyView)game_chessboard).setChessList(userTwoList,false);
    }
}
