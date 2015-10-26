package com.jianzhiniu.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jianzhiniu.activity.R;
/**
 * 自定义的progressdialog提示框
 * @author Administrator
 *
 */
@SuppressLint("InflateParams")
public class MyProgressDialog {
	public static Dialog loadingDialog;
	public static boolean isOpen;
	/**
	 * 显示自定义的progressDialog
	 * @param context
	 * @param msg
	 * @return
	 */
	public static void showDialog(Context context, int textid) {

		if (context == null) return;
		
		if (!isOpen) {
			isOpen = true;
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.progress_dialog, null);// 得到加载view
			LinearLayout layout = (LinearLayout) v.findViewById(R.id.progress_linear);// 加载布局
			TextView textView = (TextView) v.findViewById(R.id.progress_text);
			if (textid != 0) {
				textView.setText(textid);
			}
			// main.xml中的ImageView
//		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
//		 加载动画
//		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
//				context, R.anim.rotate);
//		hyperspaceJumpAnimation.setInterpolator(new LinearInterpolator());
//		 使用ImageView显示动画
//		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
			
			loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
			
			loadingDialog.setCanceledOnTouchOutside(false);
			loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
			loadingDialog.show();
		}

	}
	
	public static void Dismiss(){
		if (isOpen) {
			loadingDialog.dismiss();
			isOpen = false;
		}
	}
}
