package com.jianzhiniu.activity;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.twitter.Twitter;

import com.jianzhiniu.config.MyApplication;
import com.jianzhiniu.config.Myconfig;
import com.jianzhiniu.network.JsonTool;
import com.jianzhiniu.network.MapUtil;
import com.jianzhiniu.network.NetworkUtil;
import com.jianzhiniu.network.WebServiceUIAsyncTask;
import com.jianzhiniu.utils.ImageUtil;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.MyProgressDialog;
import com.jianzhiniu.views.RoundImageView;

public class LoginActivity extends BaseActivity implements Callback, PlatformActionListener{
	private Button button_login, button_fb, button_tw;
	private Context activity = LoginActivity.this;
	private ImageView returnView;
	private Intent intent, toIntent;
	private boolean isE, isFB, isTW;
	private WebServiceUIAsyncTask wt = null;
	private EditText accountEdit, pwdEdit;
	private MapUtil mapUtil;
	private TextView registerView, forgetView, entryText;
	private CheckBox rememberView;
	private SharedPreferences shared, shareds, einfoShared, infoShared;
	private SharedPreferences.Editor editor, editors, einfoEditor, infoEditor;
	private RoundImageView roundImg;
	private String imgurl;
	private Bitmap image;
	private View divideView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		MyApplication.getInstance().addActivity(this);
		mapUtil = new MapUtil(activity);
		
		einfoShared = getSharedPreferences("einfo", Context.MODE_PRIVATE);
		infoShared = getSharedPreferences("info", Context.MODE_PRIVATE);
		einfoEditor = einfoShared.edit();
		infoEditor = infoShared.edit();
		
		roundImg = (RoundImageView)findViewById(R.id.user_roundpic);
		registerView = (TextView)findViewById(R.id.register);
		forgetView = (TextView)findViewById(R.id.forger);
		entryText = (TextView)findViewById(R.id.entry);
		divideView = (View)findViewById(R.id.bdivide);
		rememberView = (CheckBox)findViewById(R.id.remember);
		button_login = (Button)findViewById(R.id.button_login);
		button_fb = (Button)findViewById(R.id.button_fb);
		button_tw = (Button)findViewById(R.id.button_tw);
		returnView = (ImageView)findViewById(R.id.return_btn);
		accountEdit = (EditText)findViewById(R.id.signinact_username);
		pwdEdit = (EditText)findViewById(R.id.signinact_password);
		
		pwdEdit.setTypeface(Typeface.DEFAULT);  
		pwdEdit.setTransformationMethod(new PasswordTransformationMethod());
		
		entryText.setOnClickListener(this);
		returnView.setOnClickListener(this);
		rememberView.setOnClickListener(this);
		forgetView.setOnClickListener(this);
		registerView.setOnClickListener(this);
		button_login.setOnClickListener(this);
		button_fb.setOnClickListener(this);
		button_tw.setOnClickListener(this);
		
		intent = getIntent();
		if (intent.getStringExtra("flag").equals("e")) {
			isE = true;
			button_fb.setVisibility(View.GONE);
			button_tw.setVisibility(View.GONE);
			divideView.setVisibility(View.GONE);
			entryText.setVisibility(View.GONE);
			registerView.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 
					LayoutParams.WRAP_CONTENT));
			registerView.setGravity(Gravity.CENTER);
			
			shared = getSharedPreferences("user", Context.MODE_PRIVATE);
			editor = shared.edit();
		}else {
			shareds = getSharedPreferences("users", Context.MODE_PRIVATE);
			editors = shareds.edit();
		}
		
		
		/**
		 * 监听账号输入框变化
		 */
		accountEdit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(accountEdit.getText())) {
					roundImg.setImageResource(R.drawable.userpic);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (isE) {
					imgurl = getExternalCacheDir() + "/e" + accountEdit.getText().toString().trim() + ".png";
				}else {
					imgurl = getExternalCacheDir() + "/" + accountEdit.getText().toString().trim() + ".png";
				}
				
				if (!TextUtils.isEmpty(accountEdit.getText())) {
					if (ImageUtil.isHave(imgurl)) {
						image = BitmapFactory.decodeFile(imgurl);
						roundImg.setImageBitmap(image);
					}else {
						roundImg.setImageResource(R.drawable.userpic);
					}
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (isE) {
			if (shared.getString("account", null) != null) {
				accountEdit.setText(shared.getString("account", ""));
				if (shared.getString("pwd", null) != null) {
					pwdEdit.setText(shared.getString("pwd", ""));
					rememberView.setChecked(true);
					ExecuteAsyncTask("LoginEnterprise", mapUtil.LoginMap(accountEdit.getText().toString(), 
							pwdEdit.getText().toString()));
					
				}
			}
			imgurl = getExternalCacheDir() + "/e" + accountEdit.getText().toString().trim() + ".png";
		}else {
			if (shareds.getString("account", null) != null) {
				accountEdit.setText(shareds.getString("account", ""));
				if (shareds.getString("pwd", null) != null) {
					pwdEdit.setText(shareds.getString("pwd", ""));
					rememberView.setChecked(true);
					ExecuteAsyncTask("LoginSeekers", mapUtil.LoginMap(accountEdit.getText().toString(), 
							pwdEdit.getText().toString()));
				}
			}
			imgurl = getExternalCacheDir() + "/" + accountEdit.getText().toString().trim() + ".png";
		}
		
		//显示头像
		if (!TextUtils.isEmpty(accountEdit.getText()) && ImageUtil.isHave(imgurl)) {
			image = BitmapFactory.decodeFile(imgurl);
			roundImg.setImageBitmap(image);
		}
	}
	
	private void authorize(Platform plat) {
		//判断指定平台是否已经完成授权
		if(plat.isValid()) {
			String userId = plat.getDb().getUserId();
			String nameString = plat.getDb().getUserName();
			String iconString = plat.getDb().getUserIcon();
//			String genderString = plat.getDb().getUserGender(); //性别
			if (userId != null) {
				if (plat.getName().equals("Facebook")) {
					isFB = true;
					ExecuteAsyncTask("LoginSeekersByThird", mapUtil.TLoginMap(userId, nameString, 
							iconString, "1"));
				}else {
					isTW = true;
					ExecuteAsyncTask("LoginSeekersByThird", mapUtil.TLoginMap(userId, nameString, 
							iconString, "0"));
				}
				return;
			}
		}
		plat.setPlatformActionListener(this);
		//关闭SSO授权
		plat.SSOSetting(true);
		plat.showUser(null);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.entry:
			Intent mIntent = new Intent(activity, MainActivity.class);
			mIntent.putExtra("type", isE);
			mIntent.putExtra("fb", isFB);
			mIntent.putExtra("tw", isTW);
			startActivity(mIntent);
			LoginActivity.this.finish();
			break;
		case R.id.button_login:
			if (TextUtils.isEmpty(accountEdit.getText())) {
				MyUtils.showToast2(activity, getResources().getString(R.string.input_account));
			}else if (TextUtils.isEmpty(pwdEdit.getText())) {
				MyUtils.showToast2(activity, getResources().getString(R.string.input_pwd));
			}else {
				if (isE) {
					ExecuteAsyncTask("LoginEnterprise", mapUtil.LoginMap(accountEdit.getText().toString(), 
							pwdEdit.getText().toString()));
				}else {
					ExecuteAsyncTask("LoginSeekers", mapUtil.LoginMap(accountEdit.getText().toString(), 
							pwdEdit.getText().toString()));
				}
			}
			break;
		case R.id.button_fb:
			if (!NetworkUtil.isNetWorkConnected(activity)) {
				MyUtils.showToast2(activity, getResources().getString(R.string.no_net));
			}else {
				accountEdit.setText("");
				pwdEdit.setText("");
				rememberView.setChecked(false);
				ShareSDK.initSDK(this);
				Platform facebook = ShareSDK.getPlatform(Facebook.NAME);
				authorize(facebook);
			}
			break;
		case R.id.button_tw:
			if (!NetworkUtil.isNetWorkConnected(activity)) {
				MyUtils.showToast2(activity, getResources().getString(R.string.no_net));
			}else {
				accountEdit.setText("");
				pwdEdit.setText("");
				rememberView.setChecked(false);
				ShareSDK.initSDK(this);
				Platform twitter = ShareSDK.getPlatform(Twitter.NAME);
				authorize(twitter);
			}
			break;
			
		case R.id.return_btn:
			this.finish();
			break;
		case R.id.forger:
			Intent fIntent = new Intent(activity, ResetpwdActivityOne.class);
			fIntent.putExtra("type", isE);
			startActivity(fIntent);
			break;
		case R.id.register:
			Intent rIntent = new Intent(activity, RegisterActivity.class);
			rIntent.putExtra("flag", isE);
			startActivity(rIntent);
			break;
		default:
			break;
		}
	}
	
	public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
		if (action == Platform.ACTION_USER_INFOR) {
			String idString = platform.getDb().getUserId();
			String nameString = platform.getDb().getUserName();
			String iconString = platform.getDb().getUserIcon();
//			String genderString = platform.getDb().getUserGender();
			if (idString != null) {
				if (platform.getName().equals("Facebook")) {
					isFB = true;
					ExecuteAsyncTask("LoginSeekersByThird", mapUtil.TLoginMap(idString, nameString, 
							iconString, "1"));
				}else {
					isTW = true;
					ExecuteAsyncTask("LoginSeekersByThird", mapUtil.TLoginMap(idString, nameString, 
							iconString, "0"));
				}
			}
		}
	}
	
	public void onError(Platform platform, int action, Throwable t) {
		if (action == Platform.ACTION_USER_INFOR) {
			platform.removeAccount();
			Toast.makeText(activity, R.string.auth_error, Toast.LENGTH_SHORT).show();
		}
		t.printStackTrace();
	}
	
	public void onCancel(Platform platform, int action) {
		if (action == Platform.ACTION_USER_INFOR) {
			Toast.makeText(activity, R.string.auth_cancel, Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
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
				toIntent = new Intent(activity, MainActivity.class);
				
				if (isE) {
					resultMap = JsonTool.Loginjson(result, "EnterpriseInfo");
					if (resultMap == null) {
						MyUtils.showToast2(activity, getResources().getString(R.string.data_parse_error));
					}else {
						if (resultMap.get("result").equals("0")) {
							MyUtils.showToast2(activity, resultMap.get("msg"));
						}else {
							
							einfoEditor.putString("id", resultMap.get("id"));
							einfoEditor.putString("companyname", MyUtils.ParseString(resultMap.get("companyname")));
							einfoEditor.putString("profile", MyUtils.ParseString(resultMap.get("profile")));
							einfoEditor.putString("pic", resultMap.get("pic"));
							einfoEditor.putString("contactperson", resultMap.get("contactperson"));
							einfoEditor.putString("mobile", resultMap.get("mobile"));
							einfoEditor.putString("loginid", resultMap.get("loginid"));
							einfoEditor.putString("email", resultMap.get("email"));
							einfoEditor.putString("authentication", resultMap.get("authentication"));
							einfoEditor.commit();
							
							if (rememberView.isChecked()) {
								editor.putString("account", accountEdit.getText().toString());
								editor.putString("pwd", pwdEdit.getText().toString());
							}else {
								editor.clear();
								editor.putString("account", accountEdit.getText().toString());
							}
							editor.commit();
							
							toIntent.putExtra("type", isE);
							toIntent.putExtra("fb", isFB);
							toIntent.putExtra("tw", isTW);
							startActivity(toIntent);
							Myconfig.isLogin = true;
							LoginActivity.this.finish();
						}
					}
				}else {
					resultMap = JsonTool.Loginjson(result, "JobSeekersInfo");
					if (resultMap == null) {
						MyUtils.showToast2(activity, getResources().getString(R.string.data_parse_error));
					}else {
						if (resultMap.get("result").equals("0")) {
							MyUtils.showToast2(activity, resultMap.get("msg"));
						}else {
							
							infoEditor.putString("id", resultMap.get("id"));
							infoEditor.putString("loginid", resultMap.get("loginid"));
							infoEditor.putString("name", resultMap.get("name"));
							infoEditor.putString("mobile", resultMap.get("mobile"));
							infoEditor.putString("email", resultMap.get("email"));
							infoEditor.putString("birth", resultMap.get("birth"));
							infoEditor.putString("age", resultMap.get("age"));
							infoEditor.putString("height", resultMap.get("height"));
							infoEditor.putString("school", MyUtils.ParseString(resultMap.get("school")));
							infoEditor.putString("profile", MyUtils.ParseString(resultMap.get("profile")));
							infoEditor.putString("experience", MyUtils.ParseString(resultMap.get("experience")));
							infoEditor.putString("capital", resultMap.get("capital"));
							infoEditor.putString("city", resultMap.get("city"));
							infoEditor.putString("area", MyUtils.ParseString(resultMap.get("area")));
							infoEditor.putString("freetime", resultMap.get("freetime"));
							infoEditor.putString("jobsearchid", resultMap.get("jobsearchid"));
							infoEditor.putString("jobsearch", resultMap.get("jobsearch"));
							infoEditor.putString("pic", resultMap.get("pic"));
							infoEditor.putString("sex", resultMap.get("sex"));
							infoEditor.commit();
							
							if (!TextUtils.isEmpty(accountEdit.getText())) {
								if (rememberView.isChecked()) {
									editors.putString("account", accountEdit.getText().toString());
									editors.putString("pwd", pwdEdit.getText().toString());
								}else {
									editors.clear();
									editors.putString("account", accountEdit.getText().toString());
								}
								editors.commit();
							}
							
							toIntent.putExtra("type", isE);
							toIntent.putExtra("fb", isFB);
							toIntent.putExtra("tw", isTW);

							startActivity(toIntent);
							Myconfig.isLogin = true;
							LoginActivity.this.finish();
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
	
}
