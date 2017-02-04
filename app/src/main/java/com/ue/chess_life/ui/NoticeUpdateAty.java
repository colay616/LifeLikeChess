package com.ue.chess_life.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.utils.EaseConstant;
import com.hyphenate.easeui.utils.PreferenceManager;
import com.ue.chess_life.R;
import com.ue.common.util.ToastUtil;

/**
 * Created by hawk on 2016/12/28.
 */

public class NoticeUpdateAty extends AppCompatActivity{
    private EditText anupe_code;
    private EditText anupe_name;
    private EditText anupe_url;
    private EditText anupe_ps;
    private int submitId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_notice_update);

        anupe_code= (EditText) findViewById(R.id.anupe_code);
        anupe_name= (EditText) findViewById(R.id.anupe_name);
        anupe_url= (EditText) findViewById(R.id.anupe_url);
        anupe_ps= (EditText) findViewById(R.id.anupe_ps);

        anupe_url.setText("http://113.105.73.150/imtt.dd.qq.com/16891/59D881C4F399C9CAB40A6620FE006C8C.apk?mkey=5864a28327de4984&f=8b5d&c=0&fsname=com.ue.chess_life_2.5_10.apk&csr=4d5s&p=.apk");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        submitId=menu.add("发布").getItemId();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId=item.getItemId();
        if(itemId==android.R.id.home){
            finish();
            return true;
        }
        if(itemId==submitId){
            onSubmitClick();
            return true;
        }
        return true;
    }

    private void onSubmitClick(){
        String verCodeStr=anupe_code.getText().toString();
        if(TextUtils.isEmpty(verCodeStr)){
            ToastUtil.toast("please input new version code");
            return;
        }
        String verName=anupe_name.getText().toString();
        if(TextUtils.isEmpty(verName)){
            ToastUtil.toast("please input new version name");
            return;
        }
        String verUrl=anupe_url.getText().toString();
        if(TextUtils.isEmpty(verUrl)){
            ToastUtil.toast("please input new version url");
            return;
        }
        String verPs=anupe_ps.getText().toString();
        if(TextUtils.isEmpty(verPs)){
            ToastUtil.toast("please input new version description");
            return;
        }
        ToastUtil.toast("release update notice");
        //发送更新信息到各个群
        int verCode=Integer.parseInt(verCodeStr);
        sendUpdateMsgToGroup(GameConstants.GROUP_ID_REVERSI,verCode,verName,verUrl,verPs);
        sendUpdateMsgToGroup(GameConstants.GROUP_ID_GOBANG,verCode,verName,verUrl,verPs);
        sendUpdateMsgToGroup(GameConstants.GROUP_ID_CNCHESS,verCode,verName,verUrl,verPs);
        sendUpdateMsgToGroup(GameConstants.GROUP_ID_CHESS,verCode,verName,verUrl,verPs);
        sendUpdateMsgToGroup(GameConstants.GROUP_ID_MOON,verCode,verName,verUrl,verPs);
    }

    private void sendUpdateMsgToGroup(String groupId,int verCode,String verName,String verUrl,String verPs){
        EMMessage message = EMMessage.createTxtSendMessage("*^_^* :新版本发布啦",groupId);
        message.setChatType(EMMessage.ChatType.GroupChat);
        message.setAttribute(PreferenceManager.NEW_VER_CODE,verCode);
        message.setAttribute(PreferenceManager.NEW_VER_NAME,verName);
        message.setAttribute(PreferenceManager.NEW_VER_URL,verUrl);
        message.setAttribute(PreferenceManager.NEW_VER_DESC,verPs);
        ///////////////添加用户头像和昵称
        message.setAttribute(EaseConstant.USER_NICK,PreferenceManager.getCurrentUserNick());
        message.setAttribute(EaseConstant.USER_AVATAR,PreferenceManager.getCurrentUserAvatar());
        ///////////////
        //send message
        EMClient.getInstance().chatManager().sendMessage(message);
    }
}
