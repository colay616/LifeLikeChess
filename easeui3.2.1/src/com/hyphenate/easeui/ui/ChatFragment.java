package com.hyphenate.easeui.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.utils.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.domain.EmojiconExampleGroupData;
import com.hyphenate.easeui.ui.EaseChatFragment.EaseChatFragmentHelper;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenu;
import com.ue.common.widget.dialog.MDSelectionDialog;
import com.ue.common.widget.dialog.DialogOnItemClickListener;

import java.util.List;
import java.util.ArrayList;

public class ChatFragment extends EaseChatFragment implements EaseChatFragmentHelper {
    private static final int REQUEST_CODE_GROUP_DETAIL = 13;
    private static final int REQUEST_CODE_SELECT_AT_USER = 15;
    private MDSelectionDialog contextMenu;

    /**
     * if it is chatBot
     */
//    private boolean isRobot;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void setUpView() {
        setChatFragmentListener(this);
        super.setUpView();

        ((EaseEmojiconMenu) inputMenu.getEmojiconMenu()).addEmojiconGroup(EmojiconExampleGroupData.getData());
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            inputMenu.getPrimaryMenu().getEditText().addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (count == 1 && "@".equals(String.valueOf(s.charAt(start)))) {
                        startActivityForResult(new Intent(getActivity(), PickAtUserActivity.class).
                                putExtra("groupId", toChatUsername), REQUEST_CODE_SELECT_AT_USER);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_AT_USER:
                    if (data != null) {
                        String username = data.getStringExtra("username");
                        inputAtUsername(username, false);
                    }
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void onSetMessageAttributes(EMMessage message) {
    }

    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        return new CustomChatRowProvider();
    }


    @Override
    public void onEnterToChatDetails() {
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
            if (group == null) {
                Toast.makeText(getActivity(), R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
                return;
            }
            startActivityForResult(
                    (new Intent(getActivity(), GroupDetailsActivity.class).putExtra("groupId", toChatUsername)),
                    REQUEST_CODE_GROUP_DETAIL);
        } else if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
//        	startActivityForResult(new Intent(getActivity(), ChatRoomDetailsActivity.class).putExtra("roomId", toChatUsername), REQUEST_CODE_GROUP_DETAIL);
        }
    }

    @Override
    public void onAvatarClick(String username) {
        //handling when user click avatar
        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
        intent.putExtra(EaseConstant.USER_NAME, username);
        startActivity(intent);
    }

    @Override
    public void onAvatarLongClick(String username) {
        inputAtUsername(username);
    }


    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        //消息框点击事件，demo这里不做覆盖，如需覆盖，return true
        return false;
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        super.onCmdMessageReceived(messages);
    }

    @Override
    public void onMessageBubbleLongClick(EMMessage message) {
        showContextMenu();
    }

    private void showContextMenu() {
        if (null == contextMenu) {
            contextMenu = new MDSelectionDialog.Builder(getContext())
                    .setItemHeight(45)  //设置item的高度
                    .setItemWidth(0.8f)  //屏幕宽度*0.9
                    .setItemTextSize(16)  //设置item字体大小
                    .setOnItemListener(new DialogOnItemClickListener() {
                        @Override
                        public void onItemClick(Button button, int position) {
                            contextMenu.dismiss();
                            if (position == 0) {
                                clipboard.setPrimaryClip(ClipData.newPlainText(null,
                                        ((EMTextMessageBody) contextMenuMessage.getBody()).getMessage()));
                            } else if (position == 1) {
                                conversation.removeMessage(contextMenuMessage.getMsgId());
                                messageList.refresh();
                            }
                        }
                    })
                    .setCanceledOnTouchOutside(true)  //设置是否可点击其他地方取消dialog
                    .build();
            ArrayList<String> s = new ArrayList<>();
            s.add("复制");
            s.add("删除");
            contextMenu.setDataList(s);
        }
        if (!contextMenu.isShowing()) {
            //BadTokenException,is your activity running?
            if (!((Activity) contextMenu.getContext()).isFinishing()) {
                contextMenu.show();
            }
        }
    }

    /**
     * chat row provider
     */
    private final class CustomChatRowProvider implements EaseCustomChatRowProvider {
        @Override
        public int getCustomChatRowTypeCount() {
            //here the number is the message type in EMMessage::Type
            //which is used to count the number of different chat row
            return 10;
        }

        @Override
        public int getCustomChatRowType(EMMessage message) {
            if (message.getType() == EMMessage.Type.TXT) {
            }
            return 0;
        }

        @Override
        public EaseChatRow getCustomChatRow(EMMessage message, int position, BaseAdapter adapter) {
            if (message.getType() == EMMessage.Type.TXT) {
            }
            return null;
        }

    }

}
