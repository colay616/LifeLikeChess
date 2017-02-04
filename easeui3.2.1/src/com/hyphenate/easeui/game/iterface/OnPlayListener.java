package com.hyphenate.easeui.game.iterface;

/**
 * Reference:
 * Author:
 * Date:2016/9/9.
 */
public interface OnPlayListener {
    int FLAG_I_WIN=0;
    int FLAG_OPPO_WIN=1;
    int FLAG_DRAW=2;
    void onIPlayed(int[] data);
    void onOppoPlayed(int[] data);
    void onGameOver(int resultFlag);
}
