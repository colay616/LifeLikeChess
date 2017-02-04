/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.DemoHelper;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.bmob.BmobManager;
import com.hyphenate.easeui.db.EaseDBManager;
import com.hyphenate.easeui.game.GameDialogs;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.PreferenceManager;
import com.hyphenate.exceptions.HyphenateException;
import com.ue.common.util.ToastUtil;
import com.ue.common.widget.edittext.ClearEditText;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Login screen
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private ClearEditText login_user;
    private ClearEditText login_password;
    private ImageView login_psw_visibility;
    private Button login_register;

    private boolean isProcessing = false;
    private String tipMsg = "";
    private int panelIndex = 0;
    private boolean isPswVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_login);

        setTitle("登录");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.slt_actionbar_back);

//        login_choices_panel = (RadioGroup) findViewById(login_choices_panel);
        login_user = (ClearEditText) findViewById(R.id.login_user);
        login_password = (ClearEditText) findViewById(R.id.login_password);
        login_psw_visibility = (ImageView) findViewById(R.id.login_psw_visibility);
        login_register = (Button) findViewById(R.id.login_register);

        if (DemoHelper.getInstance().getCurrentUserName() != null) {
            login_user.setText(DemoHelper.getInstance().getCurrentUserName());
        }
//        login_choices_panel.check(R.id.login_choice_login);

        setListeners();

        if (getIntent().getBooleanExtra("isConflict", false)) {
            GameDialogs.showTipDialog(LoginActivity.this, "提示", "帐号冲突,请重新登录");
        }
    }

    private void setListeners() {
        login_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isProcessing) {
                    GameDialogs.showTipDialog(LoginActivity.this,"正在处理请求,请稍等...");
                    return;
                }
                if (panelIndex == 0) {
                    login(login_user.getText().toString(), login_password.getText().toString());
                } else if (panelIndex == 1) {
                    register(login_user.getText().toString(), login_password.getText().toString());
                }
            }
        });
        login_psw_visibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPswVisible) {
                    login_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    login_psw_visibility.setImageResource(R.drawable.svg_visible);
                } else {
                    login_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    login_psw_visibility.setImageResource(R.drawable.svg_invisible);
                }
                isPswVisible = !isPswVisible;
            }
        });
    }

    private boolean isToContinue(String userName, String password) {
        if (TextUtils.isEmpty(userName)) {
            ToastUtil.toast("用户名不能为空");
            return false;
        }
        if (userName.length() > 10) {
            ToastUtil.toast("用户名长度不能超出10位");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.toast("密码不能为空");
            return false;
        }
        if (!EaseCommonUtils.isNetWorkConnected(this)) {
            ToastUtil.toast("网络异常,请检查网络后再试");
            return false;
        }
        return true;
    }

    private void register(final String userName, final String password) {
        if (!isToContinue(userName, password)) {
            return;
        }
        isProcessing = true;
        GameDialogs.showTipDialog(LoginActivity.this, "正在注册帐号,请稍等...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(userName, password);
                    DemoHelper.getInstance().setCurrentUserName(userName);
                } catch (HyphenateException e) {
                    isProcessing = false;
                    int errorCode = e.getErrorCode();
                    if (errorCode == EMError.NETWORK_ERROR) {
                        tipMsg = "网络异常，请检查网络！";
                    } else if (errorCode == EMError.USER_ALREADY_EXIST) {
                        tipMsg = "用户已存在！";
                    } else if (errorCode == EMError.USER_AUTHENTICATION_FAILED) {
                        tipMsg = "没有注册权限";
                    } else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT) {
                        tipMsg = "用户名不合法";
                    } else {
                        tipMsg = "注册失败";
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GameDialogs.dismissTipAlertDialog();
                        if (!isProcessing) {
                            ToastUtil.toast(tipMsg);
                        }
                    }
                });
                if (isProcessing) {
                    login(userName, password);
                }
            }
        }).start();
    }

    /**
     * login
     */
    public void login(final String userName, String password) {
        //如果不是从注册进入这里，需要判断参数是否合适
        if (!isProcessing && !isToContinue(userName, password)) {
            return;
        }
        isProcessing = true;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GameDialogs.showTipDialog(LoginActivity.this, "正在登录,请稍等...");
            }
        });

        // After logout，the DemoDB may still be accessed due to async callback, so the DemoDB will be re-opened again.
        // close it before login to make sure DemoDB not overlap
        EaseDBManager.getInstance();
        EaseDBManager.getInstance().closeDB();

        // reset current user name before login
        DemoHelper.getInstance().setCurrentUserName(userName);

        // call login method
//		Log.d(TAG, "EMClient.getInstance().login");
        EMClient.getInstance().login(userName, password, new EMCallBack() {
            @Override
            public void onSuccess() {
//				Log.d(TAG, "login: onSuccess");
                // ** manually load all local groups and conversation
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();

                // update current user's display name for APNs
//                boolean updatenick = EMClient.getInstance().updateCurrentUserNick(
//                        DemoApplication.currentUserNick.trim());
//                if (!updatenick) {
//                    Log.e("LoginActivity", "update current user nick fail");
//                }

                isProcessing = false;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GameDialogs.dismissTipAlertDialog();
                    }
                });
                // get user's info (this should be get from App's server or 3rd party service)
//                DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();
                asyncGetCurrentUserInfo(userName);

                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
//				Log.d(TAG, "login: onProgress");
            }

            @Override
            public void onError(final int code, final String message) {
//				Log.d(TAG, "login: onError: " + code);
                isProcessing = false;
                runOnUiThread(new Runnable() {
                    public void run() {
                        GameDialogs.dismissTipAlertDialog();
                        ToastUtil.toast("登录失败：" + message);
                    }
                });
            }
        });
    }

    private void asyncGetCurrentUserInfo(final String userName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("userName", userName);
                BmobManager.curdProfile(BmobManager.FUNC_GET_PROFILE, params, new BmobManager.OnResultListener() {
                    @Override
                    public void onResult(int code, String msg, JSONObject data) {
                        if (code == 100) {
                            data.remove("createdAt");
                            data.remove("updatedAt");
                            PreferenceManager.setCurrentUserNick(data.optString("userNick", userName));
                            PreferenceManager.setCurrentUserAvatar(data.optString("avatar", null));
                            PreferenceManager.saveUserInfo(userName, data.toString());
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId=item.getItemId();
        if(itemId==android.R.id.home){
            finish();
            return true;
        }
        if(itemId==R.id.menu_login_register){
            if(panelIndex==0){
                panelIndex=1;
                item.setTitle("登录");
                setTitle("注册");
                login_register.setText("注册");
            }else{
                panelIndex=0;
                item.setTitle("注册");
                setTitle("登录");
                login_register.setText("登录");
            }
        }
        return true;
    }
}
