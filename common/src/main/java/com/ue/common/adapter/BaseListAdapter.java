package com.ue.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListAdapter<T> extends BaseAdapter {
	public Context mContext;
	public List<T>mList;
	public LayoutInflater mInflater;
	
	public BaseListAdapter(Context ctx,List<T>list){
		mInflater=LayoutInflater.from(ctx);
		mContext=ctx;
		
		if(null==list){
			mList=new ArrayList<T>();
		}else{
			mList=list;
		}
	}
	
	public void addAll(List<T>list){
		mList.addAll(list);
		notifyDataSetChanged();
	}
	
	public void setList(List<T>list){
		mList=list;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return null==mList?0:mList.size();
	}

	@Override
	public T getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View layoutView, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		return bindView(position, layoutView, viewGroup);
	}
	
	public abstract View bindView(int position, View layoutView, ViewGroup viewGroup);

}
