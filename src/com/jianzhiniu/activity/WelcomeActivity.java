package com.jianzhiniu.activity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jianzhiniu.config.MyApplication;
import com.jianzhiniu.utils.MyLocationManager;
import com.jianzhiniu.utils.MyLocationManager.LocationCallBack;

public class WelcomeActivity extends BaseActivity implements LocationCallBack{

	private Button button_p, button_e;
	private boolean exit;
	private LinearLayout linearLayout;
	private Intent intent;
	private MyLocationManager myLocation;
	private double latitude = 0, longitude = 0;
	private String addressName = "", cityString = "";
	private SharedPreferences shared, infoPreferences, einfoPreferences;
	private SharedPreferences.Editor editor, iEditor, eEditor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		
		infoPreferences = getSharedPreferences("info", Context.MODE_PRIVATE);
		einfoPreferences = getSharedPreferences("einfo", Context.MODE_PRIVATE);
		shared = getSharedPreferences("location", Context.MODE_PRIVATE);
		editor = shared.edit();
		iEditor = infoPreferences.edit();
		eEditor = einfoPreferences.edit();
		myLocation = new MyLocationManager(WelcomeActivity.this.getApplicationContext(),
				WelcomeActivity.this);
		MyApplication.getInstance().addActivity(this);
		intent = getIntent();
		linearLayout = (LinearLayout)findViewById(R.id.lin_button);
		button_e = (Button)findViewById(R.id.button_e);
		button_p = (Button)findViewById(R.id.button_p);
		
		button_e.setOnClickListener(this);
		button_p.setOnClickListener(this);
		
		if (intent.getStringExtra("type") != null) {
			myHandler.sendEmptyMessage(1);
		}else {
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					myHandler.sendEmptyMessage(1);
				}
			}, 2000);
		}
		
	}
	
	@SuppressLint("HandlerLeak") 
	private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				linearLayout.setVisibility(View.VISIBLE);
				break;
			case 2:
				
				break;

			default:
				break;
			}
		}
	};
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (exit) {
				iEditor.clear();
				eEditor.clear();
				iEditor.commit();
				eEditor.commit();
				myLocation.destoryLocationManager();
				MyApplication.getInstance().exit();
			}else {
				exit = true;
				Toast.makeText(WelcomeActivity.this, R.string.click_exit, 
						Toast.LENGTH_SHORT).show();
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						exit = false;
					}
				}, 2000);
				return false;
			}
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_e:
			Intent eintent = new Intent(WelcomeActivity.this, LoginActivity.class);
			eintent.putExtra("flag", "e");
			startActivity(eintent);
			break;
		case R.id.button_p:
			Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
			intent.putExtra("flag", "p");
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onCurrentLocation(Location location) {
		// TODO Auto-generated method stub
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		if (latitude != 0 && longitude != 0) {
			new Thread(getAddressName).start();
		}
	}
	
	/**
	 * 
	 * 用线程异步获取
	 */
	Runnable getAddressName = new Runnable() {
		public void run() {
			getLocationAddress(latitude, longitude);
		}
	};
	
	/**
	 * 通过经纬度获取地址
	 * 
	 * @param point
	 * @return
	 */
	private void getLocationAddress(double lat, double lon) {
		Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
		try {
			List<Address> addresses = geoCoder.getFromLocation(
					lat, lon, 1);
			if (addresses != null && addresses.size() != 0) {
				Address address = addresses.get(0);
				cityString = address.getLocality();
				latitude = address.getLatitude();
				longitude = address.getLongitude();
				editor.putString("city", cityString);
				editor.putString("ll", latitude + "," + longitude);
				editor.commit();
			}
		} catch (IOException e) {
			cityString = "";
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		myLocation.destoryLocationManager();
	}
}
