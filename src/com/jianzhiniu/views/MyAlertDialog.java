package com.jianzhiniu.views;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jianzhiniu.activity.R;
import com.jianzhiniu.utils.MyUtils;

public class MyAlertDialog{

	public Dialog alertDialog;
	public AlertCallback alertCallback;
	public Activity context;
	
	public interface AlertCallback{
		public void ybutton();
	}
	
	public MyAlertDialog(Activity mActivity, AlertCallback callback){
		this.context = mActivity;
		this.alertCallback = callback;
	}
	
	public void showMyAlertDialog(int resid) {
		// TODO Auto-generated constructor stub
		View view = LayoutInflater.from(context).inflate(R.layout.exit_view, null);
		Button yButton = (Button)view.findViewById(R.id.y_button);
		Button nButton = (Button)view.findViewById(R.id.n_button);
		TextView textView = (TextView)view.findViewById(R.id.tip_text);
		if (resid != 0) {
			textView.setText(resid);
		}
		
		alertDialog = new Dialog(context, R.style.alert_dialog);
		alertDialog.setContentView(view, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		alertDialog.show();
		WindowManager.LayoutParams params = alertDialog.getWindow()   
                .getAttributes();   
        params.width = MyUtils.getwidth(context) - context.getResources().getDimensionPixelSize(R.dimen.dialogpadding);   
        alertDialog.getWindow().setAttributes(params);  
        
        /**
         * 确定按钮
         */
		yButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
				alertCallback.ybutton();
			}
		});
		
		/**
		 * 取消按钮
		 */
		nButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
			}
		});
	}

}
