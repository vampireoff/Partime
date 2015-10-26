package com.jianzhiniu.fragments;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianzhiniu.activity.R;
import com.jianzhiniu.network.JsonTool;
import com.jianzhiniu.network.MapUtil;
import com.jianzhiniu.network.WebServiceUIAsyncTask;
import com.jianzhiniu.utils.ImageUtil;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.MyProgressDialog;

/**
 * 企业信息
 * @author Administrator
 *
 */
public class EnterpriseInfoFragment extends Fragment{

	private Activity activity;
	private TextView name, introduce;
	private ImageView logo, start;
	private WebServiceUIAsyncTask wt;
	private MapUtil mapUtil;
	private Map<String, String> infomap = new HashMap<String, String>();
	private Bitmap bitmap;
	private boolean isUn;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mapUtil = new MapUtil(activity);
		IntentFilter filter = new IntentFilter("getcompany");
		activity.registerReceiver(receiver, filter);
	}
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (activity.getIntent().hasExtra("eid")) {
				ExecuteAsyncTask("GetEnterpriseInfo", mapUtil.EntInfoMap(activity.getIntent()
						.getStringExtra("eid")));
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
				MyProgressDialog.showDialog(activity, 0);
			}
			
			@Override
			public void onGetDataSuccessfulDoInUI(String result) {
				// TODO Auto-generated method stub
				//成功获取数据，解析数据
				MyProgressDialog.Dismiss();
				infomap = JsonTool.Loginjson(result, "EnterpriseInfo");
				if (infomap == null) {
					MyUtils.showToast2(activity, getResources().
							getString(R.string.data_parse_error));
				}else {
					if(infomap.get("result").equals("1"))
					{
						myHandler.sendEmptyMessage(1);
					}else{
						MyUtils.showToast2(activity, infomap.get("msg"));
					}
				}
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
				MyProgressDialog.Dismiss();
				MyUtils.showToast2(activity, getResources().getString
						
						(R.string.iserror));
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
				MyProgressDialog.Dismiss();
				MyUtils.showToast2(activity, getResources().getString(R.string.timeout));
			}
		};
		
		wt.myExecute(method, map, false);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (wt != null && !wt.isCancelled()) {
			wt.cancel(true);
		}
		if (!isUn) {
			activity.unregisterReceiver(receiver);
		}
	}
	
	/**
	 * 线程获取头像
	 * @author Administrator
	 *
	 */
	private class thread implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			bitmap = ImageUtil.getBitmapFromPathWH(infomap.get("pic").equals("") ? 
					"" : MyUtils.getPicUrl() + 
					infomap.get("pic"), 120, 120);
			if (bitmap != null) {
				myHandler.sendEmptyMessage(2);
			}
		}
	}
	
	private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				name.setText(MyUtils.ParseString(infomap.get("companyname")));
				introduce.setText(MyUtils.ParseString(infomap.get("profile")));
				if (infomap.get("authentication").equals("1")) {
					start.setVisibility(View.VISIBLE);
				}
				
				new Thread(new thread()).start();
				
				activity.unregisterReceiver(receiver);
				isUn = true;
				break;
			case 2:
				logo.setImageBitmap(ImageUtil.toRoundCorner(bitmap, 7));
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(activity).inflate(R.layout.enterpriseinfo_fview, null);
		name = (TextView)view.findViewById(R.id.enterprise_name);
		introduce = (TextView)view.findViewById(R.id.enterp_content);
		logo = (ImageView)view.findViewById(R.id.enterprise_logo);
		start = (ImageView)view.findViewById(R.id.img_start);
		return view;
	}

}
