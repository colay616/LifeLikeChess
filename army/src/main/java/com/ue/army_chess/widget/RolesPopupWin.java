package com.ue.army_chess.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ue.army_chess.R;
import com.ue.army_chess.entity.RoleItem;
import com.ue.common.adapter.BaseListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hawk on 2017/1/3.
 */

public class RolesPopupWin {
    private PopupWindow rolesWindow;
    private AdapterView.OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public RolesPopupWin(Context context) {
        View popupView = LayoutInflater.from(context).inflate(R.layout.a_popup_roles, null);
        ListView apsin_list = (ListView) popupView.findViewById(R.id.apsin_list);
        String[]rolesName=new String[]{
                "工兵", "排长", "连长", "营长", "团长", "旅长",
                "师长", "军长", "司令", "地雷", "炸弹", "军旗"
        };
        List<RoleItem>roleItems=new ArrayList<>(12);
        for(int i=0;i<12;i++){
            roleItems.add(new RoleItem(i,rolesName[i]));
        }
        apsin_list.setAdapter(new RoleAdapter(context,roleItems));
        apsin_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                rolesWindow.dismiss();
                mOnItemClickListener.onItemClick(parent,view,position,id);
            }
        });
        rolesWindow = new PopupWindow(popupView, 300, 400);
        rolesWindow.setTouchable(true);
        rolesWindow.setOutsideTouchable(true);
        rolesWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
    }

    public boolean isShowing() {
        return rolesWindow.isShowing();
    }

    public void dismiss() {
        if (rolesWindow.isShowing()) {
            rolesWindow.dismiss();
        }
    }

    public void show(View parent, int x, int y) {
        if (!rolesWindow.isShowing()) {
            rolesWindow.showAtLocation(parent, Gravity.NO_GRAVITY, x, y);
        }
    }

    class RoleAdapter extends BaseListAdapter<RoleItem>{
        public RoleAdapter(Context ctx, List<RoleItem> list) {
            super(ctx, list);
        }
        @Override
        public View bindView(int position, View layoutView, ViewGroup viewGroup) {
            if(null==layoutView){
                layoutView=mInflater.inflate(R.layout.a_popup_txt,null);
            }
            ((TextView)layoutView).setText(mList.get(position).getName());
            return layoutView;
        }
    }
}
