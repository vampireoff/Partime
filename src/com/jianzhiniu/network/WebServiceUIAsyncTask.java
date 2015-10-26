package com.jianzhiniu.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

public abstract class WebServiceUIAsyncTask extends WebServiceAsyncTask {
	private int processType;
	/**
	 * ����ʼ��ʶ
	 */
	protected final int TASK_START = 1;

	/**
	 * ��������б�ʶ
	 */
	protected final int TASK_WORK = 2;
	/**
	 * ��������ʶ
	 */
	protected final int TASK_EXCEPTION = 3;
	/**
	 * ���������������ʧ�ܱ�ʶ
	 */
	protected final int TASK_NONETWORK = 4;
	protected final int TIMEOUT_EXCEPTION = 5;
	private Context currentActivity = null;

	/**
	 * ����UI������AsynTask��
	 * 
	 * @param activity
	 *            ���õ�Activity
	 */
	public WebServiceUIAsyncTask(Context activity) {
		this.currentActivity = activity;
	}

	/**
	 * ���ڴ˺����ڣ���ӿ�ʼ���к�̨��������ʾ(UI����)
	 * 
	 * @param values
	 */
	public abstract void onTaskStartDoInUI(String... values);

	/**
	 * �ڴ�������и���UI����ʱ��Ч ���ڴ˺����ڣ�����ڴ�������е���ʾ(UI����)
	 * 
	 * @param values
	 */
	public abstract void onTaskWorkingDoInUI(String[] values);

	/**
	 * 
	 * ���ڴ˺����ڣ���ӻ�ȡ���ݳɹ�����ʾ(UI����)
	 * 
	 * @param result
	 */
	public abstract void onGetDataSuccessfulDoInUI(String result);

	/**
	 * ���ڴ˺����ڣ�����ж����ݻ�ȡ��������ʾ(UI����)
	 * 
	 * @param result
	 * 
	 */
	public abstract void onCancelledDoInUI(String result);

	/**
	 * ���ڴ˺����ڣ����û����������ʱ����ʾ(UI����)
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
	 * ���ڴ˺����ڣ���ӳ����쳣��ʱ�����ʾ(UI����)
	 * 
	 * @param values
	 */
	public abstract void onExceptionDoInUI(String[] values);
	
	/**
	 * ����ʱ
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
		// Toast.makeText(currentActivity, "��ý��Ϊ�ա�", Toast.LENGTH_SHORT)
		// .show();
	}

	/**
	 * ������ȥ��������Դ����ʹ�ñ������ڻ���������ʾUI
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
	 * ����ʼ (non-Javadoc)
	 * 
	 * @see landray.android.webService.WebServiceAsyncTask#onTaskStart()
	 */
	@Override
	public void onTaskStart() {
		// TODO Auto-generated method stub
		myPublishProgress(TASK_START, "");
	}

	/**
	 * �����쳣 (non-Javadoc)
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
	 * ��ý������ (non-Javadoc)
	 * 
	 * @see landray.android.webService.WebServiceAsyncTask#PrepareResult(java.lang.String)
	 */
	@Override
	public String PrepareResult(String result) {
		// Ĭ��ֱ�ӷ���ԭֵ
		return result;
	}

	/**
	 * �����޿�������ʱ���� (non-Javadoc)
	 * 
	 * @see landray.android.webService.WebServiceAsyncTask#onNoNetworkConnectionAvailable(java.lang.String)
	 */
	@Override
	public void onNoNetworkConnectionAvailable(String string) {
		// TODO Auto-generated method stub
		myPublishProgress(TASK_NONETWORK, string);
	}

}
