package com.jianzhiniu.config;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class MyApplication extends Application {

	private static MyApplication instance;
	private List<Activity> activityList = new LinkedList<Activity>();
	
	//添加Activity到容器中
	public void addActivity(Activity activity)
	{
		activityList.add(activity);
	}
	
	//遍历所有Activity并finish
	public void exit()
	{
		
		for(Activity activity : activityList)
		{
			activity.finish();
		}
		
		System.exit(0);
		
	}
	/**
	 * 获取activity的数量
	 * @return
	 */
	public int getCount(){
		return activityList.size();
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
	}
	
	public static MyApplication getInstance(){
		if (instance == null) {
			instance = new MyApplication();
		}
		return instance;
	}
}
