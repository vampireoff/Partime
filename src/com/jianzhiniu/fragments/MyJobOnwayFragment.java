package com.jianzhiniu.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.jianzhiniu.activity.JobInfoActivity;
import com.jianzhiniu.activity.R;
import com.jianzhiniu.adapter.MyJobOnwayAdapter;
import com.jianzhiniu.network.JsonTool;
import com.jianzhiniu.network.MapUtil;
import com.jianzhiniu.network.NetworkUtil;
import com.jianzhiniu.network.WebServiceUIAsyncTask;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.MyProgressDialog;
import com.jianzhiniu.views.XListView;

public class MyJobOnwayFragment extends Fragment implements XListView.IXListViewListener{

	private Activity activity;
	
	private List<Map<String, String>> xlist = new ArrayList<Map<String,String>>();
	private List<Map<String, String>> mlist = new ArrayList<Map<String,String>>();
	private XListView xListView;
	private MyJobOnwayAdapter xAdapter;
    private int page = 1, size = 10;
    private WebServiceUIAsyncTask wt;
    private MapUtil mapUtil;
    private boolean isLoad, isRefresh, isFirst = true, isE;
    private Intent intent;
    private SharedPreferences shared;
	
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
		intent = activity.getIntent();
		if (intent.getBooleanExtra("ise", false)) {
			isE = true;
			shared = activity.getSharedPreferences("einfo", Context.MODE_PRIVATE);
		}else {
			shared = activity.getSharedPreferences("info", Context.MODE_PRIVATE);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(activity).inflate(R.layout.myjobonway_fview, null);
		
        xListView = (XListView) view.findViewById(R.id.xlistview);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(false);
        xListView.setAutoLoadEnable(true);
        xListView.setXListViewListener(this);
//        xListView.setRefreshTime(getTime());

        xAdapter = new MyJobOnwayAdapter(xlist, activity);
        xListView.setAdapter(xAdapter);
        xListView.setOnItemClickListener(NewjobitemClick);
        xListView.setFooterDividersEnabled(false);
        xListView.autoRefresh();
        
		return view;
	}
	
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
					xlist = JsonTool.MyJobjson(result, "JobInfoList");
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
							xAdapter.refresh(mlist);
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
				Intent jIntent = new Intent(activity, JobInfoActivity.class);
				jIntent.putExtra("id", mlist.get(arg2 - 1).get("jobid"));
				jIntent.putExtra("eid", mlist.get(arg2 - 1).get("entid"));
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
				if (isE) {
					ExecuteAsyncTask("GetMyJobInfoListByEnt", mapUtil.MyJobMap(1, shared.getString(
							"id", ""), 1, size, page));
				}else {
					ExecuteAsyncTask("GetMyJobInfoListBySeeker", mapUtil.MyJobMap(0, shared.getString(
							"id", ""), 1, size, page));
				}
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
				if (isE) {
					ExecuteAsyncTask("GetMyJobInfoListByEnt", mapUtil.MyJobMap(1, shared.getString(
							"id", ""), 1, size, page));
				}else {
					ExecuteAsyncTask("GetMyJobInfoListBySeeker", mapUtil.MyJobMap(0, shared.getString(
							"id", ""), 1, size, page));
				}
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
	
}

