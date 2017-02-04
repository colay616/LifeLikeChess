package com.ue.chess.ui;

import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.base.BaseInviteModeAty;
import com.ue.chess.R;

public class InviteModeAty extends BaseInviteModeAty {
    @Override
    public void initAtyRes() {
        setAtyRes(R.layout.ic_aty_invite_m,R.drawable.chess_wking,R.drawable.chess_bking,GameConstants.GAME_IC,"国际象棋");
    }
}
