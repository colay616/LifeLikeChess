package com.ue.gobang.ui;

import android.graphics.Point;

import com.hyphenate.easeui.game.AILevelDialog;
import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.base.BaseSingleModeAty;
import com.hyphenate.easeui.game.iterface.OnAIChangedListener;
import com.hyphenate.easeui.game.iterface.OnUndoListener;
import com.ue.common.xsharedpref.XSharedPref;
import com.ue.gobang.R;
import com.ue.gobang.util.GobangAi;
import com.ue.gobang.widget.GobangView;

import java.util.ArrayList;

import static com.hyphenate.easeui.game.GameConstants.PREF_GB_DIFFICULTY;
import static com.hyphenate.easeui.game.GameConstants.PREF_GB_IS_FIRST;

public class SingleModeAty extends BaseSingleModeAty {
    private GobangAi aiPlayer;
    private ArrayList<Point> allFreePoints;
//    public GobangAILevelDialog mAILevelDialog;
    private AILevelDialog mAILevelDialog;

    private void aiPlay() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Point aiPoint = aiPlayer.doAnalysis(((GobangView)game_chessboard).getOppoChesses(), ((GobangView)game_chessboard).getMyChesses(), allFreePoints);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        game_chessboard.playChess(new int[]{aiPoint.x, aiPoint.y});
                    }
                });
            }
        }).start();
    }

    public void chooseAILevel() {
        if (null == mAILevelDialog) {
            mAILevelDialog =AILevelDialog.newInstance(GameConstants.GAME_GB, new OnAIChangedListener() {
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
//            mAILevelDialog.setOnAIChangedListener(
//                    new OnAIChangedListener() {
//                        @Override
//                        public void onAIChanged(int rDifficulty, boolean isFirst) {
//                            isIPlayFirst = isFirst;
//                            difficulty = rDifficulty;
//
//                            game_user_panel.setOppoName(NAME_OF_AI[difficulty]);
//                            initUserInfo();
//                            startGame();
//
//                            mAILevelDialog.dismiss();
//                        }
//                    }
//            );
        }
        if (!mAILevelDialog.isVisible() && !isFinishing()) {
            mAILevelDialog.show(getSupportFragmentManager(), null);
        }
    }

    //初始化空白点集合
    private void initAllFreePoints() {
        allFreePoints.clear();
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                allFreePoints.add(new Point(i, j));
            }
        }
    }

    @Override
    public void afterIPlayed(int[] data) {
        allFreePoints.remove(new Point(data[0], data[1]));
        aiPlay();
    }

    @Override
    public void afterOppoPlayed(int[] data) {
        allFreePoints.remove(new Point(data[0], data[1]));
    }

    @Override
    public void startGame() {
        initAllFreePoints();
        game_chessboard.startGame(isIPlayFirst);
        updatePanel();
        if (!isIPlayFirst) {
            game_chessboard.playChess(new int[]{7, 7});
        }
    }

    @Override
    public void initAtyRes() {
        setAtyRes(R.layout.gb_aty_single_m,R.drawable.game_black1,R.drawable.game_white1);

        game_chessboard.setOnUndoListener(new OnUndoListener() {
            @Override
            public void onUndo(int[] data) {
                allFreePoints.add(new Point(data[0], data[1]));
            }
        });

        NAME_OF_AI = new String[]{"新手", "菜鸟", "入门"};
        difficulty = XSharedPref.getInt(PREF_GB_DIFFICULTY, 0);
        isIPlayFirst = XSharedPref.getBoolean(PREF_GB_IS_FIRST, true);
        aiPlayer = new GobangAi(14, 14);//最大x坐标=14，最大y坐标=14
        aiPlayer.setRangeStep((difficulty + 1));

        allFreePoints = new ArrayList<>();
    }
}
