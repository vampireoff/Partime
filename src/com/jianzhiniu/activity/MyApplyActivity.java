package com.jianzhiniu.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianzhiniu.adapter.MyApplylistAdapter;
import com.jianzhiniu.config.MyApplication;
import com.jianzhiniu.network.JsonTool;
import com.jianzhiniu.network.MapUtil;
import com.jianzhiniu.network.NetworkUtil;
import com.jianzhiniu.network.WebServiceUIAsyncTask;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.MyProgressDialog;
import com.jianzhiniu.views.XListView;

/**
 * 我的申请界面
 * @author Administrator
 *
 */
public class MyApplyActivity extends BaseActivity implements XListView.IXListViewListener{

	private Activity activity = MyApplyActivity.this;
	private TextView centerView;
	private ImageView returnView;
	private List<Map<String, String>> xlist = new ArrayList<Map<String,String>>();
	private List<Map<String, String>> mlist = new ArrayList<Map<String,String>>();
	private XListView xListView;
	private MyApplylistAdapter xAdapter;
	private Intent intent;
    private int page = 1;
    private int size = 10;
    private SharedPreferences shared;
    private WebServiceUIAsyncTask wt;
    private MapUtil mapUtil;
	private boolean isLoad, isRefresh, isFirst = true, isE, isCancle = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myapply_view);
		MyApplication.getInstance().addActivity(this);
		
		IntentFilter filter = new IntentFilter("cancle");
		this.registerReceiver(receiver, filter);
		
		intent = getIntent();
		if (intent.getBooleanExtra("ise", false)) {
			isE = true;
			shared = getSharedPreferences("einfo", Context.MODE_PRIVATE);
		}else {
			shared = getSharedPreferences("info", Context.MODE_PRIVATE);
		}
		mapUtil = new MapUtil(activity);
		centerView = (TextView)findViewById(R.id.top_centertext);
		returnView = (ImageView)findViewById(R.id.top_return);
		xListView = (XListView)findViewById(R.id.xlistview);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(false);
        xListView.setAutoLoadEnable(true);
        xListView.setXListViewListener(this);
//        xListView.setRefreshTime(getTime());

        xAdapter = new MyApplylistAdapter(activity, mlist);
        xListView.setAdapter(xAdapter);
        xListView.setOnItemClickListener(NewjobitemClick);
        xListView.setFooterDividersEnabled(false);
		
        centerView.setText(R.string.myapply);
		returnView.setOnClickListener(this);
	}
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			isCancle = true;
		}
	};
	
	protected void onResume() {
		super.onResume();
		if (isCancle) {
			xListView.setSelection(0);
			xListView.autoRefresh();
			isCancle = false;
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
					xlist = JsonTool.MyApplyjson(result, "JobRegList");
					if (xlist == null) {
						onLoad();
						MyUtils.showToast2(activity, getResources().
								getString(R.string.data_parse_error));
					}else {
						if(xlist.get(0).containsKey("result"))
						{
							myHandler.sendEmptyMessage(2);
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
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (wt != null && !wt.isCancelled()) {
			wt.cancel(true);
		}
		
		activity.unregisterReceiver(receiver);
	}
	
	private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				MyUtils.showToast(activity, R.string.no_net);
				break;
			case 2:
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
	
	OnItemClickListener NewjobitemClick = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if (arg2 != 0) {
				Intent jIntent = new Intent(activity, ApplyFollowActivity.class);
				jIntent.putExtra("id", mlist.get(arg2 - 1).get("regid"));
				jIntent.putExtra("jobid", mlist.get(arg2 - 1).get("jobid"));
				jIntent.putExtra("jsid", mlist.get(arg2 - 1).get("jsid"));
				jIntent.putExtra("eid", mlist.get(arg2 - 1).get("entid"));
				jIntent.putExtra("title", mlist.get(arg2 - 1).get("title"));
				jIntent.putExtra("time", mlist.get(arg2 - 1).get("issuetime").split(" ")[0]);
				jIntent.putExtra("city", mlist.get(arg2 - 1).get("city"));
				jIntent.putExtra("status", mlist.get(arg2 - 1).get("status"));
				jIntent.putExtra("price", mlist.get(arg2 - 1).get("wage") + mlist.get(arg2 - 1).get("unit"));
				startActivity(jIntent);
			}
		}
	};
	
	@Override
	public void onRefresh() {
		if (NetworkUtil.isNetWorkConnected(activity)) {
			if (!isLoad) {
				isLoad = true;
				isRefresh = true;
				page = 1;
				ExecuteAsyncTask("GetMyJobRegList", mapUtil.MyApplyMap(shared.getString(
						"id", ""), size, page));
			}
		}else {
			onLoad();
			myHandler.sendEmptyMessage(1);
		}
	}

    @Override
    public void onLoadMore() {
    	if (NetworkUtil.isNetWorkConnected(activity)) {
    		if (!isLoad) {
				isLoad = true;
				isRefresh = false;
				page ++;
				ExecuteAsyncTask("GetMyJobRegList", mapUtil.MyApplyMap(shared.getString(
						"id", ""), size, page));
			}
    	}else {
    		onLoad();
    		myHandler.sendEmptyMessage(1);
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
		super.onClick(v);
		switch (v.getId()) {
		case R.id.top_return:
			this.finish();
			break;

		default:
			break;
		}
	}
}
