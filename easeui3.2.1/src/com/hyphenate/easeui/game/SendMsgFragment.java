package com.hyphenate.easeui.game;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.model.EaseDefaultEmojiconDatas;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenu;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenuBase;
import com.ue.common.util.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Reference:
 * Author:
 * Date:2016/9/3.
 */
public class SendMsgFragment extends DialogFragment implements View.OnClickListener{
    private String opponentUserName;
    private EditText game_chat_input;
    private EaseEmojiconMenuBase emojiconMenu;
    private InputMethodManager inputManager;
    private ImageView game_chat_emoji;
    private int deviation=200;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, -1);
    }

    public void setOpponentUserName(String opponentUserName) {
        this.opponentUserName = opponentUserName;
    }

    public void setDeviation(int deviation){
        this.deviation=deviation;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View layoutView = inflater.inflate(R.layout.fragment_send_game_msg, null);
        game_chat_input = (EditText) layoutView.findViewById(R.id.game_chat_input);
        emojiconMenu = (EaseEmojiconMenu) layoutView.findViewById(R.id.game_chat_emoji_panel);
        game_chat_emoji = (ImageView) layoutView.findViewById(R.id.game_chat_emoji);

        initEmojiMenu(null);

        game_chat_input.setOnClickListener(this);
        layoutView.findViewById(R.id.game_chat_rest).setOnClickListener(this);
        layoutView.findViewById(R.id.game_chat_send).setOnClickListener(this);
        game_chat_emoji.setOnClickListener(this);
        return layoutView;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.game_chat_rest) {
            dismiss();
        }else if (viewId == R.id.game_chat_input) {
            if (null != emojiconMenu && emojiconMenu.isShown()) {
                toggleEmojicon();
            }
            setKeyboardVisible(true);
        }else if (viewId == R.id.game_chat_send) {
            String txt = game_chat_input.getText().toString().trim();
            if (TextUtils.isEmpty(txt)) {
                game_chat_input.setText("");
                return;
            }
            Spannable spanTxt = EaseSmileUtils.getSmiledText(getContext(), txt);
            ToastUtil.showChatToast(getContext(), spanTxt, true,deviation);
            game_chat_input.setText("");

            EMMessage msg = EMMessage.createTxtSendMessage(txt, opponentUserName);
            msg.setAttribute(GameConstants.ATTR_GAME_CHAT, "y");
            EMClient.getInstance().chatManager().sendMessage(msg);
        }else if (viewId == R.id.game_chat_emoji) {
            setKeyboardVisible(false);
            toggleEmojicon();
        }
    }

    private void initEmojiMenu(List<EaseEmojiconGroupEntity> emojiconGroupList) {
        // emojicon menu, use default if no customized one
        if (emojiconGroupList == null) {
            emojiconGroupList = new ArrayList<>();
            emojiconGroupList.add(new EaseEmojiconGroupEntity(R.drawable.ee_1, Arrays.asList(EaseDefaultEmojiconDatas.getData())));
        }
        ((EaseEmojiconMenu) emojiconMenu).init(emojiconGroupList);
        emojiconMenu.setEmojiconMenuListener(new EaseEmojiconMenuBase.EaseEmojiconMenuListener() {
            @Override
            public void onExpressionClicked(EaseEmojicon emojicon) {
                if (emojicon.getType() != EaseEmojicon.Type.BIG_EXPRESSION) {
                    if (emojicon.getEmojiText() != null) {
                        game_chat_input.append(EaseSmileUtils.getSmiledText(getContext(), emojicon.getEmojiText()));
                    }
                }
            }

            @Override
            public void onDeleteImageClicked() {
                if (!TextUtils.isEmpty(game_chat_input.getText())) {
                    KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                    game_chat_input.dispatchKeyEvent(event);
                }
            }
        });
    }

    public boolean isEmojiShowing() {
        return emojiconMenu.getVisibility() == View.VISIBLE;
    }

    /**
     * show or hide emojicon
     */
    public void toggleEmojicon() {
        if (emojiconMenu.getVisibility() == View.VISIBLE) {
            emojiconMenu.setVisibility(View.GONE);
            game_chat_emoji.setBackgroundResource(R.drawable.ease_chatting_biaoqing_btn_normal);
        } else {
            emojiconMenu.setVisibility(View.VISIBLE);
            game_chat_emoji.setBackgroundResource(R.drawable.ease_chatting_biaoqing_btn_enable);
        }
    }

    private void setKeyboardVisible(boolean isVisible) {
        if (null == inputManager) {
            inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (isVisible) {
            inputManager.showSoftInput(game_chat_input, 0);
        } else {
            if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                inputManager.hideSoftInputFromWindow(game_chat_input.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

//    1、//隐藏软键盘
//            ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(WidgetSearchActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//
//    2、//显示软键盘,控件ID可以是EditText,TextView
//            ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(控件ID, 0);
}