package com.jianzhiniu.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class ResetpwdActivityOne extends BaseActivity{

	private Context activity = ResetpwdActivityOne.this;
	private TextView topView;
	private ImageView returnView;
	private Button nextButton;
	private Intent intent;
	private EditText editText;
	private boolean isE;
	private WebServiceUIAsyncTask wt;
	private MapUtil mapUtil;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resetpwdone);
		
		MyApplication.getInstance().addActivity(this);
		mapUtil = new MapUtil(activity);
		intent = getIntent();
		isE = intent.getBooleanExtra("type", false);
		topView = (TextView)findViewById(R.id.top_centertext);
		returnView = (ImageView)findViewById(R.id.top_return);
		nextButton = (Button)findViewById(R.id.next_btn);
		editText = (EditText)findViewById(R.id.number_edit);
		
		topView.setText(getResources().getString(R.string.resetpwd));
		
		returnView.setOnClickListener(this);
		nextButton.setOnClickListener(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (wt != null && !wt.isCancelled()) {
			wt.cancel(true);
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.top_return:
			this.finish();
			break;
		case R.id.next_btn:
			if (TextUtils.isEmpty(editText.getText())) {
				MyUtils.showToast2(activity, getResources().getString(
						R.string.phonenum_notnull));
			}else {
				if (isMobile(editText.getText().toString())) {
					if (NetworkUtil.isNetWorkConnected(activity)) {
						if (isE) {
							ExecuteAsyncTask("GetEnterpriseInfoByMobile", 
									mapUtil.VerifyphoneMap(editText.getText().toString()));
						}else {
							ExecuteAsyncTask("GetJobSeekersInfoByMobile", 
									mapUtil.VerifyphoneMap(editText.getText().toString()));
						}
					}else {
						MyUtils.showToast2(activity, getResources().getString(
								R.string.no_net));
					}
				}else {
					MyUtils.showToast2(activity, getResources().getString(
							R.string.input_true_phonenum));
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
				if (isE) {
					resultMap = JsonTool.IsphoneRegisterjson(result, "EnterpriseInfo");
				}else {
					resultMap = JsonTool.IsphoneRegisterjson(result, "JobSeekersInfo");
				}
				
				if (resultMap == null) {
					MyUtils.showToast2(activity, getResources().getString(R.string.data_parse_error));
				}else {
					if (resultMap.get("result").equals("0")) {
						MyUtils.showToast2(activity, resultMap.get("msg"));
					}else {
						Intent tIntent = new Intent(activity, ResetpwdActivityTwo.class);
						tIntent.putExtra("type", isE);
						tIntent.putExtra("id", resultMap.get("id"));
						tIntent.putExtra("num", editText.getText().toString());
						startActivity(tIntent);
						ResetpwdActivityOne.this.finish();
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
}
