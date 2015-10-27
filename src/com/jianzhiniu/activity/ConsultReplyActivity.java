package com.jianzhiniu.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jianzhiniu.adapter.ReplyAdapter;
import com.jianzhiniu.config.MyApplication;
import com.jianzhiniu.network.JsonTool;
import com.jianzhiniu.network.MapUtil;
import com.jianzhiniu.network.WebServiceUIAsyncTask;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.MyProgressDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * 咨询回复界面
 * @author Administrator
 *
 */
public class ConsultReplyActivity extends BaseActivity{

	private Activity activity = ConsultReplyActivity.this;
	private TextView topcenter, jobname, time, price, place, balert, oalert;
	private ImageView returnView, ricon, alerticon;
	private List<Map<String, String>> xlist = new ArrayList<Map<String,String>>();
	private List<Map<String, String>> mlist = new ArrayList<Map<String,String>>();
	private Map<String, String> infomap = new HashMap<String, String>();
	private ListView xListView;
	private ReplyAdapter xAdapter;
	private View jobinfoView, replyView;
    private int page = 1;
    private int size = 1000;
    private Intent intent;
    private MapUtil mapUtil;
    private WebServiceUIAsyncTask wt;
    private String webtype = "info";
    private ImageLoader loader = ImageLoader.getInstance();
	private DisplayImageOptions options;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.consult_reply);
		MyApplication.getInstance().addActivity(this);
		
		loader.init(ImageLoaderConfiguration.createDefault(this));
		//图片参数设置
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.imgloading_r)
		.showImageForEmptyUri(R.drawable.imgerror)
		.showImageOnFail(R.drawable.imgerror).cacheOnDisc()
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new FadeInBitmapDisplayer(300)).build();
		
		mapUtil = new MapUtil(activity);
		intent = getIntent();
		topcenter = (TextView)findViewById(R.id.top_centertext);
		jobname = (TextView)findViewById(R.id.jobname);
		place = (TextView)findViewById(R.id.didian_text);
		time = (TextView)findViewById(R.id.time_text);
		price = (TextView)findViewById(R.id.price_text);
		balert = (TextView)findViewById(R.id.blackalert);
		oalert = (TextView)findViewById(R.id.orangealert);
		returnView = (ImageView)findViewById(R.id.top_return);
		alerticon = (ImageView)findViewById(R.id.alertimg);
		ricon = (ImageView)findViewById(R.id.img);
		jobinfoView = (View)findViewById(R.id.to_jobinfoview);
		replyView = (View)findViewById(R.id.cr_linear);
		
//		jobname.setText(intent.getStringExtra("title"));
		returnView.setOnClickListener(this);
		
		xListView = (ListView)findViewById(R.id.xlistview);

        xAdapter = new ReplyAdapter(mlist, activity);
        xListView.setAdapter(xAdapter);
        jobinfoView.setOnClickListener(this);
        replyView.setOnClickListener(this);
        if (intent.getBooleanExtra("ise", false)) {
        	alerticon.setVisibility(View.VISIBLE);
        	oalert.setVisibility(View.VISIBLE);
        	xListView.setOnItemClickListener(itemClickListener);
        	topcenter.setText(R.string.jobconsult);
        }else {
        	balert.setVisibility(View.VISIBLE);
        	topcenter.setText(R.string.consultreply);
        }
        
        ExecuteAsyncTask("GetJobInfo", mapUtil.JobInfoMap(intent.getStringExtra("id")));
        
	}
	
	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Intent ccIntent = new Intent(activity, ContentEditActivity.class);
			ccIntent.putExtra("type", "reply");
			ccIntent.putExtra("id", mlist.get(arg2).get("qid"));
			startActivityForResult(ccIntent, 11);
		}
	};
	
	/**
	 * 使用异步的方式请求数据
	 */
	public void ExecuteAsyncTask(String method, Map<String, Object> map) {
		wt = new WebServiceUIAsyncTask(activity) {
			
			@Override
			public void onTaskStartDoInUI(String... values) {
				// TODO Auto-generated method stub
				//开始请求数据，显示加载框
				if (webtype.equals("info")) {
					MyProgressDialog.showDialog(activity, 0);
				}
			}
			
			@Override
			public void onGetDataSuccessfulDoInUI(String result) {
				// TODO Auto-generated method stub
				//成功获取数据，解析数据
				if (webtype.equals("info")) {	//工作信息
					infomap = JsonTool.JobInfojson(result, "JobInfo");
					if (infomap == null) {
						MyUtils.showToast2(activity, getResources().
								getString(R.string.data_parse_error));
					}else {
						if(infomap.get("result").equals("1"))
						{
							loader.displayImage(MyUtils.getIconUrl() + infomap.get("jsid") + ".png", ricon, options);
							place.setText(infomap.get("city"));
							jobname.setText(infomap.get("title"));
							time.setText(infomap.get("issuetime").split(" ")[0]);
							price.setText("$ " + infomap.get("wage") + infomap.get("unit"));
							webtype = "list";
							ExecuteAsyncTask("GetJobQuestionList", mapUtil.ConsultListMap(
									intent.getStringExtra("id"), size, page));
						}else{
							MyUtils.showToast2(activity, infomap.get("msg"));
						}
					}
				}else {		//咨询回复列表
					MyProgressDialog.Dismiss();
					xlist = JsonTool.ConsultListjson(result, "JobQuestionList");
					if (xlist == null) {
						MyUtils.showToast2(activity, getResources().
								getString(R.string.data_parse_error));
					}else {
						if (xlist.get(0).containsKey("result")) {
							MyUtils.showToast2(activity, xlist.get(0).get("msg"));
						}else {
							mlist.clear();
							for (Map<String, String> element : xlist) {
								if (element.get("sid").equals(intent.getStringExtra("fid"))) {
									mlist.add(element);
								}
							}
							xAdapter.refresh(mlist);
							if (!intent.getBooleanExtra("ise", false)) {
								replyView.setVisibility(View.VISIBLE);
							}
						}
					}
				}
					
					
			}
			
			@Override
			public void onUseCacheDoInUI() {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onTaskWorkingDoInUI(String[] values) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onExceptionDoInUI(String[] values) {
				// TODO Auto-generated method stub
				MyProgressDialog.Dismiss();
				MyUtils.showToast2(activity, getResources().getString(R.string.iserror));
				for (String string : values) {
					Log.i("it is error", string + "");
				}
			}
			
			@Override
			public void onCancelledDoInUI(String result) {
				// TODO Auto-generated method stub
				try {
					MyProgressDialog.Dismiss();
				} catch (Exception e) {
					// TODO: handle exception
					Log.i("it is cancle error", e.getMessage());
				}
			}
			
			@Override
			public void onTimeoutDoInUI(String[] values) {
				// TODO Auto-generated method stub
				MyProgressDialog.Dismiss();
				MyUtils.showToast2(activity, getResources().getString(R.string.timeout));
			}
		};
		
		wt.myExecute(method, map, false);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (wt != null && !wt.isCancelled()) {
			wt.cancel(true);
		}
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.top_return:
			this.finish();
			break;
		case R.id.to_jobinfoview:
			Intent jIntent = new Intent(this, JobInfoActivity.class);
			jIntent.putExtra("id", infomap.get("jobid"));
			jIntent.putExtra("eid", infomap.get("entid"));
			startActivity(jIntent);
			break;
		case R.id.cr_linear:	//进入编辑界面
			Intent ccIntent = new Intent(activity, ContentEditActivity.class);
			if (intent.getBooleanExtra("ise", false)) {
				ccIntent.putExtra("type", "reply");
				ccIntent.putExtra("id", mlist.get(mlist.size() - 1).get("qid"));
			}else {
				ccIntent.putExtra("type", "consult");
				ccIntent.putExtra("id", intent.getStringExtra("id"));
			}
			startActivityForResult(ccIntent, 11);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 提交回复后刷新列表
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 11) {
			webtype = "list";
			ExecuteAsyncTask("GetJobQuestionList", mapUtil.ConsultListMap(
					intent.getStringExtra("id"), size, page));
		}
	}
}
