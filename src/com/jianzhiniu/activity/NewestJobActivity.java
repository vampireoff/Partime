package com.jianzhiniu.activity;

import com.jianzhiniu.config.MyApplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 最新兼职界面
 * @author Administrator
 *
 */
public class NewestJobActivity extends BaseFragmentActivity{

	private TextView centerView;
	private ImageView returnView;
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newestjob_view);
		MyApplication.getInstance().addActivity(this);
		
		intent = new Intent("getnew");
		this.sendBroadcast(intent);
		centerView = (TextView)findViewById(R.id.top_centertext);
		returnView = (ImageView)findViewById(R.id.top_return);
		
		centerView.setText(R.string.newestjobm);
		
		returnView.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.top_return:
			this.finish();
			break;

		default:
			break;
		}
	}
}
