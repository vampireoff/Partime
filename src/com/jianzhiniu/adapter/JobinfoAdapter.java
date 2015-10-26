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
import com.jianzhiniu.utils.MyUtils;

public class JobinfoAdapter extends BaseAdapter {

	List<Map<String, String>> list = new ArrayList<Map<String,String>>();
	ViewHoder hoder = null;
	Context context;
	
	public static class ViewHoder{
		TextView name, reply, name2, reply2;
		View bottomView;
	}
	
	public void refresh(List<Map<String, String>> list){
		this.list = list;
		this.notifyDataSetChanged();
	}
	
	public JobinfoAdapter(List<Map<String, String>> list, Context context){
		this.list = list;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
//		return this.list.size();
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
			convertView = LayoutInflater.from(context).inflate(R.layout.jobinfolist_item, null);
			hoder.name = (TextView)convertView.findViewById(R.id.name);
			hoder.reply = (TextView)convertView.findViewById(R.id.reply);
			hoder.name2 = (TextView)convertView.findViewById(R.id.name2);
			hoder.reply2 = (TextView)convertView.findViewById(R.id.reply2);
			hoder.bottomView = (View)convertView.findViewById(R.id.cbottomview);
			convertView.setTag(hoder);
		}else {
			hoder = (ViewHoder)convertView.getTag();
		}
		
		hoder.name.setText(list.get(position).get("sname"));
		hoder.reply.setText(MyUtils.ParseString(list.get(position).get("question")));
		
		if (!list.get(position).get("ename").equals("")) {
			hoder.name2.setText(list.get(position).get("ename"));
			hoder.reply2.setText(MyUtils.ParseString(list.get(position).get("reply")));
			hoder.bottomView.setVisibility(View.VISIBLE);
		}else {
			hoder.bottomView.setVisibility(View.GONE);
		}
		return convertView;
	}

}
