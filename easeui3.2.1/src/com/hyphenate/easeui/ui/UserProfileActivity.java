package com.hyphenate.easeui.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.bmob.Bmob;
import com.hyphenate.easeui.bmob.BmobManager;
import com.hyphenate.easeui.game.GameDialogs;
import com.hyphenate.easeui.utils.EaseConstant;
import com.hyphenate.easeui.utils.PreferenceManager;
import com.ue.common.imagepicker.AndroidImagePicker;
import com.ue.common.imagepicker.bean.ImageItem;
import com.ue.common.util.ToastUtil;
import com.ue.common.widget.dialog.MDEditDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {
    private static final String TAG = UserProfileActivity.class.getSimpleName();
    private ImageView profile_avatar;
    private ImageView profile_update_avatar;
    private TextView profile_username;
    private TextView profile_nickname;
//    private TextView profile_sex;
//    private TextView profile_email;

    private boolean isMyProfile;
    private String userName;
    private boolean isFetchingData = false;
    private JSONObject profileObj;
    private String avatarUrl;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_user_profile);

        userName = getIntent().getStringExtra(EaseConstant.USER_NAME);
        if(userName.equals(EMClient.getInstance().getCurrentUser())){
            isMyProfile=true;
        }

        setTitle("个人资料");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.slt_actionbar_back);

        profile_update_avatar = (ImageView) findViewById(R.id.profile_update_avatar);
        profile_avatar = (ImageView) findViewById(R.id.profile_avatar);
        profile_username = (TextView) findViewById(R.id.profile_username);
        profile_nickname = (TextView) findViewById(R.id.profile_nickname);
//        profile_sex = (TextView) findViewById(profile_sex);
//        profile_email = (TextView) findViewById(profile_email);

        profile_username.setText(userName);

        if(isMyProfile){
            Drawable arrowDrawable=getResources().getDrawable(R.drawable.svg_arrow_right);
            arrowDrawable.setBounds(0,0,arrowDrawable.getMinimumWidth(),arrowDrawable.getMinimumHeight());
            profile_nickname.setCompoundDrawables(null,null,arrowDrawable,null);
            profile_nickname.setCompoundDrawablePadding(5);
//            profile_sex.setCompoundDrawables(null,null,arrowDrawable,null);
//            profile_sex.setCompoundDrawablePadding(5);
//            profile_email.setCompoundDrawables(null,null,arrowDrawable,null);
//            profile_email.setCompoundDrawablePadding(5);

            setLiseteners();

            String userInfoStr=PreferenceManager.getUserInfo(EMClient.getInstance().getCurrentUser());
            if(TextUtils.isEmpty(userInfoStr)){
                fetchProfile(userName);
            }else{
                try {
                    profileObj=new JSONObject(userInfoStr);
                    initViews();
                } catch (JSONException e) {
                }
            }
        }else{
//            profile_email.setVisibility(View.GONE);
            profile_update_avatar.setVisibility(View.GONE);

            fetchProfile(userName);
        }
    }

    private void setLiseteners() {
        profile_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpdateNicknameClick();
            }
        });
        profile_update_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpdateAvatarClick();
            }
        });
//        profile_sex.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onUpdateSexClick();
//            }
//        });
//        profile_email.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onUpdateEmailClick();
//            }
//        });
    }

    /**
     * 更新性别
     */
//    private void onUpdateSexClick() {
//        if (isFetchingData) {
//            GameDialogs.showTipDialog(UserProfileActivity.this, null);
//            return;
//        }
//
//        if (profileObj == null || TextUtils.isEmpty(profileObj.optString("objectId"))) {
//            fetchProfile(userName);
//            return;
//        }
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                isFetchingData = true;
//                final boolean isMale = "男".equals(profile_sex.getText().toString().trim());
//                Map<String, Object> params = new HashMap<>();
//                params.put("userId", profileObj.optString("objectId"));
//                params.put("sex", !isMale);
//                BmobManager.curdProfile(BmobManager.FUNC_UPDATE_SEX, params, new BmobManager.OnResultListener() {
//                    @Override
//                    public void onResult(final int code, final String msg, JSONObject data) {
//                        isFetchingData = false;
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                GameDialogs.dismissTipAlertDialog();
//                                if (code == 100) {
//                                    ToastUtil.toast("更新性别成功");
//                                    profile_sex.setText(isMale ? "女" : "男");
//                                    try {
//                                        profileObj.put("sex", !isMale);
//                                    } catch (JSONException e) {
//                                    }
//                                } else {
//                                    ToastUtil.toast("更新性别失败:" + msg);
//                                }
//                            }
//                        });
//                    }
//                });
//            }
//        }).start();
//    }

    private void onUpdateAvatarClick() {
        if (isFetchingData) {
            GameDialogs.showTipDialog(UserProfileActivity.this, null);
            return;
        }
        if (profileObj == null || TextUtils.isEmpty(profileObj.optString("objectId"))) {
            fetchProfile(userName);
            return;
        }
        //single select
        AndroidImagePicker.getInstance().pickSingle(UserProfileActivity.this, true, new AndroidImagePicker.OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(final List<ImageItem> items) {
                if (items != null && items.size() > 0) {
//                    LogUtil.i(TAG,"=====selected："+items.get(0).path);
//                    =====selected：/storage/emulated/0/Pictures/IMG_20161205_202127.jpg
                    GameDialogs.showTipDialog(UserProfileActivity.this, "正在上传头像,请稍等...");
                    isFetchingData = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String uploadResult = Bmob.uploadFile2(items.get(0).path);
//                            LogUtil.i(TAG,"upload result="+res);
//                            upload result={"cdn":"upyun","filename":"IMG_20161205_202127.jpg","url":"http://bmob-cdn-6668.b0.upaiyun.com/2016/12/05/6ab6e42f40ca6e9e805d6c446dab258e.jpg"}
                            boolean isUploadOk = false;
                            try {
                                JSONObject uploadResultObj = new JSONObject(uploadResult);
                                avatarUrl = uploadResultObj.getString("url");
                                isUploadOk = true;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Glide.with(UserProfileActivity.this).load(avatarUrl).placeholder(R.drawable.em_default_avatar).into(profile_avatar);
                                        GameDialogs.showTipDialog(UserProfileActivity.this, "正在更新头像,请稍等...");
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            isFetchingData = isUploadOk;
                            if (isUploadOk) {
                                isFetchingData = true;
                                Map<String, Object> params = new HashMap<>();
                                params.put("userId", profileObj.optString("objectId"));
                                params.put("userAvatar", avatarUrl);
                                BmobManager.curdProfile(BmobManager.FUNC_UPDATE_AVATAR, params, new BmobManager.OnResultListener() {
                                    @Override
                                    public void onResult(final int code, final String msg, JSONObject data) {
                                        isFetchingData = false;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                GameDialogs.dismissTipAlertDialog();
                                                if (code == 100) {
                                                    ToastUtil.toast("更新头像成功");
                                                    PreferenceManager.setCurrentUserAvatar(avatarUrl);
                                                    try {
                                                        profileObj.put("avatar", avatarUrl);
                                                    } catch (JSONException e) {
                                                    }
                                                } else {
                                                    ToastUtil.toast("更新头像失败:" + msg);
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }).start();
                }
            }
        });
    }

    /**
     * 更新邮箱
     */
//    private void onUpdateEmailClick() {
//        if (isFetchingData) {
//            GameDialogs.showTipDialog(UserProfileActivity.this, null);
//            return;
//        }
//
//        if (profileObj == null || TextUtils.isEmpty(profileObj.optString("objectId"))) {
//            fetchProfile(userName);
//            return;
//        }
//
//        GameDialogs.dismissEditDialog(true);
//        GameDialogs.showEditDialog(UserProfileActivity.this, "新邮箱:", "取消", "确定", new MDEditDialog.OnClickEditDialogListener() {
//            @Override
//            public void clickLeftButton(View view, String editText) {
//                GameDialogs.dismissEditDialog(false);
//            }
//
//            @Override
//            public void clickRightButton(View view, final String editText) {
//                GameDialogs.dismissEditDialog(false);
//
//                if (!Patterns.EMAIL_ADDRESS.matcher(editText).matches()) {
//                    ToastUtil.toast("邮件格式不正确");
//                    return;
//                }
//                GameDialogs.showTipDialog(UserProfileActivity.this, "正在更新邮箱,请稍等...");
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        isFetchingData = true;
//                        Map<String, Object> params = new HashMap<>();
//                        params.put("userId", profileObj.optString("objectId"));
//                        params.put("email", editText);
//                        BmobManager.curdProfile(BmobManager.FUNC_UPDATE_EMAIL, params, new BmobManager.OnResultListener() {
//                            @Override
//                            public void onResult(final int code, final String msg, JSONObject data) {
//                                isFetchingData = false;
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        GameDialogs.dismissTipAlertDialog();
//                                        if (code == 100) {
//                                            ToastUtil.toast("更新邮箱成功");
//                                            profile_email.setText(editText);
//                                            try {
//                                                profileObj.put("email", editText);
//                                            } catch (JSONException e) {
//                                            }
//                                        } else {
//                                            ToastUtil.toast("更新邮箱失败:" + msg);
//                                        }
//                                    }
//                                });
//                            }
//                        });
//                    }
//                }).start();
//            }
//        });
//    }

    /**
     * 更新昵称
     */
    private void onUpdateNicknameClick() {
        if (isFetchingData) {
            GameDialogs.showTipDialog(UserProfileActivity.this, null);
            return;
        }

        if (profileObj == null || TextUtils.isEmpty(profileObj.optString("objectId"))) {
            fetchProfile(userName);
            return;
        }
        GameDialogs.showReviseNickDialog(UserProfileActivity.this, new MDEditDialog.OnClickEditDialogListener() {
            @Override
            public void onClick(View view, final String editText) {
                GameDialogs.showTipDialog(UserProfileActivity.this, "正在更新昵称,请稍等...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        isFetchingData = true;
                        Map<String, Object> params = new HashMap<>();
                        params.put("userId", profileObj.optString("objectId"));
                        params.put("userNick", editText);
                        BmobManager.curdProfile(BmobManager.FUNC_UPDATE_NICKNAME, params, new BmobManager.OnResultListener() {
                            @Override
                            public void onResult(final int code, final String msg, JSONObject data) {
                                isFetchingData = false;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        GameDialogs.dismissTipAlertDialog();
                                        if (code == 100) {
                                            ToastUtil.toast("更新昵称成功");
                                            profile_nickname.setText(editText);
                                            PreferenceManager.setCurrentUserNick(editText);
                                            try {
                                                profileObj.put("userNick", editText);
                                            } catch (JSONException e) {
                                            }
                                        } else {
                                            ToastUtil.toast("更新昵称失败:" + msg);
                                        }
                                    }
                                });
                            }
                        });
                    }
                }).start();
            }
        });
    }

    /**
     * 获取用户数据
     *
     * @param userName
     */
    private void fetchProfile(final String userName) {
        GameDialogs.showTipDialog(UserProfileActivity.this, "正在获取用户数据,请稍等...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                isFetchingData = true;
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("userName", userName);
                BmobManager.curdProfile(BmobManager.FUNC_GET_PROFILE, params, new BmobManager.OnResultListener() {
                    @Override
                    public void onResult(final int code, String msg, final JSONObject data) {
                        isFetchingData = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                GameDialogs.dismissTipAlertDialog();
                                if (code == 100) {
                                    data.remove("createdAt");
                                    data.remove("updatedAt");
                                    profileObj = data;
                                    initViews();
                                } else {
                                    ToastUtil.toast("获取用户数据失败");
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }

    private void initViews() {
        Glide.with(UserProfileActivity.this).load(profileObj.optString("avatar", "no-avatar")).placeholder(R.drawable.em_default_avatar).into(profile_avatar);
        profile_nickname.setText(profileObj.optString("userNick", userName));
//        profile_sex.setText(profileObj.optBoolean("sex", true) ? "男" : "女");
//        if (isMyProfile) {
//            profile_email.setText(profileObj.optString("email", "未设置"));
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isMyProfile){
            PreferenceManager.saveUserInfo(EMClient.getInstance().getCurrentUser(), profileObj.toString());
        }
    }
}
