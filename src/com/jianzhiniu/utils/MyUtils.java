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
	 * 获取图标url
	 * @return
	 */
	public static String getIconUrl(){
		return Common.url + Common.main_dir + Common.icon_dir;
	}
	
	/**
	 * 获取图片url
	 * @return
	 */
	public static String getPicUrl(){
		return Common.url + Common.main_dir;
	}
	
	/**
	 * 获取接口url
	 * @return
	 */
	public static String getWSUrl(){
		return Common.url + Common.main_dir + Common.webservice;
	}
	/**
	 * 获取广告url
	 * @return
	 */
	public static String getAdverUrl(){
		return Common.url + Common.main_dir + Common.ahtml;
	}
	/**
	 * 获取通知url
	 * @return
	 */
	public static String getNoticeUrl(){
		return Common.url + Common.main_dir + Common.nhtml;
	}
	/**
	 * 获取授权码
	 * @return
	 */
	public static String getAccesskey(){
		return Common.accesskey;
	}
	
	/**
	 * 显示toast
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
	 * 上传数据时符号转换
	 * @param string
	 * @return
	 */
	public static String UploadString(String string){
		return string.replace("\"", "&quot;");
	}
	/**
	 * 解析数据时符号转换
	 * @param string
	 * @return
	 */
	public static String ParseString(String string){
		return string.replace("&quot;", "\"");
	}
	
	/**
	 * 获取屏幕宽度
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
	 * 获取屏幕高度
	 * @param activity
	 * @return
	 */
	public static int getheight(Activity activity){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int height = displayMetrics.heightPixels;
		return height;
	}
	
	// 检查更新
		public static boolean isUpdate(String no, Context context) {
			int num = getAppVersionCode(context, context.getString(R.string.app_name));
			if (num != 0) {
				// 版本判断
				if (Integer.parseInt(no) > num) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * 获取版本号
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
	 * 将时间戳转为代表"距现在多久之前"的字符串 
	 * @param timeStr   时间戳 
	 * @return 
	 */  
	public static String getStandardDate(String timeStr, Context context) {  
	  
		if (timeStr == null || timeStr.equals("")) {
			return "";
		}
	    StringBuffer sb = new StringBuffer();  
	    long t = getStringToDate(timeStr);
	    long time = System.currentTimeMillis() - t;  
	    long mill = (long) Math.ceil(time /1000);//秒前  
	  
	    long minute = (long) Math.ceil(time/60/1000.0f);// 分钟前  
	  
	    long hour = (long) Math.ceil(time/60/60/1000.0f);// 小时  
	  
	    long day = (long) Math.ceil(time/24/60/60/1000.0f);// 天前  
	  
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
	
	
	 /*将字符串转为时间戳*/
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
