package com.jianzhiniu.activity;

import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jianzhiniu.config.MyApplication;
import com.jianzhiniu.config.Myconfig;
import com.jianzhiniu.network.JsonTool;
import com.jianzhiniu.network.MapUtil;
import com.jianzhiniu.network.WebServiceUIAsyncTask;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.MyAlertDialog;
import com.jianzhiniu.views.MyProgressDialog;
import com.jianzhiniu.views.MyAlertDialog.AlertCallback;
import com.kyleduo.switchbutton.SwitchButton;

/**
 * 设置界面
 * @author Administrator
 *
 */
public class SettingActivity extends BaseActivity{

	private TextView centerView;
	private ImageView returnView;
	private SwitchButton push_btn;
	private Activity activity = SettingActivity.this;
	private RelativeLayout alterLayout;
	private Dialog d;
	private EditText editText2, editText3;
	private WebServiceUIAsyncTask wt;
	private MapUtil mapUtil;
	private Map<String, String> rmap = null;
	private Intent intent;
	private static int width;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_view);
		MyApplication.getInstance().addActivity(this);
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		width = displayMetrics.widthPixels;
		
		intent = getIntent();
		mapUtil = new MapUtil(activity);
		centerView = (TextView)findViewById(R.id.top_centertext);
		returnView = (ImageView)findViewById(R.id.top_return);
		push_btn = (SwitchButton)findViewById(R.id.onoff_push);
		alterLayout = (RelativeLayout)findViewById(R.id.alterpwd_view);
		push_btn.setChecked(true);
		
		centerView.setText(R.string.setting);
		alterLayout.setOnClickListener(this);
		returnView.setOnClickListener(this);
		
		push_btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (!isChecked) {
					MyUtils.showToast(activity, R.string.messagepush_close);
				}else {
					MyUtils.showToast(activity, R.string.messagepush_open);
				}
			}
		});
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
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.top_return:
			this.finish();
			break;
		case R.id.alterpwd_view:
			if (Myconfig.isLogin) {
				
				LayoutInflater inflater = getLayoutInflater();
				View view = inflater.inflate(R.layout.alterpwd, (ViewGroup)findViewById(R.id.alter));
//			editText1 = (EditText)view.findViewById(R.id.edit1);
				editText2 = (EditText)view.findViewById(R.id.edit2);
				editText3 = (EditText)view.findViewById(R.id.edit3);
				
				editText2.setTypeface(Typeface.DEFAULT);  
				editText3.setTypeface(Typeface.DEFAULT);  
				editText2.setTransformationMethod(new PasswordTransformationMethod());
				editText3.setTransformationMethod(new PasswordTransformationMethod());
				
				Button button1 = (Button)view.findViewById(R.id.p_button);
				Button button2 = (Button)view.findViewById(R.id.n_button);
				
				//设置对话框
				d = new Dialog(activity, R.style.alert_dialog);
				d.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
						LinearLayout.LayoutParams.MATCH_PARENT));
				d.show();
				d.setCanceledOnTouchOutside(false);
				WindowManager.LayoutParams params = d.getWindow()   
						.getAttributes();   
				params.width = width - getResources().getDimensionPixelSize(R.dimen.versiondialogpadding);   
				d.getWindow().setAttributes(params);  
				
				//设置按钮监听点击
				button1.setOnClickListener(this);
				button2.setOnClickListener(this);
			}else {
				new MyAlertDialog(activity, callback).showMyAlertDialog(R.string.loginfirst);
			}
			break;
		case R.id.p_button:  //点击开始修改账户密码
			if(TextUtils.isEmpty(editText2.getText()) || 
					TextUtils.isEmpty(editText3.getText()))
			{
				MyUtils.showToast(activity, R.string.info_lack);
			}else {
				if (!editText2.getText().toString().equals(editText3.
						getText().toString())) {
					MyUtils.showToast(activity, R.string.differentpwd);
				}else {
					ExecuteAsyncTask("EditPwd", mapUtil.AlterpwdMap(
							intent.getStringExtra("id"), 
							editText2.getText().toString(), 
							intent.getBooleanExtra("ise", false) ? "1" : "0"));
				}
			}
			break;
		case R.id.n_button:
			d.dismiss();
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
				rmap = JsonTool.isSuccessfuljson(result);
				if (rmap == null) {
					MyUtils.showToast2(activity, getResources().
							getString(R.string.data_parse_error));
				}else {
					MyUtils.showToast2(activity, rmap.get("msg"));
					if(rmap.get("result").equals("1"))
					{
						d.dismiss();
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
