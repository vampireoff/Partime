package com.jianzhiniu.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jianzhiniu.config.MyApplication;
import com.jianzhiniu.fragments.MyJobEndFragment;
import com.jianzhiniu.fragments.MyJobOnwayFragment;

/**
 * 我的兼职界面
 * @author Administrator
 *
 */
public class MyJobActivity extends BaseFragmentActivity{

	private TextView leftText, rightText, topcenter;
	private View leftView, rightView;
	private ViewPager viewPager;
	private LinearLayout left, right;
	private ImageView returnLayout;
	public List<Fragment> fragments = new ArrayList<Fragment>();
	private int flag;
	private Activity activity = MyJobActivity.this;
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myjob_view);
		MyApplication.getInstance().addActivity(this);
		
		intent = getIntent();
		returnLayout = (ImageView)findViewById(R.id.top_return);
		leftText = (TextView)findViewById(R.id.text_left);
		rightText = (TextView)findViewById(R.id.text_right);
		topcenter = (TextView)findViewById(R.id.top_centertext);
		leftView = (View)findViewById(R.id.view_left);
		rightView = (View)findViewById(R.id.view_right);
		viewPager = (ViewPager)findViewById(R.id.viewpagerm);
		left = (LinearLayout)findViewById(R.id.left_lin);
		right = (LinearLayout)findViewById(R.id.right_lin);
		
		LeftOrRight(0);
		
		topcenter.setText(R.string.myjob);
		returnLayout.setOnClickListener(this);
		left.setOnClickListener(new TopClick(0));
		right.setOnClickListener(new TopClick(1));
		
		fragments.add(new MyJobOnwayFragment());
		fragments.add(new MyJobEndFragment());
		
		viewPager.setAdapter(new MyFragAdapter(this.getSupportFragmentManager(), fragments));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				if(arg0 == 0){
					LeftOrRight(0);
				}else {
					LeftOrRight(1);
					Intent intent = new Intent("startend");
					activity.sendBroadcast(intent);
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
		});
		
	}
	
	/**
	 * 设置两边导航栏背景
	 * @param i
	 */
	private void LeftOrRight(int i){
		if(i == 0){
			leftText.setTextColor(getResources().getColor(R.color.blue));
			rightText.setTextColor(getResources().getColor(R.color.gray3));
			leftView.setVisibility(View.VISIBLE);
			rightView.setVisibility(View.INVISIBLE);
		}else {
			leftText.setTextColor(getResources().getColor(R.color.gray3));
			rightText.setTextColor(getResources().getColor(R.color.blue));
			leftView.setVisibility(View.INVISIBLE);
			rightView.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 导航栏点击事件
	 * @author Administrator
	 *
	 */
	private class TopClick implements OnClickListener{
		
		int index = 0;
		
		public TopClick(int index){
			this.index = index;
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			viewPager.setCurrentItem(index);
		}
		
	}
	
	/**
	 * Fragment适配器
	 * @author Administrator
	 *
	 */
	private class MyFragAdapter extends FragmentPagerAdapter{
		List<Fragment> listFragments = new ArrayList<Fragment>();
		
		public MyFragAdapter(FragmentManager fm, List<Fragment> list){
			super(fm);
			this.listFragments = list;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listFragments.size();
		}
		
		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return listFragments.get(arg0);
		}
		
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.top_return:
			this.finish();
			break;
			
		default:
			break;
		}
	}
}
