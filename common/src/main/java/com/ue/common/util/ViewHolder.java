package com.ue.common.util;

import android.util.SparseArray;
import android.view.View;

public class ViewHolder {
	public static <T extends View>T getView(View layoutView,int viewId){
		SparseArray<View>viewHolder=(SparseArray<View>) layoutView.getTag();
		if(null==viewHolder){
			viewHolder=new SparseArray<View>();
		}
		View childView=viewHolder.get(viewId);
		if(null==childView){
			childView=layoutView.findViewById(viewId);
			viewHolder.put(viewId,childView);
		}
		return (T) childView;
	}
}
