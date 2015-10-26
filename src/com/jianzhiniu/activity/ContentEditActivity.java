package com.jianzhiniu.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianzhiniu.config.MyApplication;
import com.jianzhiniu.network.JsonTool;
import com.jianzhiniu.network.MapUtil;
import com.jianzhiniu.network.WebServiceUIAsyncTask;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.MyProgressDialog;

public class ContentEditActivity extends BaseActivity{

	private Activity activity = ContentEditActivity.this;
	private TextView centerView, rightView;
	private EditText editText, emailEditText;
	private ImageView returnView;
	private Intent intent;
	private String typeString = "complain";
	private WebServiceUIAsyncTask wt;
	private MapUtil mapUtil;
	private SharedPreferences isShared, idShared;
	private Map<String, String> infomap = new HashMap<String, String>(); 
	private View emailView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contentedit_view);
		MyApplication.getInstance().addActivity(this);
		
		isShared = getSharedPreferences("isE", Context.MODE_PRIVATE);
		if (isShared.getBoolean("isE", false)) {
			idShared = getSharedPreferences("einfo", Context.MODE_PRIVATE);
		}else {
			idShared = getSharedPreferences("info", Context.MODE_PRIVATE);
		}
		mapUtil = new MapUtil(activity);
		intent = getIntent();
		centerView = (TextView)findViewById(R.id.top_centertext);
		rightView = (TextView)findViewById(R.id.top_righttext);
		editText = (EditText)findViewById(R.id.content_edit);
		emailEditText = (EditText)findViewById(R.id.email_edit);
		returnView = (ImageView)findViewById(R.id.top_return);
		emailView = (View)findViewById(R.id.emailview);
		
		centerView.setText(R.string.submit_complain);
		if (intent.getStringExtra("type").equals("consult")) {
			typeString = "consult";
			editText.setHint(R.string.input_consultcontent);
			centerView.setText(R.string.submit_consult);
		}else if (intent.getStringExtra("type").equals("feedback")) {
			typeString = "feedback";
			editText.setHint(R.string.input_feedback);
			centerView.setText(R.string.suggestion_feedback);
			emailEditText.setText(idShared.getString("email", ""));
			emailView.setVisibility(View.VISIBLE);
		}else if (intent.getStringExtra("type").equals("reply")) {
			typeString = "reply";
			editText.setHint(R.string.input_replycontent);
			centerView.setText(R.string.edit_reply);
		}
		
		rightView.setVisibility(View.VISIBLE);
		rightView.setText(R.string.submit);
		
		returnView.setOnClickListener(this);
		rightView.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.top_return:
			this.finish();
			break;
		case R.id.top_righttext:
			if (TextUtils.isEmpty(editText.getText())) {
				MyUtils.showToast(activity, R.string.content_notnull);
			}else {
				if (typeString.equals("consult")) {
					ExecuteAsyncTask("SubmitJobQuestion", mapUtil.SendConsultMap(intent.
							getStringExtra("id"), idShared.getString("id", ""), 
							MyUtils.UploadString(editText.getText().toString())));
				}else if (typeString.equals("complain")) {
					ExecuteAsyncTask("SubmitComplaint", mapUtil.SendComplainMap(intent.getStringExtra("id"), 
							"", intent.getStringExtra("eid"), editText.getText().toString(), idShared.getString("id", "")));
				}else if (typeString.equals("feedback")) {
					if (TextUtils.isEmpty(emailEditText.getText())) {
						MyUtils.showToast(activity, R.string.input_email);
					}else {
						ExecuteAsyncTask("SubmitFeedback", mapUtil.SendFeedbackMap(emailEditText.getText().toString().trim(), 
								idShared.getString("id", ""), editText.getText().toString()));
					}
				}else if (typeString.equals("reply")) {
					ExecuteAsyncTask("SubmitJobQuestionReply", mapUtil.ConsultReplyMap(intent.getStringExtra("id"), 
							idShared.getString("id", ""), MyUtils.UploadString(editText.getText().toString())));
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
					infomap = JsonTool.isSuccessfuljson(result);
					if (infomap == null) {
						MyUtils.showToast2(activity, getResources().
								getString(R.string.data_parse_error));
					}else {
						MyUtils.showToast2(activity, infomap.get("msg"));
						if(infomap.get("result").equals("1"))
						{
							if (typeString.equals("complain")) {
								
							}else {
								activity.setResult(11);
							}
							activity.finish();
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
	
}
