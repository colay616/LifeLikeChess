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
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ue.common.R;
import com.ue.common.util.DisplayUtil;

import java.util.ArrayList;

/**
 * Created by Weavey on 2016/9/3.
 */
public class NormalSelectionDialog {
    private Dialog mDialog;
    private View dialogView;
    private TextView title;
    private Button bottomBtn;
    private LinearLayout linearLayout;

    private Builder mBuilder;
    private ArrayList<String> datas;
    private int selectPosition;//最后一次选择的位置

    public NormalSelectionDialog(Builder builder) {
        this.mBuilder = builder;
        mDialog = new Dialog(builder.mContext, R.style.bottomDialogStyle);
        dialogView = View.inflate(builder.mContext, R.layout.dialog_bottom, null);
        mDialog.setContentView(dialogView); // 一定要在setAttributes(lp)之前才有效

        //设置dialog的宽
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (DisplayUtil.getScreenWidth(builder.mContext) * builder.itemWidth);
        lp.gravity = Gravity.BOTTOM;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);


        title = (TextView) dialogView.findViewById(R.id.action_dialog_title);
        linearLayout = (LinearLayout) dialogView.findViewById(R.id.action_dialog_linearlayout);
        bottomBtn = (Button) dialogView.findViewById(R.id.action_dialog_botbtn);
        bottomBtn.setText(builder.cancleButtonText);
        bottomBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mDialog.dismiss();
            }
        });
        mDialog.setCanceledOnTouchOutside(builder.isTouchOutside);
    }

    //根据数据生成item
    private void loadItem() {

        //设置标题
        if (mBuilder.boolTitle) {
            title.setVisibility(View.VISIBLE);
            title.setText(mBuilder.titleText);
            title.setTextColor(mBuilder.titleTextColor);
            title.setTextSize(mBuilder.titleTextSize);
            LinearLayout.LayoutParams l = (LinearLayout.LayoutParams) title.getLayoutParams();
            l.height = DisplayUtil.dp2px(mBuilder.mContext, mBuilder.titleHeight);
            title.setLayoutParams(l);

            if (datas.size() != 0) {
                title.setBackgroundResource(R.drawable.dialog_selector_top);

            } else {
                title.setBackgroundResource(R.drawable.dialog_selector_single);
            }
        } else {

            title.setVisibility(View.GONE);
        }

        //设置底部“取消”按钮
        bottomBtn.setTextColor(mBuilder.itemTextColor);
        bottomBtn.setTextSize(mBuilder.itemTextSize);
        LinearLayout.LayoutParams btnlp = new LinearLayout.LayoutParams(AbsListView.LayoutParams
                .MATCH_PARENT, mBuilder.itemHeight);
        btnlp.topMargin = 10;
        bottomBtn.setLayoutParams(btnlp);

        //设置数据item
        if (datas.size() == 1) {

            Button button = getButton(datas.get(0), 0);
            if (mBuilder.boolTitle)
                button.setBackgroundResource(R.drawable.dialog_selector_bottom);
            else button.setBackgroundResource(R.drawable.dialog_selector_single);

            linearLayout.addView(button);

        } else if (datas.size() > 1) {

            for (int i = 0; i < datas.size(); i++) {

                Button button = getButton(datas.get(i), i);
                if (!mBuilder.boolTitle && i == 0) {
                    button.setBackgroundResource(R.drawable.dialog_selector_top);
                } else {
                    if (i != datas.size() - 1)
                        button.setBackgroundResource(R.drawable
                                .dialog_selector_middle);
                    else
                        button.setBackgroundResource(R.drawable
                                .dialog_selector_bottom);
                }
                linearLayout.addView(button);
            }
        }


    }

    private Button getButton(String text, int position) {
        // 动态生成选择按钮
        final Button button = new Button(mBuilder.mContext);
        button.setText(text);
        button.setTag(position);
        button.setTextColor(mBuilder.itemTextColor);
        button.setTextSize(mBuilder.itemTextSize);
        button.setLayoutParams(new LinearLayout.LayoutParams(AbsListView.LayoutParams
                .MATCH_PARENT, mBuilder.itemHeight));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dismiss();
                if (mBuilder.onItemListener != null) {
                    selectPosition = Integer.parseInt(button.getTag().toString());
                    mBuilder.onItemListener.onItemClick(button, selectPosition);
                }
            }
        });

        return button;
    }

    public void setDataList(ArrayList<String> datas) {

        int count = linearLayout.getChildCount();
        if(count>1){
            linearLayout.removeViewsInLayout(1,count-1);
        }
//
        this.datas = (datas == null ? new ArrayList<String>() : datas);
        loadItem();
    }

    public boolean isShowing() {

        return mDialog.isShowing();
    }

    public void show() {
        if(!mDialog.isShowing()){
            if(!((Activity)dialogView.getContext()).isFinishing()){
                mDialog.show();
            }
        }
    }

    public void dismiss() {
        if(mDialog.isShowing()){
            mDialog.dismiss();
        }
    }

    ////
    public void setTitleText(String titleTxt){
        title.setText(titleTxt);
    }

    public Context getContext(){
        return dialogView.getContext();
    }
    ////

    public static class Builder {
        private Context mContext;
        //标题属性
        private boolean boolTitle;
        private int titleHeight;
        private String titleText;
        private int titleTextColor;
        private float titleTextSize;
        //item属性
        private DialogOnItemClickListener onItemListener;
        private int itemHeight;
        private float itemWidth;
        private int itemTextColor;
        private float itemTextSize;
        //取消按钮属性
        private String cancleButtonText;

        private boolean isTouchOutside;

        public Builder(Context context) {
            mContext = context;

            boolTitle = true; // 默认开启标题
            titleHeight = 65; // 默认标题高度  dp
            titleText = "请选择";
            titleTextColor = ContextCompat.getColor(context, R.color.black_363636);
            titleTextSize = mContext.getResources().getInteger(R.integer.dialog_size_14);

            onItemListener = null;
            itemHeight = DisplayUtil.dp2px(context, 45); // 默认item高度
            itemWidth = 0.92f;
            itemTextColor = ContextCompat.getColor(mContext, R.color.black_363636); // 默认字体颜色
            itemTextSize = mContext.getResources().getInteger(R.integer.dialog_size_14);

            cancleButtonText = "取消";
            isTouchOutside = true;
        }

        public Builder setTitleVisible(boolean isVisible) {
            this.boolTitle = isVisible;
            return this;
        }

        public Builder setTitleHeight(int dp) {
            this.titleHeight = dp;
            return this;
        }

        public Builder setTitleText(String titleText) {
            this.titleText = titleText;
            return this;
        }

        public Builder setTitleTextColor(@ColorRes int titleTextColor) {
            this.titleTextColor = ContextCompat.getColor(mContext, titleTextColor);
            return this;
        }

        public Builder setTitleTextSize(int sp) {
            this.titleTextSize = sp;
            return this;
        }

        public Builder setOnItemListener(DialogOnItemClickListener onItemListener) {
            this.onItemListener = onItemListener;
            return this;
        }

        public Builder setItemHeight(int dp) {
            this.itemHeight = DisplayUtil.dp2px(mContext, dp);
            return this;
        }

        public Builder setItemWidth(float itemWidth) {
            this.itemWidth = itemWidth;
            return this;
        }

        public Builder setItemTextColor(@ColorRes int itemTextColor) {

            this.itemTextColor = ContextCompat.getColor(mContext, itemTextColor);
            return this;
        }

        public Builder setItemTextSize(int sp) {
            this.itemTextSize = sp;
            return this;
        }

        public Builder setCancleButtonText(String cancleButtonText) {
            this.cancleButtonText = cancleButtonText;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean isTouchOutside) {

            this.isTouchOutside = isTouchOutside;
            return this;
        }

        public NormalSelectionDialog build() {

            return new NormalSelectionDialog(this);
        }
    }

}