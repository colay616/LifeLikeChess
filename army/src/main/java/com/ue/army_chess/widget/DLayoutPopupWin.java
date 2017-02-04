package com.ue.army_chess.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hyphenate.easeui.game.db.GameDbManager;
import com.hyphenate.easeui.game.db.LayoutItem;
import com.ue.army_chess.R;
import com.ue.army_chess.ui.EditLayoutAty;
import com.ue.common.adapter.BaseListAdapter;
import com.ue.common.util.DisplayUtil;

import java.util.List;

/**
 * Created by hawk on 2017/1/3.
 */

public class DLayoutPopupWin {
    private PopupWindow layoutWindow;
    private OnLayoutItemClick layoutItemClicker;
    private LayoutAdapter adapter;
    private TextView dplat_title;
    private TextView dplat_change;

    public void setLayoutItemClicker(OnLayoutItemClick layoutItemClicker){
        this.layoutItemClicker=layoutItemClicker;
    }

    public DLayoutPopupWin(final Context context){
        View layoutView= LayoutInflater.from(context).inflate(R.layout.a_d_popup_layout,null);
        layoutView.findViewById(R.id.aplat_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, EditLayoutAty.class));
            }
        });
        layoutView.findViewById(R.id.aplat_gen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutWindow.dismiss();
                if(null!=layoutItemClicker){
                    layoutItemClicker.onEditClick(-1,null);
                }
            }
        });
        dplat_title= (TextView) layoutView.findViewById(R.id.dplat_title);
        dplat_change= (TextView) layoutView.findViewById(R.id.dplat_change);
        dplat_change.setTag(0);
        dplat_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tag=(int)dplat_change.getTag();
                if(tag==0){
                    dplat_change.setTag(1);
                    dplat_title.setText("玩家二布局：");
                }else{
                    dplat_change.setTag(0);
                    dplat_title.setText("玩家一布局：");
                }
            }
        });

        ListView aplat_list= (ListView) layoutView.findViewById(R.id.aplat_list);
        adapter=new LayoutAdapter(context, GameDbManager.getInstance().getLayoutItems());
        aplat_list.setAdapter(adapter);
        layoutWindow=new PopupWindow(layoutView, DisplayUtil.getScreenWidth(context)*2/3, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutWindow.setTouchable(true);
        layoutWindow.setOutsideTouchable(true);
        layoutWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
    }

    class LayoutAdapter extends BaseListAdapter<LayoutItem>{
        public LayoutAdapter(Context ctx, List<LayoutItem> list) {
            super(ctx, list);
        }
        @Override
        public View bindView(final int position, View layoutView, ViewGroup viewGroup) {
            if(null==layoutView){
                layoutView=mInflater.inflate(R.layout.a_adt_layout,null);
            }
            TextView aalat_name= (TextView) layoutView.findViewById(R.id.aalat_name);
            ImageView aalat_edit= (ImageView) layoutView.findViewById(R.id.aalat_edit);
            aalat_name.setText(mList.get(position).getName());
            aalat_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layoutWindow.dismiss();
                    if(null!=layoutItemClicker){
                        layoutItemClicker.onNameClick((int)dplat_change.getTag(),mList.get(position));
                    }
                }
            });
            aalat_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layoutWindow.dismiss();
                    if(null!=layoutItemClicker){
                        layoutItemClicker.onEditClick((int)dplat_change.getTag(),mList.get(position));
                    }
                }
            });
            return layoutView;
        }
    }

    public void dismiss(){
        if(layoutWindow.isShowing()){
            layoutWindow.dismiss();
        }
    }

    public void show(View anchor){
        adapter.setList(GameDbManager.getInstance().getLayoutItems());
        if(!layoutWindow.isShowing()){
            layoutWindow.showAtLocation(anchor, Gravity.CENTER,0,0);
        }
    }

    public interface OnLayoutItemClick{
        void onNameClick(int tag,LayoutItem layoutItem);
        void onEditClick(int tag,LayoutItem layoutItem);
    }
}
