package com.ue.common.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ue.common.R;

/**
 * title bar
 * 常用场景:fragment
 * activity的话可以直接使用actionbar/toolbar
 */
public class ActionBarView extends FrameLayout{
    private FrameLayout actionbar_root;
    private ImageView actionbar_left_img;
    private ImageView actionbar_right_img;
    private TextView actionbar_left_txt;
    private TextView actionbar_center_txt;
    private TextView actionbar_right_txt;

    public ActionBarView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public ActionBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ActionBarView(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.view_actionbar, this);
        actionbar_root= (FrameLayout) findViewById(R.id.actionbar_root);
        actionbar_left_img= (ImageView) findViewById(R.id.actionbar_left_img);
        actionbar_left_txt= (TextView) findViewById(R.id.actionbar_left_txt);
        actionbar_center_txt= (TextView) findViewById(R.id.actionbar_center_txt);
        actionbar_right_img= (ImageView) findViewById(R.id.actionbar_right_img);
        actionbar_right_txt= (TextView) findViewById(R.id.actionbar_right_txt);

        if(attrs != null){
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ActionBarView);

            String title = ta.getString(R.styleable.ActionBarView_centerTxt);
            Drawable background = ta.getDrawable(R.styleable.ActionBarView_backgroundRes);
            String leftTxt = ta.getString(R.styleable.ActionBarView_leftTxt);
            Drawable leftImg = ta.getDrawable(R.styleable.ActionBarView_leftImg);
            String rightTxt = ta.getString(R.styleable.ActionBarView_rightTxt);
            Drawable rightImg = ta.getDrawable(R.styleable.ActionBarView_rightImg);
            int sidesTxtColor = ta.getColor(R.styleable.ActionBarView_sidesTxtColor, Color.parseColor("#ffffff"));
            float sidesTxtSize = ta.getDimension(R.styleable.ActionBarView_sidesTxtSize,20);

            actionbar_center_txt.setText(title);
            actionbar_left_txt.setTextColor(sidesTxtColor);
            actionbar_left_txt.setTextSize(sidesTxtSize);
            actionbar_right_txt.setTextColor(sidesTxtColor);
            actionbar_right_txt.setTextSize(sidesTxtSize);

            if(!TextUtils.isEmpty(leftTxt)){
                setLeftTxt(leftTxt);
            }
            if(!TextUtils.isEmpty(rightTxt)){
                setRightTxt(rightTxt);
            }
            if (null != leftImg) {
                setLeftImg(leftImg);
            }
            if (null != rightImg) {
                setRightImg(rightImg);
            }
            if(null != background) {
                actionbar_root.setBackgroundDrawable(background);
            }
            ta.recycle();
        }
    }

    public void setBackground(int backgroundRes){
        actionbar_root.setBackgroundResource(backgroundRes);
    }

    public void setBackground(Drawable backgroundRes){
        actionbar_root.setBackgroundDrawable(backgroundRes);
    }

    public void setCenterTxt(String centerTxt){
        actionbar_center_txt.setText(centerTxt);
    }

    public void setLeftTxt(String leftTxt){
        actionbar_left_txt.setVisibility(View.VISIBLE);
        actionbar_left_txt.setText(leftTxt);
        actionbar_left_img.setVisibility(View.GONE);
    }

    public void setRightTxt(String rightTxt){
        actionbar_right_txt.setVisibility(View.VISIBLE);
        actionbar_right_txt.setText(rightTxt);
        actionbar_right_img.setVisibility(View.GONE);
    }

    public void setLeftImg(int imgRes){
        actionbar_left_img.setVisibility(View.VISIBLE);
        actionbar_left_img.setImageResource(imgRes);
        actionbar_left_txt.setVisibility(View.GONE);
    }

    public void setLeftImg(Drawable imgRes){
        actionbar_left_img.setVisibility(View.VISIBLE);
        actionbar_left_img.setImageDrawable(imgRes);
        actionbar_left_txt.setVisibility(View.GONE);
    }

    public void setRightImg(int imgRes){
        actionbar_right_img.setVisibility(View.VISIBLE);
        actionbar_right_img.setImageResource(imgRes);
        actionbar_right_txt.setVisibility(View.GONE);
    }

    public void setRightImg(Drawable imgRes){
        actionbar_right_img.setVisibility(View.VISIBLE);
        actionbar_right_img.setImageDrawable(imgRes);
        actionbar_right_txt.setVisibility(View.GONE);
    }

    public void setLeftImg(int imgRes,OnClickListener onClickListener){
        setLeftImg(imgRes);
        actionbar_left_img.setOnClickListener(onClickListener);
    }

    public void setRightImg(int imgRes,OnClickListener onClickListener){
        setRightImg(imgRes);
        actionbar_right_img.setOnClickListener(onClickListener);
    }

    public void setLeftTxtListener(OnClickListener onClickListener){
        actionbar_left_txt.setOnClickListener(onClickListener);
    }
    public void setRightTxtListener(OnClickListener onClickListener){
        actionbar_right_txt.setOnClickListener(onClickListener);
    }

    public void enableLeftImgAsUp(){
        setLeftImg(R.drawable.slt_actionbar_back);
        actionbar_left_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getContext()).finish();
            }
        });
    }

}
