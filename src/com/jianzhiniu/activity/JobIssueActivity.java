package com.jianzhiniu.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jianzhiniu.adapter.Dialogadapter2;
import com.jianzhiniu.config.MyApplication;
import com.jianzhiniu.network.JsonTool;
import com.jianzhiniu.network.MapUtil;
import com.jianzhiniu.network.WebServiceUIAsyncTask;
import com.jianzhiniu.utils.MyLocationManager;
import com.jianzhiniu.utils.MyLocationManager.LocationCallBack;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.MyProgressDialog;
import com.kyleduo.switchbutton.SwitchButton;

/**
 * 兼职发布界面
 * @author Administrator
 *
 */
public class JobIssueActivity extends BaseActivity implements LocationCallBack{

	private Activity activity = JobIssueActivity.this;
	private TextView topViewtext, jobtypetext, timetypetext, unittext, citytext, jtcancle;
	private ImageView returnView;
	private EditText jobdescribe, jobtitle, jobpeople, jobtreatment, jobtime, jobaddress, 
			linkman, email, zxphone;
	private ScrollView scrollView;
	private View historyView, unitView, locationView, cityselectView, jobtypeView, timetypeView;
	private SwitchButton switchButton;
	private Button issueButton;
	private LinearLayout dialogView;
	private ListView listView;
	private Map<String, String> mmap = new HashMap<String, String>();
	private List<Map<String, String>> mList = new ArrayList<Map<String,String>>();
	private List<Map<String, String>> list = new ArrayList<Map<String,String>>();
	private List<Map<String, String>> jtlist = new ArrayList<Map<String,String>>();
	private List<Map<String, String>> ttlist = new ArrayList<Map<String,String>>();
	private List<Map<String, String>> utlist = new ArrayList<Map<String,String>>();
	private Dialogadapter2 dialogadapter;
	private ListView dialogListView;
	private Dialog alertDialog;
	private int width, height;
	private MyLocationManager myLocation;
	private double latitude = 0, longitude = 0;
	private String cityString = "", position = "", idjtString, idttString, idutString;
	private static String webtype = "";
	private boolean isCheck = true;
	private WebServiceUIAsyncTask wt;
	private MapUtil mapUtil;
	private SharedPreferences shared;
	private SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.issuejob_view);
		
		shared = getSharedPreferences("einfo", Context.MODE_PRIVATE);
		mapUtil = new MapUtil(activity);
		MyApplication.getInstance().addActivity(this);
		historyView = (View)findViewById(R.id.history_view);
		jobtypeView = (View)findViewById(R.id.jobtype_view);
		timetypeView = (View)findViewById(R.id.timetype_view);
		unitView = (View)findViewById(R.id.unitlinear);
		locationView = (View)findViewById(R.id.getloclinear);
		cityselectView = (View)findViewById(R.id.cityselect_view);
		switchButton = (SwitchButton)findViewById(R.id.onoff_zx);
		issueButton = (Button)findViewById(R.id.issuejob_btn);
		jobdescribe = (EditText)findViewById(R.id.jobdescride_edit);
		jobtitle = (EditText)findViewById(R.id.jobtitle_edit);
		jobpeople = (EditText)findViewById(R.id.jobpcount_edit);
		jobtreatment = (EditText)findViewById(R.id.jobtreat_edit);
		jobtime = (EditText)findViewById(R.id.jobtime_edit);
		jobaddress = (EditText)findViewById(R.id.jobaddress_edit);
		linkman = (EditText)findViewById(R.id.linkman_edit);
		email = (EditText)findViewById(R.id.linkemail_edit);
		zxphone = (EditText)findViewById(R.id.zxphone_edit);
		topViewtext = (TextView)findViewById(R.id.top_centertext);
		jobtypetext = (TextView)findViewById(R.id.jobtype_text);
		timetypetext = (TextView)findViewById(R.id.timetype_text);
		unittext = (TextView)findViewById(R.id.jobunit);
		citytext = (TextView)findViewById(R.id.jobcity_text);
		returnView = (ImageView)findViewById(R.id.top_return);
		scrollView = (ScrollView)findViewById(R.id.scrollview);
		dialogView = (LinearLayout)findViewById(R.id.dialog);
		listView = (ListView)findViewById(R.id.select_listview);
		
		linkman.setText(shared.getString("contactperson", ""));
		email.setText(shared.getString("email", ""));
		zxphone.setText(shared.getString("mobile", ""));
		dialogadapter = new Dialogadapter2(mList, activity);
		listView.setAdapter(dialogadapter);
		listView.setOnItemClickListener(selectItemClick);
		
		topViewtext.setText(R.string.issuejob);
		switchButton.setChecked(true);
		returnView.setOnClickListener(this);
		dialogView.setOnClickListener(this);
		historyView.setOnClickListener(this);
		jobtypeView.setOnClickListener(this);
		timetypeView.setOnClickListener(this);
		unitView.setOnClickListener(this);
		cityselectView.setOnClickListener(this);
		locationView.setOnClickListener(this);
		issueButton.setOnClickListener(this);
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		width = displayMetrics.widthPixels;
		height = displayMetrics.heightPixels;
		
		switchButton.setOnCheckedChangeListener(changeListener);
		
		/**
		 * 设置输入框可以滚动
		 */
		scrollView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				findViewById(R.id.jobdescride_edit).getParent().requestDisallowInterceptTouchEvent(false);
				return false;
			}
		});
		
		jobdescribe.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				 v.getParent().requestDisallowInterceptTouchEvent(true); 
				return false;
			}
		});
		
	}
	
	OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
				isCheck = true;
				MyUtils.showToast(activity, R.string.displayphone);
			}else {
				isCheck = false;
				MyUtils.showToast(activity, R.string.hidephone);
			}
		}
	};
	
	/**
	 * 历史兼职列表项的点击
	 */
	OnItemClickListener selectItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int mm,
				long arg3) {
			// TODO Auto-generated method stub
			dialogView.setVisibility(View.GONE);
			jobtitle.setText(mList.get(mm).get("title"));
			jobtypetext.setText(mList.get(mm).get("js"));
			timetypetext.setText(mList.get(mm).get("jt"));
			jobpeople.setText(mList.get(mm).get("recruit"));
			jobtreatment.setText(mList.get(mm).get("wage"));
			unittext.setText(mList.get(mm).get("unit"));
			jobtime.setText(mList.get(mm).get("jobtime"));
			jobdescribe.setText(mList.get(mm).get("desc"));
			citytext.setText(mList.get(mm).get("city"));
			jobaddress.setText(mList.get(mm).get("addr"));
			idjtString = mList.get(mm).get("jsid");
			idttString = mList.get(mm).get("jtid");
			try {
				latitude = Double.parseDouble(mList.get(mm).get("x"));
				longitude = Double.parseDouble(mList.get(mm).get("y"));
			} catch (Exception e) {
				// TODO: handle exception
				latitude = 0;
				longitude = 0;
			}
			if (mList.get(mm).get("istel").equals("1")) {
				switchButton.setChecked(true);
				isCheck = true;
			}else {
				switchButton.setChecked(false);
				isCheck = false;
			}
		}
	};
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.top_return:
			activity.finish();
			break;

		case R.id.dialog:
			if (dialogView.getVisibility() == View.VISIBLE) {
				dialogView.setVisibility(View.GONE);
			}
			break;
			
		case R.id.history_view:		//历史兼职
			if (dialogView.getVisibility() == View.VISIBLE) {
				dialogView.setVisibility(View.GONE);
			}else {
				if (mList != null && mList.size() != 0) {
					dialogView.setVisibility(View.VISIBLE);
				}else {
					webtype = "his";
					ExecuteAsyncTask("GetEntJobInfoList", mapUtil.HistoryMap(
							shared.getString("id", ""), 100, 1));
				}
			}
			break;
			
		case R.id.jobtype_view:		//工作类型
			webtype = "jt";
			if (jtlist != null && jtlist.size() != 0) {
				myHandler.sendEmptyMessage(3);
			}else {
				ExecuteAsyncTask("GetJobSearchList", mapUtil.AccesskeyMap());
			}
			break;
			
		case R.id.jtcanclebtn:
			alertDialog.dismiss();
			break;
		case R.id.timetype_view:	//时间类型
			webtype = "tt";
			if (ttlist != null && ttlist.size() != 0) {
				myHandler.sendEmptyMessage(4);
			}else {
				ExecuteAsyncTask("GetJobTypeList", mapUtil.AccesskeyMap());
			}
			break;
			
		case R.id.unitlinear:	//工资的单位
			webtype = "ut";
			if (utlist != null && utlist.size() != 0) {
				myHandler.sendEmptyMessage(5);
			}else {
				ExecuteAsyncTask("GetWageUnitList", mapUtil.AccesskeyMap());
			}
			break;
			
		case R.id.cityselect_view:
			startActivityForResult(new Intent(this, SelectCitysActivity.class), 101);
			break;
			
		case R.id.getloclinear:		//抓取定位
			MyProgressDialog.showDialog(activity, 0);
			myLocation = new MyLocationManager(JobIssueActivity.this.getApplicationContext(),
					JobIssueActivity.this);
			break;
			
		case R.id.issuejob_btn:		//发布按钮
			if (TextUtils.isEmpty(jobtitle.getText()) || TextUtils.isEmpty(jobtypetext.getText()) 
				 || TextUtils.isEmpty(timetypetext.getText()) || TextUtils.isEmpty(jobpeople.getText())
				 || TextUtils.isEmpty(jobtreatment.getText()) || unittext.getText().
				 equals(getString(R.string.clickselect)) || TextUtils.isEmpty(jobtime.getText())
				 || TextUtils.isEmpty(jobdescribe.getText()) || TextUtils.isEmpty(citytext.getText())
				 || TextUtils.isEmpty(jobaddress.getText()) || TextUtils.isEmpty(linkman.getText())
				 || TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(zxphone.getText())) 
			{
				MyUtils.showToast(activity, R.string.pleaseinputall);
			}else if (jobtitle.getText().toString().contains("\"")) {
				MyUtils.showToast(activity, R.string.titlenotcontain);
			}else {
				webtype = "issue";
				ExecuteAsyncTask("SubmitJob", mapUtil.IssueJobMap(shared.getString("id", ""), 
						MyUtils.UploadString(jobtitle.getText().toString()), idjtString, idttString, 
						MyUtils.UploadString(jobtime.getText().toString()), 
						jobpeople.getText().toString(), jobtreatment.getText().toString(), 
						unittext.getText().toString(), MyUtils.UploadString(jobdescribe.getText().toString()), 
						email.getText().toString(), linkman.getText().toString(), zxphone.getText().toString(), 
						isCheck == true ? "1":"0", citytext.getText().toString(), MyUtils.UploadString(jobaddress.getText().toString()), 
								String.valueOf(latitude), String.valueOf(longitude)));
			}
			break;
		default:
			break;
		}
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
				MyProgressDialog.showDialog(activity, 0);
			}
			
			@Override
			public void onGetDataSuccessfulDoInUI(String result) {
				// TODO Auto-generated method stub
				//成功获取数据，解析数据
				MyProgressDialog.Dismiss();
				
				if (webtype.equals("jt")) {
					list = JsonTool.JTUTypejson(result, "JobSearchList", 1);
				}else if (webtype.equals("tt")) {
					list = JsonTool.JTUTypejson(result, "JobTypeList", 2);
				}else if (webtype.equals("ut")) {
					list = JsonTool.JTUTypejson(result, "WageUnitList", 3);
				}else if (webtype.equals("his")) {
					list = JsonTool.JobManagejson(result, "JobInfoList");
				}else {
					mmap = JsonTool.isSuccessfuljson(result);
				}
				
				if (webtype.equals("issue")) {
					if (mmap == null) {
						MyUtils.showToast2(activity, getResources().
								getString(R.string.data_parse_error));
					}else {
						MyUtils.showToast2(activity, mmap.get("msg"));
						if (mmap.get("result").equals("1")) {
							activity.finish();
						}
					}
				}else {
					if (list == null) {
						MyUtils.showToast2(activity, getResources().
								getString(R.string.data_parse_error));
					}else {
						if(list.get(0).get("result") != null)
						{
							if (webtype.equals("his")) {
								MyUtils.showToast(activity, R.string.noissuejob);
							}else {
								MyUtils.showToast2(activity, list.get(0).get("msg"));
							}
						}else{
							if (webtype.equals("jt")) {
								jtlist = list;
								myHandler.sendEmptyMessage(3);
							}else if (webtype.equals("tt")) {
								ttlist = list;
								myHandler.sendEmptyMessage(4);
							}else if (webtype.equals("ut")) {
								utlist = list;
								myHandler.sendEmptyMessage(5);
							}else if (webtype.equals("his")) {
								mList = list;
								myHandler.sendEmptyMessage(6);
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
				if (address.getMaxAddressLineIndex() > 0) {
					position = address.getAddressLine(0).replace(cityString, "").
							replace(address.getAdminArea(), "") + address.getAddressLine(1);
				}else {
					position = address.getAddressLine(0).replace(cityString, "").
							replace(address.getAdminArea(), "");
				}
				
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
				MyProgressDialog.Dismiss();
				MyUtils.showToast(activity, R.string.locationfail);
				break;
			case 2:
				citytext.setText(cityString);
				jobaddress.setText(position);
				MyProgressDialog.Dismiss();
				break;
			case 3:
				showdialog(jtlist, 1);
				break;
			case 4:
				showdialog(ttlist, 2);
				break;
			case 5:
				showdialog(utlist, 3);
				break;
			case 6:
				dialogadapter.refresh(mList);
				dialogView.setVisibility(View.VISIBLE);
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
		if (myLocation != null) {
			myLocation.destoryLocationManager();
		}
		
		if (wt != null && !wt.isCancelled()) {
			wt.cancel(true);
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		switch (arg1) {
		case 101:
			citytext.setText(arg2.getStringExtra("name"));
			break;

		default:
			break;
		}
	}
	
	/**
	 * 显示列表对话框
	 * @param i
	 */
	private void showdialog(List<Map<String, String>> list, int i){
		View view;
		if (list.size() > 10) {
			view = LayoutInflater.from(activity).inflate(R.layout.jobtypeselect_dialogview, null);
		}else {
			view = LayoutInflater.from(activity).inflate(R.layout.timetypeselect_dialogview, null);
		}
		dialogListView = (ListView)view.findViewById(R.id.dialoglist);
		jtcancle = (TextView)view.findViewById(R.id.jtcanclebtn);
		
		alertDialog = new Dialog(activity, R.style.alert_dialog);
		alertDialog.setContentView(view, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		alertDialog.show();
		alertDialog.setCanceledOnTouchOutside(false);
		WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();   
        params.width = width - getResources().getDimensionPixelSize(R.dimen.dialogpadding);   
        if(list.size() > 10){
        	params.height = height - getResources().getDimensionPixelSize(R.dimen.dialogpadding);   
        }
        alertDialog.getWindow().setAttributes(params);  
        
        jtcancle.setOnClickListener(this);
        dialogListView.setAdapter(new jtadapter(list));
        dialogListView.setOnItemClickListener(new dialogitemclick(i));
	}
	
	public class dialogitemclick implements OnItemClickListener{

		int ii;
		
		public dialogitemclick(int i){
			this.ii = i;
		}
		
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			if (ii == 1) {
				jobtypetext.setText(jtlist.get(position).get("name"));
				idjtString = jtlist.get(position).get("id");
			}else if (ii == 2) {
				idttString = ttlist.get(position).get("id");
				timetypetext.setText(ttlist.get(position).get("name"));
			}else{
				idutString = utlist.get(position).get("id");
				unittext.setText(utlist.get(position).get("name"));
			}
			alertDialog.dismiss();
		}
	}
	
	private class jtadapter extends BaseAdapter{

		TextView itemView;
		List<Map<String, String>> alist;
		
		public jtadapter(List<Map<String, String>> alist){
			this.alist = alist;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return alist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = LayoutInflater.from(activity).inflate(R.layout.selectview_item3, null);
			itemView = (TextView)convertView.findViewById(R.id.select_item);
			itemView.setText(alist.get(position).get("name"));
			return convertView;
		}
		
	}
	
}
