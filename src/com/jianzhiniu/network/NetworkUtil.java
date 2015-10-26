package com.jianzhiniu.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * ���繤����
 * @author zwb
 */
public class NetworkUtil {
	/**
	 * ��url����ȡ�ļ���
	 * @param url
	 * @return
	 */
	public static String convertUrlToFileName(String url) {
		String[] strs = url.split("/");
		return strs[strs.length - 1];
	}
	
	/**
	 * �ж��Ƿ�����������
	 * @param context
	 * @return true/false
	 */
	public static boolean isNetWorkConnected(Context context) {
		if (context != null) {
			ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = manager.getActiveNetworkInfo();
			if (networkInfo != null) {
				return networkInfo.isAvailable();
			}
		}
		return false;
	}
	
	/**********************
	 *�Ƿ�Ϊwifi����
	 * @param icontext
	 * @return
	 */
	public static boolean isWifiActive(Context icontext){
		Context context = icontext.getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info;
		if (connectivity != null) {
			info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getTypeName().equals("WIFI") &&
							
							info[i].isConnected()) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
