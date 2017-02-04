package com.hyphenate.easeui.game.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.GameDialogs;
import com.hyphenate.easeui.game.UserPanelView;
import com.hyphenate.easeui.game.iterface.OnPlayListener;

/**
 * Created by hawk on 2016/12/11.
 */

public abstract class BaseSingleModeAty extends AppCompatActivity {
    public static final String TAG = BaseSingleModeAty.class.getSimpleName();
    public String NAME_OF_AI[];
    public UserPanelView game_user_panel;
    protected BaseGameView game_chessboard;

    public int difficulty;
    public boolean isIPlayFirst = true;

    public int firstPlayImage;
    public int lastPlayImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initAtyRes();

        game_user_panel = (UserPanelView) findViewById(R.id.game_user_panel);
        game_chessboard.setGameMode(GameConstants.MODE_SINGLE);
        game_chessboard.setPlayListener(new OnPlayListener() {
            @Override
            public void onIPlayed(int[] data) {
                updatePanel();
                afterIPlayed(data);
            }

            @Override
            public void onOppoPlayed(int[] data) {
                updatePanel();
                afterOppoPlayed(data);
            }

            @Override
            public void onGameOver(int resultFlag) {
                gameOver(resultFlag);
            }
        });

        game_user_panel.setMyName("我");
        game_user_panel.setOppoName(NAME_OF_AI[difficulty]);
        initUserInfo();

        findViewById(R.id.game_new).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });
        findViewById(R.id.game_level).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseAILevel();
            }
        });
        findViewById(R.id.game_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameDialogs.showOfflineExitDialog(BaseSingleModeAty.this);
            }
        });
        findViewById(R.id.game_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (game_chessboard.isGameStarted() && game_chessboard.isAllowedToUndo()) {
                    game_chessboard.undoChess(true);
                    updatePanel();
                }
            }
        });
        game_user_panel.setOppoName(NAME_OF_AI[difficulty]);

        startGame();
    }

    public void initUserInfo() {
        if (isIPlayFirst) {
            game_user_panel.setMyImage(firstPlayImage);
            game_user_panel.setOppoImage(lastPlayImage);
        } else {
            game_user_panel.setMyImage(lastPlayImage);
            game_user_panel.setOppoImage(firstPlayImage);
        }
    }

    public void updatePanel() {
        game_user_panel.updateFocus(game_chessboard.isMyTurn());
    }

    public void gameOver(int resultFlag) {
        String msg = "";
        if (resultFlag == OnPlayListener.FLAG_I_WIN) {
            msg = "我赢了";
        } else if (resultFlag == OnPlayListener.FLAG_DRAW) {
            msg = "平局";
        } else if (resultFlag == OnPlayListener.FLAG_OPPO_WIN) {
            msg = "我输了";
        }
        GameDialogs.showResultDialog(BaseSingleModeAty.this,msg);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            GameDialogs.showOfflineExitDialog(BaseSingleModeAty.this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setAtyRes(int layoutRes,int firstImgRes,int lastImgRes){
        setContentView(layoutRes);
        firstPlayImage=firstImgRes;
        lastPlayImage=lastImgRes;
        game_chessboard = (BaseGameView) findViewById(R.id.game_chessboard);
    }

    public abstract void initAtyRes();

    public abstract void chooseAILevel();

    public abstract void afterIPlayed(int[] data);

    public abstract void afterOppoPlayed(int[] data);

    public abstract void startGame();
}
