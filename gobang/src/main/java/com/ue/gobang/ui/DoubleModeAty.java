package com.ue.gobang.ui;

import com.hyphenate.easeui.game.base.BaseDoubleModeAty;
import com.ue.gobang.R;

public class DoubleModeAty extends BaseDoubleModeAty {
    @Override
    public void initAtyRes() {
        setAtyRes(R.layout.gb_aty_double_m,R.drawable.game_black1,R.drawable.game_white1);
    }
}