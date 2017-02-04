package com.ue.cnchess.ui;

import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.base.BaseInviteModeAty;
import com.ue.cnchess.R;

public class InviteModeAty extends BaseInviteModeAty {
    @Override
    public void initAtyRes() {
        setAtyRes(R.layout.cc_aty_invite_m,R.mipmap.shuai,R.mipmap.jiang,GameConstants.GAME_CN,"中国象棋");
    }
}
