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

public class JobManageAdapter extends BaseAdapter {

	List<Map<String, String>> list = new ArrayList<Map<String,String>>();
	ViewHoder hoder = null;
	Context context;
	private ImageLoader loader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	public static class ViewHoder{
		TextView place, time, price, state, title, count;
		ImageView img;
	}
	
	public void refresh(List<Map<String, String>> list){
		this.list = list;
		this.notifyDataSetChanged();
	}
	
	public JobManageAdapter(List<Map<String, String>> list, Context context){
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
			convertView = LayoutInflater.from(context).inflate(R.layout.jobmanage_item, null);
			hoder.place = (TextView)convertView.findViewById(R.id.didian_text);
			hoder.price = (TextView)convertView.findViewById(R.id.price_text);
			hoder.time = (TextView)convertView.findViewById(R.id.time_text);
			hoder.title = (TextView)convertView.findViewById(R.id.jobname);
			hoder.state = (TextView)convertView.findViewById(R.id.jobstate);
			hoder.count = (TextView)convertView.findViewById(R.id.peoplecount_text);
			hoder.img = (ImageView)convertView.findViewById(R.id.item_img);
			convertView.setTag(hoder);
		}else {
			hoder = (ViewHoder)convertView.getTag();
		}
		
		loader.displayImage(MyUtils.getIconUrl() + list.get(position).get("jsid") + ".png", hoder.img, options);
		
		hoder.state.setText(list.get(position).get("count"));
		hoder.state.setVisibility(View.VISIBLE);
		hoder.title.setText(list.get(position).get("title"));
		hoder.place.setText(list.get(position).get("city"));
		hoder.time.setText(MyUtils.getStandardDate(list.get(position).get("issuetime"), context));
		hoder.price.setText("$ " + list.get(position).get("wage") + list.get(position).get("unit"));
		
		return convertView;
	}

}
