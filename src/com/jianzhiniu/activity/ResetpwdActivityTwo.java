package com.jianzhiniu.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.jianzhiniu.config.MyApplication;
import com.jianzhiniu.network.NetworkUtil;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.MyProgressDialog;

public class ResetpwdActivityTwo extends BaseActivity{

	private TextView topText, getverify;
	private ImageView returnImage;
	private EditText phoneEdit;
	private Button nextButton;
	private Context activity = ResetpwdActivityTwo.this;
	private static int sec = 60;
	private boolean isE;
	private Intent intent;
	private String numString;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resetpwdtwo);
		
		MyApplication.getInstance().addActivity(this);
		intent = getIntent();
		isE = intent.getBooleanExtra("type", false);
		numString = intent.getStringExtra("num");
		initsms();
		MyProgressDialog.showDialog(activity, 0);
		SMSSDK.getVerificationCode(getResources().getString(R.string.qh), 
				numString);
		
		topText = (TextView)findViewById(R.id.top_centertext);
		getverify = (TextView)findViewById(R.id.verify_btn);
		returnImage = (ImageView)findViewById(R.id.top_return);
		nextButton = (Button)findViewById(R.id.next_btn);
		phoneEdit = (EditText)findViewById(R.id.phonenum_edit);
		
		topText.setText(R.string.resetpwd);
		getverify.setOnClickListener(this);
		returnImage.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		
	}
	
	/**
	 * 初始化短信接口
	 */
	private void initsms(){
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
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		myHandler.removeMessages(4);
		sec = 60;
		SMSSDK.unregisterAllEventHandler();
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
				Intent tIntent = new Intent(activity, ResetpwdActivityThree.class);
				tIntent.putExtra("type", isE);
				tIntent.putExtra("id", intent.getStringExtra("id"));
				startActivity(tIntent);
				ResetpwdActivityTwo.this.finish();
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
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.verify_btn:
			if (!NetworkUtil.isNetWorkConnected(activity)) {
				MyUtils.showToast2(activity, getResources().getString(R.string.no_net));
			}else {
				MyProgressDialog.showDialog(activity, 0);
				SMSSDK.getVerificationCode(getResources().getString(R.string.qh), 
						numString);
			}
			break;
		case R.id.next_btn:
			if (TextUtils.isEmpty(phoneEdit.getText())) {
				MyUtils.showToast2(activity, getResources().getString(R.string.input_verifynum));
			}else {
				if (!NetworkUtil.isNetWorkConnected(activity)) {
					MyUtils.showToast2(activity, getResources().getString(R.string.no_net));
				}else {
					MyProgressDialog.showDialog(activity, 0);
					SMSSDK.submitVerificationCode(getResources().getString(R.string.qh), 
							numString, phoneEdit.getText().toString());
				}
			}
			break;
		case R.id.top_return:
			this.finish();
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
}
