package com.jianzhiniu.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianzhiniu.config.MyApplication;
import com.jianzhiniu.fragments.RecommendFragment;

/**
 * 可能感兴趣界面，加载RecommendFragment的Activity
 * @author Administrator
 *
 */
public class InterestJobActivity extends BaseFragmentActivity{
	
	private Activity activity = InterestJobActivity.this;
	private ViewPager viewPager;
	public List<Fragment> fragments = new ArrayList<Fragment>();
	private ImageView returnView;
	private TextView centerView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.interest_view);
		MyApplication.getInstance().addActivity(this);
		
		viewPager = (ViewPager)findViewById(R.id.viewpageri);
		returnView = (ImageView)findViewById(R.id.top_return);
		centerView = (TextView)findViewById(R.id.top_centertext);
		
		fragments.add(new RecommendFragment());
		
		centerView.setText(R.string.maybeinterest);
		returnView.setOnClickListener(this);
		
		viewPager.setAdapter(new MyFragAdapter(this.getSupportFragmentManager(), fragments));
		viewPager.setCurrentItem(0);
		
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
		super.onClick(v);
		switch (v.getId()) {
		case R.id.top_return:
			activity.finish();
			break;
			
		default:
			break;
		}
	}
}
