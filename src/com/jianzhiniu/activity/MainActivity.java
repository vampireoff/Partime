package com.jianzhiniu.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;

import com.jianzhiniu.adapter.FragmentViewPagerAdapter;
import com.jianzhiniu.config.MyApplication;
import com.jianzhiniu.config.Myconfig;
import com.jianzhiniu.fragments.AssistantFragment;
import com.jianzhiniu.fragments.JobManageFragment;
import com.jianzhiniu.fragments.MainFragment;
import com.jianzhiniu.fragments.NewFragment;
import com.jianzhiniu.fragments.PersonalFragment;
import com.jianzhiniu.network.JsonTool;
import com.jianzhiniu.network.MapUtil;
import com.jianzhiniu.network.WebServiceUIAsyncTask;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.MyViewPager;

public class MainActivity extends BaseFragmentActivity {

	private Context activity = MainActivity.this;
	private LinearLayout layout1, layout2, layout3, layout4;
	private MyViewPager viewPager;
	private TextView cityView, textView1, textView2, textView3, textView4, toptext;
	private ImageView img1, img2, img3, img4, down, reddot;
	private List<Fragment> fragments = new ArrayList<Fragment>();
	private boolean isexit;
	private SharedPreferences shared, shared2, infoshare;
	private SharedPreferences.Editor editor2, infoEditor;
	private boolean isE;
	private Intent intent;
	private WebServiceUIAsyncTask wt;
	private MapUtil mapUtil;
	private List<Map<String, String>> xlist = new ArrayList<Map<String,String>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mapUtil = new MapUtil(activity);
		shared2 = getSharedPreferences("isE", Context.MODE_PRIVATE);
		editor2 = shared2.edit();
		intent = getIntent();
		MyApplication.getInstance().addActivity(this);
		ShareSDK.initSDK(this);
		viewPager = (MyViewPager)findViewById(R.id.tabpager);
		cityView = (TextView)findViewById(R.id.top_city);
		toptext = (TextView)findViewById(R.id.top_mcentertext);
		textView1 = (TextView)findViewById(R.id.shouye1_text);
		textView2 = (TextView)findViewById(R.id.shouye2_text);
		textView3 = (TextView)findViewById(R.id.shouye3_text);
		textView4 = (TextView)findViewById(R.id.shouye4_text);
		img1 = (ImageView)findViewById(R.id.shouye1_img);
		img2 = (ImageView)findViewById(R.id.shouye2_img);
		img3 = (ImageView)findViewById(R.id.shouye3_img);
		img4 = (ImageView)findViewById(R.id.shouye4_img);
		down = (ImageView)findViewById(R.id.top_mdown);
		reddot = (ImageView)findViewById(R.id.reddot);
		layout1 = (LinearLayout)findViewById(R.id.shouye1_linear);
		layout2 = (LinearLayout)findViewById(R.id.shouye2_linear);
		layout3 = (LinearLayout)findViewById(R.id.shouye3_linear);
		layout4 = (LinearLayout)findViewById(R.id.shouye4_linear);
		
		if (intent.getBooleanExtra("type", false)) {
			isE = true;
			Myconfig.isE = true;
			editor2.putBoolean("isE", true);
			cityView.setVisibility(View.GONE);
			down.setVisibility(View.GONE);
			textView2.setText(R.string.jobmanage);
			textView4.setText(R.string.enterprisecenter);
			infoshare = getSharedPreferences("einfo", Context.MODE_PRIVATE);
		}else {
			editor2.putBoolean("isE", false);
			infoshare = getSharedPreferences("info", Context.MODE_PRIVATE);
		}
		editor2.commit();
		infoEditor = infoshare.edit();
		
		if (Myconfig.isLogin) {
			ExecuteAsyncTask("GetMsgList", mapUtil.MessageMap(10, 1, infoshare.getString("id", "")));
		}
		
		viewPager.setNoScroll(true);
		initfragments();
		
		shared = getSharedPreferences("location", Context.MODE_PRIVATE);
		if (shared != null) {
			cityView.setText(shared.getString("city", "city"));
		}
		cityView.setOnClickListener(this);
	}
	
	private void initfragments(){
		fragments.add(new MainFragment());
		if (isE) {
			fragments.add(new JobManageFragment());
		}else {
			fragments.add(new NewFragment());
		}
		fragments.add(new AssistantFragment());
		fragments.add(new PersonalFragment());
		
		FragmentViewPagerAdapter adapter = new FragmentViewPagerAdapter(this.getSupportFragmentManager(), viewPager, fragments);
		adapter.setOnExtraPageChangeListener(new FragmentViewPagerAdapter.OnExtraPageChangeListener(){
			
			@Override
			public void onExtraPageScrollStateChanged(int i) {
				// TODO Auto-generated method stub
				super.onExtraPageScrollStateChanged(i);
			}
			
			//实现Fragment适配器接口，实现Viewpager页面选择监听
			@Override
			public void onExtraPageSelected(int i) {
				switch (i) {
				case 0:
					toptext.setText(R.string.app_name);
					setBack();
					img1.setImageResource(R.drawable.shouye1p);
					textView1.setTextColor(getResources().getColor(R.color.top_blue));
					break;
				case 1:
					Intent bnIntent = new Intent("getnew");
					MainActivity.this.sendBroadcast(bnIntent);
					if (isE) {
						toptext.setText(R.string.jobmanagem);
					}else {
						toptext.setText(R.string.newestjobm);
					}
					setBack();
					img2.setImageResource(R.drawable.shouye2p);
					textView2.setTextColor(getResources().getColor(R.color.top_blue));
					break;
				case 2:
					reddot.setVisibility(View.GONE);
					toptext.setText(R.string.assistant);
					setBack();
					img3.setImageResource(R.drawable.shouye3p);
					textView3.setTextColor(getResources().getColor(R.color.top_blue));
					break;
				case 3:
					if (isE) {
						toptext.setText(R.string.enterprisecenterm);
					}else {
						toptext.setText(R.string.personalcenterm);
					}
					setBack();
					img4.setImageResource(R.drawable.shouye4p);
					textView4.setTextColor(getResources().getColor(R.color.top_blue));
					break;
				default:
					break;
				}
			}
		});
		
		layout1.setOnClickListener(new MyOnClickListener(0));
		layout2.setOnClickListener(new MyOnClickListener(1));
		layout3.setOnClickListener(new MyOnClickListener(2));
		layout4.setOnClickListener(new MyOnClickListener(3));
	}
	
	/**
	 * 设置底部菜单栏图片文字背景
	 */
	private void setBack(){
		img1.setImageResource(R.drawable.shouye1);
		img2.setImageResource(R.drawable.shouye2);
		img3.setImageResource(R.drawable.shouye3);
		img4.setImageResource(R.drawable.shouye4);
		
		textView1.setTextColor(getResources().getColor(R.color.bottom_text));
		textView2.setTextColor(getResources().getColor(R.color.bottom_text));
		textView3.setTextColor(getResources().getColor(R.color.bottom_text));
		textView4.setTextColor(getResources().getColor(R.color.bottom_text));
	}
	
	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;
		
		public MyOnClickListener(int i) {
			index = i;
		}
		
		@Override
		public void onClick(View v) {
			viewPager.setCurrentItem(index);
		}
	};
	
	
	/**
	 * 键盘返回键点击事件
	 */
	@SuppressLint("ShowToast")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(!isexit){
				isexit = true;
				MyUtils.showToast(activity, R.string.click_exit);
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						isexit = false;
					}
				}, 2000);
				return false;
			}else {
				infoEditor.clear();
				infoEditor.commit();
				
				if (wt != null && !wt.isCancelled()) {
					wt.cancel(true);
				}
				Myconfig.isLogin = false;
				MyApplication.getInstance().exit();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.top_city:
			startActivityForResult(new Intent(this, SelectCitysActivity.class), 101);
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
			cityView.setText(arg2.getStringExtra("name"));
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
			}
			
			@Override
			public void onGetDataSuccessfulDoInUI(String result) {
				// TODO Auto-generated method stub
				//成功获取数据，解析数据
				xlist = JsonTool.Messagejson(result, "MsgList");
				if (xlist != null) {
					if(!xlist.get(0).containsKey("result")){
						for (Map<String, String> element : xlist) {
							if (element.get("read").equals("0")) {
								reddot.setVisibility(View.VISIBLE);
								Myconfig.isRead = true;
								break;
							}
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
				MyUtils.showToast2(activity, getResources().getString(R.string.iserror));
				for (String string : values) {
					Log.i("it is error", string + "");
				}
			}
			
			@Override
			public void onCancelledDoInUI(String result) {
				// TODO Auto-generated method stub
				try {
				} catch (Exception e) {
					// TODO: handle exception
					Log.i("it is cancle error", e.getMessage());
				}
			}
			
			@Override
			public void onTimeoutDoInUI(String[] values) {
				// TODO Auto-generated method stub
				MyUtils.showToast2(activity, getResources().getString(R.string.timeout));
			}
		};
		
		wt.myExecute(method, map, false);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		infoEditor.clear();
		infoEditor.commit();
		
		if (wt != null && !wt.isCancelled()) {
			wt.cancel(true);
		}
	}
	
	/**
	 * 回调接口
	 * @author zhaoxin5
	 *
	 */
	public interface MyTouchListener
	{
	        public void onTouchEvent(MotionEvent event);
	}

	/*
	 * 保存MyTouchListener接口的列表
	 */
	private ArrayList<MyTouchListener> myTouchListeners = new ArrayList<MainActivity.MyTouchListener>();

	/**
	 * 提供给Fragment通过getActivity()方法来注册自己的触摸事件的方法
	 * @param listener
	 */
	public void registerMyTouchListener(MyTouchListener listener)
	{
	        myTouchListeners.add( listener );
	}

	/**
	 * 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法
	 * @param listener
	 */
	public void unRegisterMyTouchListener(MyTouchListener listener)
	{
	        myTouchListeners.remove( listener );
	}

	/**
	 * 分发触摸事件给所有注册了MyTouchListener的接口
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
	        // TODO Auto-generated method stub 
	        for (MyTouchListener listener : myTouchListeners) {
	                       listener.onTouchEvent(ev);
	        }
	        return super.dispatchTouchEvent(ev);
	}
}
