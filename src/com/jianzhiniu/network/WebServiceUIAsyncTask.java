package com.jianzhiniu.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

public abstract class WebServiceUIAsyncTask extends WebServiceAsyncTask {
	private int processType;
	/**
	 * 任务开始标识
	 */
	protected final int TASK_START = 1;

	/**
	 * 任务进行中标识
	 */
	protected final int TASK_WORK = 2;
	/**
	 * 任务出错标识
	 */
	protected final int TASK_EXCEPTION = 3;
	/**
	 * 任务出现网络连接失败标识
	 */
	protected final int TASK_NONETWORK = 4;
	protected final int TIMEOUT_EXCEPTION = 5;
	private Context currentActivity = null;

	/**
	 * 会与UI交互的AsynTask类
	 * 
	 * @param activity
	 *            调用的Activity
	 */
	public WebServiceUIAsyncTask(Context activity) {
		this.currentActivity = activity;
	}

	/**
	 * 请在此函数内，添加开始进行后台工作的提示(UI操作)
	 * 
	 * @param values
	 */
	public abstract void onTaskStartDoInUI(String... values);

	/**
	 * 在处理过程中更新UI，暂时无效 请在此函数内，添加在处理过程中的提示(UI操作)
	 * 
	 * @param values
	 */
	public abstract void onTaskWorkingDoInUI(String[] values);

	/**
	 * 
	 * 请在此函数内，添加获取数据成功的提示(UI操作)
	 * 
	 * @param result
	 */
	public abstract void onGetDataSuccessfulDoInUI(String result);

	/**
	 * 请在此函数内，添加中断数据获取操作的提示(UI操作)
	 * 
	 * @param result
	 * 
	 */
	public abstract void onCancelledDoInUI(String result);

	/**
	 * 请在此函数内，添加没有网络连接时的提示(UI操作)
	 * 
	 * @param values
	 */
	private void onNoNetworkDoInUI(String[] values) {
		if (currentActivity != null && values != null && values.length > 0) {
			for (String msg : values) {
				Toast.makeText(currentActivity, msg, Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 请在此函数内，添加出现异常的时候的提示(UI操作)
	 * 
	 * @param values
	 */
	public abstract void onExceptionDoInUI(String[] values);
	
	/**
	 * 请求超时
	 * @param values
	 */
	public abstract void onTimeoutDoInUI(String[] values);

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (_isUseCache) {
			onUseCacheDoInUI();
			return;
		}
		if (result != null && !result.equals(""))
			onGetDataSuccessfulDoInUI(result);
		// else
		// Toast.makeText(currentActivity, "获得结果为空。", Toast.LENGTH_SHORT)
		// .show();
	}

	/**
	 * 不重新去访问数据源，而使用本对象内缓存重新显示UI
	 */
	public abstract void onUseCacheDoInUI();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onCancelled(java.lang.Object)
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onCancelled(String result) {
		// TODO Auto-generated method stub
		super.onCancelled(result);
		onCancelledDoInUI(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		switch (processType) {
		case TASK_START:
			onTaskStartDoInUI(values);
			break;
		case TASK_WORK:
			onTaskWorkingDoInUI(values);
			break;
		case TASK_EXCEPTION:
			onExceptionDoInUI(values);
			break;
		case TASK_NONETWORK:
			onNoNetworkDoInUI(values);
			break;
		case TIMEOUT_EXCEPTION:
			onTimeoutDoInUI(values);
			break;
		default:
			break;
		}
	}

	protected void myPublishProgress(int type, String msg) {
		processType = type;
		publishProgress(msg);
	}

	/**
	 * 任务开始 (non-Javadoc)
	 * 
	 * @see landray.android.webService.WebServiceAsyncTask#onTaskStart()
	 */
	@Override
	public void onTaskStart() {
		// TODO Auto-generated method stub
		myPublishProgress(TASK_START, "");
	}

	/**
	 * 发生异常 (non-Javadoc)
	 * 
	 * @see landray.android.webService.WebServiceAsyncTask#onException(java.lang.Exception)
	 */
	@Override
	public void onException(Exception ex) {
		// TODO Auto-generated method stub
		myPublishProgress(TASK_EXCEPTION, ex.getMessage());
	}

	@Override
	public void onTimeout(Exception ex) {
		// TODO Auto-generated method stub
		myPublishProgress(TIMEOUT_EXCEPTION, ex.getMessage());
	}
	/**
	 * 获得结果后处理 (non-Javadoc)
	 * 
	 * @see landray.android.webService.WebServiceAsyncTask#PrepareResult(java.lang.String)
	 */
	@Override
	public String PrepareResult(String result) {
		// 默认直接返回原值
		return result;
	}

	/**
	 * 网络无可用连接时发生 (non-Javadoc)
	 * 
	 * @see landray.android.webService.WebServiceAsyncTask#onNoNetworkConnectionAvailable(java.lang.String)
	 */
	@Override
	public void onNoNetworkConnectionAvailable(String string) {
		// TODO Auto-generated method stub
		myPublishProgress(TASK_NONETWORK, string);
	}

}
