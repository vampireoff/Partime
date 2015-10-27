package com.jianzhiniu.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jianzhiniu.config.MyApplication;

/**
 * ���������
 * @author Administrator
 *
 */
public class WebViewActivity extends BaseActivity{

	private ProgressBar progressBar;
	private WebView webView;
	private Intent intent;
	private ImageView returnView;
	private TextView topcenter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mywebview);
		MyApplication.getInstance().addActivity(this);
		intent = getIntent();
		
		progressBar = (ProgressBar) findViewById(R.id.ss_htmlprogessbar);
		returnView = (ImageView)findViewById(R.id.top_return);
		topcenter = (TextView)findViewById(R.id.top_centertext);
		
		returnView.setFocusable(true);
		returnView.setFocusableInTouchMode(true);
		returnView.requestFocus();
		returnView.requestFocusFromTouch();
		
		topcenter.setText(intent.getStringExtra("title"));
		returnView.setOnClickListener(this);
		progressBar.setVisibility(View.VISIBLE);
		
		initWebView();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.top_return:
			this.finish();
			break;

		default:
			break;
		}
	}
	
	/**
	 * ����webview
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		webView = (WebView)findViewById(R.id.wb_details);
		webView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_UP) {
					webView.setFocusable(false);
					returnView.setFocusable(true);
					returnView.setFocusableInTouchMode(true);
					returnView.requestFocus();
					returnView.requestFocusFromTouch();
				}
				return false;
			}
		});
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);//���ÿ�������JS�ű�
//			settings.setTextZoom(120);//Sets the text zoom of the page in percent. The default is 100.
//			settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
//			settings.setUseWideViewPort(true); //��ҳ��ʱ�� ����Ӧ��Ļ 
//			settings.setLoadWithOverviewMode(true);//��ҳ��ʱ�� ����Ӧ��Ļ 
		settings.setSupportZoom(true);// ��������webview�Ŵ�
		settings.setBuiltInZoomControls(true);
		webView.setBackgroundColor(getResources().getColor(R.color.white));
		webView.setWebChromeClient(new MyWebChromeClient());
		webView.setWebViewClient(new MyWebViewClient());
		webView.loadUrl(intent.getStringExtra("url"));  	///***����˷�����Ҫ��ͬ���߳���ִ��***///
	}
	
	// ������ҳ���ؽ���
		private class MyWebViewClient extends WebViewClient {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return super.shouldOverrideUrlLoading(view, url);
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				view.getSettings().setJavaScriptEnabled(true);
				super.onPageFinished(view, url);
				progressBar.setVisibility(View.GONE);
				webView.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				view.getSettings().setJavaScriptEnabled(true);
				super.onPageStarted(view, url, favicon);
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				progressBar.setVisibility(View.GONE);
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
		}
		
		/**
		 * ʵ�ֽ�����������ҳ�����ٶ�
		 * @author Administrator
		 *
		 */
		private class MyWebChromeClient extends WebChromeClient {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				if(newProgress != 100){
					progressBar.setProgress(newProgress);
				}
				super.onProgressChanged(view, newProgress);
			}
		}
		
		public boolean onKeyDown(int keyCode, KeyEvent event) {   
			if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {   
				webView.goBack();   
				return true;   
			}   
			return super.onKeyDown(keyCode, event);   
		}  
}
