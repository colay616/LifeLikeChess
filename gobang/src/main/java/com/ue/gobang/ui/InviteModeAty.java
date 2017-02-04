package com.ue.gobang.ui;

import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.base.BaseInviteModeAty;
import com.ue.gobang.R;

public class InviteModeAty extends BaseInviteModeAty {
    @Override
    public void initAtyRes() {
        setAtyRes(R.layout.gb_aty_invite_m,R.drawable.game_black1,R.drawable.game_white1,GameConstants.GAME_GB,"五子棋");
    }
}
