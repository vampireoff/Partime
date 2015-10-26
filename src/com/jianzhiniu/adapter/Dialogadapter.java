package com.jianzhiniu.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jianzhiniu.activity.R;

public class Dialogadapter extends BaseAdapter {

	List<Map<String, String>> list = new ArrayList<Map<String,String>>();
	ViewHoder hoder = null;
	Context context;
	
	public static class ViewHoder{
		TextView textView;
	}
	
	public void refresh(List<Map<String, String>> list){
		this.list = list;
		this.notifyDataSetChanged();
	}
	
	public Dialogadapter(List<Map<String, String>> list, Context context){
		this.list = list;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		hoder = new ViewHoder();
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.selectview_item, null);
			hoder.textView = (TextView)convertView.findViewById(R.id.select_item);
			convertView.setTag(hoder);
		}else {
			hoder = (ViewHoder)convertView.getTag();
		}
		
		hoder.textView.setText(list.get(position).get("name"));
		return convertView;
	}

}
