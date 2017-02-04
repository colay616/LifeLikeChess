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
 * Created by Weavey on 2016/9/3.
 */
public class TipAlertDialog{
    private TextView mContent;
    private Dialog mDialog;
    private View mDialogView;

    public TipAlertDialog(Builder builder) {
        mDialog = new Dialog(builder.mContext, R.style.NormalDialogStyle);
        mDialogView = View.inflate(builder.mContext, R.layout.dialog_tip, null);
        mContent = (TextView) mDialogView.findViewById(R.id.dialog_tip_content);
        mDialogView.setMinimumHeight((int) (DisplayUtil.getScreenHeight(builder.mContext) * builder.height));
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

        mContent.setText(builder.getContentText());
        mContent.setTextColor(builder.contentTextColor);
        mContent.setTextSize(builder.contentTextSize);
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

    /////根据需求新增
    public boolean isShowing() {
        return mDialog.isShowing();
    }

    public void setContentText(String contentText) {
        mContent.setText(contentText);
    }

    public Context getContext(){
        return mDialogView.getContext();
    }
    ////

    public static class Builder {
        private Context mContext;
        private String contentText;
        private int contentTextColor;
        private int contentTextSize;
        private boolean isTouchOutside;
        private float height;
        private float width;

        public Builder(Context context) {
            mContext = context;

            contentText = "";
            contentTextColor = ContextCompat.getColor(mContext, R.color.black_363636);
            isTouchOutside = true;
            height = 0.15f;
            width = 0.65f;
            contentTextSize = mContext.getResources().getInteger(R.integer.dialog_size_14);
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

        public Builder setCanceledOnTouchOutside(boolean isTouchOutside) {
            this.isTouchOutside = isTouchOutside;
            return this;
        }

        public Builder setContentTextSize(int contentTextSize) {
            this.contentTextSize = contentTextSize;
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

        public TipAlertDialog build() {

            return new TipAlertDialog(this);
        }
    }
}
