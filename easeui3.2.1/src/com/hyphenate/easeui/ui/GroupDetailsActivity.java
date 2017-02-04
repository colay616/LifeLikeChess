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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.utils.EaseConstant;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseExpandGridView;
import com.hyphenate.exceptions.HyphenateException;
import com.ue.common.util.ToastUtil;
import com.ue.common.widget.dialog.NormalAlertDialog;

import java.util.ArrayList;
import java.util.List;

public class GroupDetailsActivity extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "GroupDetailsActivity";

    private String groupId;
    private ProgressBar loadingPB;
    private EMGroup group;
    private GridAdapter adapter;

    public static GroupDetailsActivity instance;

    String st = "";

    private NormalAlertDialog clearMessagesDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        groupId = getIntent().getStringExtra("groupId");
        group = EMClient.getInstance().groupManager().getGroup(groupId);

        // we are not supposed to show the group if we don't find the group
        if (group == null) {
            finish();
            return;
        }

        setContentView(R.layout.em_activity_group_details);
        instance = this;
        st = getResources().getString(R.string.people);
        RelativeLayout clearAllHistory = (RelativeLayout) findViewById(R.id.clear_all_history);
        EaseExpandGridView userGridview = (EaseExpandGridView) findViewById(R.id.gridview);
        loadingPB = (ProgressBar) findViewById(R.id.progressBar);
        RelativeLayout blacklistLayout = (RelativeLayout) findViewById(R.id.rl_blacklist);
        RelativeLayout changeGroupNameLayout = (RelativeLayout) findViewById(R.id.rl_change_group_name);
        RelativeLayout idLayout = (RelativeLayout) findViewById(R.id.rl_group_id);
        idLayout.setVisibility(View.VISIBLE);
        TextView idText = (TextView) findViewById(R.id.tv_group_id_value);

        idText.setText(groupId);
        if (TextUtils.isEmpty(group.getOwner())
                || !group.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
            blacklistLayout.setVisibility(View.GONE);
            changeGroupNameLayout.setVisibility(View.GONE);
        }

        GroupChangeListener groupChangeListener = new GroupChangeListener();
        EMClient.getInstance().groupManager().addGroupChangeListener(groupChangeListener);

        ((TextView) findViewById(R.id.group_name)).setText(group.getGroupName() + "(" + group.getAffiliationsCount() + st);

        List<String> members = new ArrayList<String>();
        members.addAll(group.getMembers());

        adapter = new GridAdapter(this, R.layout.em_grid, members);
        userGridview.setAdapter(adapter);

        // 保证每次进详情看到的都是最新的group
        updateGroup();

        clearAllHistory.setOnClickListener(this);
        blacklistLayout.setOnClickListener(this);
        changeGroupNameLayout.setOnClickListener(this);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        String st1 = getResources().getString(R.string.being_added);
//        String st5 = getResources().getString(R.string.is_modify_the_group_name);
//        final String st6 = getResources().getString(R.string.Modify_the_group_name_successful);
//        final String st7 = getResources().getString(R.string.change_the_group_name_failed_please);
//
//        if (resultCode == RESULT_OK) {
//            if (progressDialog == null) {
//                progressDialog = new ProgressDialog(GroupDetailsActivity.this);
//                progressDialog.setMessage(st1);
//                progressDialog.setCanceledOnTouchOutside(false);
//            }
//        }
//    }

    protected void addUserToBlackList(final String username) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(getString(R.string.Are_moving_to_blacklist));
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMClient.getInstance().groupManager().blockUser(groupId, username);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            refreshMembers();
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.Move_into_blacklist_success, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.failed_to_move_into, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void refreshMembers() {
        adapter.clear();
        List<String> members = new ArrayList<String>();
        members.addAll(group.getMembers());
        adapter.addAll(members);

        adapter.notifyDataSetChanged();
    }

    /**
     * 清空群聊天记录
     */
    private void clearGroupHistory() {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(group.getGroupId(), EMConversationType.GroupChat);
        if (conversation != null) {
            conversation.clearAllMessages();
        }
        ToastUtil.toast("已清空聊天记录");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.clear_all_history) {
            if (null == clearMessagesDialog) {
                clearMessagesDialog = new NormalAlertDialog.Builder(GroupDetailsActivity.this)
                        .setCanceledOnTouchOutside(false)
                        .setTitleText("提示")
                        .setContentText("确定清空此群的聊天记录吗?")
                        .setLeftButton("取消",null)
                        .setRightButton("确定", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clearGroupHistory();
                            }
                        })
                        .build();
            }
            clearMessagesDialog.show();
        }
    }

    /**
     * 群组成员gridadapter
     *
     * @author admin_new
     */
    private class GridAdapter extends ArrayAdapter<String> {

        private int res;

        public GridAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            res = textViewResourceId;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(res, null);
                holder.imageView = (ImageView) convertView.findViewById(R.id.iv_avatar);
                holder.textView = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final LinearLayout button = (LinearLayout) convertView.findViewById(R.id.button_avatar);
                final String username = getItem(position);
                convertView.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                EaseUserUtils.setUserNick(username, holder.textView);
                EaseUserUtils.setUserAvatar(getContext(), username, holder.imageView);
                button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 正常情况下点击user，进入用户详情页面
                        startActivity(new Intent(GroupDetailsActivity.this, UserProfileActivity.class).putExtra(EaseConstant.USER_NAME, username));
                    }
                });

                button.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        if (EMClient.getInstance().getCurrentUser().equals(username)) {
                            return true;
                        }
                        if (group.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
                            showAddToBlackListDialog(username);
                        }
                        return false;
                    }
                });
            return convertView;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }
    }

    private void showAddToBlackListDialog(final String username) {
         new NormalAlertDialog.Builder(GroupDetailsActivity.this)
                 .setCanceledOnTouchOutside(true)
                 .setTitleText("提示")
                 .setContentText("确定将此成员加入至此群黑名单吗?")
                 .setLeftButton("取消",null)
                 .setRightButton("确定", new OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         addUserToBlackList(username);
                     }
                 })
                .build().show();
    }

    protected void updateGroup() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMClient.getInstance().groupManager().getGroupFromServer(groupId);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            ((TextView) findViewById(R.id.group_name)).setText(group.getGroupName() + "(" + group.getAffiliationsCount()
                                    + ")");
                            loadingPB.setVisibility(View.INVISIBLE);
                            refreshMembers();
                        }
                    });

                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            loadingPB.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        }).start();
    }

    public void back(View view) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }

    private class GroupChangeListener implements EMGroupChangeListener {

        @Override
        public void onInvitationReceived(String groupId, String groupName,
                                         String inviter, String reason) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onApplicationReceived(String groupId, String groupName,
                                          String applyer, String reason) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onApplicationAccept(String groupId, String groupName,
                                        String accepter) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onApplicationDeclined(String groupId, String groupName,
                                          String decliner, String reason) {

        }

        @Override
        public void onInvitationAccepted(String groupId, String inviter, String reason) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    refreshMembers();
                }

            });

        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee,
                                         String reason) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            finish();

        }

        @Override
        public void onGroupDestroyed(String groupId, String groupName) {
            finish();

        }

        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
            // TODO Auto-generated method stub

        }

    }

}
