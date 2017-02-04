package com.ue.common.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ue.common.R;
import com.ue.common.util.DisplayUtil;

/**
 * Created by Weavey on 2016/9/4.
 */
public class MDAlertDialog{
    private Dialog mDialog;
    private View mDialogView;
    private TextView mTitle;
    private TextView mContent;
    private TextView mLeftBtn;
    private TextView mRightBtn;
    private Builder mBuilder;

    public MDAlertDialog(Builder builder) {

        mBuilder = builder;
        mDialog = new Dialog(builder.mContext, R.style.MyDialogStyle);
        mDialogView = View.inflate(builder.mContext, R.layout.dialog_md, null);
        mTitle = (TextView) mDialogView.findViewById(R.id.md_dialog_title);
        mContent = (TextView) mDialogView.findViewById(R.id.md_dialog_content);
        mLeftBtn = (TextView) mDialogView.findViewById(R.id.md_dialog_leftbtn);
        mRightBtn = (TextView) mDialogView.findViewById(R.id.md_dialog_rightbtn);
        mDialogView.setMinimumHeight((int) (DisplayUtil.getScreenHeight(builder.mContext)* builder.height));
        mDialog.setContentView(mDialogView);

        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (DisplayUtil.getScreenWidth(builder.mContext) * builder.width);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        initDialog();

    }

    private void initDialog() {

        mDialog.setCanceledOnTouchOutside(mBuilder.isTouchOutside);

        if (mBuilder.isTitleVisible) {

            mTitle.setVisibility(View.VISIBLE);
        } else {

            mTitle.setVisibility(View.GONE);
        }

        mTitle.setText(mBuilder.titleText);
        mTitle.setTextColor(mBuilder.titleTextColor);
        mTitle.setTextSize(mBuilder.titleTextSize);
        mContent.setText(mBuilder.contentText);
        mContent.setTextColor(mBuilder.contentTextColor);
        mContent.setTextSize(mBuilder.contentTextSize);
        mLeftBtn.setTextColor(mBuilder.leftButtonTextColor);
        mLeftBtn.setTextSize(mBuilder.buttonTextSize);
        mRightBtn.setTextColor(mBuilder.rightButtonTextColor);
        mRightBtn.setTextSize(mBuilder.buttonTextSize);

        setButton(mLeftBtn,mBuilder.leftButtonText,mBuilder.leftClicker);
        setButton(mRightBtn,mBuilder.rightButtonText,mBuilder.rightClicker);
    }

    private void setButton(TextView button, String btnTxt, final View.OnClickListener clicker){
        button.setText(btnTxt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(null!=clicker){
                    clicker.onClick(v);
                }
            }
        });
    }

    public void show() {
        if(!mDialog.isShowing()){
            if(!((Activity)mDialogView.getContext()).isFinishing()){
                mDialog.show();
            }
        }
    }

    public void dismiss() {
        if(mDialog.isShowing()){
            mDialog.dismiss();
        }
    }

    public static class Builder {
        private Context mContext;
        private String titleText;
        private int titleTextColor;
        private int titleTextSize;
        private String contentText;
        private int contentTextColor;
        private int contentTextSize;
        private String leftButtonText;
        private int leftButtonTextColor;
        private String rightButtonText;
        private int rightButtonTextColor;
        private int buttonTextSize;
        private boolean isTitleVisible;
        private boolean isTouchOutside;
        private float height;
        private float width;
        private View.OnClickListener leftClicker;
        private View.OnClickListener rightClicker;

        public Builder(Context context) {
            mContext = context;
            titleText = "提示";
            titleTextColor = ContextCompat.getColor(mContext, R.color.black_363636);
            contentText = "";
            contentTextColor = ContextCompat.getColor(mContext, R.color.black_363636);
            leftButtonText = "取消";
            leftButtonTextColor = ContextCompat.getColor(mContext, R.color.black_363636);
            rightButtonText = "确定";
            rightButtonTextColor = ContextCompat.getColor(mContext, R.color.black_363636);
            leftClicker=null;
            rightClicker=null;
            isTitleVisible = true;
            isTouchOutside = true;
            height = 0.21f;
            width = 0.73f;
            titleTextSize = mContext.getResources().getInteger(R.integer.dialog_size_16);
            contentTextSize = mContext.getResources().getInteger(R.integer.dialog_size_14);
            buttonTextSize = mContext.getResources().getInteger(R.integer.dialog_size_14);
        }

        public Builder setTitleText(String titleText) {
            this.titleText = titleText;
            return this;
        }

        public Builder setTitleTextColor(@ColorRes int titleTextColor) {
            this.titleTextColor = ContextCompat.getColor(mContext, titleTextColor);
            return this;
        }

        public Builder setContentText(String contentText) {
            this.contentText = contentText;
            return this;
        }

        public Builder setContentTextColor(@ColorRes int contentTextColor) {
            this.contentTextColor = ContextCompat.getColor(mContext, contentTextColor);
            return this;
        }

        public Builder setLeftButton(String leftButtonText, View.OnClickListener leftClicker) {
            this.leftButtonText = leftButtonText;
            this.leftClicker=leftClicker;
            return this;
        }

        public Builder setRightButton(String rightButtonText, View.OnClickListener rightClicker) {
            this.rightButtonText = rightButtonText;
            this.rightClicker=rightClicker;
            return this;
        }

        public Builder setLeftButtonTextColor(@ColorRes int leftButtonTextColor) {
            this.leftButtonTextColor = ContextCompat.getColor(mContext, leftButtonTextColor);
            return this;
        }

        public Builder setRightButtonTextColor(@ColorRes int rightButtonTextColor) {
            this.rightButtonTextColor = ContextCompat.getColor(mContext, rightButtonTextColor);
            return this;
        }

        public Builder setTitleVisible(boolean titleVisible) {
            isTitleVisible = titleVisible;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean touchOutside) {
            isTouchOutside = touchOutside;
            return this;
        }

        public Builder setHeight(float height) {
            this.height = height;
            return this;
        }

        public Builder setWidth(float width) {
            this.width = width;
            return this;
        }

        public Builder setContentTextSize(int contentTextSize) {
            this.contentTextSize = contentTextSize;
            return this;
        }

        public Builder setTitleTextSize(int titleTextSize) {
            this.titleTextSize = titleTextSize;
            return this;
        }

        public Builder setButtonTextSize(int buttonTextSize) {
            this.buttonTextSize = buttonTextSize;
            return this;
        }

        public MDAlertDialog build() {

            return new MDAlertDialog(this);
        }
    }

}
