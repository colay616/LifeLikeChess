package com.hyphenate.easeui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.model.EaseDefaultEmojiconDatas;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.widget.EaseChatPrimaryMenuBase.EaseChatPrimaryMenuListener;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenu;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenuBase;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenuBase.EaseEmojiconMenuListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * input menu
 * 
 * including below component:
 *    EaseChatPrimaryMenu: main menu bar, text input, send button
 *    EaseChatExtendMenu: grid menu with image, file, location, etc
 *    EaseEmojiconMenu: emoji icons
 */
public class EaseChatInputMenu extends LinearLayout {
    FrameLayout primaryMenuContainer, emojiconMenuContainer;
    protected EaseChatPrimaryMenuBase chatPrimaryMenu;
    protected EaseEmojiconMenuBase emojiconMenu;
    protected LayoutInflater layoutInflater;

    private Handler handler = new Handler();
    private ChatInputMenuListener listener;
    private Context context;
    private boolean inited;

    public EaseChatInputMenu(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public EaseChatInputMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EaseChatInputMenu(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.ease_widget_chat_input_menu, this);
        primaryMenuContainer = (FrameLayout) findViewById(R.id.primary_menu_container);
        emojiconMenuContainer = (FrameLayout) findViewById(R.id.emojicon_menu_container);
    }

    /**
     * init view 
     * 
     * This method should be called after registerExtendMenuItem(), setCustomEmojiconMenu() and setCustomPrimaryMenu().
     * @param emojiconGroupList --will use default if null
     */
    @SuppressLint("InflateParams")
    public void init(List<EaseEmojiconGroupEntity> emojiconGroupList) {
        if(inited){
            return;
        }
        // primary menu, use default if no customized one
        if(chatPrimaryMenu == null){
            chatPrimaryMenu = (EaseChatPrimaryMenu) layoutInflater.inflate(R.layout.ease_layout_chat_primary_menu, null);
        }
        primaryMenuContainer.addView(chatPrimaryMenu);

        // emojicon menu, use default if no customized one
        if(emojiconMenu == null){
            emojiconMenu = (EaseEmojiconMenu) layoutInflater.inflate(R.layout.ease_layout_emojicon_menu, null);
            if(emojiconGroupList == null){
                emojiconGroupList = new ArrayList<EaseEmojiconGroupEntity>();
                emojiconGroupList.add(new EaseEmojiconGroupEntity(R.drawable.ee_1,  Arrays.asList(EaseDefaultEmojiconDatas.getData())));
            }
            ((EaseEmojiconMenu)emojiconMenu).init(emojiconGroupList);
        }
        emojiconMenu.setVisibility(View.GONE);
        emojiconMenuContainer.addView(emojiconMenu);

        processChatMenu();

        inited = true;
    }
    
    public void init(){
        init(null);
    }
    
    /**
     * set custom emojicon menu
     * @param customEmojiconMenu
     */
    public void setCustomEmojiconMenu(EaseEmojiconMenuBase customEmojiconMenu){
        this.emojiconMenu = customEmojiconMenu;
    }
    
    /**
     * set custom primary menu
     * @param customPrimaryMenu
     */
    public void setCustomPrimaryMenu(EaseChatPrimaryMenuBase customPrimaryMenu){
        this.chatPrimaryMenu = customPrimaryMenu;
    }
    
    public EaseChatPrimaryMenuBase getPrimaryMenu(){
        return chatPrimaryMenu;
    }
    
    public EaseEmojiconMenuBase getEmojiconMenu(){
        return emojiconMenu;
    }
    
    protected void processChatMenu() {
        // send message button
        chatPrimaryMenu.setChatPrimaryMenuListener(new EaseChatPrimaryMenuListener() {

            @Override
            public void onSendBtnClicked(String content) {
                if (listener != null)
                    listener.onSendMessage(content);
            }

            @Override
            public void onToggleEmojiconClicked() {
                toggleEmojicon();
            }

            @Override
            public void onEditTextClicked() {
                emojiconMenu.setVisibility(View.GONE);
            }
        });

        // emojicon menu
        emojiconMenu.setEmojiconMenuListener(new EaseEmojiconMenuListener() {

            @Override
            public void onExpressionClicked(EaseEmojicon emojicon) {
                if(emojicon.getType() != EaseEmojicon.Type.BIG_EXPRESSION){
                    if(emojicon.getEmojiText() != null){
                        chatPrimaryMenu.onEmojiconInputEvent(EaseSmileUtils.getSmiledText(context,emojicon.getEmojiText()));
                    }
                }else{
                    if(listener != null){
                        listener.onBigExpressionClicked(emojicon);
                    }
                }
            }

            @Override
            public void onDeleteImageClicked() {
                chatPrimaryMenu.onEmojiconDeleteEvent();
            }
        });

    }
    
   
    /**
     * insert text
     * @param text
     */
    public void insertText(String text){
        getPrimaryMenu().onTextInsert(text);
    }

    /**
     * show or hide emojicon
     */
    protected void toggleEmojicon() {
        if (emojiconMenu.getVisibility() == View.VISIBLE) {
            emojiconMenu.setVisibility(View.GONE);
        } else {
            handler.postDelayed(new Runnable() {
                    public void run() {
                        hideKeyboard();
                        emojiconMenu.setVisibility(View.VISIBLE);
                    }
                }, 50);
        }
        }

    /**
     * hide keyboard
     */
    private void hideKeyboard() {
        chatPrimaryMenu.hideKeyboard();
    }

    /**
     * when back key pressed
     * 
     * @return false--extend menu is on, will hide it first
     *         true --extend menu is off 
     */
    public boolean onBackPressed() {
        if(emojiconMenu.getVisibility() == View.VISIBLE){
            emojiconMenu.setVisibility(View.GONE);
            return false;
        }else{
            return true;
        }

    }
    

    public void setChatInputMenuListener(ChatInputMenuListener listener) {
        this.listener = listener;
    }

    public interface ChatInputMenuListener {
        /**
         * when send message button pressed
         * 
         * @param content
         *            message content
         */
        void onSendMessage(String content);
        
        /**
         * when big icon pressed
         * @param emojicon
         */
        void onBigExpressionClicked(EaseEmojicon emojicon);
    }
    
}
