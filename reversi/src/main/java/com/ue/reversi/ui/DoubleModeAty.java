package com.ue.reversi.ui;

import com.hyphenate.easeui.game.base.BaseDoubleModeAty;
import com.ue.reversi.R;
import com.ue.reversi.bean.Statistic;
import com.ue.reversi.util.Rule;
import com.ue.reversi.widget.ReversiView;

public class DoubleModeAty extends BaseDoubleModeAty {

    public void updatePanel(){
        game_user_panel.updateFocus(game_chessboard.isMyTurn());
        Statistic statistic = Rule.analyse(((ReversiView)game_chessboard).getChessBoard(), ((ReversiView)game_chessboard).getMyColor());
        game_user_panel.setMyExtraTxt(" × " + statistic.PLAYER);
        game_user_panel.setOppoExtraTxt(" × " + statistic.AI);
    }

    @Override
    public void initAtyRes() {
        setAtyRes(R.layout.rv_aty_double_m,R.drawable.game_black1,R.drawable.game_white1);
    }

    public void initUserInfo() {
        if(isIPlayFirst){
            game_user_panel.setMyImage(firstPlayImage);
            game_user_panel.setOppoImage(lastPlayImage);
        }else{
            game_user_panel.setMyImage(lastPlayImage);
            game_user_panel.setOppoImage(firstPlayImage);
        }
        game_user_panel.updateFocus(game_chessboard.isMyTurn());
        game_user_panel.setMyExtraTxt(" × " + 2);
        game_user_panel.setOppoExtraTxt(" × " + 2);
    }
}