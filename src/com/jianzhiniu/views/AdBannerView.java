package com.jianzhiniu.views;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jianzhiniu.activity.R;
import com.jianzhiniu.entity.AdInfo;
import com.jianzhiniu.utils.AsyncImageLoader;
import com.jianzhiniu.utils.ImageCallback;


public class AdBannerView extends FrameLayout
{

	// 轮播图图片数�?
	private final static int IMAGE_COUNT = 5;
	// 自动轮播的时间间�?
	private final static int TIME_INTERVAL = 5;
	// 自动轮播启用�?�?
	private final static boolean isAutoPlay = true;

	// 自定义轮播图的资�?
	private List<AdInfo> adList;
	// 放轮播图片的ImageView 的list
	private List<ImageView> imageViewsList;
	// 放圆点的View的list
	private List<View> dotViewsList;

	private ViewPager viewPager;
	// 当前轮播�?
	private int currentItem = 0;
	// 定时任务
	private ScheduledExecutorService scheduledExecutorService;

	private Context context;

	private TextView mAdDescTv;

	private AsyncImageLoader asyncImageLoader;
	// Handler
	private Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			viewPager.setCurrentItem(currentItem, true);
		}

	};

	public AdBannerView(Context context)
	{
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public AdBannerView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public AdBannerView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		this.context = context;

	}

	private Handler mHandler;
	private int clickFlag = 0;

	public int getClickFlag()
	{
		return clickFlag;
	}

	public void setClickFlag(int clickFlag)
	{
		this.clickFlag = clickFlag;
	}

	public void init(Handler mHandler, List<AdInfo> adList)
	{
		this.mHandler = mHandler;
		this.adList = adList;
		initData();
		if (isAutoPlay)
		{
			startPlay();
		}
	}

	/**
	 * �?始轮播图切换
	 */
	private void startPlay()
	{
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, 4, TimeUnit.SECONDS);
	}

	/**
	 * 停止轮播图切�?
	 */
	private void stopPlay()
	{
		scheduledExecutorService.shutdown();
	}

	/**
	 * 初始化相关Data
	 */
	private void initData()
	{
		asyncImageLoader = new AsyncImageLoader(context);
		imageViewsList = new ArrayList<ImageView>();
		dotViewsList = new ArrayList<View>();

		// �?步任务获取图�?
		// new GetListTask().execute("");
		initUI(context);
	}

	/**
	 * 初始化Views等UI
	 */
	private void initUI(Context context)
	{
		if (adList == null || adList.size() == 0) return;

		View rootView = LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this, true);

		mAdDescTv = (TextView) rootView.findViewById(R.id.iv_bottomNavPoint);
		LinearLayout dotLayout = (LinearLayout) rootView.findViewById(R.id.dotLayout);
		dotLayout.removeAllViews();

		// 热点个数与图片特殊相�?
		for (int i = 0; i < adList.size(); i++)
		{
			ImageView view = new ImageView(context);
			view.setTag(adList.get(i).getAdvImg());
			if (i == 0) // 给一个默认图
			view.setBackgroundResource(R.drawable.imgloading);
			view.setScaleType(ScaleType.CENTER_CROP);
			imageViewsList.add(view);

			ImageView dotView = new ImageView(context);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.leftMargin = 4;
			params.rightMargin = 4;
			dotLayout.addView(dotView, params);
			dotViewsList.add(dotView);
		}

		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setFocusable(true);
		viewPager.setAdapter(new MyPagerAdapter());
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
		
		//ViewPagerScroller用于viewPage切换的过�?
		ViewPagerScroller mViewPagerScroller = new ViewPagerScroller(context);
		mViewPagerScroller.initViewPagerScroll(viewPager);
	}

	/**
	 * 填充ViewPager的页面�?�配�?
	 * 
	 */
	private class MyPagerAdapter extends PagerAdapter
	{

		@Override
		public void destroyItem(View container, int position, Object object)
		{
			// TODO Auto-generated method stub
			// ((ViewPag.er)container).removeView((View)object);
			((ViewPager) container).removeView(imageViewsList.get(position));
		}

		@Override
		public Object instantiateItem(View container, int position)
		{
			final int p = position;
			ImageView imageView = imageViewsList.get(position);
			String picUrl = imageView.getTag().toString();
			final ImageView childImg = imageViewsList.get(position);
			childImg.setTag(picUrl);
			Bitmap cachedImage = asyncImageLoader.loadDrawable(picUrl, new ImageCallback()
			{
				public void imageLoaded(Bitmap imageDrawable, String imageUrl)
				{
					if (childImg != null && null != imageDrawable)
					{
						childImg.setImageBitmap(imageDrawable);
					}
				}
			});
			if (cachedImage == null)
			{

				childImg.setImageResource(R.drawable.imgloading);

			}
			else
			{
				childImg.setImageBitmap(cachedImage);
			}

			((ViewPager) container).addView(imageViewsList.get(position));

			imageView.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					Message msg = new Message();
					msg.what = clickFlag;
					msg.obj = p;
					mHandler.sendMessage(msg);
				}
			});

			return imageViewsList.get(position);
		}

		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return imageViewsList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public Parcelable saveState()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void finishUpdate(View arg0)
		{
			// TODO Auto-generated method stub

		}

	}

	/**
	 * ViewPager的监听器 当ViewPager中页面的状�?�发生改变时调用
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener
	{

		boolean isAutoPlay = false;

		@Override
		public void onPageScrollStateChanged(int arg0)
		{
			// TODO Auto-generated method stub
			switch (arg0)
			{
				case 1:// 手势滑动，空闲中
					isAutoPlay = false;
					break;
				case 2:// 界面切换�?
					isAutoPlay = true;
					break;
				case 0:// 滑动结束，即切换完毕或�?�加载完�?
						// 当前为最后一张，此时从右向左滑，则切换到第一�?
					if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isAutoPlay)
					{
						viewPager.setCurrentItem(0);
					}
					// 当前为第�?张，此时从左向右滑，则切换到�?后一�?
					else if (viewPager.getCurrentItem() == 0 && !isAutoPlay)
					{
						viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
					}
					break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int pos)
		{
			mAdDescTv.setText(adList.get(pos).getAdvDesc());
			currentItem = pos;
			for (int i = 0; i < dotViewsList.size(); i++)
			{
				if (i == pos)
				{
					((View) dotViewsList.get(pos)).setBackgroundResource(R.drawable.icon_circle_focus_on);
				}
				else
				{
					((View) dotViewsList.get(i)).setBackgroundResource(R.drawable.icon_circle_focus_off);
				}
			}
		}

	}

	/**
	 * 执行轮播图切换任�?
	 * 
	 */
	private class SlideShowTask implements Runnable
	{

		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			synchronized (viewPager)
			{
				if (null != imageViewsList && imageViewsList.size() > 1)
				{
					currentItem = (currentItem + 1) % imageViewsList.size();
					handler.obtainMessage().sendToTarget();
				}
			}
		}

	}

	/**
	 * �?毁ImageView资源，回收内�?
	 * 
	 */
	private void destoryBitmaps()
	{

		for (int i = 0; i < IMAGE_COUNT; i++)
		{
			ImageView imageView = imageViewsList.get(i);
			Drawable drawable = imageView.getDrawable();
			if (drawable != null)
			{
				// 解除drawable对view的引�?
				drawable.setCallback(null);
			}
		}
	}

}