package com.jianzhiniu.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jianzhiniu.activity.LoginActivity;
import com.jianzhiniu.activity.MyApplyActivity;
import com.jianzhiniu.activity.MyJobActivity;
import com.jianzhiniu.activity.NewMessageActivity;
import com.jianzhiniu.activity.R;
import com.jianzhiniu.config.Myconfig;
import com.jianzhiniu.views.MyAlertDialog;
import com.jianzhiniu.views.MyAlertDialog.AlertCallback;

/**
 * –°÷˙ ÷
 * @author Administrator
 *
 */
public class AssistantFragment extends Fragment implements OnClickListener{

	private Activity activity;
	private ListView listView;
	private List<String> list = new ArrayList<String>();
	private TextView itemtext;
	private ImageView itemimg, reddot;
	private boolean isE;
	private Intent intent;
	private Myadapter myadapter;
	private MyAlertDialog myAlertDialog;
	
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
		myadapter = new Myadapter();
		myAlertDialog = new MyAlertDialog(activity, callback);
		intent = activity.getIntent();
		if (intent.getBooleanExtra("type", false)) {
			isE = true;
		}
	}
	
	AlertCallback callback = new AlertCallback() {
		
		@Override
		public void ybutton() {
			// TODO Auto-generated method stub
			Intent lintent = new Intent(activity, LoginActivity.class);
			lintent.putExtra("flag", "p");
			startActivity(lintent);
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(activity).inflate(R.layout.assistantfragment, null);
		listView = (ListView)view.findViewById(R.id.assist_listview);
		
		list = Arrays.asList(getResources().getStringArray(R.array.assistlist));
		listView.setAdapter(new Myadapter());
		listView.setOnItemClickListener(itemClickListener);
		return view;
	}
	
	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			switch (arg2) {
			case 0:
				Intent nIntent = new Intent(activity, NewMessageActivity.class);
				nIntent.putExtra("type", "notice");
				startActivity(nIntent);
				break;
			case 1:
				if (Myconfig.isLogin) {
					
					Intent mIntent = new Intent(activity, NewMessageActivity.class);
					mIntent.putExtra("type", "message");
					mIntent.putExtra("ise", isE);
					startActivity(mIntent);
					Myconfig.isRead = false;
					myadapter.refresh();
					
				}else {
					myAlertDialog.showMyAlertDialog(R.string.loginfirst);
				}
				break;
			case 2:
				if (Myconfig.isLogin) {
					
					Intent aIntent = new Intent(activity, MyApplyActivity.class);
					aIntent.putExtra("ise", isE);
					startActivity(aIntent);
				}else {
					myAlertDialog.showMyAlertDialog(R.string.loginfirst);
				}
				break;
			case 3:
				if (Myconfig.isLogin) {
					
					Intent mjIntent = new Intent(activity, MyJobActivity.class);
					mjIntent.putExtra("ise", isE);
					startActivity(mjIntent);
				}else {
					myAlertDialog.showMyAlertDialog(R.string.loginfirst);
				}
				break;

			default:
				break;
			}
		}
	};
	
	public void onResume() {
		super.onResume();
		if (Myconfig.isRead) {
			myadapter.refresh();
		}
	};
	
	public class Myadapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (isE) {
				return list.size() - 2;
			}else {
				return list.size();
			}
		}
		
		public void refresh(){
			notifyDataSetChanged();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = LayoutInflater.from(activity).inflate(R.layout.assistlist_item, null);
			itemtext = (TextView)convertView.findViewById(R.id.assist_text);
			itemimg = (ImageView)convertView.findViewById(R.id.assist_img);
			reddot = (ImageView)convertView.findViewById(R.id.areddot);
			
			switch (position) {
			case 0:
				itemimg.setImageResource(R.drawable.assist1);
				reddot.setVisibility(View.GONE);
				break;
			case 1:
				itemimg.setImageResource(R.drawable.assist2);
				if (Myconfig.isRead) {
					reddot.setVisibility(View.VISIBLE);
				}else {
					reddot.setVisibility(View.GONE);
					notifyDataSetChanged();
				}
				break;
			case 2:
				itemimg.setImageResource(R.drawable.assist3);
				reddot.setVisibility(View.GONE);
				break;
			case 3:
				itemimg.setImageResource(R.drawable.assist4);
				reddot.setVisibility(View.GONE);
				break;

			default:
				break;
			}
			
			itemtext.setText(list.get(position));
			return convertView;
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
