package com.jianzhiniu.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianzhiniu.config.MyApplication;
import com.jianzhiniu.network.JsonTool;
import com.jianzhiniu.network.MapUtil;
import com.jianzhiniu.network.WebServiceUIAsyncTask;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.MyAlertDialog;
import com.jianzhiniu.views.MyAlertDialog.AlertCallback;
import com.jianzhiniu.views.MyProgressDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * 报名跟踪界面
 * @author Administrator
 *
 */
public class ApplyFollowActivity extends BaseActivity{

	private Activity activity = ApplyFollowActivity.this;
	private TextView topcenterView, cancleView, status1, status2, status3, content1, content2, 
		content3, jobname, didian, time, price;
	private ImageView returnView, img, start, round2, round3, round1;
	private View jobinfoView, topView;
	private MapUtil mapUtil;
	private WebServiceUIAsyncTask wt;
	private List<Map<String, String>> list = new ArrayList<Map<String,String>>();
	private Map<String, String> rMap = new HashMap<String, String>();
	private Map<String, String> infoMap = null;
	private Intent intent;
	private boolean isCancle;
	private ImageLoader loader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private MyAlertDialog myAlertDialog;
	private String webtype;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apply_follow);
		MyApplication.getInstance().addActivity(this);
		
		//提示框
		myAlertDialog = new MyAlertDialog(activity, callback);
		//图片显示控件初始化
		loader.init(ImageLoaderConfiguration.createDefault(this));
		//图片参数设置
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.imgloading_r)
		.showImageForEmptyUri(R.drawable.imgerror)
		.showImageOnFail(R.drawable.imgerror).cacheOnDisc()
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new FadeInBitmapDisplayer(300)).build();
		
		intent = getIntent();
		mapUtil = new MapUtil(activity);
		topcenterView = (TextView)findViewById(R.id.top_centertext);
		cancleView = (TextView)findViewById(R.id.cancle_text);
		status1 = (TextView)findViewById(R.id.af_status1);
		status2 = (TextView)findViewById(R.id.af_status2);
		status3 = (TextView)findViewById(R.id.af_status3);
		content1 = (TextView)findViewById(R.id.af_content1);
		content2 = (TextView)findViewById(R.id.af_content2);
		content3 = (TextView)findViewById(R.id.af_content3);
		jobname = (TextView)findViewById(R.id.jobname);
		didian = (TextView)findViewById(R.id.didian_text);
		time = (TextView)findViewById(R.id.time_text);
		price = (TextView)findViewById(R.id.price_text);
		jobinfoView = (View)findViewById(R.id.to_jobinfoview);
		topView = (View)findViewById(R.id.topinfoview);
		returnView = (ImageView)findViewById(R.id.top_return);
		start = (ImageView)findViewById(R.id.nj_start);
		img = (ImageView)findViewById(R.id.img);
		round1 = (ImageView)findViewById(R.id.round1);
		round2 = (ImageView)findViewById(R.id.round2);
		round3 = (ImageView)findViewById(R.id.round3);
		
		//设置取消按钮消失
		if (intent.getStringExtra("status").equals("2") || intent.getStringExtra("status").equals("3") || 
				intent.getStringExtra("status").equals("-1")) {
			cancleView.setVisibility(View.GONE);
		}
		
		
		if (intent.hasExtra("title")) {
			loader.displayImage(MyUtils.getIconUrl() + intent.getStringExtra("jsid") + ".png", img, options);
			jobname.setText(intent.getStringExtra("title"));
			didian.setText(intent.getStringExtra("city"));
			time.setText(intent.getStringExtra("time"));
			price.setText("$ " + intent.getStringExtra("price"));
			if (!intent.getStringExtra("status").equals("-1")) {
				webtype = "list";
				ExecuteAsyncTask("GetJobRegLogList", mapUtil.ApplyFollowMap(intent.getStringExtra("id")));
			}
		}else {
			webtype = "info";
			ExecuteAsyncTask("GetJobRegInfo", mapUtil.ApplyFollowMap(intent.getStringExtra("id")));
		}
		
		topcenterView.setText(R.string.applyfollow);
		returnView.setOnClickListener(this);
		jobinfoView.setOnClickListener(this);
		cancleView.setOnClickListener(this);
	}
	
	//提示框确定按钮点击
	AlertCallback callback = new AlertCallback() {
		
		@Override
		public void ybutton() {
			// TODO Auto-generated method stub
			webtype = "cancle";
			ExecuteAsyncTask("UpdateJobRegStatus", mapUtil.CancleApplyMap(intent.getStringExtra("id"), "-1"));
		}
	};
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.top_return:
			this.finish();
			break;
		case R.id.cancle_text:
			myAlertDialog.showMyAlertDialog(R.string.suretocancle);
			break;
		case R.id.to_jobinfoview:
			//点击进入工作详情界面
			Intent jIntent = new Intent(this, JobInfoActivity.class);
			if (intent.hasExtra("eid")) {
				jIntent.putExtra("id", intent.getStringExtra("jobid"));
				jIntent.putExtra("eid", intent.getStringExtra("eid"));
				startActivity(jIntent);
			}else if(infoMap != null && !infoMap.containsKey("result")){
				jIntent.putExtra("id", infoMap.get("jobid"));
				jIntent.putExtra("eid", infoMap.get("entid"));
				startActivity(jIntent);
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
					if (webtype.equals("cancle")) {					//取消报名
						rMap = JsonTool.isSuccessfuljson(result);
						if (rMap == null) {
							MyUtils.showToast2(activity, getResources().
									getString(R.string.data_parse_error));
						}else {
							MyUtils.showToast2(activity, rMap.get("msg"));
							if (rMap.get("result").equals("1")) {
								Intent bIntent = new Intent("cancle");
								activity.sendBroadcast(bIntent);
								cancleView.setVisibility(View.GONE);
								content1.setText("");
								content2.setText("");
								status1.setText(R.string.noapply);
								status2.setText(R.string.waitforlooked);
								round1.setImageResource(R.drawable.no);
								round2.setImageResource(R.drawable.no);
							}
						}
					}else if(webtype.equals("list")){				//跟踪列表
						list = JsonTool.ApplyFollowjson(result, "JobRegLgoList");
						if (list == null) {
							MyUtils.showToast2(activity, getResources().
									getString(R.string.data_parse_error));
						}else {
							if (list.get(0).containsKey("result")) {
								MyUtils.showToast2(activity, list.get(0).get("msg"));
							}else {
								if (list.size() > 0) {
									round1.setImageResource(R.drawable.yes);
									status1.setText(list.get(0).get("status"));
									content1.setText(list.get(0).get("time") + "\n" + list.get(0).get("content"));
									if (list.size() > 1) {
										round2.setImageResource(R.drawable.yes);
										status2.setText(list.get(1).get("status"));
										content2.setText(list.get(1).get("time") + "\n" + list.get(1).get("content"));
										if (list.size() > 2) {
											if (list.get(2).get("statusnum").equals("2")) {
												round3.setImageResource(R.drawable.no2);
											}else {
												round3.setImageResource(R.drawable.yes);
											}
											status3.setText(list.get(2).get("status"));
											content3.setText(list.get(2).get("time") + "\n" + list.get(2).get("content"));
										}
									}
								}
							}
						}
					}else {				//工作信息
						infoMap = JsonTool.RegJobInfojson(result);
						if (infoMap == null) {
							MyUtils.showToast(activity, R.string.data_parse_error);
						}else {
							if (infoMap.containsKey("result")) {
								MyUtils.showToast2(activity, infoMap.get("msg"));
							}else {
								loader.displayImage(MyUtils.getIconUrl() + infoMap.get("jsid") + ".png", img, options);
								jobname.setText(infoMap.get("title"));
								didian.setText(infoMap.get("city"));
								time.setText(infoMap.get("issuetime").split(" ")[0]);
								price.setText("$ " + infoMap.get("wage") + infoMap.get("unit"));
							}
						}
						if (!intent.getStringExtra("status").equals("-1")) {
							webtype = "list";
							ExecuteAsyncTask("GetJobRegLogList", mapUtil.ApplyFollowMap(intent.getStringExtra("id")));
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
		
	}
}
