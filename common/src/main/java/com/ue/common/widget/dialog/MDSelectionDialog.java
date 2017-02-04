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
import android.widget.LinearLayout;

import com.ue.common.R;
import com.ue.common.util.DisplayUtil;

import java.util.ArrayList;

/**
 * Created by Weavey on 2016/9/4.
 */
public class MDSelectionDialog {
    private Dialog mDialog;
    private View dialogView;
    private LinearLayout linearLayout;

    private Builder mBuilder;
    private ArrayList<String> datas;
    private int selectPosition;//最后一次选择的位置

    public MDSelectionDialog(Builder builder) {

        this.mBuilder = builder;
        mDialog = new Dialog(builder.mContext, R.style.MyDialogStyle);
        dialogView = View.inflate(builder.mContext, R.layout.dialog_md_mid, null);
        linearLayout = (LinearLayout) dialogView.findViewById(R.id.md_mid_dialog_linear);
        mDialog.setContentView(dialogView); // 一定要在setAttributes(lp)之前才有效
        //设置dialog的宽
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (DisplayUtil.getScreenWidth(builder.mContext) * builder.itemWidth);
        lp.gravity = Gravity.CENTER;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
        mDialog.setCanceledOnTouchOutside(builder.isTouchOutside);


    }

    //根据数据生成item
    private void loadItem() {


        //设置数据item
        if (datas.size() == 1) {

            Button button = getButton(datas.get(0), 0);
            button.setBackgroundResource(R.drawable.dialog_selector_md_single);
            linearLayout.addView(button);

        } else if (datas.size() > 1) {

            for (int i = 0; i < datas.size(); i++) {

                Button button = getButton(datas.get(i), i);
                if (i == 0) {
                    button.setBackgroundResource(R.drawable.dialog_selector_md_top);
                } else if (i > 0 && i != datas.size() - 1) {
                    button.setBackgroundResource(R.drawable.dialog_selector_md_middle);

                } else {
                    button.setBackgroundResource(R.drawable.dialog_selector_md_bottom);
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
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .MATCH_PARENT, mBuilder.itemHeight);
        button.setLayoutParams(lp);
        button.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        button.setPadding(DisplayUtil.dp2px(mBuilder.mContext,10),0, DisplayUtil.dp2px(mBuilder.mContext,10),0);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dismiss();
                if (mBuilder.getOnItemListener() != null) {
                    selectPosition = Integer.parseInt(button.getTag().toString());
                    mBuilder.getOnItemListener().onItemClick(button, selectPosition);
                }
            }
        });

        return button;
    }

    public void setDataList(ArrayList<String> datas) {
        linearLayout.removeAllViews();
        this.datas = (datas == null ? new ArrayList<String>() : datas);
        loadItem();
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

    /////////////

    public boolean isShowing(){
        return mDialog.isShowing();
    }

    public Context getContext(){
        return mBuilder.mContext;
    }

    /////////////


    public static class Builder {
        private Context mContext;
        //item属性
        private DialogOnItemClickListener onItemListener;
        private int itemHeight;
        private float itemWidth;
        private int itemTextColor;
        private float itemTextSize;
        private boolean isTouchOutside;

        public Builder(Context context) {

            mContext = context;
            onItemListener = null;
            itemHeight = DisplayUtil.dp2px(context, 45); // 默认item高度
            itemWidth = 0.75f;
            itemTextColor = ContextCompat.getColor(mContext, R.color.black_363636); // 默认字体颜色
            itemTextSize = mContext.getResources().getInteger(R.integer.dialog_size_14);

            isTouchOutside = true;
        }


        public DialogOnItemClickListener getOnItemListener() {
            return onItemListener;
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

        public Builder setCanceledOnTouchOutside(boolean isTouchOutside) {

            this.isTouchOutside = isTouchOutside;
            return this;
        }

        public MDSelectionDialog build() {

            return new MDSelectionDialog(this);
        }
    }

}
