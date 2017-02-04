package com.ue.reversi.ui;

import com.hyphenate.easeui.game.AILevelDialog;
import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.base.BaseSingleModeAty;
import com.hyphenate.easeui.game.iterface.OnAIChangedListener;
import com.hyphenate.easeui.game.iterface.OnUndoListener;
import com.ue.common.xsharedpref.XSharedPref;
import com.ue.reversi.R;
import com.ue.reversi.bean.ReversiMove;
import com.ue.reversi.bean.Statistic;
import com.ue.reversi.util.Algorithm;
import com.ue.reversi.util.Rule;
import com.ue.reversi.util.Util;
import com.ue.reversi.widget.ReversiView;

import static com.hyphenate.easeui.game.GameConstants.PREF_AP_DIFFICULTY;
import static com.hyphenate.easeui.game.GameConstants.PREF_AP_IS_FIRST;
import static java.lang.Thread.sleep;

/**
 * 逻辑描述：
 * 进入本页面在获取等级和先后手数据进行初始化后即可开始游戏
 * 游戏结束后点击新局重新初始化
 * 点击等级按钮可以选择等级和先后手
 */
public class SingleModeAty extends BaseSingleModeAty {
    private AILevelDialog mAILevelDialog;
    private byte[][] tmp = new byte[8][8];
    private int sleepTime;

    private void aiPlay() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int legalMoves = Rule.getLegalMoves(((ReversiView) game_chessboard).getChessBoard(), ((ReversiView) game_chessboard).getOppoColor()).size();
                if (legalMoves > 0) {
                    Util.copyBinaryArray(((ReversiView) game_chessboard).getChessBoard(), tmp);
                    final ReversiMove move = Algorithm.getGoodMove(tmp, difficulty, ((ReversiView) game_chessboard).getOppoColor(), difficulty);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            game_chessboard.playChess(new int[]{move.x, move.y});
                        }
                    });
                }
            }
        }).start();
    }

    public void updatePanel() {
        Statistic statistic = Rule.analyse(((ReversiView) game_chessboard).getChessBoard(), ((ReversiView) game_chessboard).getMyColor());
        game_user_panel.setMyExtraTxt(" × " + statistic.PLAYER);
        game_user_panel.setOppoExtraTxt(" × " + statistic.AI);
        game_user_panel.updateFocus(game_chessboard.isMyTurn());
    }

    @Override
    public void afterIPlayed(int[] data) {
        if (!game_chessboard.isMyTurn()) {
            aiPlay();
        }
    }

    @Override
    public void afterOppoPlayed(int[] data) {
        if (!game_chessboard.isMyTurn()) {
            aiPlay();
        }
    }

    @Override
    public void startGame() {
        game_chessboard.startGame(isIPlayFirst);
        updatePanel();
        if (!game_chessboard.isMyTurn()) {
            aiPlay();
        }
    }

    public void initUserInfo() {
        if (isIPlayFirst) {
            game_user_panel.setMyImage(firstPlayImage);
            game_user_panel.setOppoImage(lastPlayImage);
        } else {
            game_user_panel.setMyImage(lastPlayImage);
            game_user_panel.setOppoImage(firstPlayImage);
        }
        game_user_panel.setMyExtraTxt(" × " + 2);
        game_user_panel.setOppoExtraTxt(" × " + 2);
        game_user_panel.updateFocus(game_chessboard.isMyTurn());

        game_chessboard.setOnUndoListener(new OnUndoListener() {
            @Override
            public void onUndo(int[] data) {
                if (!game_chessboard.isMyTurn()) {
                    aiPlay();
                }
            }
        });
    }

    @Override
    public void chooseAILevel() {
        if (null == mAILevelDialog) {
            mAILevelDialog = AILevelDialog.newInstance(GameConstants.GAME_AP, new OnAIChangedListener() {
                @Override
                public void onAIChanged(int rDifficulty, boolean isFirst) {
                    isIPlayFirst = isFirst;
                    difficulty = rDifficulty;

                    game_user_panel.setOppoName(NAME_OF_AI[difficulty]);
                    sleepTime = difficulty >= 5 ? 0 : 2000;
                    initUserInfo();
                    startGame();
                    mAILevelDialog.dismiss();
                }
            });
        }
        if (!mAILevelDialog.isVisible() && !isFinishing()) {
            mAILevelDialog.show(getSupportFragmentManager(), null);
        }
    }

    @Override
    public void initAtyRes() {
        setAtyRes(R.layout.rv_aty_single_m, R.drawable.game_black1, R.drawable.game_white1);
        difficulty = XSharedPref.getInt(PREF_AP_DIFFICULTY, 0);
        isIPlayFirst = XSharedPref.getBoolean(PREF_AP_IS_FIRST, true);
        sleepTime = difficulty >= 5 ? 0 : 2000;
        NAME_OF_AI = new String[]{"新手", "菜鸟", "入门", "棋手", "棋士", "棋圣"};
    }
}
