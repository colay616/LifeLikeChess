package com.ue.chess_life.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.ui.ChatActivity;
import com.hyphenate.easeui.ui.LoginActivity;
import com.hyphenate.easeui.utils.EaseConstant;
import com.ue.chess_life.R;
import com.ue.common.util.PackageUtil;
import com.ue.common.util.ToastUtil;


/**
 * Reference:
 * Author:
 * Date:2016/10/8.
 */
public class AboutActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String xyStoreAuth = "复制这条信息，打开手机淘宝即可看到【夏樱牛仔小C店】￥AAJXXJyz￥http://e22a.com/h.0XKrbI?cv=AAJXXJyz&sm=ebf944";
    private static final String xyStoreLink = "https://shop150959835.taobao.com/?spm=a230r.7195193.1997079397.2.ozsaXC";
    private static final String yyStoreAuth = "复制这条信息，打开手机淘宝即可看到【衣衣不舍之阳光衣橱】￥AAJhAgxf￥http://e22a.com/h.0W9YNe?cv=AAJhAgxf&sm=eb73e4";
    private static final String yyStoreLink = "https://shop104638463.taobao.com/?spm=a230r.7195193.1997079397.2.lrkTSy";
    private static final String officialWebsite="http://android.myapp.com/myapp/detail.htm?apkName=com.ue.chess_life";
    private Button aabt_new_release;
    private int feedbackId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_about);

        setTitle("关于");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.slt_actionbar_back);

        TextView aabt_about = (TextView) findViewById(R.id.aabt_about);
        String aboutTxt = getResources().getString(R.string.about_txt);
        aabt_about.setText(String.format(aboutTxt, PackageUtil.getVersionName(AboutActivity.this)));
        findViewById(R.id.aabt_taobao_xy).setOnClickListener(this);
        findViewById(R.id.aabt_taobao_yy).setOnClickListener(this);
        findViewById(R.id.aabt_url).setOnClickListener(this);

        aabt_new_release = (Button) findViewById(R.id.aabt_new_release);
        if (EMClient.getInstance().getCurrentUser().equals("admin")) {
            aabt_new_release.setVisibility(View.VISIBLE);
            aabt_new_release.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(AboutActivity.this, NoticeUpdateAty.class));
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.aabt_taobao_xy) {
            ToastUtil.toast("传送门已经开启，请稍等...");
            openTaobao(xyStoreAuth, xyStoreLink);
            return;
        }
        if (viewId == R.id.aabt_taobao_yy) {
            ToastUtil.toast("传送门已经开启，请稍等...");
            openTaobao(yyStoreAuth, yyStoreLink);
            return;
        }
        if(viewId==R.id.aabt_url){
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri uri = Uri.parse(officialWebsite);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    /**
     * 如果安装了淘宝app，就复制店铺口令，然后打开淘宝app
     * 如果没有安装淘宝app，直接用浏览器打开店铺链接
     *
     * @param storeAuth
     * @param storeLink
     */
    private void openTaobao(String storeAuth, String storeLink) {
        // 通过包名获取要跳转的app，创建intent对象
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.taobao.taobao");
        if (intent != null) {//安装了淘宝
            // 得到剪贴板管理器
            ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setPrimaryClip(ClipData.newPlainText("label", storeAuth));
            startActivity(intent);
        } else {
            intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri uri = Uri.parse(storeLink);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem feedbackItem=menu.add("反馈");
        feedbackItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        feedbackId = feedbackItem.getItemId();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        if (itemId == feedbackId) {
            if (!EMClient.getInstance().isLoggedInBefore()) {
                ToastUtil.toast("*^_^* <登录后才能提交反馈内容>");
                startActivity(new Intent(AboutActivity.this, LoginActivity.class));
                return true;
            }
            //如果不是管理员，跳转到与管理员的聊天页面进行反馈
            if (!EMClient.getInstance().getCurrentUser().equals("admin")) {
                startActivity(new Intent(AboutActivity.this, ChatActivity.class).putExtra("chatType", EaseConstant.CHATTYPE_SINGLE).putExtra("userId", "admin"));
            }
        }
        return true;
    }
}