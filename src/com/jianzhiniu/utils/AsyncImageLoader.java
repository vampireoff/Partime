package com.jianzhiniu.utils;

import java.util.concurrent.ExecutorService;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;

import com.jianzhiniu.activity.R;
import com.jianzhiniu.network.HttpConnect;



public class AsyncImageLoader
{
	/**
	 * 缓存Image的类，当存储Image的大小大于LruCache设定的�?�，系统自动释放内存
	 */
	private LruCache<String, Bitmap> mMemoryCache;
	
	private Context mContext;
	/**
	 * 下载Image的线程池
	 */
	private ExecutorService mImageThreadPool = null;

	public AsyncImageLoader(Context context)
	{
		this.mContext = context;
		// 获取系统分配给每个应用程序的�?大内存，每个应用系统分配32M
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int mCacheSize = maxMemory / 4;
		// 给LruCache分配1/4 8M
		mMemoryCache = new LruCache<String, Bitmap>(mCacheSize)
		{

			// 必须重写此方法，来测量Bitmap的大�?
			@Override
			protected int sizeOf(String key, Bitmap value)
			{
				return value.getRowBytes() * value.getHeight();
			}

		};

	}

	public Bitmap loadDrawable(final String imageUrl, final ImageCallback imageCallback)
	{
		if (getBitmapFromMemCache(imageUrl) != null)
		{
			return getBitmapFromMemCache(imageUrl);
		}
		final Handler handler = new Handler()
		{
			public void handleMessage(Message message)
			{
				imageCallback.imageLoaded((Bitmap) message.obj, imageUrl);
			}
		};
		new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					Bitmap bitmap = loadImageFromUrl(imageUrl);
					addBitmapToMemoryCache(imageUrl, bitmap);
					if(null == bitmap){
						bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.loadingerror);
					}
					Message message = handler.obtainMessage(0, bitmap);
					handler.sendMessage(message);
				}
				catch (Exception e)
				{

				}
			}
		}.start();
		return null;
	}

	public Bitmap loadImageFromUrl(String url)
	{

		Bitmap bitmap = null;
		try
		{
			bitmap = HttpConnect.getHttpBitmap(url);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return bitmap;
	}

	/**
	 * 添加Bitmap到内存缓�?
	 * 
	 * @param key
	 * @param bitmap
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap)
	{
		if (getBitmapFromMemCache(key) == null && bitmap != null)
		{
			mMemoryCache.put(key, bitmap);
		}
	}

	/**
	 * 从内存缓存中获取�?个Bitmap
	 * 
	 * @param key
	 * @return bitmap
	 */
	public Bitmap getBitmapFromMemCache(String key)
	{
		return mMemoryCache.get(key);
	}

}
