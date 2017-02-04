package com.ue.moon_chess.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.ui.LoginActivity;
import com.ue.common.util.ToastUtil;
import com.ue.moon_chess.R;

public class MainAty extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mo_aty_main);
    }

    public void clickBtn(View view){
        int viewId=view.getId();
        if(viewId==R.id.moon_double){
            startActivity(new Intent(MainAty.this,DoubleModeAty.class));
        } else if(viewId==R.id.moon_invite){
            if (!EMClient.getInstance().isLoggedInBefore()) {
                startActivity(new Intent(MainAty.this, LoginActivity.class));
                return;
            }
            startActivity(new Intent(MainAty.this, InviteModeAty.class));
        }else if(viewId==R.id.moon_online){
            if (!EMClient.getInstance().isLoggedInBefore()) {
                startActivity(new Intent(MainAty.this, LoginActivity.class));
                return;
            }
            startActivity(new Intent(MainAty.this,OnlineModeAty.class));
        }else if(viewId==R.id.moon_single){
            ToastUtil.toast("on the way");
        }
    }
}
