package com.jianzhiniu.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianzhiniu.adapter.NewjoblistAdapter;
import com.jianzhiniu.config.MyApplication;
import com.jianzhiniu.network.JsonTool;
import com.jianzhiniu.network.MapUtil;
import com.jianzhiniu.network.NetworkUtil;
import com.jianzhiniu.network.WebServiceUIAsyncTask;
import com.jianzhiniu.utils.MyLocationManager;
import com.jianzhiniu.utils.MyLocationManager.LocationCallBack;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.MyProgressDialog;
import com.jianzhiniu.views.XListView;

public class NearbyJobActivity extends BaseActivity implements 
	LocationCallBack, XListView.IXListViewListener{

	private Activity activity = NearbyJobActivity.this;
	
	private List<Map<String, String>> xlist = new ArrayList<Map<String,String>>();
	private List<Map<String, String>> mlist = new ArrayList<Map<String,String>>();
	private XListView xListView;
	private NewjoblistAdapter xAdapter;
	private TextView topTextView, locationView;
	private ImageView returnView, refreshView;
    private int page = 1, size = 10, itemdis;
    private MyLocationManager myLocation;
	private double latitude = 0, longitude = 0, distance = 15000;
	private String addressName = "", cityString = "", position = "", dist;
	private SharedPreferences shared;
	private SharedPreferences.Editor editor;
	private boolean isRefresh, isLoad, isFirst = true;
	private WebServiceUIAsyncTask wt;
	private MapUtil mapUtil;
	private static boolean isDes = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearbyjob_view);
		MyApplication.getInstance().addActivity(this);
		
		isDes = false;
		mapUtil = new MapUtil(activity);
		shared = getSharedPreferences("location", Context.MODE_PRIVATE);
		editor = shared.edit();
		xListView = (XListView)findViewById(R.id.xlistview);
		returnView = (ImageView)findViewById(R.id.top_return);
		refreshView = (ImageView)findViewById(R.id.location_fresh);
		topTextView = (TextView)findViewById(R.id.top_centertext);
		locationView = (TextView)findViewById(R.id.locationtext);
		
		myLocation = new MyLocationManager(NearbyJobActivity.this.getApplicationContext(),
				NearbyJobActivity.this);
		
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(false);
        xListView.setAutoLoadEnable(true);
        xListView.setXListViewListener(this);
        xListView.setFooterDividersEnabled(false);
//        xListView.setRefreshTime(getTime());

        topTextView.setText(R.string.nearjob);
        returnView.setOnClickListener(this);
        refreshView.setOnClickListener(this);
        
        xAdapter = new NewjoblistAdapter(activity, xlist, true);
        xListView.setAdapter(xAdapter);
        xListView.setOnItemClickListener(NewjobitemClick);
	}
	
	
	OnItemClickListener NewjobitemClick = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if (arg2 != 0) {
				Intent jIntent = new Intent(activity, JobInfoActivity.class);
				jIntent.putExtra("id", mlist.get(arg2 - 1).get("jobid"));
				jIntent.putExtra("eid", mlist.get(arg2 - 1).get("entid"));
				dist = mlist.get(arg2 - 1).get("distance");
				if (dist.contains(".")) {
					itemdis = Integer.parseInt(dist.substring(0, dist.indexOf(".")));
				}else {
					itemdis = Integer.parseInt(dist);
				}
				if (itemdis < 1000) {
					jIntent.putExtra("dis", itemdis + " m");
				}else {
					jIntent.putExtra("dis", itemdis/1000 + " km");
				}
				startActivity(jIntent);
			}
		}
	};
	
	/**
	 * 使用异步的方式请求数据
	 */
	public void ExecuteAsyncTask(String method, Map<String, Object> map) {
		wt = new WebServiceUIAsyncTask(activity) {
			
			@Override
			public void onTaskStartDoInUI(String... values) {
				// TODO Auto-generated method stub
				//开始请求数据，显示加载框
				if (isFirst) {
					MyProgressDialog.showDialog(activity, 0);
				}
			}
			
			@Override
			public void onGetDataSuccessfulDoInUI(String result) {
				// TODO Auto-generated method stub
				//成功获取数据，解析数据
				if (isFirst) {
					MyProgressDialog.Dismiss();
					isFirst = false;
				}
					xlist = JsonTool.NewJobjson(result, "JobInfoList", true);
					if (xlist == null) {
						onLoad();
						MyUtils.showToast2(activity, getResources().
								getString(R.string.data_parse_error));
					}else {
						if(xlist.get(0).containsKey("result"))
						{
							myHandler.sendEmptyMessage(4);
						}else{
							if (isRefresh) {
								mlist.clear();
							}
							mlist.addAll(xlist);
							xAdapter.refreshdata(mlist);
							onLoad();
							if (xlist.size() < size) {
								xListView.setPullLoadEnable(false);
							}else {
								xListView.setPullLoadEnable(true);
							}
							xListView.setFooterDividersEnabled(true);
						}
					}
					isLoad = false;
			}
			
			@Override
			public void onUseCacheDoInUI() {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onTaskWorkingDoInUI(String[] values) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onExceptionDoInUI(String[] values) {
				// TODO Auto-generated method stub
				if (isFirst) {
					MyProgressDialog.Dismiss();
					isFirst = false;
				}
				onLoad();
				isLoad = false;
				MyUtils.showToast2(activity, getResources().getString(R.string.iserror));
				for (String string : values) {
					Log.i("it is error", string + "");
				}
			}
			
			@Override
			public void onCancelledDoInUI(String result) {
				// TODO Auto-generated method stub
				try {
					MyProgressDialog.Dismiss();
				} catch (Exception e) {
					// TODO: handle exception
					Log.i("it is cancle error", e.getMessage());
				}
			}
			
			@Override
			public void onTimeoutDoInUI(String[] values) {
				// TODO Auto-generated method stub
				if (isFirst) {
					MyProgressDialog.Dismiss();
					isFirst = false;
				}
				MyUtils.showToast2(activity, getResources().getString(R.string.timeout));
			}
		};
		
		wt.myExecute(method, map, false);
	}
	
	
	@Override
	public void onRefresh() {
		if (NetworkUtil.isNetWorkConnected(activity)) {
			if (!isLoad) {
				isLoad = true;
				isRefresh = true;
				page = 1;
				ExecuteAsyncTask("GetJobInfoListByDistance", mapUtil.NearbyJobMap("", "", "", 
						latitude, longitude, distance, size, page));
			}
		}else {
			onLoad();
			myHandler.sendEmptyMessage(3);
		}
	}

    @Override
    public void onLoadMore() {
    	if (NetworkUtil.isNetWorkConnected(activity)) {
    		if (!isLoad) {
				isLoad = true;
				isRefresh = false;
				page ++;
				ExecuteAsyncTask("GetJobInfoListByDistance", mapUtil.NearbyJobMap("", "", "", 
						latitude, longitude, distance, size, page));
			}
    	}else {
    		onLoad();
    		myHandler.sendEmptyMessage(3);
    	}
    }

    private void onLoad() {
        xListView.stopRefresh();
        xListView.stopLoadMore();
//        xListView.setRefreshTime(getTime());
    }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.top_return:
			activity.finish();
			break;
		case R.id.location_fresh:
			locationView.setText(R.string.getlocationing);
			locationView.setTextColor(getResources().getColor(R.color.black));
			myLocation.destoryLocationManager();
			myLocation = new MyLocationManager(NearbyJobActivity.this.getApplicationContext(),
					NearbyJobActivity.this);
			break;
		default:
			break;
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
				position = address.getAddressLine(0);
			}
			if (cityString.equals("") || latitude == 0 || longitude == 0 
					|| position.equals("")) {
				myHandler.sendEmptyMessage(1);
			}else {
				myHandler.sendEmptyMessage(2);
			}
			
		} catch (IOException e) {
			cityString = "";
			e.printStackTrace();
		}
	}
	
	@SuppressLint("HandlerLeak") 
	private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				locationView.setText(R.string.locationfail);
				locationView.setTextColor(getResources().getColor(R.color.red));
				break;
			case 2:
				editor.putString("city", cityString);
				editor.putString("ll", latitude + "," + longitude);
				editor.commit();
				if (!isDes) {
					locationView.setText(position);
					locationView.setTextColor(getResources().getColor(R.color.top_blue));
					xListView.setVisibility(View.VISIBLE);
					xListView.autoRefresh();
				}
				break;
			case 3:
				MyUtils.showToast(activity, R.string.no_net);
				break;
			case 4:
				if (isRefresh) {
					mlist.clear();
					xAdapter.notifyDataSetChanged();
					xListView.setFooterDividersEnabled(false);
				}
				onLoad();
				xListView.setPullLoadEnable(false);
				MyUtils.showToast2(activity, xlist.get(0).get("msg"));
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
		isDes = true;
		myLocation.destoryLocationManager();
		if (wt != null && !wt.isCancelled()) {
			wt.cancel(true);
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
}
