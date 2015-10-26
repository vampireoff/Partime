package com.jianzhiniu.activity;

import com.jianzhiniu.config.MyApplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MyCollectActivity extends BaseFragmentActivity{

	private TextView centerView;
	private ImageView returnView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mycollect_view);
		MyApplication.getInstance().addActivity(this);
		
		centerView = (TextView)findViewById(R.id.top_centertext);
		returnView = (ImageView)findViewById(R.id.top_return);
		
		centerView.setText(R.string.mycollect);
		
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
