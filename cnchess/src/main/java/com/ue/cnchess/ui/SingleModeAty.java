package com.ue.cnchess.ui;

import com.hyphenate.easeui.game.AILevelDialog;
import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.base.BaseSingleModeAty;
import com.hyphenate.easeui.game.iterface.OnAIChangedListener;
import com.hyphenate.easeui.game.iterface.OnUndoListener;
import com.ue.cnchess.R;
import com.ue.cnchess.entity.CNChessRecord;
import com.ue.cnchess.entity.CnChessPoint;
import com.ue.cnchess.util.AlphaBetaEngine;
import com.ue.cnchess.util.CNChessUtil;
import com.ue.cnchess.widget.CNChessView;
import com.ue.common.util.LogUtil;
import com.ue.common.xsharedpref.XSharedPref;

import static com.hyphenate.easeui.game.GameConstants.PREF_CC_DIFFICULTY;
import static com.hyphenate.easeui.game.GameConstants.PREF_CC_IS_FIRST;

public class SingleModeAty extends BaseSingleModeAty {
    private AILevelDialog mAILevelDialog;
    private AlphaBetaEngine pSE;
    private CnChessPoint[][] tempChessPoints = new CnChessPoint[9][10];
    private int fromX, fromY, toX, toY;

    private void aiPlay() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pSE.searchAGoodMove(tempChessPoints);//在找到最好的位置后就移动棋子
                final CNChessRecord cm = pSE.getBestMove();//获取最好位置
                LogUtil.i(TAG, "best move=" + cm);//null

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        game_chessboard.playChess(new int[]{cm.fromX, cm.fromY, cm.toX, cm.toY});//保存
                    }
                });
            }
        }).start();
    }


    public void chooseAILevel() {
        if (null == mAILevelDialog) {
            mAILevelDialog = AILevelDialog.newInstance(GameConstants.GAME_CN,new OnAIChangedListener() {
                @Override
                public void onAIChanged(int rDifficulty, boolean isFirst) {
                    isIPlayFirst = isFirst;
                    difficulty = rDifficulty;

                    game_user_panel.setOppoName(NAME_OF_AI[difficulty]);
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

    public void startGame() {
        game_chessboard.startGame(isIPlayFirst);
        updatePanel();

        pSE = new AlphaBetaEngine(isIPlayFirst ? CNChessUtil.BLACK : CNChessUtil.RED);//搜索算法
        pSE.setSearchDepth((difficulty + 1));

        CnChessPoint[][] chessPoints = ((CNChessView)game_chessboard).getChessArray();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 10; j++) {
                tempChessPoints[i][j] = null == chessPoints[i][j] ? null : chessPoints[i][j].copyAll();
            }
        }

        if (!game_chessboard.isMyTurn()) {
            aiPlay();
        }
    }

    @Override
    public void afterIPlayed(int[] data) {
        moveChess(data);
        aiPlay();
    }

    @Override
    public void afterOppoPlayed(int[] data) {
        moveChess(data);
    }

    private void moveChess(int[] data) {
        fromX = data[0];
        fromY = data[1];
        toX = data[2];
        toY = data[3];
        // 移动棋子
        if (null == tempChessPoints[toX][toY]) {
            tempChessPoints[toX][toY] = new CnChessPoint(tempChessPoints[fromX][fromY].f(), toX, toY, tempChessPoints[fromX][fromY].c);
        } else {
            tempChessPoints[toX][toY].copyFrom(tempChessPoints[fromX][fromY]);
        }
        tempChessPoints[fromX][fromY] = null;//将原来处置空
    }

    @Override
    public void initAtyRes() {
        setAtyRes(R.layout.cc_aty_single_m, R.mipmap.shuai, R.mipmap.jiang);

        game_chessboard.setOnUndoListener(new OnUndoListener() {
            @Override
            public void onUndo(int[] data) {
                int f = data[0], x = data[1], y = data[2], c = data[3];
                tempChessPoints[x][y] = new CnChessPoint(f, x, y, c);
                f = data[4];
                x = data[5];
                y = data[6];
                c = data[7];
                tempChessPoints[x][y] = f == -1 ? null : new CnChessPoint(f, x, y, c);
            }
        });

        NAME_OF_AI = new String[]{"新手", "菜鸟", "入门"};
        difficulty = XSharedPref.getInt(PREF_CC_DIFFICULTY, 0);
        isIPlayFirst = XSharedPref.getBoolean(PREF_CC_IS_FIRST, true);
    }
}
