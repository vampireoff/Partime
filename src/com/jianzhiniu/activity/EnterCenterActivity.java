package com.jianzhiniu.activity;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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
import com.jianzhiniu.utils.ImageUtil;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.MyProgressDialog;
import com.jianzhiniu.views.RoundImageView;
import com.jianzhiniu.views.SelectPicPopupWindow;

/**
 * ��ҵ��������
 * @author Administrator
 *
 */
public class EnterCenterActivity extends BaseActivity {

	private Activity activity = EnterCenterActivity.this;
	private TextView topcenter, topright, txTextView;
	private ImageView returView, start;
	private RoundImageView img;
	private EditText linkman, linkphone, linkemail, comintroduce, name;
	private boolean isEnable, isPic;
	private SharedPreferences shared;
	private SharedPreferences.Editor editor;
	private WebServiceUIAsyncTask wt;
	private MapUtil mapUtil;
	private Map<String, String> rmap = new HashMap<String, String>();
	private String imgurl;
	private Bitmap imgBitmap, upBitmap;
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entercenter_view);
		MyApplication.getInstance().addActivity(this);
		
		intent = getIntent();
		mapUtil = new MapUtil(activity);
		shared = getSharedPreferences("einfo", Context.MODE_PRIVATE);
		editor = shared.edit();
		returView = (ImageView)findViewById(R.id.top_return);
		topcenter = (TextView)findViewById(R.id.top_centertext);
		topright = (TextView)findViewById(R.id.top_righttext);
		txTextView = (TextView)findViewById(R.id.txtext);
		name = (EditText)findViewById(R.id.ec_name);
		start = (ImageView)findViewById(R.id.start);
		img = (RoundImageView)findViewById(R.id.touxiang);
		linkman = (EditText)findViewById(R.id.ec_linkman);
		linkphone = (EditText)findViewById(R.id.ec_phone);
		linkemail = (EditText)findViewById(R.id.ec_email);
		comintroduce = (EditText)findViewById(R.id.ec_introduce);
		
		//����ͷ��
		imgurl = getExternalCacheDir() + "/e" + shared.getString("loginid", "") + ".png";
		if (ImageUtil.isHave(imgurl)) {
			imgBitmap = BitmapFactory.decodeFile(imgurl);
			img.setImageBitmap(imgBitmap);
		}else {
			//�̻߳�ȡͷ��
			new Thread(new mythread()).start();
		}
		
		name.setText(shared.getString("companyname", ""));
		linkman.setText(shared.getString("contactperson", ""));
		linkphone.setText(shared.getString("mobile", ""));
		linkemail.setText(shared.getString("email", ""));
		comintroduce.setText(shared.getString("profile", ""));
		if (shared.getString("authentication", "").equals("1")) {
			start.setVisibility(View.VISIBLE);
		}
		
		topcenter.setText(R.string.enterpriseinfo);
		topright.setVisibility(View.VISIBLE);
		topright.setText(R.string.edit_info);
		returView.setOnClickListener(this);
		topright.setOnClickListener(this);
		img.setOnClickListener(this);
		txTextView.setOnClickListener(this);
		
		if (intent.hasExtra("complete")) {
			topright.setText(R.string.submit);
			setEnable(true);
		}
	}
	
	/**
	 * ��ȡͷ����߳�
	 * @author Administrator
	 *
	 */
	private class mythread implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			imgBitmap = ImageUtil.getBitmapFromPathWH(MyUtils.getPicUrl() + shared.getString("pic", ""), 
					120, 120);
			if (imgBitmap != null) {
				myHandler.sendEmptyMessage(3);
			}
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
				img.setImageBitmap(imgBitmap);
				ImageUtil.savePNG(imgBitmap, imgurl);
				break;
			case 4:		//�ϴ�ͷ��
				if (upBitmap != null) {
					isPic = true;
					ExecuteAsyncTask("uploadimage", mapUtil.UploadPicMap(shared.getString(
							"id", ""), ImageUtil.compressImage64(upBitmap), "1"));
				}else {
					MyProgressDialog.Dismiss();
					MyUtils.showToast2(activity, rmap.get("msg"));
				}
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * ʹ���첽�ķ�ʽ��������
	 */
	public void ExecuteAsyncTask(String method, Map<String, Object> map) {
		wt = new WebServiceUIAsyncTask(activity) {
			
			@Override
			public void onTaskStartDoInUI(String... values) {
				// TODO Auto-generated method stub
				//��ʼ�������ݣ���ʾ���ؿ�
				if (!isPic) {
					MyProgressDialog.showDialog(activity, 0);
				}
			}
			
			@Override
			public void onGetDataSuccessfulDoInUI(String result) {
				// TODO Auto-generated method stub
				//�ɹ���ȡ���ݣ���������
				rmap = JsonTool.isSuccessfuljson(result);
				if (rmap == null) {
					MyProgressDialog.Dismiss();
					MyUtils.showToast2(activity, getResources().
							getString(R.string.data_parse_error));
				}else {
					if(rmap.get("result").equals("1"))
					{
						if (isPic) {
							MyProgressDialog.Dismiss();
							MyUtils.showToast(activity, R.string.updatesuccess);
							ImageUtil.savePNG(upBitmap, imgurl);
						}else {
							setEnable(false);
							topright.setText(R.string.edit_info);
							editor.putString("companyname", name.getText().toString());
							editor.putString("profile", comintroduce.getText().toString());
							editor.putString("contactperson", linkman.getText().toString());
							editor.putString("mobile", linkphone.getText().toString());
							editor.putString("email", linkemail.getText().toString());
							editor.commit();
							myHandler.sendEmptyMessage(4);
						}
					}else {
						MyProgressDialog.Dismiss();
						MyUtils.showToast2(activity, rmap.get("msg"));
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
		case R.id.top_return:
			this.finish();
			break;
		case R.id.top_righttext:	//�ύ��Ϣ
			if (isEnable) {
				if (TextUtils.isEmpty(name.getText()) || TextUtils.isEmpty(linkman.getText()) || TextUtils.isEmpty(linkemail.getText()) || 
						TextUtils.isEmpty(linkphone.getText()) || TextUtils.isEmpty(comintroduce.getText())) {
					MyUtils.showToast(activity, R.string.info_lack);
				}else {
					isPic = false;
					ExecuteAsyncTask("EditEntInfo", mapUtil.AlterEinfoMap(shared.getString("id", 
							""), MyUtils.UploadString(name.getText().toString()), linkman.getText().toString(), 
							linkemail.getText().toString(), linkphone.getText().toString(), 
							MyUtils.UploadString(comintroduce.getText().toString())));
				}
			}else {
				setEnable(true);
				topright.setText(R.string.submit);
			}
			break;

		case R.id.touxiang:		//����ͷ��ѡ��Ի���
			if (isEnable) {
				startActivityForResult(new Intent(activity,
						SelectPicPopupWindow.class), 1);
			}
			break;
		case R.id.txtext:
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
		case 1:
			if (arg2 != null) {
				//ȡ�÷��ص�Uri,������ѡ����Ƭ��ʱ�򷵻ص�����Uri��ʽ���������������еû�����Uri�ǿյģ�����Ҫ�ر�ע��
				Uri mImageCaptureUri = arg2.getData();
				//���ص�Uri��Ϊ��ʱ����ôͼƬ��Ϣ���ݶ�����Uri�л�á����Ϊ�գ���ô���Ǿͽ�������ķ�ʽ��ȡ
				if (mImageCaptureUri != null) {
					
					try {
						//��������Ǹ���Uri��ȡBitmapͼƬ�ľ�̬����
						upBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
						if (upBitmap != null) {
							upBitmap = ImageUtil.centerSquareScaleBitmap(upBitmap, 120);
							img.setImageBitmap(upBitmap);
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
						//��������Щ���պ��ͼƬ��ֱ�Ӵ�ŵ�Bundle�е��������ǿ��Դ��������ȡBitmapͼƬ
						try {
							upBitmap = extras.getParcelable("data");
							if (upBitmap != null) {
								upBitmap = ImageUtil.centerSquareScaleBitmap(upBitmap, 120);
								img.setImageBitmap(upBitmap);
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
	
	/**
	 * ����������״̬
	 * @param bool
	 */
	private void setEnable(boolean bool){
		if (bool) {
			txTextView.setVisibility(View.VISIBLE);
		}else {
			txTextView.setVisibility(View.GONE);
		}
		isEnable = bool;
		linkman.setEnabled(bool);
		linkphone.setEnabled(bool);
		linkemail.setEnabled(bool);
		comintroduce.setEnabled(bool);
		name.setEnabled(bool);
	}
}
