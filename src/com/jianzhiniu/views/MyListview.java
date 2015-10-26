package com.jianzhiniu.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class MyListview extends ListView {
	
	public MyListview(Context context){
		super(context);
	}
	
	public MyListview(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
				MeasureSpec.AT_MOST);  
		super.onMeasure(widthMeasureSpec, expandSpec);  
	}
	
}
