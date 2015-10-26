package com.jianzhiniu.network;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

import com.jianzhiniu.activity.R;
import com.jianzhiniu.config.Common;
import com.jianzhiniu.config.MyApplication;
import com.jianzhiniu.utils.MyUtils;

public abstract class WebServiceAsyncTask extends 
	AsyncTask<Map<String, Object>, String, String> {

	protected boolean _isUseCache = false;

	/**
	 * 不会与UI进行交互
	 */
	public WebServiceAsyncTask() {
	}

	@Override
	protected String doInBackground(Map<String, Object>... params) {
		try {
			if (_isUseCache) {
				return null;
			} else {
				
				if (NetworkUtil.isNetWorkConnected(MyApplication.getInstance())) {
					onTaskStart();
					return PrepareResult(CallingWS(params));
				} else {
					onNoNetworkConnectionAvailable(MyApplication.getInstance().getString(R.string.no_net));
					return null;
				}
			}
		} catch (Exception e) {
			onException(e);
			return null;
		}

	}

	protected String CallingWS(Map<String, Object>... params) {
		Object result = null;
		try {
			// 传入调用的函数名
			if (params.length != 2) {
				throw new Exception("参数params要求传入两个成员");
			}

			String methodName = params[0].get("method").toString();
			// String xmlParamValue = params[1].get("xmlParam").toString();

			result = WSUtil.getSoapObjectByCallingWS(
					Common.namespace, methodName, params[1],
					MyUtils.getWSUrl());

		}catch(TimeoutException exception){
			onTimeout(exception);
		}catch (Exception ex) {
			// Log.e("Exception", ex.getMessage());
			onException(ex);
		}
		return result.toString();
	}

	/**
	 * 请使用此方法执行线程任务
	 * 
	 * @param methodName
	 *            调用的方法名称
	 * @param params1
	 *            参数映射表
	 * @param isUseCache
	 *            是否使用缓存字符串
	 * @param cache
	 *            缓存字符串
	 */
//	@SuppressLint("NewApi")
//	@SuppressWarnings("unchecked")
//	public void myExecute(String methodName, Map<String, Object> params1,
//			boolean isUseCache, String cache) {
//		// _isUseCache = isUseCache;
//		if (_isUseCache && cache != null && !cache.equals("")) {
//			_isUseCache = true;
//			Map<String, Object> params0 = new HashMap<String, Object>();
//			params0.put("cache", cache);
//			this.execute(params0);
//		} else {
//			_isUseCache = false;
//			Map<String, Object> params0 = new HashMap<String, Object>();
//			params0.put("method", methodName);
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
//				this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params0,
//						params1);
//			else
//				this.execute(params0, params1);
//		}
//	}

	@SuppressLint("NewApi")
	@SuppressWarnings("unchecked")
	public void myExecute(String methodName, Map<String, Object> params1,
			boolean isUseCache) {
		// _isUseCache = isUseCache;
		if (isUseCache) {
			_isUseCache = true;
			Map<String, Object> params0 = new HashMap<String, Object>();
			// params0.put("cache", cache);
			this.execute(params0);
		} else {
			_isUseCache = false;
			Map<String, Object> params0 = new HashMap<String, Object>();
			params0.put("method", methodName);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params0,
						params1);
			else
				this.execute(params0, params1);
		}
	}

	public abstract void onTaskStart();

	public abstract void onException(Exception ex);
	public abstract void onTimeout(Exception ex);

	/**
	 * 获得结果后，对结果字符串进行处理
	 * 
	 * @param result
	 * @return
	 */
	public abstract String PrepareResult(String result);

	public abstract void onNoNetworkConnectionAvailable(String string);

}
