package com.ue.chess_life.ui;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.DemoHelper;
import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.GameDialogs;
import com.hyphenate.easeui.game.GameUtil;
import com.hyphenate.easeui.ui.ConversationsActivity;
import com.hyphenate.easeui.ui.LoginActivity;
import com.hyphenate.easeui.ui.UserProfileActivity;
import com.hyphenate.easeui.utils.EaseConstant;
import com.hyphenate.easeui.utils.PreferenceManager;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.ue.chess_life.R;
import com.ue.chess_life.adapter.GameAdapter;
import com.ue.chess_life.entity.GameItem;
import com.ue.chess_life.utils.UpdateService;
import com.ue.common.util.ApkUtil;
import com.ue.common.util.DateUtils;
import com.ue.common.util.LogUtil;
import com.ue.common.util.PackageUtil;
import com.ue.common.util.ToastUtil;
import com.ue.common.util.UIRouter;

import java.util.ArrayList;
import java.util.List;

/**
 * 邀请通知、关于(带反馈)、个人资料
 *
 * banner:5010619550546934
 *
 */
public class MainActivity extends AppCompatActivity {
    private long enterTime;//用户进入该界面的时间，用作参考排除之前的透传消息影响
    private CompleteReceiver completeReceiver;
    private long downloadApkId;

    ViewGroup bannerContainer;
    BannerView bv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_main);

        enterTime = System.currentTimeMillis();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("人生如棋");
        toolbar.setNavigationIcon(R.drawable.slt_avatar);
        setSupportActionBar(toolbar);

        ListView aman_list = (ListView) findViewById(R.id.aman_list);
        GameAdapter gameAdapter = new GameAdapter(MainActivity.this, getGameItems());
        aman_list.setAdapter(gameAdapter);


        bannerContainer = (ViewGroup) this.findViewById(R.id.bannerContainer);
//        this.findViewById(R.id.refreshBanner).setOnClickListener(this);
//        this.findViewById(R.id.closeBanner).setOnClickListener(this);
        this.initBanner();
        this.bv.loadAD();

        startService(new Intent(MainActivity.this, UpdateService.class));
    }

    private void initBanner() {
        this.bv = new BannerView(this, ADSize.BANNER, "1105569037","5010619550546934");
        bv.setRefresh(30);
        bv.setADListener(new AbstractBannerADListener() {

            @Override
            public void onNoAD(int arg0) {
                LogUtil.i("AD_DEMO", "BannerNoAD，eCode=" + arg0);
            }

            @Override
            public void onADReceiv() {
                LogUtil.i("AD_DEMO", "ONBannerReceive");
            }
        });
        bannerContainer.addView(bv);
    }

    private List<GameItem> getGameItems() {
        List<GameItem> gameItemList = new ArrayList<>();
        gameItemList.add(new GameItem("五子棋", GameConstants.GAME_GB, true, true, true, true, GameConstants.GROUP_ID_GOBANG));
        gameItemList.add(new GameItem("走月光", GameConstants.GAME_MC, true, false, true, true, GameConstants.GROUP_ID_MOON));
        gameItemList.add(new GameItem("黑白棋", GameConstants.GAME_AP, true, true, true, true, GameConstants.GROUP_ID_REVERSI));
        gameItemList.add(new GameItem("中国象棋", GameConstants.GAME_CN, true, true, true, true, GameConstants.GROUP_ID_CNCHESS));
        gameItemList.add(new GameItem("国际象棋", GameConstants.GAME_IC, true, false, true, true, GameConstants.GROUP_ID_CHESS));
        gameItemList.add(new GameItem("军棋-翻棋", GameConstants.GAME_ACF, true, false, false, false, GameConstants.GROUP_ID_ARMY));
        gameItemList.add(new GameItem("军棋-暗棋", GameConstants.GAME_ACA, false, false, true, true, GameConstants.GROUP_ID_ARMY));
        gameItemList.add(new GameItem("军棋-明棋", GameConstants.GAME_ACM, true, false, true, true, GameConstants.GROUP_ID_ARMY));
        return gameItemList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //判断是否有更新
        int newVerCode = PreferenceManager.getNewVerCode();
        int curVerCode = PackageUtil.getVersionCode(MainActivity.this);
        if (newVerCode <= curVerCode) {
            menu.findItem(R.id.menu_main_update).setVisible(false);
        } else {
            menu.findItem(R.id.menu_main_update).setVisible(true);
            completeReceiver = new CompleteReceiver();
            /** register download success broadcast **/
            registerReceiver(completeReceiver,
                    new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                if (!EMClient.getInstance().isLoggedInBefore()) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    return true;
                }
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class)
                        .putExtra(EaseConstant.USER_NAME, EMClient.getInstance().getCurrentUser());
                startActivity(intent);
                break;
            case R.id.menu_main_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case R.id.menu_main_update:
                //弹出新版本信息，用户可点击下载更新
                if(TextUtils.isEmpty(PreferenceManager.getNewVerUrl())){
                    item.setVisible(false);
                    return true;
                }
                String newVerDesc=PreferenceManager.getNewVerDesc();
                String[]desc=newVerDesc.split(";");
                StringBuffer buffer=new StringBuffer()
                        .append("新版本：")
                        .append(PreferenceManager.getNewVerName())
                        .append("\n更新内容：\n");
                for(int i=0,len=desc.length;i<len;i++){
                    buffer.append(desc[i]).append("\n");
                }
                GameDialogs.showUpdateApkDialog(MainActivity.this, buffer.toString(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String apkName=String.format("UeChessLife-%s.apk",PreferenceManager.getNewVerName());
                        downloadApkId= ApkUtil.downloadApk(MainActivity.this,apkName,"正在下载...",PreferenceManager.getNewVerUrl(),apkName);
                    }
                });
                break;
            case R.id.menu_main_msg:
                startActivity(new Intent(MainActivity.this, ConversationsActivity.class));
                break;
        }
        return true;
    }

    private EMMessageListener mEMMessageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> list) {
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {

        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {
            for (EMMessage emMessage : list) {
                if (emMessage.getFrom().equals(EMClient.getInstance().getCurrentUser())) {
                    continue;
                }
                if (emMessage.getMsgTime() < enterTime) {//过滤以前的透传消息
                    continue;
                }
                if (System.currentTimeMillis() - emMessage.getMsgTime() >= 10000) {//邀请超时
                    continue;
                }
                onCmdMsgReceived(emMessage);
                break;//只处理一个符合条件的消息
            }
        }
    };

    private String activityName;

    private void onCmdMsgReceived(final EMMessage emMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EMCmdMessageBody cmdMessageBody = (EMCmdMessageBody) emMessage.getBody();
                String action = cmdMessageBody.action();
                if (action.equals(GameConstants.ACTION_INVITE)) {//收到邀请
                    String gameName = "";
                    activityName = null;
                    int gameFlag = emMessage.getIntAttribute(GameConstants.WHICH_GAME, -1);
                    switch (gameFlag) {
                        case GameConstants.GAME_IC:
                            gameName = "国际象棋";
                            activityName = UIRouter.CHESS_INVITE_MODE_ACTIVITY;
                            break;
                        case GameConstants.GAME_CN:
                            gameName = "中国象棋";
                            activityName = UIRouter.CNCHESS_INVITE_MODE_ACTIVITY;
                            break;
                        case GameConstants.GAME_GB:
                            gameName = "五子棋";
                            activityName = UIRouter.GOBANG_INVITE_MODE_ACTIVITY;
                            break;
                        case GameConstants.GAME_AP:
                            gameName = "黑白棋";
                            activityName = UIRouter.REVERSI_INVITE_MODE_ACTIVITY;
                            break;
                        case GameConstants.GAME_MC:
                            gameName = "走月光";
                            activityName = UIRouter.MOON_INVITE_MODE_ACTIVITY;
                            break;
                        case GameConstants.GAME_ACA:
                            gameName = "军棋-暗棋";
                            activityName = UIRouter.ACA_INVITE_MODE_ACTIVITY;
                            break;
                        case GameConstants.GAME_ACF:
                            gameName = "军棋-翻棋";
                            activityName = UIRouter.ACF_INVITE_MODE_ACTIVITY;
                            break;
                        case GameConstants.GAME_ACM:
                            gameName = "军棋-明棋";
                            activityName = UIRouter.ACM_INVITE_MODE_ACTIVITY;
                            break;
                        default:
                            gameName = "[???](未知游戏,请检查版本是否已更新)";
                            activityName = null;
                    }

                    String timeStr = DateUtils.getFormatTime(emMessage.getMsgTime(), DateUtils.FORMAT_SHORT_DATETIME);
                    String msg = String.format("[%s] %s 邀你一起来玩 %s", timeStr, emMessage.getFrom(), gameName);
                    GameDialogs.showGotInvitationDialog(MainActivity.this, msg, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (System.currentTimeMillis() - emMessage.getMsgTime() > 8000) {
                                ToastUtil.toast("邀请已失效");
                                return;
                            }
                            if (TextUtils.isEmpty(activityName)) {
                                ToastUtil.toast("*^_^* : 升级到新版本试试吧.");
                                return;
                            }
                            //接受
                            GameUtil.sendCMDMessage(emMessage.getFrom(), GameConstants.ACTION_ACCEPT, null);
                            Bundle arguments = new Bundle();
                            arguments.putString(GameConstants.OPPO_USER_NAME, emMessage.getFrom());
                            UIRouter.startActivityForName(MainActivity.this, activityName, arguments);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        DemoHelper.getInstance().pushActivity(this);
        EMClient.getInstance().chatManager().addMessageListener(mEMMessageListener);
//        if (bv == null) {
//            initBanner();
//        }
//        bv.loadAD();
    }

    @Override
    protected void onStop() {
        EMClient.getInstance().chatManager().removeMessageListener(mEMMessageListener);
        DemoHelper.getInstance().popActivity(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=completeReceiver){
            unregisterReceiver(completeReceiver);
        }
    }

    class CompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // get complete download id
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            // to do here
            if(downloadApkId==completeDownloadId){
                DownloadManager dManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                Intent install = new Intent(Intent.ACTION_VIEW);
                Uri downloadFileUri = dManager.getUriForDownloadedFile(downloadApkId);
                install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(install);
            }
        }
    }
}
