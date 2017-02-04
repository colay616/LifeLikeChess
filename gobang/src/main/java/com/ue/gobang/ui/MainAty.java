package com.ue.gobang.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.ui.LoginActivity;
import com.ue.gobang.R;

public class MainAty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gb_aty_main);
    }

    public void clickBtn(View view){
        int viewId=view.getId();
        if(viewId==R.id.gobang_double){
            startActivity(new Intent(MainAty.this,DoubleModeAty.class));
        }else if(viewId==R.id.gobang_invite){
            if (!EMClient.getInstance().isLoggedInBefore()) {
                startActivity(new Intent(MainAty.this, LoginActivity.class));
                return;
            }
            startActivity(new Intent(MainAty.this, InviteModeAty.class));
        }else if(viewId==R.id.gobang_online){
            if (!EMClient.getInstance().isLoggedInBefore()) {
                startActivity(new Intent(MainAty.this, LoginActivity.class));
                return;
            }
            startActivity(new Intent(MainAty.this,OnlineModeAty.class));
        }else if(viewId==R.id.gobang_single){
            startActivity(new Intent(MainAty.this,SingleModeAty.class));
        }
    }
}
