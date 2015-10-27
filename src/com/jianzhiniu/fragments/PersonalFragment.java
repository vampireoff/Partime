package com.jianzhiniu.fragments;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.twitter.Twitter;

import com.jianzhiniu.activity.ContentEditActivity;
import com.jianzhiniu.activity.EnterCenterActivity;
import com.jianzhiniu.activity.LoginActivity;
import com.jianzhiniu.activity.PersonalCenterActivity;
import com.jianzhiniu.activity.R;
import com.jianzhiniu.activity.SettingActivity;
import com.jianzhiniu.activity.WelcomeActivity;
import com.jianzhiniu.config.Myconfig;
import com.jianzhiniu.network.JsonTool;
import com.jianzhiniu.network.MapUtil;
import com.jianzhiniu.network.WebServiceUIAsyncTask;
import com.jianzhiniu.utils.ImageUtil;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.MyAlertDialog;
import com.jianzhiniu.views.MyProgressDialog;
import com.jianzhiniu.views.RoundImageView;
import com.jianzhiniu.views.MyAlertDialog.AlertCallback;

/**
 * 个人中心fragment
 * @author Administrator
 *
 */
public class PersonalFragment extends Fragment implements OnClickListener{

	private Activity activity;
	private RelativeLayout personalLayout, collectLayout, feedbackLayout, settingLayout, 
							exitLayout, checkupdate;
	private Button yButton, nButton, vyButton, vnButton;
	private Dialog alertDialog, versionDialog;
	private Platform facebook, twitter;
	private static int width, height;
	private ImageView start;
	private Intent intent;
	private boolean isE;
	private SharedPreferences shared;
	private RoundImageView touxiang;
	private TextView name, phone, versioninfo;
	private Bitmap picBitmap;
	private String imgurl;
	private WebServiceUIAsyncTask wt;
	private MapUtil mapUtil;
	private Map<String, String> versionMap = new HashMap<String, String>();
	private ProgressDialog progressDialog;
	private String mSavePath; /* 下载保存路径 */
	private String Name; /* 下载的文件名 */
	private static final int DOWNLOAD = 1; /* 下载中 */
	private static final int DOWNLOAD_FINISH = 2; /* 下载结束 */
	private int progress; /* 记录进度条数量 */
	private boolean cancelUpdate; /* 是否取消更新 */
	private File apkFile;
	private SharedPreferences ushareds; 
	private SharedPreferences.Editor ueditor;
	
 	
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
		mSavePath = Environment.getExternalStorageDirectory() + "/ptDownload";
		mapUtil = new MapUtil(activity);
		intent = activity.getIntent();
		if (intent.getBooleanExtra("type", false)) {
			isE = true;
			shared = activity.getSharedPreferences("einfo", Context.MODE_PRIVATE);
		}else {
			shared = activity.getSharedPreferences("info", Context.MODE_PRIVATE);
		}
		
		if (isE) {
			ushareds = activity.getSharedPreferences(
					"user", Context.MODE_PRIVATE);
		}else {
			ushareds = activity.getSharedPreferences(
					"users", Context.MODE_PRIVATE);
		}
		ueditor = ushareds.edit();
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(activity).inflate(R.layout.personalfragment, null);
		personalLayout = (RelativeLayout)view.findViewById(R.id.personalview);
		collectLayout = (RelativeLayout)view.findViewById(R.id.collectview);
		feedbackLayout = (RelativeLayout)view.findViewById(R.id.feedbackview);
		settingLayout = (RelativeLayout)view.findViewById(R.id.settingview);
		exitLayout = (RelativeLayout)view.findViewById(R.id.exitview);
		checkupdate = (RelativeLayout)view.findViewById(R.id.checkupdateview);
		start = (ImageView)view.findViewById(R.id.start);
		touxiang = (RoundImageView)view.findViewById(R.id.roundimg);
		name = (TextView)view.findViewById(R.id.p_name);
		phone = (TextView)view.findViewById(R.id.p_number);
		
		checkupdate.setOnClickListener(this);
		personalLayout.setOnClickListener(this);
		collectLayout.setOnClickListener(this);
		feedbackLayout.setOnClickListener(this);
		settingLayout.setOnClickListener(this);
		exitLayout.setOnClickListener(this);
		
		ShareSDK.initSDK(activity);
		facebook = ShareSDK.getPlatform(Facebook.NAME);
		twitter = ShareSDK.getPlatform(Twitter.NAME);
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		width = displayMetrics.widthPixels;
		height = displayMetrics.heightPixels;
		
		return view;
	}
	
	private class mythread implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			picBitmap = ImageUtil.getBitmapFromPathWH(MyUtils.getPicUrl() + shared.getString("pic", ""), 
					120, 120);
			if (picBitmap != null) {
				myHandler.sendEmptyMessage(1);
			}
		}
	}
	
	@SuppressLint("HandlerLeak")
	private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				touxiang.setImageBitmap(picBitmap);
				ImageUtil.savePNG(picBitmap, imgurl);
				break;

			default:
				break;
			}
			
		}
	};
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//显示号码
		if (shared.getString("mobile", "").equals("")) {
			phone.setVisibility(View.GONE);
		}else {
			phone.setText(shared.getString("mobile", ""));
			phone.setVisibility(View.VISIBLE);
		}
		
		//显示名字或提示
		if (isE) {
			imgurl = activity.getExternalCacheDir() + "/e" + shared.getString("loginid", "") + ".png";

			if (shared.getString("companyname", "").equals("")) {
				name.setText(activity.getString(R.string.no_comanyname));
				name.setTextColor(activity.getResources().getColor(R.color.red));
			}else {
				name.setText(shared.getString("companyname", ""));
				name.setTextColor(activity.getResources().getColor(R.color.black));
			}
			if (shared.getString("authentication", "0").equals("1")) {
				start.setVisibility(View.VISIBLE);
			}
		}else {
			imgurl = activity.getExternalCacheDir() + "/" + shared.getString("loginid", "") + ".png";

			if (Myconfig.isLogin) {
				
				if (shared.getString("name", "").equals("")) {
					name.setText(activity.getString(R.string.no_personname));
					name.setTextColor(activity.getResources().getColor(R.color.red));
				}else {
					name.setText(shared.getString("name", ""));
					name.setTextColor(activity.getResources().getColor(R.color.black));
				}
				
			}else {
				name.setText(activity.getString(R.string.notlogin));
				name.setTextColor(activity.getResources().getColor(R.color.light_red));
			}
		}
		
		//设置头像
		if (ImageUtil.isHave(imgurl)) {
			picBitmap = BitmapFactory.decodeFile(imgurl);
			touxiang.setImageBitmap(picBitmap);
		}else {
			//获取头像
			new Thread(new mythread()).start();
		}
	}
	
	AlertCallback callback = new AlertCallback() {
		
		@Override
		public void ybutton() {
			// TODO Auto-generated method stub
			Intent lintent = new Intent(activity, LoginActivity.class);
			lintent.putExtra("flag", "p");
			startActivity(lintent);
		}
	};

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.personalview:
			if (isE) {
				startActivity(new Intent(activity, EnterCenterActivity.class));
			}else {
				if (Myconfig.isLogin) {
					startActivity(new Intent(activity, PersonalCenterActivity.class));
				}else {
					new MyAlertDialog(activity, callback).showMyAlertDialog(R.string.loginfirst);
				}
			}
			break;
		case R.id.collectview:
//			startActivity(new Intent(activity, MyCollectActivity.class));
			
			break;
		case R.id.feedbackview:
			Intent fIntent = new Intent(activity, ContentEditActivity.class);
			fIntent.putExtra("type", "feedback");
			startActivity(fIntent);
			break;
		case R.id.settingview:
			Intent sIntent = new Intent(activity, SettingActivity.class);
			sIntent.putExtra("ise", isE);
			sIntent.putExtra("id", shared.getString("id", ""));
			startActivity(sIntent);
			break;
		case R.id.exitview:
			View view = LayoutInflater.from(activity).inflate(R.layout.exit_view, null);
			yButton = (Button)view.findViewById(R.id.y_button);
			nButton = (Button)view.findViewById(R.id.n_button);
			
			alertDialog = new Dialog(activity, R.style.alert_dialog);
			alertDialog.setContentView(view, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
			alertDialog.show();
			WindowManager.LayoutParams params = alertDialog.getWindow()   
	                .getAttributes();   
	        params.width = width - activity.getResources().getDimensionPixelSize(R.dimen.dialogpadding);   
	        alertDialog.getWindow().setAttributes(params);  
	        
			yButton.setOnClickListener(this);
			nButton.setOnClickListener(this);
			break;
		case R.id.y_button:
			alertDialog.dismiss();
			
			//清除账户信息
			if (intent.getBooleanExtra("fb", false)) {
				facebook.removeAccount();
			}else if (intent.getBooleanExtra("tw", false)) {
				twitter.removeAccount();
			}else {
				ueditor.clear();
				ueditor.commit();
			}
			
			Intent eIntent = new Intent(activity, WelcomeActivity.class);
			eIntent.putExtra("type", "exit");
			startActivity(eIntent);
			Myconfig.isLogin = false;
			activity.finish();
			break;
		case R.id.n_button:
			alertDialog.dismiss();
			break;
		case R.id.u_button:
			versionDialog.dismiss();
			if (apkFile.exists()) {
				//apk存在则直接安装
				installApk();
			}else {
				//显示下载对话框
				showDownloadDialog();
			}
			break;
		case R.id.nu_button:
			versionDialog.dismiss();
			break;
		case R.id.checkupdateview:
			ExecuteAsyncTask("GetVersion", mapUtil.CheckVersionMap());
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
				versionMap = JsonTool.CheckVersionjson(result);
				if (versionMap == null) {
					MyUtils.showToast2(activity, getResources().
							getString(R.string.data_parse_error));
				}else {
					if(versionMap.get("result").equals("0"))
					{
						MyUtils.showToast2(activity, versionMap.get("msg"));
					}else{
						Name = versionMap.get("path").substring(versionMap.get("path").lastIndexOf("/") + 1);
						apkFile = new File(mSavePath, Name);
						checkUpdate();
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
				MyUtils.showToast(activity, R.string.iserror);
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
	
	/**
	 * 检查版本
	 */
	public void checkUpdate() {
		if (MyUtils.isUpdate(versionMap.get("no"), activity)) {
			// 显示提示对话框
			showNoticeDialog();
		} else {
			MyUtils.showToast(activity, R.string.nownewest);
		}
	}
	
	/**
	 * 显示版本更新对话框
	 */
	private void showNoticeDialog(){
		View view = LayoutInflater.from(activity).inflate(R.layout.versiontip_view, null);
		vyButton = (Button)view.findViewById(R.id.u_button);
		vnButton = (Button)view.findViewById(R.id.nu_button);
		versioninfo = (TextView)view.findViewById(R.id.version_info);
		versioninfo.setText(activity.getString(R.string.versionnum) + versionMap.get("name") + 
				"\n" + activity.getString(R.string.vissuetime) + versionMap.get("time") + "\n" 
				+ activity.getString(R.string.updatecontent) + "\n" + versionMap.get("desc"));
		
		versionDialog = new Dialog(activity, R.style.alert_dialog);
		versionDialog.setContentView(view, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		versionDialog.show();
		versionDialog.setCanceledOnTouchOutside(false);
		WindowManager.LayoutParams params = versionDialog.getWindow()   
                .getAttributes();   
        params.width = width - activity.getResources().getDimensionPixelSize(R.dimen.versiondialogpadding);   
//        params.height = height - 200;   
        versionDialog.getWindow().setAttributes(params);  
        
		vyButton.setOnClickListener(this);
		vnButton.setOnClickListener(this);
	}
	
	private Handler mHandler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 正在下载
			case DOWNLOAD:
				// 设置进度条位置
				if (progressDialog != null && progressDialog.isShowing())
					progressDialog.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// 安装文件
				installApk();
				break;
			default:
				break;
			}
		};
	};
	
	/**
	 * 显示软件下载对话框
	 */
	@SuppressLint("NewApi")
	private void showDownloadDialog() {
		// 构造软件下载对话框
		progressDialog = new ProgressDialog(activity);
		progressDialog.setTitle(R.string.soft_updating);
		progressDialog.setMax(100);
		// 设置进度条风格STYLE_HORIZONTAL
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setProgress(0);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		// 下载文件
		downloadApk();
	}

	/**
	 * 下载apk文件
	 */
	private void downloadApk() {
		// 启动新线程下载软件
		new downloadApkThread().start();
	}

	/**
	 * 下载文件线程
	 */
	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					
					URL url = new URL(MyUtils.getPicUrl() + versionMap.get("path"));
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入流
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists()) {
						file.mkdir();
					}
					
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do {
						int numread = is.read(buf);
						count += numread;
						// 计算进度条位置
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0) {
							// 下载完成
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// 点击取消就停止下载.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 取消下载对话框显示
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.cancel();
			}
		}
	};

	/**
	 * 安装APK文件
	 */
	private void installApk() {
		if (!apkFile.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkFile.toString()),
				"application/vnd.android.package-archive");
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
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
