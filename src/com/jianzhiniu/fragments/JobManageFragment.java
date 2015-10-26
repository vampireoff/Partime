package com.jianzhiniu.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jianzhiniu.activity.R;
/**
 * 兼职管理主fragment
 * @author Administrator
 *
 */
public class JobManageFragment extends Fragment{

	private TextView leftText, rightText, midtext;
	private View leftView, rightView, midView;
	private ViewPager viewPager;
	private LinearLayout left, right, middle;
	public List<Fragment> fragments = new ArrayList<Fragment>();
	private Activity activity;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(activity).inflate(R.layout.jobmanage_view, null);
		leftText = (TextView)view.findViewById(R.id.text_left);
		midtext = (TextView)view.findViewById(R.id.text_middle);
		rightText = (TextView)view.findViewById(R.id.text_right);
		leftView = (View)view.findViewById(R.id.view_left);
		midView = (View)view.findViewById(R.id.view_mid);
		rightView = (View)view.findViewById(R.id.view_right);
		viewPager = (ViewPager)view.findViewById(R.id.viewpagerm);
		left = (LinearLayout)view.findViewById(R.id.left_lin);
		right = (LinearLayout)view.findViewById(R.id.right_lin);
		middle = (LinearLayout)view.findViewById(R.id.middle_lin);
		
		LeftOrRight(0);
		
		left.setOnClickListener(new TopClick(0));
		middle.setOnClickListener(new TopClick(1));
		right.setOnClickListener(new TopClick(2));
		
		fragments.add(new JobManageFragment1());
		fragments.add(new JobManageFragment2());
		fragments.add(new JobManageFragment3());
		
		viewPager.setAdapter(new MyFragAdapter(this.getChildFragmentManager(), fragments));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				if(arg0 == 0){
					LeftOrRight(0);
				}else if (arg0 == 1) {
					LeftOrRight(1);
					Intent intent = new Intent("start1");
					activity.sendBroadcast(intent);
				}
				else {
					LeftOrRight(2);
					Intent intent = new Intent("start2");
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
		
		return view;
	}
	
	/**
	 * 设置两边导航栏背景
	 * @param i
	 */
	private void LeftOrRight(int i){
		if(i == 0){
			leftText.setTextColor(getResources().getColor(R.color.blue));
			midtext.setTextColor(getResources().getColor(R.color.gray3));
			rightText.setTextColor(getResources().getColor(R.color.gray3));
			leftView.setVisibility(View.VISIBLE);
			midView.setVisibility(View.INVISIBLE);
			rightView.setVisibility(View.INVISIBLE);
		}else if(i == 1){
			leftText.setTextColor(getResources().getColor(R.color.gray3));
			midtext.setTextColor(getResources().getColor(R.color.blue));
			rightText.setTextColor(getResources().getColor(R.color.gray3));
			leftView.setVisibility(View.INVISIBLE);
			midView.setVisibility(View.VISIBLE);
			rightView.setVisibility(View.INVISIBLE);
		}else {
			leftText.setTextColor(getResources().getColor(R.color.gray3));
			midtext.setTextColor(getResources().getColor(R.color.gray3));
			rightText.setTextColor(getResources().getColor(R.color.blue));
			leftView.setVisibility(View.INVISIBLE);
			midView.setVisibility(View.INVISIBLE);
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
	
}
