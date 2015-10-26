package com.jianzhiniu.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianzhiniu.activity.R;

public class MessageAdapter extends BaseAdapter {

	List<Map<String, String>> list = new ArrayList<Map<String,String>>();
	ViewHoder hoder = null;
	Context context;
	boolean isNotice;
	
	public static class ViewHoder{
		TextView title, time, state;
		ImageView imageView;
	}
	
	public void refresh(List<Map<String, String>> list){
		this.list = list;
		this.notifyDataSetChanged();
	}
	
	public MessageAdapter(List<Map<String, String>> list, Context context, 
			boolean isNotice){
		this.list = list;
		this.context = context;
		this.isNotice = isNotice;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		Log.i("xxx", "getcount");
		return this.list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		Log.i("xxx", "getitem");

		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		Log.i("xxx", "getitemid");

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Log.i("xxx", "getview");

		hoder = new ViewHoder();
		if (convertView == null) {
			Log.i("xxx", "convertview == null");

			if (isNotice) {
				convertView = LayoutInflater.from(context).inflate(R.layout.notice_item, null);
			}else {
				convertView = LayoutInflater.from(context).inflate(R.layout.message_item, null);
				hoder.imageView = (ImageView)convertView.findViewById(R.id.msg_img);
				hoder.state = (TextView)convertView.findViewById(R.id.msg_state);
			}
			hoder.title = (TextView)convertView.findViewById(R.id.msg_title);
			hoder.time = (TextView)convertView.findViewById(R.id.msg_time);
			convertView.setTag(hoder);
		}else {
			Log.i("xxx", "convertview != null");

			hoder = (ViewHoder)convertView.getTag();
		}
		
		if (!isNotice) {
			if (list.get(position).get("read").equals("0")) {
				hoder.state.setVisibility(View.VISIBLE);
			}else {
				hoder.state.setVisibility(View.GONE);
			}
			hoder.title.setText(list.get(position).get("content"));
			switch (Integer.parseInt(list.get(position).get("type"))) {
			case 0:
//				hoder.state.setText(context.getString(R.string.jobconsult));
				hoder.imageView.setImageResource(R.drawable.gzzx);
				break;
			case 1:
//				hoder.state.setText(context.getString(R.string.consultreply));
				hoder.imageView.setImageResource(R.drawable.zxhf);
				break;
			case 2:
//				hoder.state.setText(context.getString(R.string.jobapply));
				hoder.imageView.setImageResource(R.drawable.jzbm);
				break;
			case 3:
//				hoder.state.setText(context.getString(R.string.applyfollow));
				hoder.imageView.setImageResource(R.drawable.bmgz);
				break;
			case 4:
//				hoder.state.setText(context.getString(R.string.newnotice));
				hoder.imageView.setImageResource(R.drawable.assist2);
				break;

			default:
				break;
			}
		}else {
			hoder.title.setText(list.get(position).get("title"));
		}
		hoder.time.setText(list.get(position).get("issuetime"));
		return convertView;
	}

}
