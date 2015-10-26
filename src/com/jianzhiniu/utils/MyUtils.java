package com.jianzhiniu.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.jianzhiniu.activity.R;
import com.jianzhiniu.config.Common;
import com.jianzhiniu.config.MyApplication;

public class MyUtils {

	/**
	 * ��ȡͼ��url
	 * @return
	 */
	public static String getIconUrl(){
		return Common.url + Common.main_dir + Common.icon_dir;
	}
	
	/**
	 * ��ȡͼƬurl
	 * @return
	 */
	public static String getPicUrl(){
		return Common.url + Common.main_dir;
	}
	
	/**
	 * ��ȡ�ӿ�url
	 * @return
	 */
	public static String getWSUrl(){
		return Common.url + Common.main_dir + Common.webservice;
	}
	/**
	 * ��ȡ���url
	 * @return
	 */
	public static String getAdverUrl(){
		return Common.url + Common.main_dir + Common.ahtml;
	}
	/**
	 * ��ȡ֪ͨurl
	 * @return
	 */
	public static String getNoticeUrl(){
		return Common.url + Common.main_dir + Common.nhtml;
	}
	/**
	 * ��ȡ��Ȩ��
	 * @return
	 */
	public static String getAccesskey(){
		return Common.accesskey;
	}
	
	/**
	 * ��ʾtoast
	 * @param context
	 * @param string
	 */
	public static void showToast2(Context context, String string){
		Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
	}
	
	public static void showToast(Context context, int id){
		Toast.makeText(context, id, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * �ϴ�����ʱ����ת��
	 * @param string
	 * @return
	 */
	public static String UploadString(String string){
		return string.replace("\"", "&quot;");
	}
	/**
	 * ��������ʱ����ת��
	 * @param string
	 * @return
	 */
	public static String ParseString(String string){
		return string.replace("&quot;", "\"");
	}
	
	/**
	 * ��ȡ��Ļ���
	 * @param activity
	 * @return
	 */
	public static int getwidth(Activity activity){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int width = displayMetrics.widthPixels;
		return width;
	}
	
	/**
	 * ��ȡ��Ļ�߶�
	 * @param activity
	 * @return
	 */
	public static int getheight(Activity activity){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int height = displayMetrics.heightPixels;
		return height;
	}
	
	// ������
		public static boolean isUpdate(String no, Context context) {
			int num = getAppVersionCode(context, context.getString(R.string.app_name));
			if (num != 0) {
				// �汾�ж�
				if (Integer.parseInt(no) > num) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * ��ȡ�汾��
		 * @param appPackageCode
		 * @return
		 */
		public static int getAppVersionCode(Context context, String appPackageCode) {
			PackageInfo info = null;
			try {
				info = context.getPackageManager().getPackageInfo(
						MyApplication.getInstance().getPackageName(), 0);
			} catch (NameNotFoundException e) {
				info = null;
				e.printStackTrace();
			}
			if (null == info) {
				return 0;
			}
			return info.versionCode;
		}
	
	/** 
	 * ��ʱ���תΪ����"�����ڶ��֮ǰ"���ַ��� 
	 * @param timeStr   ʱ��� 
	 * @return 
	 */  
	public static String getStandardDate(String timeStr, Context context) {  
	  
		if (timeStr == null || timeStr.equals("")) {
			return "";
		}
	    StringBuffer sb = new StringBuffer();  
	    long t = getStringToDate(timeStr);
	    long time = System.currentTimeMillis() - t;  
	    long mill = (long) Math.ceil(time /1000);//��ǰ  
	  
	    long minute = (long) Math.ceil(time/60/1000.0f);// ����ǰ  
	  
	    long hour = (long) Math.ceil(time/60/60/1000.0f);// Сʱ  
	  
	    long day = (long) Math.ceil(time/24/60/60/1000.0f);// ��ǰ  
	  
	    if (day - 1 > 0) {  
	    	if ((day - 1) == 1) {
	    		sb.append(context.getString(R.string.yesterday));  
			}else if((day - 1) < 4){
				sb.append((day - 1) + context.getString(R.string.dayago));  
			}else {
				sb.append(timeStr.split(" ")[0].replace("/", "-"));
			}
	    } else if (hour - 1 > 0) {  
	        if (hour >= 24) {  
	            sb.append(context.getString(R.string.yesterday));  
	        } else {  
	            sb.append((hour - 1) + context.getString(R.string.hourago));  
	        }  
	    } else if (minute - 1 > 0) {  
	        if (minute == 60) {  
	            sb.append("1" + context.getString(R.string.hourago1));  
	        } else {  
	            sb.append((minute - 1) + context.getString(R.string.minuteago));  
	        }  
	    } else if (mill - 1 > 0) {  
	        if (mill == 60) {  
	            sb.append("1" + context.getString(R.string.minuteago1));  
	        } else {  
	            sb.append(context.getString(R.string.just));  
	        }  
	    } else {  
	        sb.append(context.getString(R.string.just));  
	    }  
	    return sb.toString();  
	}  
	
	
	 /*���ַ���תΪʱ���*/
    @SuppressLint("SimpleDateFormat")
	public static long getStringToDate(String time) {
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try{
            date = sdf.parse(time.replace("/", "-"));
        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }
}
