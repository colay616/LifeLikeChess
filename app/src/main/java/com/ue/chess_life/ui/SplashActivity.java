package com.ue.chess_life.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;

import com.hyphenate.chat.EMClient;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.ue.chess_life.R;


/**
 * 开屏页
 *
 */
public class SplashActivity extends Activity implements SplashADListener {
    @SuppressWarnings("unused")
    private SplashAD splashAD;
    private ViewGroup container;

    public boolean canJump = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_splash);
        container = (ViewGroup) this.findViewById(R.id.splash_container);
        splashAD = new SplashAD(this, container, "1105569037", "5060912550607717", this,3000);

        /**
         * 开屏广告现已增加新的接口，可以由开发者在代码中设置开屏的超时时长
         * SplashAD(Activity activity, ViewGroup container, String appId, String posId, SplashADListener adListener, int fetchDelay)
         * fetchDelay参数表示开屏的超时时间，单位为ms，取值范围[3000, 5000]。设置为0时表示使用广点通的默认开屏超时配置
         *
         * splashAD = new SplashAD(this, container, Constants.APPID, Constants.SplashPosID, this, 3000);可以设置超时时长为3000ms
         */
        if(EMClient.getInstance().isLoggedInBefore()){
            EMClient.getInstance().groupManager().loadAllGroups();
            EMClient.getInstance().chatManager().loadAllConversations();
        }
    }

    @Override
    public void onADPresent() {
        Log.i("AD_DEMO", "SplashADPresent");
    }

    @Override
    public void onADClicked() {
        Log.i("AD_DEMO", "SplashADClicked");
    }

    @Override
    public void onADDismissed() {
        Log.i("AD_DEMO", "SplashADDismissed");
        next();
    }

    @Override
    public void onNoAD(int errorCode) {
        Log.i("AD_DEMO", "LoadSplashADFail, eCode=" + errorCode);
        /** 如果加载广告失败，则直接跳转 */
        this.startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

    /**
     * 设置一个变量来控制当前开屏页面是否可以跳转，当开屏广告为普链类广告时，点击会打开一个广告落地页，此时开发者还不能打开自己的App主页。当从广告落地页返回以后，
     * 才可以跳转到开发者自己的App主页；当开屏广告是App类广告时只会下载App。
     */
    private void next() {
        if (canJump) {
            this.startActivity(new Intent(this, MainActivity.class));
            this.finish();
        } else {
            canJump = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        canJump = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJump) {
            next();
        }
        canJump = true;
    }

    /** 开屏页最好禁止用户对返回按钮的控制 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
