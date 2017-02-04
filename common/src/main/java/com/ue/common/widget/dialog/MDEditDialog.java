package com.ue.common.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ue.common.R;
import com.ue.common.util.DisplayUtil;

/**
 * Created by Weavey on 2016/9/4.
 */
public class MDEditDialog implements DialogInterface.OnDismissListener {
    private Dialog mDialog;
    private View mDialogView;
    private TextView mTitle;
    private EditText mEdit;
    private TextView mLeftBtn;
    private TextView mRightBtn;
    private View lineView;
    private Builder mBuilder;

    public MDEditDialog(Builder builder) {
        mBuilder = builder;
        mDialog = new Dialog(builder.mContext, R.style.MyDialogStyle);
        mDialogView = View.inflate(builder.mContext, R.layout.dialog_edit, null);
        mTitle = (TextView) mDialogView.findViewById(R.id.edit_dialog_title);
        mEdit = (EditText) mDialogView.findViewById(R.id.edit_dialog_exittext);
        mLeftBtn = (TextView) mDialogView.findViewById(R.id.edit_dialog_leftbtn);
        mRightBtn = (TextView) mDialogView.findViewById(R.id.edit_dialog_rightbtn);
        lineView = (View) mDialogView.findViewById(R.id.edit_dialog_line);
        mDialogView.setMinimumHeight((int) (DisplayUtil.getScreenHeight(builder.mContext) * builder.height));
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

        mDialog.setOnDismissListener(this);
        mDialog.setCanceledOnTouchOutside(mBuilder.isTouchOutside);

        if (mBuilder.isTitleVisible) {

            mTitle.setVisibility(View.VISIBLE);
        } else {

            mTitle.setVisibility(View.GONE);
        }

        mTitle.setText(mBuilder.titleText);
        mTitle.setTextColor(mBuilder.titleTextColor);
        mTitle.setTextSize(mBuilder.titleTextSize);
        mEdit.setText(mBuilder.contentText);
        mEdit.setSelection(mBuilder.contentText.length());
        mEdit.setTextColor(mBuilder.contentTextColor);
        mEdit.setTextSize(mBuilder.contentTextSize);
        mLeftBtn.setTextColor(mBuilder.leftButtonTextColor);
        mLeftBtn.setTextSize(mBuilder.buttonTextSize);
        mRightBtn.setTextColor(mBuilder.rightButtonTextColor);
        mRightBtn.setTextSize(mBuilder.buttonTextSize);
        lineView.setBackgroundColor(mBuilder.lineColor);

        setButton(mLeftBtn,mBuilder.leftButtonText,mBuilder.leftClicker);
        setButton(mRightBtn,mBuilder.rightButtonText,mBuilder.rightClicker);

        mEdit.setHint(mBuilder.hintText);
        mEdit.setHintTextColor(mBuilder.hintTextColor);
        if (mBuilder.lines != -1) {

            mEdit.setLines(mBuilder.lines);
        }
        if (mBuilder.maxLines != -1) {
            mEdit.setMaxLines(mBuilder.maxLines);
        }
        if (mBuilder.maxLength != -1) {
            mEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mBuilder.maxLength)});
        }

    }

    private void setButton(TextView button, String btnTxt, final OnClickEditDialogListener clicker){
        button.setText(btnTxt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(null!=clicker){
                    clicker.onClick(v,mEdit.getText().toString());
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

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        mEdit.setText("");
    }

    //////

    public Context getContext() {
        return mDialogView.getContext();
    }
    public void setTitle(String title){
        mTitle.setText(title);
    }
    public void setLeftButton(String leftButtonText, final OnClickEditDialogListener clicker) {
        mLeftBtn.setText(leftButtonText);
        mLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(null!=clicker){
                    clicker.onClick(mLeftBtn,mEdit.getText().toString());
                }
            }
        });
    }
    public void setRightButton(String rightButtonText, final OnClickEditDialogListener clicker) {
        mRightBtn.setText(rightButtonText);
        mRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(null!=clicker){
                    clicker.onClick(mRightBtn,mEdit.getText().toString());
                }
            }
        });
    }
    public boolean isShowing(){
        return mDialog.isShowing();
    }

    //////

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
        private int lineColor;
        private int lines;
        private int maxLines;
        private int maxLength;
        private boolean isTitleVisible;
        private boolean isTouchOutside;
        private float height;
        private float width;
        private String hintText;
        private int hintTextColor;
        private OnClickEditDialogListener leftClicker;
        private OnClickEditDialogListener rightClicker;

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
            lineColor = ContextCompat.getColor(mContext, R.color.black_363636);
            leftClicker=null;
            rightClicker=null;
            isTitleVisible = true;
            isTouchOutside = true;
            hintText = "";
            hintTextColor = ContextCompat.getColor(mContext, R.color.gray_dcdcdc);
            lines = -1;
            maxLines = -1;
            maxLength = -1;
            height = 0.28f;
            width = 0.8f;
            titleTextSize = mContext.getResources().getInteger(R.integer.dialog_size_20);
            contentTextSize = mContext.getResources().getInteger(R.integer.dialog_size_18);
            buttonTextSize = mContext.getResources().getInteger(R.integer.dialog_size_16);
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

        //************
        public Builder setLeftButton(String leftButtonText,OnClickEditDialogListener clicker) {
            this.leftButtonText = leftButtonText;
            this.leftClicker=clicker;
            return this;
        }
        public Builder setRightButton(String rightButtonText,OnClickEditDialogListener clicker) {
            this.rightButtonText = rightButtonText;
            this.rightClicker=clicker;
            return this;
        }
        //************

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

        public Builder setMinHeight(float height) {
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

        public Builder setLineColor(@ColorRes int lineColor) {
            this.lineColor = ContextCompat.getColor(mContext, lineColor);
            return this;
        }

        public Builder setLines(int lines) {
            this.lines = lines;
            return this;
        }

        public Builder setMaxLines(int maxLines) {
            this.maxLines = maxLines;
            return this;
        }

        public Builder setMaxLength(int maxLength) {
            this.maxLength = maxLength;
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

        public Builder setHintText(String hintText) {
            this.hintText = hintText;
            return this;
        }

        public Builder setHintTextColor(int hintTextColor) {
            this.hintTextColor = ContextCompat.getColor(mContext, hintTextColor);
            return this;
        }

        public MDEditDialog build() {
            return new MDEditDialog(this);
        }
    }

    public interface OnClickEditDialogListener {
        void onClick(View view, String editText);
    }
}
