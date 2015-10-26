package com.jianzhiniu.activity;

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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianzhiniu.adapter.MessageAdapter;
import com.jianzhiniu.config.MyApplication;
import com.jianzhiniu.network.JsonTool;
import com.jianzhiniu.network.MapUtil;
import com.jianzhiniu.network.NetworkUtil;
import com.jianzhiniu.network.WebServiceUIAsyncTask;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.MyProgressDialog;
import com.jianzhiniu.views.XListView;

/**
 * 最新通知、最新消息
 * @author Administrator
 *
 */
public class NewMessageActivity extends BaseActivity implements XListView.IXListViewListener{

	private Activity activity = NewMessageActivity.this;
	private Intent intent;
	private List<Map<String, String>> xlist = new ArrayList<Map<String,String>>();
	private List<Map<String, String>> mlist = new ArrayList<Map<String,String>>();
	private Map<String, String> rMap = new HashMap<String, String>();
	private XListView xListView;
	private MessageAdapter xAdapter;
	private TextView topTextView;
	private ImageView returnView;
    private int page = 1;
    private int size = 10;
    private int position;
    private boolean isNotice, isFirst = true, isRefresh, isLoad, isE, isReaded;
    private WebServiceUIAsyncTask wt;
    private MapUtil mapUtil;
    private SharedPreferences shared;
    private String itemtitle;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newmessage_view);
		MyApplication.getInstance().addActivity(this);
		
		IntentFilter filter = new IntentFilter("isread");
		this.registerReceiver(receiver, filter);
		intent = getIntent();
		mapUtil = new MapUtil(activity);
		xListView = (XListView)findViewById(R.id.xlistview);
		returnView = (ImageView)findViewById(R.id.top_return);
		topTextView = (TextView)findViewById(R.id.top_centertext);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(false);
        xListView.setAutoLoadEnable(true);
        xListView.setXListViewListener(this);
//        xListView.setRefreshTime(getTime());

        if (intent.getBooleanExtra("ise", false)) {
        	isE = true;
        	shared = getSharedPreferences("einfo", Context.MODE_PRIVATE);
		}else {
			shared = getSharedPreferences("info", Context.MODE_PRIVATE);
		}
        
        if (intent.getStringExtra("type").equals("notice")) {
			isNotice = true;
			topTextView.setText(R.string.newnotice);
		}else {
			topTextView.setText(R.string.newmessage);
		}
        
        returnView.setOnClickListener(this);
        
        xAdapter = new MessageAdapter(mlist, this, isNotice);
        xListView.setAdapter(xAdapter);
        xListView.setOnItemClickListener(NewjobitemClick);
        xListView.setFooterDividersEnabled(false);
        xListView.autoRefresh();
	}
	
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
//			mlist.get(position).put("read", "1");
		}
	};
	
	OnItemClickListener NewjobitemClick = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if (arg2 > 0) {
				position = arg2 - 1;
				if (!isNotice) {
					Intent mIntent = null;
					if (mlist.get(arg2 - 1).get("type").equals("0") || mlist.get(arg2 - 1).get("type").equals("1")) {
						//工作咨询，咨询回复
						mIntent = new Intent(activity, ConsultReplyActivity.class);
						mIntent.putExtra("id", mlist.get(arg2 - 1).get("reid"));
						mIntent.putExtra("ise", isE);
						if (isE) {
							mIntent.putExtra("fid", mlist.get(arg2 - 1).get("formid"));
						}else {
							mIntent.putExtra("fid", mlist.get(arg2 - 1).get("toid"));
						}
						startActivity(mIntent);
					}else if (mlist.get(arg2 - 1).get("type").equals("2")) {
						//兼职报名
						mIntent = new Intent(activity, JobManInfoActivity.class);
						mIntent.putExtra("id", mlist.get(arg2 - 1).get("reid"));
						mIntent.putExtra("type", 1);
						startActivity(mIntent);
					}else if (mlist.get(arg2 - 1).get("type").equals("3")) {
						//报名跟踪
						mIntent = new Intent(activity, ApplyFollowActivity.class);
						mIntent.putExtra("id", mlist.get(arg2 - 1).get("reid"));
						mIntent.putExtra("status", "2");
						startActivity(mIntent);
					}
					isReaded = true;
					ExecuteAsyncTask("UpdateMsgRead", mapUtil.IsReadedMap(mlist.get(arg2 - 1).get("id")));
				}else {
					//通知网页界面
					Intent wIntent = new Intent(activity, WebViewActivity.class);
					wIntent.putExtra("title", getString(R.string.detailcontent));
					wIntent.putExtra("url", MyUtils.getNoticeUrl() + "accessKey=" + 
							MyUtils.getAccesskey() + "&ID=" + mlist.get(arg2 - 1).get("id"));
					startActivity(wIntent);
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
				if (isReaded) {
					rMap = JsonTool.isSuccessfuljson(result);
					if (rMap == null) {
						MyUtils.showToast2(activity, getResources().
								getString(R.string.data_parse_error));
					}else {
						mlist.get(position).put("read", "1");
						xAdapter.refresh(mlist);
//						MyUtils.showToast2(activity, rMap.get("msg"));
					}
				}else {
					if (isNotice) {
						xlist = JsonTool.Noticejson(result, "NoticeList");
					}else {
						xlist = JsonTool.Messagejson(result, "MsgList");
					}
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
		
		this.unregisterReceiver(receiver);
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
	public void onRefresh() {
		if (NetworkUtil.isNetWorkConnected(activity)) {
			if (!isLoad) {
				isLoad = true;
				isRefresh = true;
				page = 1;
				isReaded = false;
				if (isNotice) {
					ExecuteAsyncTask("GetNoticeList", mapUtil.NoticeMap(size, page));
				}else {
					ExecuteAsyncTask("GetMsgList", mapUtil.MessageMap(size, page, shared.getString("id", "")));
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
				isReaded = false;
				if (isNotice) {
					ExecuteAsyncTask("GetNoticeList", mapUtil.NoticeMap(size, page));
				}else {
					ExecuteAsyncTask("GetMsgList", mapUtil.MessageMap(size, page, shared.getString("id", "")));
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
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.top_return:
			activity.finish();
			break;

		default:
			break;
		}
	}
	
	
}
