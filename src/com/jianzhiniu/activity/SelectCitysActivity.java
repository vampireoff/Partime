package com.jianzhiniu.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jianzhiniu.config.MyApplication;
import com.jianzhiniu.utils.MyLocationManager;
import com.jianzhiniu.utils.MyLocationManager.LocationCallBack;
import com.jianzhiniu.utils.PinyinComparator;
import com.jianzhiniu.views.IndexBar;
import com.jianzhiniu.views.IndexBar.OnIndexSelectedListener;

/**
 * 城市选择界面
 * @author Administrator
 *
 */
public class SelectCitysActivity extends BaseActivity implements LocationCallBack{

	private Activity activity = SelectCitysActivity.this;
	private TextView toptext;
	IndexBar bar;  //右侧引导栏
	ListView lv;
	private boolean issearch;
	public static TextView tvShow, locationView;
	private TextView deleteView;
	private EditText autotv_search;
	private List<Contacts> beans = new ArrayList<Contacts>();
	private List<Contacts> beans_search = new ArrayList<Contacts>(); //存放搜索的联系人
	private List<String> nameList = new ArrayList<String>(); //存放联系人的名字
	private ImageView returnView, refreshView;
	private MyLocationManager myLocation;
	private double latitude = 0, longitude = 0;
	private String addressName = "", cityString = "";
	private SharedPreferences shared;
	private SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectcitys_view);
	
		init();
		LoadContacts();
	}
	
	private void init(){
		shared = getSharedPreferences("location", Context.MODE_PRIVATE);
		editor = shared.edit();
		
		MyApplication.getInstance().addActivity(this);
		toptext = (TextView)findViewById(R.id.top_centertext);
		toptext.setText(R.string.choosecity);
		tvShow = (TextView) findViewById(R.id.tvShow);
		locationView = (TextView) findViewById(R.id.location_text);
		returnView = (ImageView)findViewById(R.id.top_return);
		refreshView = (ImageView)findViewById(R.id.location_fresh_s);
		bar = (IndexBar) findViewById(R.id.ib);
		lv = (ListView) findViewById(R.id.lv);
		deleteView = (TextView)findViewById(R.id.t_delete);
		deleteView.setOnClickListener(this);
		returnView.setOnClickListener(this);
		locationView.setOnClickListener(this);
		refreshView.setOnClickListener(this);
		
		myLocation = new MyLocationManager(SelectCitysActivity.this.getApplicationContext(),
				SelectCitysActivity.this);
		//监听右侧引导栏
		bar.setOnIndexSelectedListener(new OnIndexSelectedListener() {
			@Override
			public void indexSelected(char index) {
				tvShow.setVisibility(View.GONE);
			}
			
			@Override
			public void indexChange(char index) {
				tvShow.setVisibility(View.VISIBLE);
				tvShow.setText(index + "");
				if(beans.size() != 0){
					for (int i = 0; i < beans.size(); i++) {
						if (beans.get(i).isHead && beans.get(i).sortKey == index) {
							//如果是第一个，listview跳到指定项
							lv.setSelection(i);
							return;
						}
					}
					for (int i = 0; i < beans.size(); i++) {
						if (beans.get(i).isHead && beans.get(i).sortKey > index) {
							lv.setSelection(i);
							return;
						}
					}
					lv.setSelection(beans.size() - 1);
				}
			}
		});
		
		autotv_search = (EditText)findViewById(R.id.search);
		//监听搜索框输入事件
		autotv_search.addTextChangedListener(new TextWatcher() {
			/**
			 * 输入中
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(autotv_search.getText().toString().trim().equals("")){
					//如果是空，加载全部联系人
					issearch = false;
					lv.setAdapter(new MyAdapter(activity, beans));
				}else {
					//否则，加载搜索到的联系人
					beans_search.clear();
					for (int i = 0; i < beans.size(); i++) {
						if (beans.get(i).name.contains(convertTouppercase(
								autotv_search.getText().toString().trim()))) {
							beans_search.add(beans.get(i));
						}
					}
					issearch = true;
					lv.setAdapter(new MyAdapter(activity, beans_search));
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		
	}
	
	/**
	 * 首字母转大写
	 * @param string
	 * @return
	 */
	private String convertTouppercase(String string){
		String mString = "";
		char[] chars = string.toCharArray();
		mString += String.valueOf(chars[0]).toUpperCase();
		for (int i = 1; i < chars.length; i++) {
			mString += String.valueOf(chars[i]).toLowerCase();
		}
		return mString;
	}
	
	/**
	 * 取汉字首大写字母
	 * @param chines
	 * @return
	 */
	public static String converterToFirstSpell(String chines){             
		String pinyinName = "";      
		char[] nameChar = chines.toCharArray();      
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();      
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);      
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);      
		for (int i = 0; i < nameChar.length; i++) {      
			if (nameChar[i] > 128) {
				//如果是汉字，转化为拼音      
				try {      
					pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0].charAt(0);      
				} catch (BadHanyuPinyinOutputFormatCombination e) {      
					e.printStackTrace();      
				}      
			}else{      
				pinyinName += nameChar[i];      
			}      
		}      
		return pinyinName;      
	}
	
	/**
	 * 加载联系人数据，初始化适配器
	 */
	@SuppressWarnings("unchecked")
	private void LoadContacts(){
		Contacts bean = null;
		char sortKey = ' ', ch;
		nameList = Arrays.asList(getResources().getStringArray(R.array.citys));
		//按联系人名字排序
		Collections.sort(nameList, new PinyinComparator());
		for(int i=0; i<nameList.size(); i++){
			bean = new Contacts();
			bean.name = nameList.get(i);
			if ((nameList.get(i).charAt(0) >> 7) == 0) {
				// 判断是否为汉字，如果左移7为0就不是汉字，否则是汉字
				ch = nameList.get(i).toUpperCase().charAt(0);
			} else {
				ch = converterToFirstSpell(nameList.get(i)).charAt(0);  //取汉字首字母
			}
			//如果是英文字母直接赋值
			if (ch >= 65 && ch <= 90) {
				bean.sortKey = ch;
			} else {
				bean.sortKey = '#';
			}
			if (sortKey != bean.sortKey) {
				sortKey = bean.sortKey;
				bean.isHead = true;
			}
			beans.add(bean);
		}
		//初始化适配器
		lv.setAdapter(new MyAdapter(activity, beans));
		lv.setOnItemClickListener(listener);
		//输入框hint
		autotv_search.setHint(R.string.input_city);
		
	}
	
	OnItemClickListener listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			if (issearch) {
				intent.putExtra("name", beans_search.get(arg2).name);
			}else {
				intent.putExtra("name", beans.get(arg2).name);
			}
			activity.setResult(101, intent);
			activity.finish();
		}
	};
	
	/**
	 * 适配器
	 * @author Administrator
	 *
	 */
	public class MyAdapter extends BaseAdapter {
		Context context;
		List<Contacts> nameStrings = new ArrayList<SelectCitysActivity.Contacts>();
		LayoutInflater inflater;
		
		public MyAdapter(Context context, List<Contacts> names){
			this.context = context;
			this.nameStrings = names;
			inflater = LayoutInflater.from(activity);
		}
		
		@Override
		public int getCount() {
			return nameStrings.size();
		}
		
		@Override
		public Object getItem(int i) {
			return nameStrings.get(i);
		}
		
		@Override
		public long getItemId(int arg0) {
			return 0;
		}
		
		
		@SuppressLint("InflateParams")
		@Override
		public View getView(int i, View v, ViewGroup vg) {
			ViewHolder holder;
			if (v == null) {
				holder = new ViewHolder();
				v = inflater.inflate(R.layout.allcontacts_item, null);
				holder.tvName = (TextView) v.findViewById(R.id.tvName);
				v.setTag(holder);
			}else {
				holder = (ViewHolder) v.getTag();
			}
			
			holder.tvName.setText(nameStrings.get(i).name);
			
			if(issearch == false){
				if (nameStrings.get(i).isHead) {
					v.findViewById(R.id.llShowIndex).setVisibility(View.VISIBLE);
					((TextView) v.findViewById(R.id.tvIndex)).setText(""
							+ nameStrings.get(i).sortKey);
				}else {
					v.findViewById(R.id.llShowIndex).setVisibility(View.GONE);
				}
			}
			return v;
		}
		
	}
	
	static class ViewHolder{
		TextView tvName;
	}
	
	/**
	 * 联系人构造类
	 * @author Administrator
	 *
	 */
	class Contacts {
		String name = "";
		char sortKey;
		boolean isHead;
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.t_delete:
			autotv_search.setText("");
			break;
		case R.id.top_return:
			this.finish();
			break;
		case R.id.location_text:
			Intent intent = new Intent();
			intent.putExtra("name", locationView.getText().toString());
			activity.setResult(101, intent);
			activity.finish();
			break;
		case R.id.location_fresh_s:
			locationView.setEnabled(false);
			locationView.setText(R.string.getlocationing);
			locationView.setTextColor(getResources().getColor(R.color.top_blue));
			myLocation.destoryLocationManager();
			myLocation = new MyLocationManager(SelectCitysActivity.this.getApplicationContext(), 
					SelectCitysActivity.this);
			break;
		default:
			break;
		}
	}

	@Override
	public void onCurrentLocation(Location location) {
		// TODO Auto-generated method stub
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		if (latitude != 0 && longitude != 0) {
			new Thread(getAddressName).start();
		}else {
			myHandler.sendEmptyMessage(1);
		}
	}
	
	/**
	 * 
	 * 用线程异步获取
	 */
	Runnable getAddressName = new Runnable() {
		public void run() {
			getLocationAddress(latitude, longitude);
		}
	};
	
	/**
	 * 通过经纬度获取地址
	 * 
	 * @param point
	 * @return
	 */
	private void getLocationAddress(double lat, double lon) {
		Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
		try {
			List<Address> addresses = geoCoder.getFromLocation(
					lat, lon, 1);
			if (addresses != null && addresses.size() != 0) {
				Address address = addresses.get(0);
				cityString = address.getLocality();
				latitude = address.getLatitude();
				longitude = address.getLongitude();
			}
			if (cityString.equals("") || latitude == 0 || longitude == 0) {
				myHandler.sendEmptyMessage(1);
			}else {
				myHandler.sendEmptyMessage(2);
			}
			
		} catch (IOException e) {
			cityString = "";
			e.printStackTrace();
		}
	}
	
	private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				locationView.setText(R.string.locationfail);
				locationView.setTextColor(getResources().getColor(R.color.red));
				locationView.setEnabled(false);
				break;
			case 2:
				editor.putString("city", cityString);
				editor.putString("ll", latitude + "," + longitude);
				editor.commit();
				locationView.setText(cityString);
				locationView.setTextColor(getResources().getColor(R.color.top_blue));
				locationView.setEnabled(true);
				break;

			default:
				break;
			}
		}
	};
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		myLocation.destoryLocationManager();
	}
	
}
