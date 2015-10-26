package com.jianzhiniu.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianzhiniu.config.MyApplication;
import com.jianzhiniu.fragments.EnterpriseInfoFragment;
import com.jianzhiniu.fragments.JobInfoFragment;
import com.jianzhiniu.network.JsonTool;
import com.jianzhiniu.network.MapUtil;
import com.jianzhiniu.network.WebServiceUIAsyncTask;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.MyProgressDialog;

/**
 * 职位信息界面
 * @author Administrator
 *
 */
public class JobInfoActivity extends BaseFragmentActivity{

	private ImageView returnView;
	private TextView topright;
	private Activity activity = JobInfoActivity.this;
	private ViewPager viewPager;
	public List<Fragment> fragments = new ArrayList<Fragment>();
	private TextView leftView, rightView;
	private Intent intent;
	private WebServiceUIAsyncTask wt;
	private MapUtil mapUtil;
	private Map<String, String> infomap = new HashMap<String, String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jobinfo_mainview);
		MyApplication.getInstance().addActivity(this);
		
		intent = getIntent();
		returnView = (ImageView)findViewById(R.id.top_return);
		topright = (TextView)findViewById(R.id.ji_collect);
		viewPager = (ViewPager)findViewById(R.id.ji_viewpager);
		leftView = (TextView)findViewById(R.id.left_text);
		rightView = (TextView)findViewById(R.id.right_text);

		fragments.add(new JobInfoFragment());
		fragments.add(new EnterpriseInfoFragment());
		
		returnView.setOnClickListener(this);
		topright.setOnClickListener(this);
		leftView.setOnClickListener(new TopClick(0));
		rightView.setOnClickListener(new TopClick(1));
		
		viewPager.setAdapter(new MyFragAdapter(this.getSupportFragmentManager(), fragments));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				if(arg0 == 0){
					LeftOrRight(0);
				}else {
					Intent bIntent = new Intent("getcompany");
					activity.sendBroadcast(bIntent);
					LeftOrRight(1);
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
			leftView.setTextColor(getResources().getColor(R.color.white));
			leftView.setBackgroundResource(R.drawable.topleft_blue);
			rightView.setTextColor(getResources().getColor(R.color.top_blue));
			rightView.setBackgroundResource(R.drawable.topright_white);
		}else {
			rightView.setTextColor(getResources().getColor(R.color.white));
			rightView.setBackgroundResource(R.drawable.topright_blue);
			leftView.setTextColor(getResources().getColor(R.color.top_blue));
			leftView.setBackgroundResource(R.drawable.topleft_white);
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
					infomap = JsonTool.isSuccessfuljson(result);
					if (infomap == null) {
						MyUtils.showToast2(activity, getResources().
								getString(R.string.data_parse_error));
					}else {
						MyUtils.showToast2(activity, infomap.get("msg"));
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
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.top_return:
			this.finish();
			break;
		case R.id.ji_collect:
			//收藏兼职
//			ExecuteAsyncTask("", map);
			break;

		default:
			break;
		}
	}
}
