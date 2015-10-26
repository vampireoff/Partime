package com.jianzhiniu.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.jianzhiniu.utils.BackGestureListener;
/**
 * ȫ��activity�̳д��࣬ʵ�����Ƽ���
 * @author Administrator
 *
 */
public class BaseActivity extends Activity implements OnClickListener{
	
	/** ���Ƽ��� */
	GestureDetector mGestureDetector;
	/** �Ƿ���Ҫ�������ƹرչ��� */
	private boolean mNeedBackGesture = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initGestureDetector();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	/**
	 * ��ʼ���رս�������Ƽ���
	 */
	private void initGestureDetector() {
		if (mGestureDetector == null) {
			mGestureDetector = new GestureDetector(getApplicationContext(),
					new BackGestureListener(this));
		}
	}
	
	/**
	 * �ж��Ƿ��������Ƽ���
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if(mNeedBackGesture){
			return mGestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
		}
		return super.dispatchTouchEvent(ev);
	}
	
	/*
	 * �����Ƿ�������Ƽ���
	 */
	public void setNeedBackGesture(boolean mNeedBackGesture){
		this.mNeedBackGesture = mNeedBackGesture;
	}
	
	/*
	 * ����
	 */
	public void doBack(View view) {
		onBackPressed();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
}
