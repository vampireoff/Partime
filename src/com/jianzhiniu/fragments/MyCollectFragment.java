package com.jianzhiniu.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.jianzhiniu.activity.JobInfoActivity;
import com.jianzhiniu.activity.R;
import com.jianzhiniu.adapter.NewjoblistAdapter;
import com.jianzhiniu.utils.MyUtils;
import com.jianzhiniu.views.XListView;

public class MyCollectFragment extends Fragment implements OnClickListener, XListView.IXListViewListener{

	private Activity activity;
	
	private List<Map<String, String>> xlist = new ArrayList<Map<String,String>>();
	private XListView xListView;
	private NewjoblistAdapter xAdapter;
    private int page = 1;
    private int size = 10;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(activity).inflate(R.layout.auth_view, null);
		
        xListView = (XListView) view.findViewById(R.id.xlistview);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(true);
        xListView.setAutoLoadEnable(true);
        xListView.setXListViewListener(this);
//        xListView.setRefreshTime(getTime());

        getxlist();
        xAdapter = new NewjoblistAdapter(activity, xlist, false);
        xListView.setAdapter(xAdapter);
        xListView.setOnItemClickListener(NewjobitemClick);
        
		return view;
	}
	
	public void getxlist(){
		Map<String, String> xmap = null;
		for (int i = 0; i < 10; i++) {
			xmap = new HashMap<String, String>();
			xlist.add(xmap);
		}
	}
	
	OnItemClickListener NewjobitemClick = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			MyUtils.showToast2(activity, "µã»÷ÁË" + arg2);
			Intent jIntent = new Intent(activity, JobInfoActivity.class);
			startActivity(jIntent);
		}
	};
	
	@Override
    public void onRefresh() {
		new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                page = 1;
                xlist.clear();
                getxlist();
                xAdapter.refreshdata(xlist);
                onLoad();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
    	new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            	page ++;
            	getxlist();
                xAdapter.notifyDataSetChanged();
                onLoad();
            }
        }, 2000);
    }

    private void onLoad() {
        xListView.stopRefresh();
        xListView.stopLoadMore();
//        xListView.setRefreshTime(getTime());
    }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.top_return:
			
			break;

		default:
			break;
		}
	}
	
	
}
