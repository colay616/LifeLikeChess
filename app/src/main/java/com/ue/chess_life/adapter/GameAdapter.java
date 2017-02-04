package com.ue.chess_life.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.ui.ChatActivity;
import com.hyphenate.easeui.ui.LoginActivity;
import com.hyphenate.easeui.utils.EaseConstant;
import com.hyphenate.easeui.utils.GroupProxy;
import com.ue.chess_life.R;
import com.ue.chess_life.entity.GameItem;
import com.ue.chess_life.ui.GameRuleActivity;
import com.ue.chess_life.utils.AppConstants;
import com.ue.common.adapter.BaseListAdapter;
import com.ue.common.util.ToastUtil;
import com.ue.common.util.UIRouter;
import com.ue.common.util.ViewHolder;

import java.util.List;

/**
 * Created by hawk on 2016/12/3.
 */

public class GameAdapter extends BaseListAdapter<GameItem>{
    public GameAdapter(Context ctx, List<GameItem> list) {
        super(ctx, list);
    }

    @Override
    public View bindView(int position, View layoutView, ViewGroup viewGroup) {
        if(null==layoutView){
            layoutView=mInflater.inflate(R.layout.adt_game,null);
        }
        TextView igae_game_name= ViewHolder.getView(layoutView,R.id.igae_game_name);
        TextView igae_game_rule= ViewHolder.getView(layoutView, R.id.igae_game_rule);
        TextView igae_double_mode= ViewHolder.getView(layoutView, R.id.igae_double_mode);
        View igae_d_divider=ViewHolder.getView(layoutView,R.id.igae_d_divider);
        TextView igae_single_mode= ViewHolder.getView(layoutView,R.id.igae_single_mode);
        View igae_s_divider=ViewHolder.getView(layoutView,R.id.igae_s_divider);
        TextView igae_online_mode= ViewHolder.getView(layoutView,R.id.igae_online_mode);
        View igae_o_divider=ViewHolder.getView(layoutView,R.id.igae_o_divider);
        TextView igae_invite_mode= ViewHolder.getView(layoutView,R.id.igae_invite_mode);
        TextView igae_game_group= ViewHolder.getView(layoutView,R.id.igae_game_group);

        final GameItem gameItem=mList.get(position);

        igae_game_name.setText(gameItem.getGameName());
        int visibility=gameItem.isDoubleModeEnabled()?View.VISIBLE:View.GONE;
        igae_double_mode.setVisibility(visibility);
        igae_d_divider.setVisibility(visibility);

        visibility=gameItem.isSingleModeEnabled()?View.VISIBLE:View.GONE;
        igae_single_mode.setVisibility(visibility);
        igae_s_divider.setVisibility(visibility);

        visibility=gameItem.isOnlineModeEnabled()?View.VISIBLE:View.GONE;
        igae_online_mode.setVisibility(visibility);
        igae_o_divider.setVisibility(visibility);
        igae_invite_mode.setVisibility(gameItem.isInviteModeEnabled()?View.VISIBLE:View.GONE);

        igae_game_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1、判断是否已经登录
                if (!EMClient.getInstance().isLoggedInBefore()) {
                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                    return;
                }
                //2、加入群聊(对于违规用户通过环信网页加入黑名单，加入黑名单后就无法加入群聊了;或者禁用该帐户)
                    GroupProxy.joinGroup(gameItem.getGroupId(), new GroupProxy.onJoinGroupListener() {
                        @Override
                        public void onSuccess() {
                            mContext.startActivity(new Intent(mContext, ChatActivity.class).putExtra("chatType", EaseConstant.CHATTYPE_GROUP).putExtra("userId",gameItem.getGroupId()));
                        }
                        @Override
                        public void onFailure(final String msg) {
                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.toast("加群失败:"+msg);
                                }
                            });
                        }
                    });
            }
        });

        igae_game_rule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (gameItem.getGameFlag()){
                    case GameConstants.GAME_AP:
                        mContext.startActivity(new Intent(mContext, GameRuleActivity.class).putExtra(AppConstants.GAME_FLAG, GameConstants.GAME_AP));
                        break;
                    case GameConstants.GAME_GB:
                        mContext.startActivity(new Intent(mContext, GameRuleActivity.class).putExtra(AppConstants.GAME_FLAG, GameConstants.GAME_GB));
                        break;
                    case GameConstants.GAME_CN:
                        mContext.startActivity(new Intent(mContext, GameRuleActivity.class).putExtra(AppConstants.GAME_FLAG, GameConstants.GAME_CN));
                        break;
                    case GameConstants.GAME_IC:
                        mContext.startActivity(new Intent(mContext, GameRuleActivity.class).putExtra(AppConstants.GAME_FLAG, GameConstants.GAME_IC));
                        break;
                    case GameConstants.GAME_MC:
                        mContext.startActivity(new Intent(mContext, GameRuleActivity.class).putExtra(AppConstants.GAME_FLAG, GameConstants.GAME_MC));
                        break;
                    case GameConstants.GAME_ACA:
                        mContext.startActivity(new Intent(mContext, GameRuleActivity.class).putExtra(AppConstants.GAME_FLAG, GameConstants.GAME_ACA));
                        break;
                    case GameConstants.GAME_ACF:
                        mContext.startActivity(new Intent(mContext, GameRuleActivity.class).putExtra(AppConstants.GAME_FLAG, GameConstants.GAME_ACF));
                        break;
                    case GameConstants.GAME_ACM:
                        mContext.startActivity(new Intent(mContext, GameRuleActivity.class).putExtra(AppConstants.GAME_FLAG, GameConstants.GAME_ACM));
                        break;
                }
            }
        });

        igae_double_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (gameItem.getGameFlag()){
                    case GameConstants.GAME_AP:
                        UIRouter.startActivityForName(mContext,UIRouter.REVERSI_DOUBLE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_GB:
                        UIRouter.startActivityForName(mContext,UIRouter.GOBANG_DOUBLE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_CN:
                        UIRouter.startActivityForName(mContext,UIRouter.CNCHESS_DOUBLE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_IC:
                        UIRouter.startActivityForName(mContext,UIRouter.CHESS_DOUBLE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_MC:
                        UIRouter.startActivityForName(mContext,UIRouter.MOON_DOUBLE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_ACF:
                        UIRouter.startActivityForName(mContext,UIRouter.ACF_DOUBLE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_ACM:
                        UIRouter.startActivityForName(mContext,UIRouter.ACM_DOUBLE_MODE_ACTIVITY,null);
                        break;
                }
            }
        });

        igae_single_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (gameItem.getGameFlag()){
                    case GameConstants.GAME_AP:
                        UIRouter.startActivityForName(mContext,UIRouter.REVERSI_SINGLE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_GB:
                        UIRouter.startActivityForName(mContext,UIRouter.GOBANG_SINGLE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_CN:
                        UIRouter.startActivityForName(mContext,UIRouter.CNCHESS_SINGLE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_IC:
                        UIRouter.startActivityForName(mContext,UIRouter.CHESS_SINGLE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_MC:
                        UIRouter.startActivityForName(mContext,UIRouter.MOON_SINGLE_MODE_ACTIVITY,null);
                }
            }
        });

        igae_online_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1、判断是否已经登录
                if (!EMClient.getInstance().isLoggedInBefore()) {
                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                    return;
                }
                switch (gameItem.getGameFlag()){
                    case GameConstants.GAME_AP:
                        UIRouter.startActivityForName(mContext,UIRouter.REVERSI_ONLINE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_GB:
                        UIRouter.startActivityForName(mContext,UIRouter.GOBANG_ONLINE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_CN:
                        UIRouter.startActivityForName(mContext,UIRouter.CNCHESS_ONLINE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_IC:
                        UIRouter.startActivityForName(mContext,UIRouter.CHESS_ONLINE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_MC:
                        UIRouter.startActivityForName(mContext,UIRouter.MOON_ONLINE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_ACA:
                        UIRouter.startActivityForName(mContext,UIRouter.ACA_ONLINE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_ACF:
                        UIRouter.startActivityForName(mContext,UIRouter.ACF_ONLINE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_ACM:
                        UIRouter.startActivityForName(mContext,UIRouter.ACM_ONLINE_MODE_ACTIVITY,null);
                        break;
                }
            }
        });

        igae_invite_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1、判断是否已经登录
                if (!EMClient.getInstance().isLoggedInBefore()) {
                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                    return;
                }
                switch (gameItem.getGameFlag()){
                    case GameConstants.GAME_AP:
                        UIRouter.startActivityForName(mContext,UIRouter.REVERSI_INVITE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_GB:
                        UIRouter.startActivityForName(mContext,UIRouter.GOBANG_INVITE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_CN:
                        UIRouter.startActivityForName(mContext,UIRouter.CNCHESS_INVITE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_IC:
                        UIRouter.startActivityForName(mContext,UIRouter.CHESS_INVITE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_MC:
                        UIRouter.startActivityForName(mContext,UIRouter.MOON_INVITE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_ACA:
                        UIRouter.startActivityForName(mContext,UIRouter.ACA_INVITE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_ACF:
                        UIRouter.startActivityForName(mContext,UIRouter.ACF_INVITE_MODE_ACTIVITY,null);
                        break;
                    case GameConstants.GAME_ACM:
                        UIRouter.startActivityForName(mContext,UIRouter.ACM_INVITE_MODE_ACTIVITY,null);
                        break;
                }
            }
        });
        return layoutView;
    }
}
