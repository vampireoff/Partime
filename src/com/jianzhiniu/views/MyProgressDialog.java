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
 * �Զ����progressdialog��ʾ��
 * @author Administrator
 *
 */
@SuppressLint("InflateParams")
public class MyProgressDialog {
	public static Dialog loadingDialog;
	public static boolean isOpen;
	/**
	 * ��ʾ�Զ����progressDialog
	 * @param context
	 * @param msg
	 * @return
	 */
	public static void showDialog(Context context, int textid) {

		if (context == null) return;
		
		if (!isOpen) {
			isOpen = true;
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.progress_dialog, null);// �õ�����view
			LinearLayout layout = (LinearLayout) v.findViewById(R.id.progress_linear);// ���ز���
			TextView textView = (TextView) v.findViewById(R.id.progress_text);
			if (textid != 0) {
				textView.setText(textid);
			}
			// main.xml�е�ImageView
//		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
//		 ���ض���
//		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
//				context, R.anim.rotate);
//		hyperspaceJumpAnimation.setInterpolator(new LinearInterpolator());
//		 ʹ��ImageView��ʾ����
//		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
			
			loadingDialog = new Dialog(context, R.style.loading_dialog);// �����Զ�����ʽdialog
			
			loadingDialog.setCanceledOnTouchOutside(false);
			loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT));// ���ò���
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
