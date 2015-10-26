package com.jianzhiniu.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.jianzhiniu.activity.ContentEditActivity;
import com.jianzhiniu.activity.GoogleMapActivity;
import com.jianzhiniu.activity.LoginActivity;
import com.jianzhiniu.activity.R;
import com.jianzhiniu.adapter.JobinfoAdapter;
import com.jianzhiniu.config.Myconfig;
import com.jianzhiniu.network.JsonTool;
import com.jianzhiniu.network.MapUtil;
import com.jianzhiniu.network.WebServiceUIAsyncTask;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.MyAlertDialog;
import com.jianzhiniu.views.MyAlertDialog.AlertCallback;
import com.jianzhiniu.views.MyListview;
import com.jianzhiniu.views.MyProgressDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * 工作信息fragment
 * @author Administrator
 *
 */
public class JobInfoFragment extends Fragment implements OnClickListener{
	
	private Activity activity;
	private MyListview listView;
	private JobinfoAdapter adapter;
	private LinearLayout shareview, complainview, applyview, consultview, mapview;
	private RelativeLayout phoneview;
	private List<Map<String, String>> list = new ArrayList<Map<String,String>>();
	private WebServiceUIAsyncTask wt;
	private MapUtil mapUtil;
	private Map<String, String> infomap = null;
	private ImageView typeimg, callimg;
	private TextView title, city, issuetime, price, companyname, peonum, price2, jobtime, content, 
	place, linkman, phonenum, totalconsult;
	private Intent intent;
	private View mainView, bottomView;
	private String webtype = "info";
	private SharedPreferences isShared, idShared;
	private boolean isE;
	private ImageLoader loader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private MyAlertDialog myAlertDialog;
	
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
		
		myAlertDialog = new MyAlertDialog(activity, callback);
		loader.init(ImageLoaderConfiguration.createDefault(activity));
		//图片参数设置
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.imgloading_r)
		.showImageForEmptyUri(R.drawable.imgerror)
		.showImageOnFail(R.drawable.imgerror).cacheOnDisc()
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new FadeInBitmapDisplayer(300)).build();
		
		mapUtil = new MapUtil(activity);
		intent = activity.getIntent();
		isShared = activity.getSharedPreferences("isE", Context.MODE_PRIVATE);
		if (isShared.getBoolean("isE", false)) {
			isE = true;
			idShared = activity.getSharedPreferences("einfo", Context.MODE_PRIVATE);
		}else {
			idShared = activity.getSharedPreferences("info", Context.MODE_PRIVATE);
		}
	}
	
	AlertCallback callback = new AlertCallback() {
		
		@Override
		public void ybutton() {
			// TODO Auto-generated method stub
			webtype = "apply";
			ExecuteAsyncTask("SubmitJobReg", mapUtil.ApplyJobMap(idShared.getString("id", ""), 
					intent.getStringExtra("id")));
		}
	};
	
	AlertCallback callback2 = new AlertCallback() {
		
		@Override
		public void ybutton() {
			// TODO Auto-generated method stub
			Intent lintent = new Intent(activity, LoginActivity.class);
			lintent.putExtra("flag", "p");
			startActivity(lintent);
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(activity).inflate(R.layout.jobinfo_view, null);
		mainView = (View)view.findViewById(R.id.mainview);
		bottomView = (View)view.findViewById(R.id.bottomview);
		phonenum = (TextView)view.findViewById(R.id.linknumber_text);
		totalconsult = (TextView)view.findViewById(R.id.totalconsult);
		title = (TextView)view.findViewById(R.id.jobname);
		city = (TextView)view.findViewById(R.id.didian_text);
		issuetime = (TextView)view.findViewById(R.id.time_text);
		price = (TextView)view.findViewById(R.id.price_text);
		companyname = (TextView)view.findViewById(R.id.issuec_text);
		peonum = (TextView)view.findViewById(R.id.recruitnum_text);
		price2 = (TextView)view.findViewById(R.id.payoffer_text);
		jobtime = (TextView)view.findViewById(R.id.worktime_text);
		content = (TextView)view.findViewById(R.id.workcontent_text);
		place = (TextView)view.findViewById(R.id.workplace_text);
		linkman = (TextView)view.findViewById(R.id.linkman_text);
		listView = (MyListview)view.findViewById(R.id.jobinfolistview);
		typeimg = (ImageView)view.findViewById(R.id.img);
		callimg = (ImageView)view.findViewById(R.id.callimg);
		phoneview = (RelativeLayout)view.findViewById(R.id.phone_linear);
		shareview = (LinearLayout)view.findViewById(R.id.share_linear);
		mapview = (LinearLayout)view.findViewById(R.id.map_linear);
		complainview = (LinearLayout)view.findViewById(R.id.complain_linear);
		applyview = (LinearLayout)view.findViewById(R.id.apply_linear);
		consultview = (LinearLayout)view.findViewById(R.id.consult_linear);
		title.setSingleLine(false);
		
		if (isE) {
			consultview.setVisibility(View.GONE);
		}
		if (intent.hasExtra("id")) {
			ExecuteAsyncTask("GetJobInfo", mapUtil.JobInfoMap(intent.getStringExtra("id")));
		}
		
		listView.setFocusable(false);
		adapter = new JobinfoAdapter(list, activity);
		listView.setAdapter(adapter);
		
		mapview.setOnClickListener(this);
//		phoneview.setOnClickListener(this);
		callimg.setOnClickListener(this);
		consultview.setOnClickListener(this);
		shareview.setOnClickListener(this);
		complainview.setOnClickListener(this);
		applyview.setOnClickListener(this);
		
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
				if (webtype.equals("info") || webtype.equals("apply")) {
					MyProgressDialog.showDialog(activity, 0);
				}
			}
			
			@Override
			public void onGetDataSuccessfulDoInUI(String result) {
				// TODO Auto-generated method stub
				//成功获取数据，解析数据
				if (webtype.equals("apply")) {
					MyProgressDialog.Dismiss();
					Map<String, String> mMap = JsonTool.isSuccessfuljson(result);
					if (mMap == null) {
						MyUtils.showToast2(activity, getResources().
								getString(R.string.data_parse_error));
					}else {
						MyUtils.showToast2(activity, mMap.get("msg"));
					}
				}else if (webtype.equals("list")) {
					MyProgressDialog.Dismiss();
					list = JsonTool.ConsultListjson(result, "JobQuestionList");
					if (list == null) {
						MyUtils.showToast2(activity, getResources().
								getString(R.string.data_parse_error));
					}else {
						if (list.get(0).containsKey("result")) {
							list.clear();
						}
						myHandler.sendEmptyMessage(2);
					}
					
				}else {
					infomap = JsonTool.JobInfojson(result, "JobInfo");
					if (infomap == null) {
						MyUtils.showToast2(activity, getResources().
								getString(R.string.data_parse_error));
					}else {
						if(infomap.get("result").equals("1"))
						{
							myHandler.sendEmptyMessage(1);
						}else{
							MyProgressDialog.Dismiss();
							MyUtils.showToast2(activity, infomap.get("msg"));
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
				loader.displayImage(MyUtils.getIconUrl() + infomap.get("jsid") + ".png", typeimg, options);
				
				if (infomap.get("telconsult").equals("1") && !infomap.get("status").equals("2")) {
					callimg.setVisibility(View.VISIBLE);
				}
				if (infomap.get("status").equals("0") && !isE) {
					bottomView.setVisibility(View.VISIBLE);
				}
				if (infomap.get("status").equals("2")) {
					consultview.setVisibility(View.GONE);
				}
				if (intent.hasExtra("dis")) {
					city.setText(intent.getStringExtra("dis"));
				}else {
					city.setText(infomap.get("city"));
				}
				title.setText(MyUtils.ParseString(infomap.get("title")));
				issuetime.setText(infomap.get("issuetime").split(" ")[0]);
				price.setText("$ " + infomap.get("wage") + infomap.get("unit"));
				companyname.setText(MyUtils.ParseString(infomap.get("comname")));
				peonum.setText(infomap.get("recruit"));
				price2.setText(infomap.get("wage") + infomap.get("unit"));
				jobtime.setText(MyUtils.ParseString(infomap.get("jobtime")));
				content.setText(MyUtils.ParseString(infomap.get("descrip")));
				place.setText(MyUtils.ParseString(infomap.get("addr")) + ", " + infomap.get("city"));
				linkman.setText(infomap.get("linkman"));
				if (infomap.get("telconsult").equals("0")) {
					phonenum.setText("***********");
				}else {
					phonenum.setText(infomap.get("phone"));
				}
				mainView.setVisibility(View.VISIBLE);
				webtype = "list";
				ExecuteAsyncTask("GetJobQuestionList", mapUtil.ConsultListMap(
						intent.getStringExtra("id"), 100, 1));
				break;
				
			case 2:
				totalconsult.setText(activity.getString(R.string.gy) + list.size() + 
						activity.getString(R.string.tzx));
				adapter.refresh(list);
				setListViewHeightBasedOnChildren(listView);
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * 动态设置ListView的高度
	 * @param listView
	 */
	public void setListViewHeightBasedOnChildren(ListView listView) { 
		if(listView == null) return;
		
		ListAdapter listAdapter = listView.getAdapter(); 
		if (listAdapter == null) { 
			// pre-condition 
			return; 
		} 
		
		int totalHeight = 0; 
		for (int i = 0; i < listAdapter.getCount(); i++) { 
			View listItem = listAdapter.getView(i, null, listView); 
			listItem.measure(0, 0); 
			totalHeight += listItem.getMeasuredHeight(); 
		} 
//		if (totalHeight > halfpmheight - 100) {
//			totalHeight = halfpmheight - 100;
//		}
		ViewGroup.LayoutParams params = listView.getLayoutParams(); 
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)); 
		listView.setLayoutParams(params); 
	}
	
	@SuppressLint("SdCardPath") 
	private void showShare() {
		ShareSDK.initSDK(activity);
		OnekeyShare oks = new OnekeyShare();
		//关闭sso授权
		oks.disableSSOWhenAuthorize(); 
		oks.setSilent(false);
		oks.setDialogMode();
		oks.setText("Job：" + MyUtils.ParseString(infomap.get("title")) + "\n" + activity.getString(R.string.issuecompany) + 
				MyUtils.ParseString(infomap.get("comname")) + "\n" + activity.getString(R.string.recruitnumber) + 
				infomap.get("recruit") + "\n" + activity.getString(R.string.payoffer) + 
				"$ " + infomap.get("wage") + infomap.get("unit") + "\n" + 
				activity.getString(R.string.jobtime) + MyUtils.ParseString(infomap.get("jobtime")) + "\n" + 
				activity.getString(R.string.workcontent) + "：" + MyUtils.ParseString(infomap.get("descrip")) + "\n" + 
				activity.getString(R.string.workplace) + infomap.get("city") + MyUtils.ParseString(infomap.get("addr")) 
				+ "\n" + activity.getString(R.string.linkman) + infomap.get("linkman") + "\n" + 
				activity.getString(R.string.linknumber) + (infomap.get("telconsult").equals("1") ? 
				infomap.get("phone") : "***********") + "\n\n------from " + activity.getString(
				R.string.app_name) + " APP");
		oks.setImagePath(Environment.getExternalStorageDirectory() + "/test.jpg");
		// 启动分享GUI
		oks.show(activity);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.map_linear:
			Intent mapIntent = new Intent(activity, GoogleMapActivity.class);
			if (infomap != null) {
				mapIntent.putExtra("place", infomap.get("addr"));
				mapIntent.putExtra("lat", infomap.get("x"));
				mapIntent.putExtra("long", infomap.get("y"));
			}
			startActivity(mapIntent);
			break;
		case R.id.share_linear:
			showShare();
			break;
		case R.id.complain_linear:
			if (Myconfig.isLogin) {
				
				Intent cIntent = new Intent(activity, ContentEditActivity.class);
				cIntent.putExtra("type", "complain");
				cIntent.putExtra("id", intent.getStringExtra("id"));
				cIntent.putExtra("eid", infomap.get("entid"));
				startActivity(cIntent);
				
			}else {
				new MyAlertDialog(activity, callback2).showMyAlertDialog(R.string.loginfirst);
			}
			break;
		case R.id.apply_linear:
			if (Myconfig.isLogin) {
				myAlertDialog.showMyAlertDialog(R.string.suretoapply);
			}else {
				new MyAlertDialog(activity, callback2).showMyAlertDialog(R.string.loginfirst);
			}
			
			break;
		case R.id.callimg:
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("tel:"
					+ phonenum.getText().toString().trim())));
			break;
		case R.id.consult_linear:
			if (Myconfig.isLogin) {
				
				if (idShared.getString("name", "").equals("")) {
					MyUtils.showToast(activity, R.string.wsjl);
				}else {
					Intent ccIntent = new Intent(activity, ContentEditActivity.class);
					ccIntent.putExtra("type", "consult");
					ccIntent.putExtra("id", intent.getStringExtra("id"));
					startActivityForResult(ccIntent, 11);
				}
				
			}else {
				new MyAlertDialog(activity, callback2).showMyAlertDialog(R.string.loginfirst);
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 11) {
			webtype = "list";
			ExecuteAsyncTask("GetJobQuestionList", mapUtil.ConsultListMap(
					intent.getStringExtra("id"), 100, 1));
		}
	}
	
}
