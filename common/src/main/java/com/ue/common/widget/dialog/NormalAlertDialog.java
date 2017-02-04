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
import android.widget.Button;
import android.widget.TextView;

import com.ue.common.R;
import com.ue.common.util.DisplayUtil;

/**
 * Created by Weavey on 2016/9/3.
 */
public class NormalAlertDialog {
    private TextView mTitle;
    private TextView mContent;
    private Button mLeftBtn;
    private Button mRightBtn;
    private Button mSingleBtn;
    private TextView mLine;
    private Dialog mDialog;
    private View mDialogView;
    private Builder mBuilder;

    public NormalAlertDialog(Builder builder) {

        this.mBuilder = builder;
        mDialog = new Dialog(builder.mContext, R.style.NormalDialogStyle);
        mDialogView = View.inflate(builder.mContext, R.layout.dialog_normal, null);
        mTitle = (TextView) mDialogView.findViewById(R.id.dialog_normal_title);
        mContent = (TextView) mDialogView.findViewById(R.id.dialog_normal_content);
        mLeftBtn = (Button) mDialogView.findViewById(R.id.dialog_normal_leftbtn);
        mRightBtn = (Button) mDialogView.findViewById(R.id.dialog_normal_rightbtn);
        mSingleBtn = (Button) mDialogView.findViewById(R.id.dialog_normal_midbtn);
        mLine = (TextView) mDialogView.findViewById(R.id.dialog_normal_line);
        mDialogView.setMinimumHeight((int) (DisplayUtil.getScreenHeight(builder.mContext)* builder.height));
        mDialog.setContentView(mDialogView);

        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (DisplayUtil.getScreenWidth(builder.mContext) * builder.width);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);

        initDialog(builder);
    }

    private void initDialog(Builder builder) {
        mDialog.setCanceledOnTouchOutside(builder.isTouchOutside);
        mTitle.setVisibility(builder.isTitleVisible ? View.VISIBLE : View.GONE);
        if (builder.isSingleMode) {
            mSingleBtn.setVisibility(View.VISIBLE);
            mLine.setVisibility(View.GONE);
            mLeftBtn.setVisibility(View.GONE);
            mRightBtn.setVisibility(View.GONE);
        }

        mTitle.setText(builder.titleText);
        mTitle.setTextColor(builder.titleTextColor);
        mTitle.setTextSize(builder.titleTextSize);
        mContent.setText(builder.getContentText());
        mContent.setTextColor(builder.contentTextColor);
        mContent.setTextSize(builder.contentTextSize);
        mLeftBtn.setTextColor(builder.leftButtonTextColor);
        mLeftBtn.setTextSize(builder.buttonTextSize);
        mRightBtn.setTextColor(builder.rightButtonTextColor);
        mRightBtn.setTextSize(builder.buttonTextSize);
        mSingleBtn.setTextColor(builder.singleButtonTextColor);
        mSingleBtn.setTextSize(builder.buttonTextSize);

        setButton(mLeftBtn,builder.leftButtonText,mBuilder.leftBtnListener);
        setButton(mRightBtn,builder.rightButtonText,mBuilder.rightBtnListener);
        setButton(mSingleBtn,builder.singleButtonText,mBuilder.singleListener);
    }

    public void show() {
        if (!mDialog.isShowing()) {
            ////BadTokenException,is your activity running?
            if (!((Activity) mDialogView.getContext()).isFinishing()) {
                mDialog.show();
            }
        }
    }

    public void dismiss() {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    /////根据需求新增
    public boolean isShowing() {
        return mDialog.isShowing();
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setContentText(String contentText) {
        mContent.setText(contentText);
    }

    public void setLeftBtn(String leftBtnTxt, final View.OnClickListener clicker) {
        setButton(mLeftBtn,leftBtnTxt,clicker);
    }

    public void setRightBtn(String rightBtnTxt, final View.OnClickListener clicker) {
        setButton(mRightBtn,rightBtnTxt,clicker);
    }

    public void setSingleBtn(String btnTxt, final View.OnClickListener clicker){
        setButton(mSingleBtn,btnTxt,clicker);
    }

    private void setButton(Button button, String btnTxt, final View.OnClickListener clicker){
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

    public Context getContext() {
        return mDialogView.getContext();
    }

    private int flag = -1;//如果涉及到共用，可以根据这个标识进行相应处理

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }
    ////

    public static class Builder {
        private Context mContext;
        private String titleText;
        private int titleTextColor;
        private int titleTextSize;
        private String contentText;
        private int contentTextColor;
        private int contentTextSize;
        private boolean isSingleMode;
        private String singleButtonText;
        private int singleButtonTextColor;
        private String leftButtonText;
        private int leftButtonTextColor;
        private String rightButtonText;
        private int rightButtonTextColor;
        private int buttonTextSize;
        private View.OnClickListener leftBtnListener;
        private View.OnClickListener rightBtnListener;
        private View.OnClickListener singleListener;
        private boolean isTitleVisible;
        private boolean isTouchOutside;
        private float height;
        private float width;

        public Builder(Context context) {
            mContext = context;
            titleTextColor = ContextCompat.getColor(mContext, R.color.black_363636);

            contentText = "";
            contentTextColor = ContextCompat.getColor(mContext, R.color.black_363636);
            isSingleMode = false;
            singleButtonTextColor = ContextCompat.getColor(mContext, R.color.black_363636);
            leftButtonTextColor = ContextCompat.getColor(mContext, R.color.black_363636);
            rightButtonTextColor = ContextCompat.getColor(mContext, R.color.black_363636);
            leftBtnListener = null;
            rightBtnListener = null;
            singleListener = null;
            isTitleVisible = true;
            isTouchOutside = true;
            height = 0.23f;
            width = 0.65f;
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

        public String getContentText() {
            return contentText;
        }

        public Builder setContentText(String contentText) {
            this.contentText = contentText;
            return this;
        }

        public Builder setContentTextColor(@ColorRes int contentTextColor) {
            this.contentTextColor = ContextCompat.getColor(mContext, contentTextColor);
            return this;
        }

        public Builder setSingleMode(boolean singleMode) {
            isSingleMode = singleMode;
            return this;
        }

        public Builder setSingleButtonTextColor(@ColorRes int singleButtonTextColor) {
            this.singleButtonTextColor = ContextCompat.getColor(mContext, singleButtonTextColor);
            return this;
        }

        public Builder setLeftButtonTextColor(@ColorRes int leftButtonTextColor) {
            this.leftButtonTextColor = ContextCompat.getColor(mContext, leftButtonTextColor);
            return this;
        }

        //*****************
        public Builder setSingleButton(String singleButtonText, View.OnClickListener clicker) {
            this.singleButtonText = singleButtonText;
            this.singleListener = clicker;
            return this;
        }

        public Builder setLeftButton(String leftButtonText, View.OnClickListener clicker) {
            this.leftButtonText = leftButtonText;
            this.leftBtnListener = clicker;
            return this;
        }

        public Builder setRightButton(String rightButtonText, View.OnClickListener clickListener) {
            this.rightButtonText = rightButtonText;
            this.rightBtnListener = clickListener;
            return this;
        }
        //*****************

        public Builder setRightButtonTextColor(@ColorRes int rightButtonTextColor) {
            this.rightButtonTextColor = ContextCompat.getColor(mContext, rightButtonTextColor);
            return this;
        }

        public Builder setTitleVisible(boolean isVisible) {
            isTitleVisible = isVisible;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean isTouchOutside) {

            this.isTouchOutside = isTouchOutside;
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

        public Builder setHeight(float height) {
            this.height = height;
            return this;
        }

        public Builder setWidth(float width) {
            this.width = width;
            return this;
        }

        public NormalAlertDialog build() {

            return new NormalAlertDialog(this);
        }
    }
}
