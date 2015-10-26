package com.jianzhiniu.fragments;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jianzhiniu.activity.JobInfoActivity;
import com.jianzhiniu.activity.MainActivity;
import com.jianzhiniu.activity.R;
import com.jianzhiniu.adapter.Dialogadapter;
import com.jianzhiniu.adapter.NewjoblistAdapter;
import com.jianzhiniu.network.JsonTool;
import com.jianzhiniu.network.MapUtil;
import com.jianzhiniu.network.NetworkUtil;
import com.jianzhiniu.network.WebServiceUIAsyncTask;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.MyProgressDialog;
import com.jianzhiniu.views.XListView;

/**
 * 最新兼职
 * @author Administrator
 *
 */
public class NewFragment extends Fragment implements OnClickListener, XListView.IXListViewListener{

	private Activity activity;
	private LinearLayout timeLayout, typeLayout;
	private LinearLayout dialogView;
	private ListView listView;
	private Dialogadapter dialogadapter;
	private boolean isTime;
	private WebServiceUIAsyncTask wt;
	private List<Map<String, String>> xlist = new ArrayList<Map<String,String>>();
	private XListView xListView;
	private NewjoblistAdapter xAdapter;
    private int page = 1;
    private int size = 10;
    private MapUtil mapUtil;
    private List<Map<String, String>> mlist = new ArrayList<Map<String,String>>();
    private List<Map<String, String>> dlist = new ArrayList<Map<String,String>>();
    private List<Map<String, String>> timelist = null;
    private List<Map<String, String>> typelist = null;
    private Map<String, String> allmap = new HashMap<String, String>();
    private SharedPreferences shared;
    private boolean isRefresh, isCloseBroad, isLoad, isMainlist, isAuto, isMainpar;
    private TextView timetext, typetext, mcitytext;
    private String timeid = "", typeid = "", cityString = "";
    private MainActivity mainActivity;
    private Intent intent;
	
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
		allmap.put("id", "");
		allmap.put("name", activity.getString(R.string.all));
		intent = activity.getIntent();
		if (!intent.hasExtra("newjob")) {
			isMainpar = true;
			mainActivity = (MainActivity)getActivity();
			mcitytext = (TextView)mainActivity.findViewById(R.id.top_city);
		}
		IntentFilter filter = new IntentFilter("getnew");
		activity.registerReceiver(receiver, filter);
		mapUtil = new MapUtil(activity);
		shared = activity.getSharedPreferences("location", Context.MODE_PRIVATE);
	}
	
	BroadcastReceiver receiver = new BroadcastReceiver(){
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (!isMainpar) {
				xListView.autoRefresh();
			}else {
				if (!cityString.equals(mcitytext.getText().toString())) {
					cityString = mcitytext.getText().toString();
					xListView.autoRefresh();
				}
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
				if (!isMainlist) {
					MyProgressDialog.showDialog(activity, 0);
				}
			}
			
			@Override
			public void onGetDataSuccessfulDoInUI(String result) {
				// TODO Auto-generated method stub
				//成功获取数据，解析数据
				if (!isMainlist) {
					MyProgressDialog.Dismiss();
				}
				if (isMainlist) {
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
				}else {
					if (isTime) {
						timelist = JsonTool.JTUTypejson(result, "JobTypeList", 2);
						if (timelist == null) {
							MyUtils.showToast2(activity, getResources().
									getString(R.string.data_parse_error));
						}else {
							if(timelist.get(0).containsKey("result"))
							{
								MyUtils.showToast2(activity, timelist.get(0).get("msg"));
							}else{
								timelist.add(0, allmap);
								dialogadapter.refresh(timelist);
								dialogView.setVisibility(View.VISIBLE);
							}
						}
					}else {
						typelist = JsonTool.JTUTypejson(result, "JobSearchList", 1);
						if (typelist == null) {
							MyUtils.showToast2(activity, getResources().
									getString(R.string.data_parse_error));
						}else {
							if(typelist.get(0).containsKey("result"))
							{
								MyUtils.showToast2(activity, typelist.get(0).get("msg"));
							}else{
								typelist.add(0, allmap);
								dialogadapter.refresh(typelist);
								dialogView.setVisibility(View.VISIBLE);
							}
						}
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
				if (!isMainlist) {
					MyProgressDialog.Dismiss();
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
					if (!isMainlist) {
						MyProgressDialog.Dismiss();
					}
				} catch (Exception e) {
					// TODO: handle exception
					Log.i("it is cancle error", e.getMessage());
				}
			}
			
			@Override
			public void onTimeoutDoInUI(String[] values) {
				// TODO Auto-generated method stub
				if (!isMainlist) {
					MyProgressDialog.Dismiss();
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
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(activity).inflate(R.layout.newfragment, null);
		timeLayout = (LinearLayout)view.findViewById(R.id.linear_time);
		typeLayout = (LinearLayout)view.findViewById(R.id.linear_type);
		dialogView = (LinearLayout)view.findViewById(R.id.dialog);
		timetext = (TextView)view.findViewById(R.id.new_timetext);
		typetext = (TextView)view.findViewById(R.id.new_typetext);
		listView = (ListView)view.findViewById(R.id.select_listview);
		
		dialogadapter = new Dialogadapter(xlist, activity);
		listView.setAdapter(dialogadapter);
		listView.setOnItemClickListener(selectItemClick);
		
		dialogView.setOnClickListener(this);
		timeLayout.setOnClickListener(this);
		typeLayout.setOnClickListener(this);
		
        xListView = (XListView) view.findViewById(R.id.list_view);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(false);
        xListView.setAutoLoadEnable(true);
        xListView.setXListViewListener(this);
        
//        xListView.setRefreshTime(getTime());

        xAdapter = new NewjoblistAdapter(activity, xlist, false);
        xListView.setAdapter(xAdapter);
        xListView.setOnItemClickListener(NewjobitemClick);
        
        
		return view;
	}
	
	OnItemClickListener selectItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			isAuto = true;
			if (isTime) {
				timetext.setText(timelist.get(arg2).get("name"));
				timeid = timelist.get(arg2).get("id");
			}else {
				typetext.setText(typelist.get(arg2).get("name"));
				typeid = typelist.get(arg2).get("id");
			}
			dialogView.setVisibility(View.GONE);
			xListView.setSelection(0);
			xListView.autoRefresh();
		}
	};
	
	OnItemClickListener NewjobitemClick = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if (arg2 != 0) {
				Intent infoIntent = new Intent(activity, JobInfoActivity.class);
				infoIntent.putExtra("id", mlist.get(arg2 - 1).get("jobid"));
				infoIntent.putExtra("eid", mlist.get(arg2 - 1).get("entid"));
				startActivity(infoIntent);
			}
		}
	};
	
	
	@Override
	public void onRefresh() {
		if (NetworkUtil.isNetWorkConnected(activity)) {
			if (!isLoad) {
				if (!isAuto) {
					timeid = "";
					typeid = "";
					timetext.setText(getString(R.string.choosetime));
					typetext.setText(getString(R.string.choosetypes));
				}
				isLoad = true;
				isRefresh = true;
				page = 1;
				isMainlist = true;
				ExecuteAsyncTask("GetJobInfoListByReleaseTime", mapUtil.NewJobMap(typeid, timeid, "", 
						isMainpar ? mcitytext.getText().toString() : "", size, page));
			}
		}else {
			onLoad();
			myHandler.sendEmptyMessage(1);
		}
		isAuto = false;
	}

    @Override
    public void onLoadMore() {
    	if (NetworkUtil.isNetWorkConnected(activity)) {
    		if (!isLoad) {
				isLoad = true;
				isRefresh = false;
				page ++;
				isMainlist = true;
				ExecuteAsyncTask("GetJobInfoListByReleaseTime", mapUtil.NewJobMap(typeid, timeid, "", 
						isMainpar ? mcitytext.getText().toString() : "", size, page));
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
		case R.id.linear_time:
			if (dialogView.getVisibility() == View.VISIBLE) {
				if (isTime) {
					dialogView.setVisibility(View.GONE);
				}else {
					if (timelist != null) {
						dialogadapter.refresh(timelist);
					}else {
						isMainlist = false;
						ExecuteAsyncTask("GetJobTypeList", mapUtil.AccesskeyMap());
					}
				}
			}else {
				if (timelist != null) {
					dialogadapter.refresh(timelist);
					dialogView.setVisibility(View.VISIBLE);
				}else {
					isMainlist = false;
					ExecuteAsyncTask("GetJobTypeList", mapUtil.AccesskeyMap());
				}
			}
			isTime = true;
			break;
		case R.id.linear_type:
			if (dialogView.getVisibility() == View.VISIBLE) {
				if (!isTime) {
					dialogView.setVisibility(View.GONE);
				}else {
					if (typelist != null) {
						dialogadapter.refresh(typelist);
					}else {
						isMainlist = false;
						ExecuteAsyncTask("GetJobSearchList", mapUtil.AccesskeyMap());
					}
				}
			}else {
				if (typelist != null) {
					dialogadapter.refresh(typelist);
					dialogView.setVisibility(View.VISIBLE);
				}else {
					isMainlist = false;
					ExecuteAsyncTask("GetJobSearchList", mapUtil.AccesskeyMap());
				}
			}
			isTime = false;
			break;
		case R.id.dialog:
			if (dialogView.getVisibility() == View.VISIBLE) {
				dialogView.setVisibility(View.GONE);
			}
			break;

		default:
			break;
		}
	}
	
	
}
