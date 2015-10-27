package com.jianzhiniu.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jianzhiniu.config.MyApplication;
import com.jianzhiniu.network.JsonTool;
import com.jianzhiniu.network.MapUtil;
import com.jianzhiniu.network.WebServiceUIAsyncTask;
import com.jianzhiniu.utils.ImageUtil;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.CustomMultiChoiceDialog;
import com.jianzhiniu.views.DateTimePickerDialog;
import com.jianzhiniu.views.DateTimePickerDialog.OnDateTimeSetListener;
import com.jianzhiniu.views.MyAlertDialog;
import com.jianzhiniu.views.MyAlertDialog.AlertCallback;
import com.jianzhiniu.views.MyProgressDialog;
import com.jianzhiniu.views.RoundImageView;
import com.jianzhiniu.views.SelectPicPopupWindow;

/**
 * 求职者个人中心界面
 * @author Administrator
 *
 */
@SuppressLint("InflateParams") 
public class PersonalCenterActivity extends BaseActivity{
	
	private Activity activity = PersonalCenterActivity.this;
	private ImageView returnView, to1, to2, to3, to4, phonecall;
	private TextView topcenter, topright, sexLayout, birthLayout, jobobjective, 
	city, man, woman, nametext, phonetext, txTextView;
	private boolean isEnable, isHire, isRead;
	private EditText name, age, height, school, selfintro, experience, 
	email, phone, fb, tw;
	private WebServiceUIAsyncTask wt;
	private MapUtil mapUtil;
	private int pmwidth, pmheight;
	private Dialog alertDialog;
	private View objectView, bottomView;
	private String[] arrs;
	private boolean[] boos;
	private CustomMultiChoiceDialog.Builder multiChoiceDialogBuilder;
	private SharedPreferences shared, shared2;
	private SharedPreferences.Editor editor;
	private RoundImageView touxiang;
	private Map<String, String> rmap = new HashMap<String, String>();
	private Map<String, String> smap = new HashMap<String, String>();
	private List<Map<String, String>> typelist = null;
	private String sidString = "", imgurl, webtype = "";
	private Bitmap image, upBitmap;
	private Intent intent, bIntent;
	private Button yesButton, noButton;
	private MyAlertDialog myAlertDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.personalcenter_view);
		MyApplication.getInstance().addActivity(this);
		
		myAlertDialog = new MyAlertDialog(activity, callback);
		shared2 = getSharedPreferences("isE", Context.MODE_PRIVATE);
		intent = getIntent();
		mapUtil = new MapUtil(activity);
		shared = getSharedPreferences("info", Context.MODE_PRIVATE);
		editor = shared.edit();
		imgurl = getExternalCacheDir() + "/" + shared.getString("loginid", "") + ".png";
		
		bottomView = (View)findViewById(R.id.pc_bottom);
		yesButton = (Button)findViewById(R.id.button_yes);
		noButton = (Button)findViewById(R.id.button_no);
		returnView = (ImageView)findViewById(R.id.top_return);
		phonecall = (ImageView)findViewById(R.id.phoneimg);
		touxiang = (RoundImageView)findViewById(R.id.touxiang);
		nametext = (TextView)findViewById(R.id.p_name);
		phonetext = (TextView)findViewById(R.id.p_number);
		txTextView = (TextView)findViewById(R.id.txtext);
		to1 = (ImageView)findViewById(R.id.to1);
		to2 = (ImageView)findViewById(R.id.to2);
		to3 = (ImageView)findViewById(R.id.to3);
		to4 = (ImageView)findViewById(R.id.to4);
		topcenter = (TextView)findViewById(R.id.top_centertext);
		topright = (TextView)findViewById(R.id.top_righttext);
		sexLayout = (TextView)findViewById(R.id.pc_sex);
		birthLayout = (TextView)findViewById(R.id.pc_birth);
		jobobjective = (TextView)findViewById(R.id.pc_jobobjective);
		objectView = (View)findViewById(R.id.jobobjective_relative);
		city = (TextView)findViewById(R.id.pc_city);
		name = (EditText)findViewById(R.id.pc_name);
		age = (EditText)findViewById(R.id.pc_age);
		height = (EditText)findViewById(R.id.pc_height);
		school = (EditText)findViewById(R.id.pc_school);
		selfintro = (EditText)findViewById(R.id.pc_selfintro);
		experience = (EditText)findViewById(R.id.pc_experience);
		email = (EditText)findViewById(R.id.pc_email);
		phone = (EditText)findViewById(R.id.pc_phonenum);
		fb = (EditText)findViewById(R.id.pc_facebook);
		tw = (EditText)findViewById(R.id.pc_twitter);
		
		if (intent.hasExtra("read")) {
			isRead = true;
			ExecuteAsyncTask2("UpdateJobRegStatus", mapUtil.CancleApplyMap(intent.getStringExtra("rid"), "1"));
		}
		
		if (shared2.getBoolean("isE", false)) {
			phonecall.setVisibility(View.VISIBLE);
		}
		
		if (intent.hasExtra("type") && intent.getIntExtra("type", 1) == 1) {
			if (!intent.getStringExtra("state").equals("2") && !intent.getStringExtra("state").equals("3") 
					&& !intent.getStringExtra("state").equals("-1")) {
				bottomView.setVisibility(View.VISIBLE);
			}
		}
		
		if (intent.hasExtra("id")) {
			webtype = "info";
			ExecuteAsyncTask("GetJobSeekersInfo", mapUtil.UserInfoMap(intent.getStringExtra("id")));
		}else {
			topright.setVisibility(View.VISIBLE);
			topright.setText(R.string.edit_resume);
			if (shared.getString("name", "").equals("")) {
				nametext.setText(activity.getString(R.string.no_personname));
				nametext.setTextColor(getResources().getColor(R.color.red));
			}else {
				nametext.setText(shared.getString("name", ""));
			}
			name.setText(shared.getString("name", ""));
			phonetext.setText(shared.getString("mobile", ""));
			phone.setText(shared.getString("mobile", ""));
			sexLayout.setText(shared.getString("sex", ""));
			age.setText(shared.getString("age", ""));
			height.setText(shared.getString("height", ""));
			city.setText(shared.getString("city", ""));
			school.setText(shared.getString("school", ""));
			birthLayout.setText(shared.getString("birth", ""));
			jobobjective.setText(shared.getString("jobsearch", "").contains(",") ? 
					shared.getString("jobsearch", "").replace(",", "，") : shared.getString("jobsearch", ""));
			selfintro.setText(shared.getString("profile", ""));
			experience.setText(shared.getString("experience", ""));
			email.setText(shared.getString("email", ""));
			fb.setText(shared.getString("fb", ""));
			tw.setText(shared.getString("tw", ""));
			sidString = shared.getString("jobsearchid", "");
			
			if (ImageUtil.isHave(imgurl)) {
				image = BitmapFactory.decodeFile(imgurl);
				touxiang.setImageBitmap(image);
			}else {
				//线程获取头像
				new Thread(new mythread()).start();
			}
		}
		objectView.setEnabled(false);
		topcenter.setText(R.string.personal_resume);
		if (intent.hasExtra("complete")) {
			topright.setText(R.string.submit);
			setEnable(true);
		}
		
		txTextView.setOnClickListener(this);
		sexLayout.setOnClickListener(this);
		birthLayout.setOnClickListener(this);
		topright.setOnClickListener(this);
		returnView.setOnClickListener(this);
		objectView.setOnClickListener(this);
		city.setOnClickListener(this);
		touxiang.setOnClickListener(this);
		yesButton.setOnClickListener(this);
		noButton.setOnClickListener(this);
		phonecall.setOnClickListener(this);
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		pmwidth = displayMetrics.widthPixels;
		pmheight = displayMetrics.heightPixels;
	}
	
	private class mythread implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (webtype.equals("info")) {
				image = ImageUtil.getBitmapFromPathWH(MyUtils.getPicUrl() + rmap.get("pic"), 
						120, 120);
				if (image != null) {
					myHandler.sendEmptyMessage(6);
				}
			}else {
				image = ImageUtil.getBitmapFromPathWH(MyUtils.getPicUrl() + shared.getString("pic", ""), 
						120, 120);
				if (image != null) {
					myHandler.sendEmptyMessage(3);
				}
			}
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
				if (!webtype.equals("image")) {
					MyProgressDialog.showDialog(activity, 0);
				}
			}
			
			@Override
			public void onGetDataSuccessfulDoInUI(String result) {
				// TODO Auto-generated method stub
				//成功获取数据，解析数据
				if (webtype.equals("info")) {
					MyProgressDialog.Dismiss();
					rmap = JsonTool.Loginjson(result, "JobSeekersInfo");
					if (rmap == null) {
						MyUtils.showToast2(activity, getResources().
								getString(R.string.data_parse_error));
					}else {
						if (rmap.get("result").equals("0")) {
							MyUtils.showToast2(activity, rmap.get("msg"));
						}else {
							myHandler.sendEmptyMessage(5);
						}
					}
				}else if(webtype.equals("alter")){
					smap = JsonTool.isSuccessfuljson(result);
					if (smap == null) {
						MyProgressDialog.Dismiss();
						MyUtils.showToast2(activity, getResources().
								getString(R.string.data_parse_error));
					}else {
						
						if(smap.get("result").equals("1"))
						{
							setEnable(false);
							topright.setText(R.string.edit_resume);
							if (TextUtils.isEmpty(name.getText().toString())) {
								nametext.setText(activity.getString(R.string.no_personname));
								nametext.setTextColor(getResources().getColor(R.color.red));
							}else {
								nametext.setText(name.getText().toString());
								nametext.setTextColor(getResources().getColor(R.color.black));
							}
							phonetext.setText(phone.getText().toString());
							editor.putString("name", name.getText().toString());
							editor.putString("mobile", phone.getText().toString());
							editor.putString("sex", sexLayout.getText().toString());
							editor.putString("age", age.getText().toString());
							editor.putString("height", height.getText().toString());
							editor.putString("city", city.getText().toString());
							editor.putString("school", school.getText().toString());
							editor.putString("birth", birthLayout.getText().toString());
							editor.putString("jobsearch", jobobjective.getText().toString());
							editor.putString("jobsearchid", sidString);
							editor.putString("profile", selfintro.getText().toString());
							editor.putString("experience", experience.getText().toString());
							editor.putString("email", email.getText().toString());
							editor.putString("fb", fb.getText().toString());
							editor.putString("tw", tw.getText().toString());
							editor.commit();
							
							myHandler.sendEmptyMessage(4);
						}else {
							MyProgressDialog.Dismiss();
							MyUtils.showToast2(activity, smap.get("msg"));
						}
					}
				}else if (webtype.equals("image")) {
					MyProgressDialog.Dismiss();
					smap = JsonTool.isSuccessfuljson(result);
					if (smap == null) {
						MyUtils.showToast(activity, R.string.data_parse_error);
					}else {
						if (smap.get("result").equals("1")) {
							MyUtils.showToast(activity, R.string.updatesuccess);
							ImageUtil.savePNG(upBitmap, imgurl);
						}else {
							MyUtils.showToast2(activity, smap.get("msg"));
						}
					}
					
				}else if (webtype.equals("slist")) {
					MyProgressDialog.Dismiss();
					typelist = JsonTool.JTUTypejson(result, "JobSearchList", 1);
					if (typelist == null) {
						MyUtils.showToast2(activity, getResources().
								getString(R.string.data_parse_error));
					}else {
						if(typelist.get(0).containsKey("result"))
						{
							MyUtils.showToast2(activity, typelist.get(0).get("msg"));
						}else{
							int size = typelist.size();
							boos = new boolean[size];
							arrs = new String[size];
							for (int i = 0; i < size; i++) {
								arrs[i] = typelist.get(i).get("name");
							}
							String jsString = shared.getString("jobsearch", "");
							List<Integer> indexlist = new ArrayList<Integer>();
							List<String> nList = new ArrayList<String>();
							for (Map<String, String> nMap : typelist) {
								nList.add(nMap.get("name"));
							}
							String[] indexStrings = null; 
							if(!jsString.equals("")){
								if (jsString.contains("，") || jsString.contains(",")) {
									if (jsString.contains("，")) {
										indexStrings = jsString.split("，");
									}else if(jsString.contains(",")){
										indexStrings = jsString.split(",");
									}
									int isize = indexStrings.length;
									for (int i = 0; i < isize; i++) {
										indexlist.add(nList.indexOf(indexStrings[i]));
									}
									int ilsize = indexlist.size();
									for (int i = 0; i < ilsize; i++) {
										boos[indexlist.get(i)] = true;
									}
								}else {
									boos[nList.indexOf(jsString)] = true;
								}
							}
							showMultiChoiceDialog();
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
	
	/**
	 * 录用和拒绝
	 */
	public void ExecuteAsyncTask2(String method, Map<String, Object> map) {
		wt = new WebServiceUIAsyncTask(activity) {
			
			@Override
			public void onTaskStartDoInUI(String... values) {
				// TODO Auto-generated method stub
				//开始请求数据，显示加载框
				if (!isRead) {
					MyProgressDialog.showDialog(activity, 0);
				}
			}
			
			@Override
			public void onGetDataSuccessfulDoInUI(String result) {
				// TODO Auto-generated method stub
				//成功获取数据，解析数据
					smap = JsonTool.isSuccessfuljson(result);
					if (smap == null) {
						MyUtils.showToast(activity, R.string.data_parse_error);
					}else {
						if (!isRead) {
							MyProgressDialog.Dismiss();
							MyUtils.showToast2(activity, smap.get("msg"));
							bottomView.setVisibility(View.GONE);
							if (intent.hasExtra("mid")) {
//								bIntent = new Intent("isread");
							}else {
								bIntent = new Intent("result");
								bIntent.putExtra("ishire", isHire);
								activity.sendBroadcast(bIntent);
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
				if (!isRead) {
					MyProgressDialog.Dismiss();
				}
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
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.txtext:
			if (isEnable) {
				startActivityForResult(new Intent(activity,
						SelectPicPopupWindow.class), 1);
			}
			break;
		case R.id.phoneimg:
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + phone.getText().toString().trim())));
			break;
		case R.id.button_yes:
			//录用
			isHire = true;
			myAlertDialog.showMyAlertDialog(R.string.suretohire);
			
			break;
		case R.id.button_no:
			//拒绝
			isHire = false;
			myAlertDialog.showMyAlertDialog(R.string.suretorefuse);
			
			break;	
		case R.id.top_return:
			this.finish();
			break;
		case R.id.top_righttext:
			if (isEnable) {
				if (TextUtils.isEmpty(name.getText()) || TextUtils.isEmpty(age.getText()) || TextUtils.isEmpty(email.getText()) || 
						TextUtils.isEmpty(phone.getText()) || TextUtils.isEmpty(birthLayout.getText()) || 
						TextUtils.isEmpty(selfintro.getText()) || TextUtils.isEmpty(experience.getText()) || TextUtils.isEmpty(city.getText()) || 
						TextUtils.isEmpty(jobobjective.getText()) || TextUtils.isEmpty(sexLayout.getText())) {
					MyUtils.showToast(activity, R.string.info_lack);
				}else {
					webtype = "alter";
					ExecuteAsyncTask("EditResume", mapUtil.AlterPinfoMap(shared.getString("id", ""), 
							MyUtils.UploadString(name.getText().toString()), age.getText().toString(), email.getText().toString(), 
							phone.getText().toString(), birthLayout.getText().toString(), height.getText().toString(), 
							MyUtils.UploadString(school.getText().toString()), MyUtils.UploadString(selfintro.getText().toString()), 
							MyUtils.UploadString(experience.getText().toString()), 
							city.getText().toString(), "", "", "", sidString, jobobjective.getText().toString().contains("，") 
							? jobobjective.getText().toString().replace("，", ",") : jobobjective.getText().toString(), 
									sexLayout.getText().toString()));
				}
			}else {
				setEnable(true);
				topright.setText(R.string.submit);
			}
			break;
		case R.id.pc_sex:
			View view = LayoutInflater.from(activity).inflate(R.layout.sexselect_view, null);
			man = (TextView)view.findViewById(R.id.man);
			woman = (TextView)view.findViewById(R.id.woman);
			
			alertDialog = new Dialog(activity, R.style.alert_dialog);
			alertDialog.setContentView(view, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
			alertDialog.show();
			WindowManager.LayoutParams params = alertDialog.getWindow()   
					.getAttributes();   
			params.width = pmwidth - getResources().getDimensionPixelSize(R.dimen.dialogpadding);   
			alertDialog.getWindow().setAttributes(params);  
			
			man.setOnClickListener(this);
			woman.setOnClickListener(this);
			break;
		case R.id.man:
			sexLayout.setText(man.getText().toString());
			alertDialog.dismiss();
			break;
		case R.id.woman:
			sexLayout.setText(woman.getText().toString());
			alertDialog.dismiss();
			break;
		case R.id.pc_birth:
			ShowDateDialog();
			break;
		case R.id.jobobjective_relative:
			if (typelist != null) {
				showMultiChoiceDialog();
			}else {
				webtype = "slist";
				ExecuteAsyncTask("GetJobSearchList", mapUtil.AccesskeyMap());
			}
			break;
		case R.id.pc_city:
			startActivityForResult(new Intent(this, SelectCitysActivity.class), 101);
			break;
		case R.id.touxiang:
			if (isEnable) {
				startActivityForResult(new Intent(activity,
						SelectPicPopupWindow.class), 1);
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		switch (arg1) {
		case 101:
			city.setText(arg2.getStringExtra("name"));
			break;
		case 1:
			if (arg2 != null) {
				//取得返回的Uri,基本上选择照片的时候返回的是以Uri形式，但是在拍照中有得机子呢Uri是空的，所以要特别注意
				Uri mImageCaptureUri = arg2.getData();
				//返回的Uri不为空时，那么图片信息数据都会在Uri中获得。如果为空，那么我们就进行下面的方式获取
				if (mImageCaptureUri != null) {
					
					try {
						//这个方法是根据Uri获取Bitmap图片的静态方法
						upBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
						if (upBitmap != null) {
							upBitmap = ImageUtil.centerSquareScaleBitmap(upBitmap, 120);
							touxiang.setImageBitmap(upBitmap);
						}
					} catch(OutOfMemoryError error){
						myHandler.sendEmptyMessage(1);
					} catch (Exception e) {
						e.printStackTrace();
						myHandler.sendEmptyMessage(2);
						Log.i("pic_exception", e.getMessage().toString());
					}
				} else {
					Bundle extras = arg2.getExtras();
					if (extras != null) {
						//这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
						try {
							upBitmap = extras.getParcelable("data");
							if (upBitmap != null) {
								upBitmap = ImageUtil.centerSquareScaleBitmap(upBitmap, 120);
								touxiang.setImageBitmap(upBitmap);
							}
							
						} catch(OutOfMemoryError error){
							myHandler.sendEmptyMessage(1);
						} catch (Exception e) {
							// TODO: handle exception
							myHandler.sendEmptyMessage(2);
							Log.i("pic_exception", e.getMessage().toString());
						}
					}
				}
				
			}
			break;
		default:
			break;
		}
	}
	
	@SuppressLint("HandlerLeak")
	private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				MyUtils.showToast(activity, R.string.pic_outofmem);
				break;
			case 2:
				MyUtils.showToast(activity, R.string.data_parse_error);
				break;
			case 3:
				touxiang.setImageBitmap(image);
				ImageUtil.savePNG(image, imgurl);
				break;
			case 4:
				if (upBitmap != null) {
					webtype = "image";
					ExecuteAsyncTask("uploadimage", mapUtil.UploadPicMap(shared.getString(
							"id", ""), ImageUtil.compressImage64(upBitmap), "0"));
				}else {
					MyProgressDialog.Dismiss();
					MyUtils.showToast2(activity, smap.get("msg"));
				}
				break;
			case 5:
				new Thread(new mythread()).start();
				if (rmap.get("name").equals("")) {
					nametext.setText(activity.getString(R.string.no_personname));
					nametext.setTextColor(getResources().getColor(R.color.red));
				}else {
					nametext.setText(rmap.get("name"));
				}
				name.setText(rmap.get("name"));
				phonetext.setText(rmap.get("mobile"));
				phone.setText(rmap.get("mobile"));
				sexLayout.setText(rmap.get("sex"));
				age.setText(rmap.get("age"));
				height.setText(rmap.get("height"));
				city.setText(rmap.get("city"));
				school.setText(MyUtils.ParseString(rmap.get("school")));
				birthLayout.setText(rmap.get("birth"));
				jobobjective.setText(rmap.get("jobsearch").contains(",") ? 
						rmap.get("jobsearch").replace(",", "，") : rmap.get("jobsearch"));
				selfintro.setText(MyUtils.ParseString(rmap.get("profile")));
				experience.setText(MyUtils.ParseString(rmap.get("experience")));
				email.setText(rmap.get("email"));
				break;
			case 6:
				touxiang.setImageBitmap(image);
				break;
			default:
				break;
			}
		}
	};
	
	public void showMultiChoiceDialog() {
		
		multiChoiceDialogBuilder = new CustomMultiChoiceDialog.Builder(this);
		CustomMultiChoiceDialog multiChoiceDialog = multiChoiceDialogBuilder.setTitle(R.string.chooseall)
				.setMultiChoiceItems(arrs, boos, null, true)
				.setPositiveButton(R.string.sure, new PositiveClickListener())
				.setNegativeButton(R.string.cancel, null).create();
		multiChoiceDialog.setCanceledOnTouchOutside(false);
		multiChoiceDialog.show();
		WindowManager.LayoutParams params = multiChoiceDialog.getWindow()   
				.getAttributes();   
		params.width = pmwidth - getResources().getDimensionPixelSize(R.dimen.dialogpadding);   
		params.height = pmheight - getResources().getDimensionPixelSize(R.dimen.dialogpadding);
		multiChoiceDialog.getWindow().setAttributes(params);  
	}
	
	class PositiveClickListener implements OnClickListener {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			String s = "";
			sidString = "";
			boos = multiChoiceDialogBuilder.getCheckedItems();
			if (boos.length != 0 || boos != null) {
				for (int i = 0; i < boos.length; i++) {
					if (boos[i]) {
						s += arrs[i];
						sidString += typelist.get(i).get("id");
						if (i < boos.length - 1) {
							s += "，";
							sidString += ",";
						}
					}else {
						if (i == boos.length - 1) {
							if (s.length() != 0) {
								s = s.substring(0, s.length() - 1);
								sidString = sidString.substring(0, sidString.length() - 1);
							}
						}
					} 
				}
			}
			
			jobobjective.setText(s);
		}
	}
	
	/**
	 * 显示日期选择对话框
	 * @param m
	 */
	public void ShowDateDialog()
	{
		DateTimePickerDialog dialog = new DateTimePickerDialog(activity, System.currentTimeMillis(), false);
		dialog.setOnDateTimeSetListener(new listener2());
		dialog.show();
	}
	
	/**
	 * 日期选择对话框确定键监听事件
	 * @author Administrator
	 *
	 */
	public class listener2 implements OnDateTimeSetListener{
		
		@Override
		public void OnDateTimeSet(AlertDialog dialog, long date) {
			// TODO Auto-generated method stub
			birthLayout.setText(getStringDate(date));
			dialog.dismiss();
		}
	};
	
	/**
	 * 将长时间格式字符串转换为时间 yyyy-MM-dd
	 *
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getStringDate(Long date) 
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(date);
		
		return dateString;
	}
	
	private void setEnable(boolean enable){
		isEnable = enable;
		sexLayout.setEnabled(enable);
		birthLayout.setEnabled(enable);
		objectView.setEnabled(enable);
		city.setEnabled(enable);
		name.setEnabled(enable);
		age.setEnabled(enable);
		height.setEnabled(enable);
		school.setEnabled(enable);
		selfintro.setEnabled(enable);
		experience.setEnabled(enable);
		email.setEnabled(enable);
		phone.setEnabled(enable);
		fb.setEnabled(enable);
		tw.setEnabled(enable);
		if (enable) {
			to1.setVisibility(View.VISIBLE);
			to2.setVisibility(View.VISIBLE);
			to3.setVisibility(View.VISIBLE);
			to4.setVisibility(View.VISIBLE);
			txTextView.setVisibility(View.VISIBLE);
		}else {
			txTextView.setVisibility(View.GONE);
			to1.setVisibility(View.GONE);
			to2.setVisibility(View.GONE);
			to3.setVisibility(View.GONE);
			to4.setVisibility(View.GONE);
		}
	}

	AlertCallback callback = new AlertCallback() {
		
		@Override
		public void ybutton() {
			// TODO Auto-generated method stub
			isRead = false;
			if (isHire) {
				ExecuteAsyncTask2("EmployJobReg", mapUtil.HireRefuseMap(
						intent.getStringExtra("rid"), 3));
			}else {
				ExecuteAsyncTask2("EmployJobReg", mapUtil.HireRefuseMap(
						intent.getStringExtra("rid"), 2));
			}
		}
	};
	
}
