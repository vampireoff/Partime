package com.jianzhiniu.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jianzhiniu.activity.R;
import com.jianzhiniu.utils.MyUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class MyJobOnwayAdapter extends BaseAdapter {

	List<Map<String, String>> list = new ArrayList<Map<String,String>>();
	ViewHoder hoder = null;
	Activity context;
	private ImageLoader loader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	public static class ViewHoder{
		TextView title, company, contacter, phone;
		ImageView img;
		LinearLayout layout;
	}
	
	public void refresh(List<Map<String, String>> list){
		this.list = list;
		this.notifyDataSetChanged();
	}
	
	public MyJobOnwayAdapter(List<Map<String, String>> list, Activity context){
		this.list = list;
		this.context = context;
		loader.init(ImageLoaderConfiguration.createDefault(this.context));
		//Õº∆¨≤Œ ˝…Ë÷√
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.imgloading_r)
		.showImageForEmptyUri(R.drawable.imgerror)
		.showImageOnFail(R.drawable.imgerror).cacheOnDisc()
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new FadeInBitmapDisplayer(300)).build();
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
			convertView = LayoutInflater.from(context).inflate(R.layout.myjoblist_item, null);
			hoder.contacter = (TextView)convertView.findViewById(R.id.mj_name);
			hoder.phone = (TextView)convertView.findViewById(R.id.mj_phone);
			hoder.layout = (LinearLayout)convertView.findViewById(R.id.mj_rightphone);
			hoder.img = (ImageView)convertView.findViewById(R.id.mj_img);
			hoder.title = (TextView)convertView.findViewById(R.id.mj_title);
			hoder.company = (TextView)convertView.findViewById(R.id.mj_company);
			convertView.setTag(hoder);
		}else {
			hoder = (ViewHoder)convertView.getTag();
		}
		
		loader.displayImage(MyUtils.getIconUrl() + list.get(position).get("jsid") + ".png", hoder.img, options);
		
		hoder.contacter.setText(list.get(position).get("linkman"));
		hoder.phone.setText(list.get(position).get("phone"));
		hoder.title.setText(list.get(position).get("title"));
		hoder.company.setText(list.get(position).get("comname"));
		hoder.layout.setOnClickListener(new phoneclick(position));
		return convertView;
	}

	public class phoneclick implements OnClickListener{

		int pp;
		
		public phoneclick(int p){
			this.pp = p;
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			context.startActivity(new Intent(Intent.ACTION_VIEW, 
					Uri.parse("tel:" + list.get(pp).get("phone"))));
		}
		
	}
}
