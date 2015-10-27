package com.jianzhiniu.fragments;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jianzhiniu.activity.EnterCenterActivity;
import com.jianzhiniu.activity.InterestJobActivity;
import com.jianzhiniu.activity.JobIssueActivity;
import com.jianzhiniu.activity.LoginActivity;
import com.jianzhiniu.activity.MainActivity;
import com.jianzhiniu.activity.NearbyJobActivity;
import com.jianzhiniu.activity.NewestJobActivity;
import com.jianzhiniu.activity.PersonalCenterActivity;
import com.jianzhiniu.activity.R;
import com.jianzhiniu.activity.WebViewActivity;
import com.jianzhiniu.config.Myconfig;
import com.jianzhiniu.entity.AdInfo;
import com.jianzhiniu.network.JsonTool;
import com.jianzhiniu.network.MapUtil;
import com.jianzhiniu.network.WebServiceUIAsyncTask;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.AdBannerView;
import com.jianzhiniu.views.MyAlertDialog;
import com.jianzhiniu.views.MyAlertDialog.AlertCallback;

public class MainFragment extends Fragment implements OnClickListener{
	
	private Activity activity;
	private AdBannerView mAdBannerView = null;
	private final static int AD_ONCLICK = 0X44;
	private ArrayList<AdInfo> mAdInfoList = new ArrayList<AdInfo>();
	private RelativeLayout reLayout1, reLayout2, reLayout3;
	private TextView text1, text2, city;
	private LinearLayout lastLayout;
	private boolean isE;
	private WebServiceUIAsyncTask wt;
	private MapUtil mapUtil;
	private List<Map<String, String>> list = new ArrayList<Map<String,String>>();
	private Map<String, String> versionMap = new HashMap<String, String>();
	private MainActivity mainActivity;
	private ProgressDialog progressDialog;
	private String mSavePath; /* ���ر���·�� */
	private String Name; /* ���ص��ļ��� */
	private static final int DOWNLOAD = 2; /* ������ */
	private static final int DOWNLOAD_FINISH = 3; /* ���ؽ��� */
	private int progress; /* ��¼���������� */
	private boolean cancelUpdate; /* �Ƿ�ȡ������ */
	private Button vyButton, vnButton;
	private Dialog versionDialog;
	private TextView versioninfo;
	private static int width;
	private File apkFile;
	private View line;
	private SharedPreferences shared, eshared;
	private MyAlertDialog myAlertDialog, myAlertDialog2;
	private Intent toIntent;
	private boolean dissuccess;
	
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
		myAlertDialog2 = new MyAlertDialog(activity, callback2);
		shared = activity.getSharedPreferences("info", Context.MODE_PRIVATE);
		eshared = activity.getSharedPreferences("einfo", Context.MODE_PRIVATE);
		mainActivity = (MainActivity)getActivity();
		city = (TextView)mainActivity.findViewById(R.id.top_city);
		
		mSavePath = Environment.getExternalStorageDirectory() + "/ptDownload";
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		width = displayMetrics.widthPixels;
	}
	
	AlertCallback callback = new AlertCallback() {
		
		@Override
		public void ybutton() {
			// TODO Auto-generated method stub
			if (isE) {
				toIntent = new Intent(activity, EnterCenterActivity.class);
			}else {
				toIntent = new Intent(activity, PersonalCenterActivity.class);
			}
			toIntent.putExtra("complete", "");
			startActivity(toIntent);
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
	
	@SuppressLint("InflateParams") @Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(activity).inflate(R.layout.mainfragment, null);
		mAdBannerView = (AdBannerView) view.findViewById(R.id.page_banner_view);
		reLayout1 = (RelativeLayout)view.findViewById(R.id.relative1);
		reLayout2 = (RelativeLayout)view.findViewById(R.id.relative2);
		reLayout3 = (RelativeLayout)view.findViewById(R.id.relative3);
		text1 = (TextView)view.findViewById(R.id.text1);
		text2 = (TextView)view.findViewById(R.id.text2);
		line = (View)view.findViewById(R.id.maingoneline);
		lastLayout = (LinearLayout)view.findViewById(R.id.mainlastview);
		
		if (activity.getIntent().getBooleanExtra("type", false)) {
			isE = true;
			lastLayout.setVisibility(View.VISIBLE);
			text1.setText(R.string.issuejob);
			text2.setText(R.string.applyauth);
			reLayout2.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
			
			if ((eshared.getString("companyname", "").equals("") || 
					eshared.getString("profile", "").equals("")) && Myconfig.isLogin) {
				myAlertDialog.showMyAlertDialog(R.string.wsqyxx);
			}
		}else {
			if ((shared.getString("name", "").equals("") || 
					shared.getString("age", "").equals("") || 
					shared.getString("profile", "").equals("") || 
					shared.getString("experience", "").equals("") || 
					shared.getString("city", "").equals("") || 
					shared.getString("sex", "").equals("") || 
					shared.getString("jobsearchid", "").equals("")) && Myconfig.isLogin) {
				
				myAlertDialog.showMyAlertDialog(R.string.wsjl);
			}
		}
		
		mapUtil = new MapUtil(activity);
		ExecuteAsyncTask2("GetVersion", mapUtil.CheckVersionMap());
		
		reLayout1.setOnClickListener(this);
		reLayout2.setOnClickListener(this);
		reLayout3.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!dissuccess) {
			ExecuteAsyncTask("GetAdvertList", mapUtil.AccesskeyMap());
		}
		
	}
	
	/**
	 * ʹ���첽�ķ�ʽ��������
	 */
	public void ExecuteAsyncTask(String method, Map<String, Object> map) {
		wt = new WebServiceUIAsyncTask(activity) {
			
			@Override
			public void onTaskStartDoInUI(String... values) {
				// TODO Auto-generated method stub
				//��ʼ�������ݣ���ʾ���ؿ�
			}
			
			@Override
			public void onGetDataSuccessfulDoInUI(String result) {
				// TODO Auto-generated method stub
				//�ɹ���ȡ���ݣ���������
				list = JsonTool.AdvertiseImgjson(MyUtils.getPicUrl(), 
						result, "AdvertList");
				if (list == null) {
					MyUtils.showToast2(activity, getResources().
							getString(R.string.data_parse_error));
				}else {
					if(list.get(0).containsKey("result"))
					{
						MyUtils.showToast2(activity, list.get(0).get("msg"));
					}else{
						mHandler.sendEmptyMessage(1);
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
				MyUtils.showToast(activity, R.string.iserror);
				for (String string : values) {
					Log.i("it is error", string + "");
				}
			}
			
			@Override
			public void onCancelledDoInUI(String result) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onTimeoutDoInUI(String[] values) {
				// TODO Auto-generated method stub
				MyUtils.showToast2(activity, getResources().getString(R.string.timeout));
			}
		};
		
		wt.myExecute(method, map, false);
	}
	
	/**
	 * ���汾
	 */
	public void ExecuteAsyncTask2(String method, Map<String, Object> map) {
		wt = new WebServiceUIAsyncTask(activity) {
			
			@Override
			public void onTaskStartDoInUI(String... values) {
				// TODO Auto-generated method stub
				//��ʼ�������ݣ���ʾ���ؿ�
			}
			
			@Override
			public void onGetDataSuccessfulDoInUI(String result) {
				// TODO Auto-generated method stub
				//�ɹ���ȡ���ݣ���������
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
				MyUtils.showToast2(activity, getResources().getString(R.string.iserror));
				for (String string : values) {
					Log.i("it is error", string + "");
				}
			}
			
			@Override
			public void onCancelledDoInUI(String result) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onTimeoutDoInUI(String[] values) {
				// TODO Auto-generated method stub
				MyUtils.showToast2(activity, getResources().getString(R.string.timeout));
			}
		};
		
		wt.myExecute(method, map, false);
	}
	
	/**
	 * ���汾
	 */
	public void checkUpdate() {
		if (MyUtils.isUpdate(versionMap.get("no"), activity)) {
			// ��ʾ��ʾ�Ի���
			showNoticeDialog();
		}
	}
	
	/**
	 * ��ʾ�汾���¶Ի���
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
	
	/**
	 * ��ʾ������ضԻ���
	 */
	@SuppressLint("NewApi")
	private void showDownloadDialog() {
		// ����������ضԻ���
		progressDialog = new ProgressDialog(activity);
		progressDialog.setTitle(R.string.soft_updating);
		progressDialog.setMax(100);
		// ���ý��������STYLE_HORIZONTAL
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setProgress(0);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		// �����ļ�
		downloadApk();
	}

	/**
	 * ����apk�ļ�
	 */
	private void downloadApk() {
		// �������߳��������
		new downloadApkThread().start();
	}

	/**
	 * �����ļ��߳�
	 */
	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				// �ж�SD���Ƿ���ڣ������Ƿ���ж�дȨ��
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					
					URL url = new URL(MyUtils.getPicUrl() + versionMap.get("path"));
					// ��������
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.connect();
					// ��ȡ�ļ���С
					int length = conn.getContentLength();
					// ����������
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// �ж��ļ�Ŀ¼�Ƿ����
					if (!file.exists()) {
						file.mkdir();
					}
					
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// ����
					byte buf[] = new byte[1024];
					// д�뵽�ļ���
					do {
						int numread = is.read(buf);
						count += numread;
						// ���������λ��
						progress = (int) (((float) count / length) * 100);
						// ���½���
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0) {
							// �������
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// д���ļ�
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// ���ȡ����ֹͣ����.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// ȡ�����ضԻ�����ʾ
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.cancel();
			}
		}
	};

	/**
	 * ��װAPK�ļ�
	 */
	private void installApk() {
		if (!apkFile.exists()) {
			return;
		}
		// ͨ��Intent��װAPK�ļ�
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
	
	@SuppressLint("HandlerLeak") 
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
			case AD_ONCLICK:
				
				//���λ����¼�
				Intent wIntent = new Intent(activity, WebViewActivity.class);
				wIntent.putExtra("url", MyUtils.getAdverUrl() + 
						"accessKey=" + MyUtils.getAccesskey() + "&ID=" + 
						list.get(Integer.parseInt(msg.obj.toString())).get("id"));
				wIntent.putExtra("title", activity.getString(R.string.detailcontent));
				startActivity(wIntent);
				break;
			case 1:
				int usize = list.size();
				for (int i = 0; i < usize; i++) {
					AdInfo mAdInfo = new AdInfo();
					mAdInfo.setAdvImg(list.get(i).get("pic"));
					mAdInfoList.add(mAdInfo);
				}
				mAdBannerView.setClickFlag(AD_ONCLICK);
				mAdBannerView.init(mHandler, mAdInfoList);
				dissuccess = true;
				break;
				// ��������
			case DOWNLOAD:
				// ���ý�����λ��
				if (progressDialog != null && progressDialog.isShowing())
					progressDialog.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// ��װ�ļ�
				installApk();
				break;
			}
		}
	};
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.relative1:
			Intent intenti = null;
			if (isE) {
				if (eshared.getString("companyname", "").equals("") || 
						eshared.getString("profile", "").equals("")) {
					myAlertDialog.showMyAlertDialog(R.string.wsqyxx);
				}else {
					intenti = new Intent(activity, JobIssueActivity.class);
					startActivity(intenti);
				}
			}else {
				if (Myconfig.isLogin) {
					
					if (shared.getString("jobsearch", "").equals("")) {
						myAlertDialog.showMyAlertDialog(R.string.wsjl);
					}else {
						intenti = new Intent(activity, InterestJobActivity.class);
						intenti.putExtra("type", "interest");
						intenti.putExtra("city", city.getText().toString());
						startActivity(intenti);
					}
					
				}else {
					myAlertDialog2.showMyAlertDialog(R.string.loginfirst);
				}
			}
			break;
		case R.id.relative2:
			Intent intentn = null;
			if (isE) {
				MyUtils.showToast2(activity, "����δ����");
			}else {
				intentn = new Intent(activity, NearbyJobActivity.class);
				startActivity(intentn);
			}
			break;
		case R.id.relative3:
			Intent newIntent = new Intent(activity, NewestJobActivity.class);
			newIntent.putExtra("newjob", "newjob");
			startActivity(newIntent);
			break;
		case R.id.u_button:
			versionDialog.dismiss();
			if (apkFile.exists()) {
				//apk������ֱ�Ӱ�װ
				installApk();
			}else {
				//��ʾ���ضԻ���
				showDownloadDialog();
			}
			break;
		case R.id.nu_button:
			versionDialog.dismiss();
			break;
		default:
			break;
		}
	}
}
