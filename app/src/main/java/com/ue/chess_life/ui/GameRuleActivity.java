package com.ue.chess_life.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;

import com.hyphenate.easeui.game.GameConstants;
import com.ue.chess_life.R;
import com.ue.chess_life.utils.AppConstants;

public class GameRuleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_game_rule);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.slt_actionbar_back);

        TextView textView = (TextView) findViewById(R.id.rule);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());

        int whichGame = getIntent().getIntExtra(AppConstants.GAME_FLAG, GameConstants.GAME_GB);
        String rule = "";
        switch (whichGame) {
            case GameConstants.GAME_IC:
                setTitle("国际象棋规则");
                rule = getResources().getString(R.string.rule_chess);
                break;
            case GameConstants.GAME_CN:
                setTitle("中国象棋规则");
                rule = getResources().getString(R.string.rule_cnchess);
                break;
            case GameConstants.GAME_GB:
                setTitle("五子棋规则");
                rule = getResources().getString(R.string.rule_gobang);
                break;
            case GameConstants.GAME_AP:
                setTitle("黑白棋规则");
                rule = getResources().getString(R.string.rule_reversi);
                break;
            case GameConstants.GAME_MC:
                setTitle("走月光规则");
                rule = getResources().getString(R.string.rule_moon);
                break;
            case GameConstants.GAME_ACA:
                setTitle("军棋-暗棋规则");
                rule = getResources().getString(R.string.rule_army);
                break;
            case GameConstants.GAME_ACF:
                setTitle("军棋-翻棋规则");
                rule = getResources().getString(R.string.rule_army);
                break;
            case GameConstants.GAME_ACM:
                setTitle("军棋-明棋规则");
                rule = getResources().getString(R.string.rule_army);
                break;
        }
        textView.setText(rule);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            GameRuleActivity.this.finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}