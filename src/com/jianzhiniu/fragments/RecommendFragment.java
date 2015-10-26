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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.jianzhiniu.activity.JobInfoActivity;
import com.jianzhiniu.activity.R;
import com.jianzhiniu.adapter.NewjoblistAdapter;
import com.jianzhiniu.network.JsonTool;
import com.jianzhiniu.network.MapUtil;
import com.jianzhiniu.network.NetworkUtil;
import com.jianzhiniu.network.WebServiceUIAsyncTask;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.MyProgressDialog;
import com.jianzhiniu.views.XListView;

/**
 * 感兴趣兼职
 * @author Administrator
 *
 */
public class RecommendFragment extends Fragment implements OnClickListener, XListView.IXListViewListener{

	private Activity activity;
	
	private List<Map<String, String>> xlist = new ArrayList<Map<String,String>>();
	private List<Map<String, String>> mlist = new ArrayList<Map<String,String>>();
	private XListView xListView;
	private NewjoblistAdapter xAdapter;
	private Intent intent;
    private int page = 1;
    private int size = 10;
    private SharedPreferences shared;
    private WebServiceUIAsyncTask wt;
    private MapUtil mapUtil;
    private String cityString;
	private boolean isLoad, isRefresh, isFirst = true;
    
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
		intent = activity.getIntent();
		cityString = intent.getStringExtra("city");
		shared = activity.getSharedPreferences("info", Context.MODE_PRIVATE);
		mapUtil = new MapUtil(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(activity).inflate(R.layout.recommend_view, null);
		
        xListView = (XListView) view.findViewById(R.id.xlistview);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(false);
        xListView.setAutoLoadEnable(true);
        xListView.setXListViewListener(this);
//        xListView.setRefreshTime(getTime());

        xAdapter = new NewjoblistAdapter(activity, xlist, false);
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
					xlist = JsonTool.NewJobjson(result, "JobInfoList", false);
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
				ExecuteAsyncTask("GetJobInfoListByInterest", mapUtil.InterestJobMap(shared.getString(
						"jobsearchid", ""), "", "", "", size, page));
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
				ExecuteAsyncTask("GetJobInfoListByInterest", mapUtil.InterestJobMap(shared.getString(
						"jobsearchid", ""), "", "", "", size, page));
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
		switch (v.getId()) {
		case R.id.top_return:
			
			break;

		default:
			break;
		}
	}
	
	
}
