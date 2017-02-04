package com.ue.moon_chess.ui;

import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.base.BaseInviteModeAty;
import com.ue.moon_chess.R;

public class InviteModeAty extends BaseInviteModeAty {

    @Override
    public void initAtyRes() {
        setAtyRes(R.layout.mo_aty_invite_m,R.mipmap.black1,R.mipmap.white1,GameConstants.GAME_MC,"走月光");
    }
}
