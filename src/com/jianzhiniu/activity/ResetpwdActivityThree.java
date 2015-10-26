package com.jianzhiniu.activity;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianzhiniu.config.MyApplication;
import com.jianzhiniu.network.JsonTool;
import com.jianzhiniu.network.MapUtil;
import com.jianzhiniu.network.NetworkUtil;
import com.jianzhiniu.network.WebServiceUIAsyncTask;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.MyProgressDialog;

public class ResetpwdActivityThree extends BaseActivity{

	private ImageView returnView;
	private Button submit;
	private Context activity = ResetpwdActivityThree.this;
	private boolean isE;
	private EditText pwdEditText, pwdtEditText;
	private TextView topTextView;
	private Intent intent;
	private String idString = "";
	private WebServiceUIAsyncTask wt;
	private MapUtil mapUtil;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resetpwdthree);
		
		MyApplication.getInstance().addActivity(this);
		mapUtil = new MapUtil(activity);
		intent = getIntent();
		isE = intent.getBooleanExtra("type", false);
		idString = intent.getStringExtra("id");
		returnView = (ImageView)findViewById(R.id.top_return);
		submit = (Button)findViewById(R.id.next_btn);
		pwdEditText = (EditText)findViewById(R.id.pwd_edit);
		pwdtEditText = (EditText)findViewById(R.id.pwdt_edit);
		topTextView = (TextView)findViewById(R.id.top_centertext);
		
		pwdEditText.setTypeface(Typeface.DEFAULT);  
		pwdtEditText.setTypeface(Typeface.DEFAULT);  
		pwdEditText.setTransformationMethod(new PasswordTransformationMethod());
		pwdtEditText.setTransformationMethod(new PasswordTransformationMethod());
		
		topTextView.setText(R.string.resetpwd);
		submit.setOnClickListener(this);
		returnView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.top_return:
			this.finish();
			break;

		case R.id.next_btn:
			if (TextUtils.isEmpty(pwdEditText.getText()) || 
					TextUtils.isEmpty(pwdtEditText.getText())) {
				MyUtils.showToast(activity, R.string.input_newpwd);
			}else if (!pwdEditText.getText().toString().equals(pwdtEditText
					.getText().toString())) {
				MyUtils.showToast(activity, R.string.differentpwd);
			}else {
				if (NetworkUtil.isNetWorkConnected(activity)) {
					if (isE) {
						ExecuteAsyncTask("EditPwd", mapUtil.AlterpwdMap(idString, 
								pwdEditText.getText().toString(), "1"));
					}else {
						ExecuteAsyncTask("EditPwd", mapUtil.AlterpwdMap(idString, 
								pwdEditText.getText().toString(), "0"));
					}
				}else {
					MyUtils.showToast(activity, R.string.no_net);
				}
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
				Map<String, String> resultMap = new HashMap<String, String>();
				resultMap = JsonTool.isSuccessfuljson(result);
				if (resultMap == null) {
					MyUtils.showToast(activity, R.string.data_parse_error);
				}else {
					if (resultMap.get("result").equals("0")) {
						MyUtils.showToast2(activity, resultMap.get("msg"));
					}else {
						MyUtils.showToast(activity, R.string.alterpwd_success);
						ResetpwdActivityThree.this.finish();
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
				MyUtils.showToast(activity, R.string.timeout);
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
}
