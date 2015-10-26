package com.jianzhiniu.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.jianzhiniu.config.MyApplication;
import com.jianzhiniu.network.JsonTool;
import com.jianzhiniu.network.MapUtil;
import com.jianzhiniu.network.NetworkUtil;
import com.jianzhiniu.network.WebServiceUIAsyncTask;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.MyProgressDialog;

public class RegisterActivity extends BaseActivity{
	
	private TextView getverify, agreement, topTextView;
	private ImageView returnView;
	private Button registerButton;
	private CheckBox checkBox;
	private EditText phoneEdit, verifyEdit, pwdEdit;
	private Context activity = RegisterActivity.this;
	private WebServiceUIAsyncTask wt = null;
	private MapUtil mapUtil;
	private boolean isE;
	private Intent intent;
	private static int sec = 60;
	private String webtype;
	private SharedPreferences shared;
	private SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		MyApplication.getInstance().addActivity(this);
		intent = getIntent();
		isE = intent.getBooleanExtra("flag", false);
		
		mapUtil = new MapUtil(activity);
		getverify = (TextView)findViewById(R.id.verify_btn);
		topTextView = (TextView)findViewById(R.id.top_centertext);
		agreement = (TextView)findViewById(R.id.agreement_text);
		returnView = (ImageView)findViewById(R.id.top_return);
		registerButton = (Button)findViewById(R.id.register_btn);
		checkBox = (CheckBox)findViewById(R.id.checkbox);
		phoneEdit = (EditText)findViewById(R.id.phonenum_edit);
		verifyEdit = (EditText)findViewById(R.id.verifynum_edit);
		pwdEdit = (EditText)findViewById(R.id.pwd_edit);
		
		checkBox.setChecked(true);
		registerButton.setOnClickListener(this);
		returnView.setOnClickListener(this);
		getverify.setOnClickListener(this);
		agreement.setOnClickListener(this);
		
		if (isE) {
			topTextView.setText(R.string.enterpriseregister);
			shared = getSharedPreferences("user", Context.MODE_PRIVATE);
			editor = shared.edit();
		}else {
			shared = getSharedPreferences("users", Context.MODE_PRIVATE);
			editor = shared.edit();
		}
		
		SMSSDK.initSDK(activity, "8f902714f291", "8708070190debcea6051ed2fab9aedb2");
		EventHandler eventHandler = new EventHandler() {
			public void afterEvent(int event, int result, Object data) {
				if (result == SMSSDK.RESULT_COMPLETE) {
					//回调完成
					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
						//验证码正确
						myHandler.sendEmptyMessage(2);
					}else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
						//获取验证码成功
						myHandler.sendEmptyMessage(1);
					}else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
						//返回支持发送验证码的国家列表
					} 
					
				}else{                         
					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
						//验证码错误
						myHandler.sendEmptyMessage(3);
					}
					((Throwable)data).printStackTrace(); 
				}
			}
		};
		// 注册回调监听接口
		SMSSDK.registerEventHandler(eventHandler);
	}
	
	@SuppressLint("HandlerLeak")
	private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				//发送验证码成功
				MyProgressDialog.Dismiss();
				MyUtils.showToast2(activity, getResources().getString(
						R.string.verify_send_success));
				
				getverify.setEnabled(false);
				getverify.setBackgroundResource(R.drawable.verifybtn_bgg);
				getverify.setText(getResources().getString(R.string.wait) + sec + "s");
				myHandler.sendEmptyMessageDelayed(4, 1000);
				break;
			case 2:
				//验证码正确
				webtype = "reg";
				if (isE) {
					ExecuteAsyncTask("RegEnterprise", mapUtil.SeekerRegisterMap(phoneEdit.getText().toString(), 
							pwdEdit.getText().toString()));
				}else {
					ExecuteAsyncTask("RegSeekers", mapUtil.SeekerRegisterMap(phoneEdit.getText().toString(), 
							pwdEdit.getText().toString()));
				}
				break;
			case 3:
				//验证码错误
				MyProgressDialog.Dismiss();
				MyUtils.showToast2(activity, getResources().getString(
						R.string.verify_error));
				break;
			case 4:
				sec -= 1;
				if (sec == 0) {
					sec = 60;
					getverify.setEnabled(true);
					getverify.setBackgroundResource(R.drawable.verifybtn_bgo);
					getverify.setText(R.string.get_verifynum);
				}else{
					getverify.setText(getResources().getString(R.string.wait) + sec + "s");
					myHandler.sendEmptyMessageDelayed(4, 1000);
				}
				break;
				
			default:
				break;
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
			}
			
			@Override
			public void onGetDataSuccessfulDoInUI(String result) {
				// TODO Auto-generated method stub
				//成功获取数据，解析数据
				Map<String, String> map = new HashMap<String, String>();
				if (webtype.equals("reg")) {
					MyProgressDialog.Dismiss();
					map = JsonTool.isSuccessfuljson(result);
					if (map == null) {
						MyUtils.showToast2(activity, getResources().getString(R.string.data_parse_error));
					}else {
						if(map.get("result").equals("1"))
						{
							editor.putString("account", phoneEdit.getText().toString());
							editor.putString("pwd", pwdEdit.getText().toString());
							editor.commit();
							MyUtils.showToast2(activity, getResources().getString(R.string.register_success));
							RegisterActivity.this.finish();
						}else{
							MyUtils.showToast2(activity, map.get("msg"));
						}
					}
				}else if(webtype.equals("ver")){
					if (isE) {
						map = JsonTool.IsphoneRegisterjson(result, "EnterpriseInfo");
					}else {
						map = JsonTool.IsphoneRegisterjson(result, "JobSeekersInfo");
					}
					
					if (map == null) {
						MyProgressDialog.Dismiss();
						MyUtils.showToast2(activity, getResources().getString(R.string.data_parse_error));
					}else {
						if (map.get("result").equals("0")) {
							if (!NetworkUtil.isNetWorkConnected(activity)) {
								MyUtils.showToast2(activity, getResources().getString(R.string.no_net));
							}else {
								SMSSDK.getVerificationCode(getResources().getString(R.string.qh), 
										phoneEdit.getText().toString());
							}
						}else {
							MyProgressDialog.Dismiss();
							MyUtils.showToast(activity, R.string.phoneregisted);
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
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (wt != null && !wt.isCancelled()) {
			wt.cancel(true);
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.register_btn:
			if (TextUtils.isEmpty(phoneEdit.getText())) {
				MyUtils.showToast2(activity, getResources().getString(R.string.phonenum_notnull));
			}else if (TextUtils.isEmpty(verifyEdit.getText())) {
				MyUtils.showToast2(activity, getResources().getString(R.string.verifynum_notnull));
			}else if (TextUtils.isEmpty(pwdEdit.getText())) {
				MyUtils.showToast2(activity, getResources().getString(R.string.pwd_notnull));
			}else if (!checkBox.isChecked()) {
				MyUtils.showToast2(activity, getResources().getString(R.string.mustagree));
			}else {
				if (!NetworkUtil.isNetWorkConnected(activity)) {
					MyUtils.showToast2(activity, getResources().getString(R.string.no_net));
				}else {
					MyProgressDialog.showDialog(activity, 0);
					SMSSDK.submitVerificationCode(getResources().getString(R.string.qh), 
							phoneEdit.getText().toString(), verifyEdit.getText().toString());
				}
			}
			break;
		case R.id.top_return:
			RegisterActivity.this.finish();
			break;
		case R.id.verify_btn:
			if (TextUtils.isEmpty(phoneEdit.getText())) {
				MyUtils.showToast2(activity, getResources().getString(R.string.input_phonenum));
			}else {
				if (isMobile(phoneEdit.getText().toString())) {
					webtype = "ver";
					MyProgressDialog.showDialog(activity, 0);
					if (isE) {
						ExecuteAsyncTask("GetEnterpriseInfoByMobile", 
								mapUtil.VerifyphoneMap(phoneEdit.getText().toString()));
					}else {
						ExecuteAsyncTask("GetJobSeekersInfoByMobile", 
								mapUtil.VerifyphoneMap(phoneEdit.getText().toString()));
					}
					
				}else {
					MyUtils.showToast2(activity, getResources().getString(R.string.input_true_phonenum));
				}
			}
			
			break;
		case R.id.agreement_text:
			
			break;
			
		default:
			break;
		}
	}
	
	/**
	 * 验证是否是手机号码
	 * 
	 * @param str
	 * @return
	 */
	public boolean isMobile(String str) {
		Pattern pattern = Pattern.compile("1[0-9]{10}");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		myHandler.removeMessages(4);
		sec = 60;
		SMSSDK.unregisterAllEventHandler();
	}
}
