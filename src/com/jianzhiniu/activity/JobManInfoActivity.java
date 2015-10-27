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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianzhiniu.adapter.JobManInfoAdapter;
import com.jianzhiniu.config.MyApplication;
import com.jianzhiniu.network.JsonTool;
import com.jianzhiniu.network.MapUtil;
import com.jianzhiniu.network.WebServiceUIAsyncTask;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.MyAlertDialog;
import com.jianzhiniu.views.MyAlertDialog.AlertCallback;
import com.jianzhiniu.views.MyProgressDialog;
import com.jianzhiniu.views.XListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * 兼职管理界面
 * @author Administrator
 *
 */
public class JobManInfoActivity extends BaseActivity implements XListView.IXListViewListener{

	private Activity activity = JobManInfoActivity.this;
	private TextView topcenter, title, time, city, price, action, apply, 
		hire, refuse, mhire;
	private ImageView returnView, typeimg;
	private List<Map<String, String>> xlist = new ArrayList<Map<String,String>>();
	private Map<String, String> rMap = new HashMap<String, String>();
	private Map<String, String> infoMap = null;
	private XListView xListView;
	private JobManInfoAdapter xAdapter;
	private View jobinfoView, actionView, statusView;
    private int page = 1, size = 1000, position;
    private Intent intent, bIntent, toIntent;
    private MapUtil mapUtil;
    private WebServiceUIAsyncTask wt;
    private String webtype;
    private IntentFilter filter;
    private ImageLoader loader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private MyAlertDialog myAlertDialog;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jobmanageinfo_view);
		MyApplication.getInstance().addActivity(this);
		
		myAlertDialog = new MyAlertDialog(activity, callback);
		filter = new IntentFilter("result");
		this.registerReceiver(receiver, filter);
		intent = getIntent();
		mapUtil = new MapUtil(activity);
		topcenter = (TextView)findViewById(R.id.top_centertext);
		mhire = (TextView)findViewById(R.id.mhirecount);
		title = (TextView)findViewById(R.id.jobname);
		city = (TextView)findViewById(R.id.didian_text);
		time = (TextView)findViewById(R.id.time_text);
		price = (TextView)findViewById(R.id.price_text);
		action = (TextView)findViewById(R.id.action_text);
		apply = (TextView)findViewById(R.id.applycount);
		hire = (TextView)findViewById(R.id.hirecount);
		refuse = (TextView)findViewById(R.id.refusecount);
		returnView = (ImageView)findViewById(R.id.top_return);
		typeimg = (ImageView)findViewById(R.id.img);
		jobinfoView = (View)findViewById(R.id.to_jobinfoview);
		actionView = (View)findViewById(R.id.action_linear);
		statusView = (View)findViewById(R.id.statusview);
		
		loader.init(ImageLoaderConfiguration.createDefault(this));
		//图片参数设置
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.imgloading_r)
		.showImageForEmptyUri(R.drawable.imgerror)
		.showImageOnFail(R.drawable.imgerror).cacheOnDisc()
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new FadeInBitmapDisplayer(300)).build();
		
		if (intent.hasExtra("city")) {
			loader.displayImage(MyUtils.getIconUrl() + intent.getStringExtra("jsid") + ".png", typeimg, options);
			title.setText(intent.getStringExtra("title"));
			city.setText(intent.getStringExtra("city"));
			time.setText(intent.getStringExtra("time"));
			price.setText("$ " + intent.getStringExtra("price"));
			
			if (intent.getIntExtra("type", 1) == 1) {
				actionView.setVisibility(View.VISIBLE);
				webtype = "list";
				ExecuteAsyncTask("GetJobRegListWithTJ", mapUtil.JobApplyersMap(
						intent.getStringExtra("id"), size, page));
			}else {
				statusView.setVisibility(View.GONE);
				mhire.setVisibility(View.VISIBLE);
				if (intent.getIntExtra("type", 1) == 2) {
					action.setText(R.string.xj);
					actionView.setVisibility(View.VISIBLE);
				}else if (intent.getIntExtra("type", 1) == 3) {
					actionView.setVisibility(View.GONE);
				}
				webtype = "hlist";
				ExecuteAsyncTask("GetJobEmployList", mapUtil.JobApplyersMap(
						intent.getStringExtra("id"), size, page));
			}
		}else {
			webtype = "info";
			ExecuteAsyncTask("GetJobRegInfo", mapUtil.ApplyFollowMap(intent.getStringExtra("id")));
		}
		
		topcenter.setText(R.string.rec_status);
		returnView.setOnClickListener(this);
		actionView.setOnClickListener(this);
		
		xListView = (XListView)findViewById(R.id.xlistview);
        xListView.setPullRefreshEnable(false);
        xListView.setPullLoadEnable(false);
        xListView.setAutoLoadEnable(false);
        xListView.setXListViewListener(this);
//        xListView.setRefreshTime(getTime());

        xAdapter = new JobManInfoAdapter(xlist, activity);
        xListView.setAdapter(xAdapter);
        xListView.setOnItemClickListener(itemClick);
        jobinfoView.setOnClickListener(this);
        
	}
	
	AlertCallback callback = new AlertCallback() {
		
		@Override
		public void ybutton() {
			// TODO Auto-generated method stub
			if (intent.getIntExtra("type", 1) == 1) {
				webtype = "zm";
				ExecuteAsyncTask("UpJobStatus", mapUtil.ZMXJMap(intent.getStringExtra("id"), 1));
			}else {
				webtype = "xj";
				ExecuteAsyncTask("UpJobStatus", mapUtil.ZMXJMap(intent.getStringExtra("id"), 2));
			}
		}
	};
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getBooleanExtra("ishire", false)) {
				xlist.get(position).put("status", getString(R.string.ishired));
				xlist.get(position).put("statusnum", "3");
				xAdapter.refresh(xlist);
				hire.setText(getString(R.string.lycount) + (Integer.valueOf(hire.getText().toString().split("：")[1]) + 1));
			}else {
				xlist.get(position).put("status", getString(R.string.isrefused));
				xlist.get(position).put("statusnum", "2");
				xAdapter.refresh(xlist);
				refuse.setText(getString(R.string.jjcount) + (Integer.valueOf(refuse.getText().toString().split("：")[1]) + 1));
			}
		}
	};
	
	OnItemClickListener itemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if (arg2 > 0) {
				position = arg2 - 1;
				toIntent = new Intent(activity, PersonalCenterActivity.class);
				toIntent.putExtra("id", xlist.get(arg2 - 1).get("seekerid"));
				toIntent.putExtra("rid", xlist.get(arg2 - 1).get("regid"));
				toIntent.putExtra("read", true);
				toIntent.putExtra("type", intent.getIntExtra("type", 1));
				toIntent.putExtra("state", xlist.get(arg2 - 1).get("statusnum"));
				startActivity(toIntent);
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
					if (webtype.equals("zm")) {
						rMap = JsonTool.isSuccessfuljson(result);
						if (rMap == null) {
							MyUtils.showToast2(activity, getResources().
									getString(R.string.data_parse_error));
						}else {
							MyUtils.showToast2(activity, rMap.get("msg"));
							if (rMap.get("result").equals("1")) {
								actionView.setVisibility(View.GONE);
								bIntent = new Intent("remove1");
								activity.sendBroadcast(bIntent);
							}
						}
					}else if (webtype.equals("xj")) {
						rMap = JsonTool.isSuccessfuljson(result);
						if (rMap == null) {
							MyUtils.showToast2(activity, getResources().
									getString(R.string.data_parse_error));
						}else {
							MyUtils.showToast2(activity, rMap.get("msg"));
							if (rMap.get("result").equals("1")) {
								actionView.setVisibility(View.GONE);
								bIntent = new Intent("remove2");
								activity.sendBroadcast(bIntent);
							}
						}
					}else if(webtype.equals("list")){
						
						xlist = JsonTool.JobApplyerListjson(result);
						if (xlist == null) {
							MyUtils.showToast2(activity, getResources().
									getString(R.string.data_parse_error));
						}else {
							if(xlist.get(0).containsKey("result"))
							{
								MyUtils.showToast2(activity, xlist.get(0).get("msg"));
							}else {
								apply.setText(getString(R.string.shqcount) + xlist.get(0).get("total"));
								hire.setText(getString(R.string.lycount) + xlist.get(0).get("hire"));
								refuse.setText(getString(R.string.jjcount) + xlist.get(0).get("refuse"));
								xlist.remove(0);
								xAdapter.refresh(xlist);
							}
						}
					}else if(webtype.equals("info")){
						infoMap = JsonTool.RegJobInfojson(result);
						if (infoMap == null) {
							MyUtils.showToast2(activity, getResources().
									getString(R.string.data_parse_error));
						}else {
							if (infoMap.containsKey("result")) {
								MyUtils.showToast2(activity, infoMap.get("msg"));
							}else {
								loader.displayImage(MyUtils.getIconUrl() + infoMap.get("jsid") + ".png", typeimg, options);
								title.setText(infoMap.get("title"));
								city.setText(infoMap.get("city"));
								time.setText(infoMap.get("issuetime").split(" ")[0]);
								price.setText("$ " + infoMap.get("wage") + infoMap.get("unit"));
								
								webtype = "list";
								ExecuteAsyncTask("GetJobRegListWithTJ", mapUtil.JobApplyersMap(
										infoMap.get("jobid"), size, page));
							}
						}
					}else {
						xlist = JsonTool.HireListjson(result);
						if (xlist == null) {
							MyUtils.showToast2(activity, getResources().
									getString(R.string.data_parse_error));
						}else {
							if(xlist.get(0).containsKey("result"))
							{
								MyUtils.showToast2(activity, xlist.get(0).get("msg"));
							}else {
								mhire.setText(getString(R.string.lycount) + xlist.size());
								xAdapter.refresh(xlist);
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
				MyProgressDialog.Dismiss();
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
		this.unregisterReceiver(receiver);
	}
	
	@Override
    public void onRefresh() {
    }

    @Override
    public void onLoadMore() {
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.top_return:
			this.finish();
			break;
		case R.id.to_jobinfoview:
			Intent jIntent = new Intent(this, JobInfoActivity.class);
			if (intent.hasExtra("eid")) {
				jIntent.putExtra("id", intent.getStringExtra("id"));
				jIntent.putExtra("eid", intent.getStringExtra("eid"));
				startActivity(jIntent);
			}else if (infoMap != null && !infoMap.containsKey("result")) {
				jIntent.putExtra("id", infoMap.get("jobid"));
				jIntent.putExtra("eid", infoMap.get("entid"));
				startActivity(jIntent);
			}
			break;
		case R.id.action_linear:
			//招满、下架
			if (intent.getIntExtra("type", 1) == 1) {
				myAlertDialog.showMyAlertDialog(R.string.suretozm);
			}else {
				myAlertDialog.showMyAlertDialog(R.string.suretoxj);
			}
			break;
		default:
			break;
		}
	}
}
