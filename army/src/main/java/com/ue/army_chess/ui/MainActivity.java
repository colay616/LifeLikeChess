package com.ue.army_chess.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.ui.LoginActivity;
import com.ue.army_chess.R;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_aty_main);

    }

    public void clickBtn(View view) {
        int viewId = view.getId();
        if (viewId == R.id.army_double_f) {
            startActivity(new Intent(MainActivity.this, FDoubleModeAty.class));
            return;
        }
//        if (viewId == R.id.army_invite_f) {
//            if (!EMClient.getInstance().isLoggedInBefore()) {
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                return;
//            }
//            startActivity(new Intent(MainActivity.this, FInviteModeAty.class));
//            return;
//        }
//        if (viewId == R.id.army_online_f) {
//            if (!EMClient.getInstance().isLoggedInBefore()) {
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                return;
//            }
//            startActivity(new Intent(MainActivity.this, FOnlineModeAty.class));
//            return;
//        }
        if (viewId == R.id.army_online_a) {
            if (!EMClient.getInstance().isLoggedInBefore()) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return;
            }
            startActivity(new Intent(MainActivity.this, AOnlineModeAty.class));
            return;
        }
        if (viewId == R.id.army_invite_a) {
            if (!EMClient.getInstance().isLoggedInBefore()) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return;
            }
            startActivity(new Intent(MainActivity.this, AInviteModeAty.class));
            return;
        }
        if (viewId == R.id.army_online_m) {
            if (!EMClient.getInstance().isLoggedInBefore()) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return;
            }
            startActivity(new Intent(MainActivity.this, MOnlineModeAty.class));
            return;
        }
        if (viewId == R.id.army_invite_m) {
            if (!EMClient.getInstance().isLoggedInBefore()) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return;
            }
            startActivity(new Intent(MainActivity.this, MInviteModeAty.class));
            return;
        }
        if (viewId == R.id.army_double_m) {
            startActivity(new Intent(MainActivity.this, MDoubleModeAty.class));
            return;
        }
    }
}
