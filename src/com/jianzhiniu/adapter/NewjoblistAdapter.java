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

public class NewjoblistAdapter extends BaseAdapter {

	List<Map<String, String>> list = new ArrayList<Map<String,String>>();
	Context context;
	ViewHoder hoder = null;
	boolean isNear;
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
	
	public NewjoblistAdapter(Context context, List<Map<String, String>> list, boolean isNear){
		this.context = context;
		this.list = list;
		this.isNear = isNear;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.newjob_item, null);
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
		
		loader.displayImage(MyUtils.getIconUrl() + list.get(position).get("jsid") + ".png", hoder.img, options);
		
		hoder.title.setText(list.get(position).get("title"));
		if (isNear) {
			dist = list.get(position).get("distance");
			if (dist.contains(".")) {
				distance = Integer.parseInt(dist.substring(0, dist.indexOf(".")));
			}else {
				distance = Integer.parseInt(dist);
			}
			if (distance < 1000) {
				hoder.place.setText(distance + " m");
			}else {
				hoder.place.setText(distance/1000 + " km");
			}
		}else {
			hoder.place.setText(list.get(position).get("city"));
		}
		hoder.time.setText(MyUtils.getStandardDate(list.get(position).get("issuetime"), context));
		hoder.price.setText("$ " + list.get(position).get("wage") + list.get(position).get("unit"));
		
		return convertView;
	}

}
