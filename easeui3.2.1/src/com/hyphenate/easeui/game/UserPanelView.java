package com.hyphenate.easeui.game;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.easeui.R;

/**
 * Created by hawk on 2016/12/24.
 */

public class UserPanelView extends FrameLayout{
    private static final int TYPE_LAYOUT_REVERSI=1;
    private static final int TYPE_LAYOUT_ARMY=2;
    private LinearLayout game_my_panel;
    private TextView game_my_name;
    private ImageView game_my_image;
    private LinearLayout game_oppo_panel;
    private TextView game_oppo_name;
    private ImageView game_oppo_image;
    private TextView myExtraTxt;
    private TextView oppoExtraTxt;

    public UserPanelView(Context context) {
        super(context);
        init(context,null);
    }

    public UserPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs){
        int typeLayout=0;
        if(attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.UserPanelView);
            if(ta.hasValue(R.styleable.UserPanelView_typeLayout)){
                typeLayout=ta.getInt(R.styleable.UserPanelView_typeLayout,0);
                ta.recycle();
            }
        }
        int layoutRes;
        if(typeLayout==TYPE_LAYOUT_REVERSI){
            layoutRes=R.layout.rv_user_panel;
        }else if(typeLayout==TYPE_LAYOUT_ARMY){
            layoutRes=R.layout.am_user_panel;
        }else{
            layoutRes=R.layout.common_user_panel;
        }
        LayoutInflater.from(context).inflate(layoutRes,this);
        game_my_panel= (LinearLayout) findViewById(R.id.game_my_panel);
        game_my_name= (TextView) findViewById(R.id.game_my_name);
        game_my_image= (ImageView) findViewById(R.id.game_my_image);
        game_oppo_panel= (LinearLayout) findViewById(R.id.game_oppo_panel);
        game_oppo_name= (TextView) findViewById(R.id.game_oppo_name);
        game_oppo_image= (ImageView) findViewById(R.id.game_oppo_image);
        if(typeLayout==TYPE_LAYOUT_REVERSI){
            myExtraTxt= (TextView) findViewById(R.id.game_my_chesses);
            oppoExtraTxt= (TextView) findViewById(R.id.game_oppo_chesses);
        }
    }

    public void setMyName(String myName){
        game_my_name.setText(myName);
    }
    public void setMyImage(int myImage){
        game_my_image.setImageResource(myImage);
    }
    public void setOppoName(String oppoName){
        game_oppo_name.setText(oppoName);
    }
    public void setOppoImage(int oppoImage){
        game_oppo_image.setImageResource(oppoImage);
    }
    public void setMyExtraTxt(String txt){if(null!=myExtraTxt){myExtraTxt.setText(txt);}}
    public void setOppoExtraTxt(String txt){if(null!=oppoExtraTxt){oppoExtraTxt.setText(txt);}}
    public void updateFocus(boolean isMyTurn){
        if (isMyTurn) {
            game_my_panel.setBackgroundResource(R.drawable.game_rect);
            game_oppo_panel.setBackgroundResource(R.drawable.game_rect_normal);
        } else {
            game_my_panel.setBackgroundResource(R.drawable.game_rect_normal);
            game_oppo_panel.setBackgroundResource(R.drawable.game_rect);
        }
    }
}