package com.jianzhiniu.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianzhiniu.activity.R;
import com.jianzhiniu.utils.MyUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class MyApplylistAdapter extends BaseAdapter {

	List<Map<String, String>> list = new ArrayList<Map<String,String>>();
	Context context;
	ViewHoder hoder = null;
	String dist;
	int distance;
	private ImageLoader loader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	public static class ViewHoder{
		TextView place, time, price, state, title;
		ImageView start, img;
	}
	
	public void refreshdata(List<Map<String, String>> list){
		this.list = list;
		this.notifyDataSetChanged();
	}
	
	public MyApplylistAdapter(Context context, List<Map<String, String>> list){
		this.context = context;
		this.list = list;
		loader.init(ImageLoaderConfiguration.createDefault(this.context));
		//图片参数设置
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
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		hoder = new ViewHoder();
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.myapply_item, null);
			hoder.place = (TextView)convertView.findViewById(R.id.didian_text);
			hoder.time = (TextView)convertView.findViewById(R.id.time_text);
			hoder.price = (TextView)convertView.findViewById(R.id.price_text);
			hoder.state = (TextView)convertView.findViewById(R.id.jobstate);
			hoder.title = (TextView)convertView.findViewById(R.id.jobname);
			hoder.start = (ImageView)convertView.findViewById(R.id.nj_start);
			hoder.img = (ImageView)convertView.findViewById(R.id.img);
			convertView.setTag(hoder);
		}else {
			hoder = (ViewHoder) convertView.getTag();
		}
		
		if (list.get(position).get("status").equals("2")) {
			//拒绝
			hoder.state.setBackgroundResource(R.drawable.applystate_red_bg);
		}else if (list.get(position).get("status").equals("3")) {
			//录用
			hoder.state.setBackgroundResource(R.drawable.applystate_green_bg);
		}else if (list.get(position).get("status").equals("-1")) {
			//取消
			hoder.state.setBackgroundResource(R.drawable.applystate_orange_bg);
		}else {
			//报名
			hoder.state.setBackgroundResource(R.drawable.applystate_blue_bg);
		}
		hoder.state.setText(list.get(position).get("statusdisplay"));
		loader.displayImage(MyUtils.getIconUrl() + list.get(position).get("jsid") + ".png", hoder.img, options);
		hoder.title.setText(list.get(position).get("title"));
		hoder.place.setText(list.get(position).get("city"));
		hoder.time.setText(MyUtils.getStandardDate(list.get(position).get("regtime"), context));
		hoder.price.setText("$ " + list.get(position).get("wage") + list.get(position).get("unit"));
		
		return convertView;
	}

}
