package com.jianzhiniu.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.jianzhiniu.activity.R;
/**
 * 图片处理工具类
 * @author Administrator
 *
 */
public class ImageUtil {
//	private static final Logger logger = LoggerFactory
//			.getLogger(ImageUtil.class);
//	ImageFileCache imageFileCache = new ImageFileCache();
	private static ImageUtil imageUtil = new ImageUtil();
	private String imageName;
	private File updateFile;
	
	
	public static ImageUtil getInstance() {
		return imageUtil;
	}
	
	/***
	 * 根据图片的网络路径得到图片的 byte数组
	 */
	public static byte[] getBitMap(String imagePath)
			throws OutOfMemoryError {
		try {
			URL url = new URL(imagePath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}
			is.close();
			outputStream.close();
			conn.disconnect();
			return outputStream.toByteArray();
		} catch (Exception ex) {
			Log.i("gettouxiang", ex.getMessage().toString());
		}
		return null;
	}
	/**
	 * 将byte数组转为bitmap并压缩,     **放线程里执行**
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap getBitmapFromPathWH(String path,int width,int height){
		Bitmap bitmap = null;
		if (path.equals("") || path == null) {
			return null;
		}
		/*
	            如何设置恰当的inSampleSize是解决该问题的关键之一。BitmapFactory.Options提供了另一个成员inJustDecodeBounds。
	           设置inJustDecodeBounds为true后，decodeFile并不分配空间，但可计算出原始图片的长度和宽度，即opts.width和opts.height。
	           有了这两个参数，再通过一定的算法，即可得到一个恰当的inSampleSize。*/
		byte[] data = getBitMap(path);
		
		if (data != null) {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(data, 0, data.length, opts);
			opts.inSampleSize = 1;
			if(opts.outHeight > height || opts.outWidth > width){
				if(opts.outWidth > opts.outHeight){
					opts.inSampleSize = Math.round((float)opts.outHeight / (float)height);
				}else {
					opts.inSampleSize = Math.round((float)opts.outWidth / (float)width);
				}
			}
			opts.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
			bitmap = getBitmapThumbnail(bitmap, width, height);
		}
		
		if(bitmap != null){
			return compressImage(bitmap);
		}else {
			return bitmap;
		}
	}
	/**
	 * 比例压缩
	 * @param bmp
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap getBitmapThumbnail(Bitmap bmp,int width,int height){
		Bitmap bitmap = null;
		if(bmp != null ){
			int bmpWidth = bmp.getWidth();
			int bmpHeight = bmp.getHeight();
			if(width != 0 && height !=0){
				Matrix matrix = new Matrix();
				float scaleWidth = ((float) width / bmpWidth);
				float scaleHeight = ((float) height / bmpHeight);
				matrix.postScale(scaleWidth, scaleHeight);
				bitmap = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);
			}else{
				bitmap = bmp;
			}
		}
		return bitmap;
	}
	
	/**  裁剪图片正中心
	   * @param bitmap      原图
	   * @param edgeLength  希望得到的正方形部分的边长
	   * @return  缩放截取正中部分后的位图。
	   */
	  public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength)
	  {
	   if(null == bitmap || edgeLength <= 0)
	   {
	    return  null;
	   }
	                                                                                 
	   Bitmap result = bitmap;
	   int widthOrg = bitmap.getWidth();
	   int heightOrg = bitmap.getHeight();
	                                                                                 
	   if(widthOrg > edgeLength || heightOrg > edgeLength)
	   {
	    //压缩到一个最小长度是edgeLength的bitmap
	    int longerEdge = (int)(edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
	    int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
	    int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
	    Bitmap scaledBitmap;
	                                                                                  
	          try{
	           scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
	          }
	          catch(Exception e){
	           return null;
	          }
	                                                                                       
	       //从图中截取正中间的正方形部分。
	       int xTopLeft = (scaledWidth - edgeLength) / 2;
	       int yTopLeft = (scaledHeight - edgeLength) / 2;
	                                                                                     
	       try{
	        result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
	        scaledBitmap.recycle();
	       }
	       catch(Exception e){
	        return null;
	       }       
	   }
	                                                                                      
	   return compressImage(result);
	  }
	
	/**
	 * 圆角图片
	 * @param source
	 * @param radius
	 * @return
	 */
	public static Bitmap roundCorners(final Bitmap bitmap, final float pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), android.graphics.Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
	
	/**
	 * 64位编码
	 * @param image
	 * @return
	 */
	public static String compressImage64(Bitmap image) {  
		if (image == null) {
			return "";
		}
		String string = "";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
		string = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
		return string.replace("+", "%2B"); 
	}  
	
	public Bitmap returnBitMap(String url) {
		
		URL fileUrl = null;
		Bitmap bitmap = null;
		InputStream is = null;
		try {
			fileUrl = new URL(url);
		} catch (Exception ex) {
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) fileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
		} catch (Exception e) {
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}
	
	/**
	 * 比例压缩
	 * @param image
	 * @return
	 */
	public static Bitmap getBitMap2(Bitmap image){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();         
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
		int opp = 80;
		while( (baos.toByteArray().length / 1024)>200) {//判断如果图片大于500k,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出    
			baos.reset();//重置baos即清空baos  
			opp -= 10;
			image.compress(Bitmap.CompressFormat.JPEG, opp, baos);//这里压缩50%，把压缩后的数据存放到baos中  
		}  
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());  
		BitmapFactory.Options newOpts = new BitmapFactory.Options();  
		//开始读入图片，此时把options.inJustDecodeBounds 设回true了  
		newOpts.inJustDecodeBounds = true;  
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
		newOpts.inJustDecodeBounds = false;  
//	    int w = newOpts.outWidth;  
//	    int h = newOpts.outHeight;  
//	    float hh = Config.width * 4 / 8;
//		float ww = Config.width / 2;
//		int be = (int) ((w/ww + h/hh) / 2);
//	    if (be < 1)  
//	        be = 1;  
		if((baos.toByteArray().length / 1024)>1000)
			newOpts.inSampleSize = 2;//设置缩放比例  
		else {
			newOpts.inSampleSize = 1;//设置缩放比例  
		}
		//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
		isBm = new ByteArrayInputStream(baos.toByteArray());  
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
		return bitmap;//压缩好比例大小后再进行质量压缩  
		
	}
	/**
	 * 质量压缩到100k以内
	 * @param image
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image) {  
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
		int options = 100;  
		while ( (baos.toByteArray().length / 1024)>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
			baos.reset();//重置baos即清空baos  
			options -= 10;//每次都减少10  
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
		}  
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
		return bitmap;  
	}  
	/**
	 * 判断文件是否存在
	 */
	public void doFile(String path) {
		File file = new File(path);
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			updateFile = new File(file + "/" + imageName);
			if (!file.exists()) {
				file.mkdirs();
			}
			if (!updateFile.exists()) {
				try {
					updateFile.createNewFile();
				} catch (IOException e) {
				}
			}
			
		}
	}
	
	/**
	 * 保存网络图片到本地
	 * 
	 * @param imgName
	 *            图片名称
	 * @param networkPath
	 *            网络图片路径
	 * @param diskPath
	 *            本地存储路径
	 * @return 是否成功
	 */
	public static void MysaveBitmapToDisk(String imgName, Bitmap bmp,
			String diskPath) {
//		logger.info("save bitmp to disk = " + diskPath + imgName);
		try {
//			Bitmap bmp = getBitMap(networkPath, false);
			if (bmp != null) {
				File file = new File(diskPath);
				if (!file.exists()) {
					file.mkdirs();
				}
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(diskPath + imgName));
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
				bos.flush();
				bos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 保存bitmap图片到本地二维码文件夹
	 * 
	 * @param imgName
	 *            图片名称
	 * @param bmp
	 *            bitmap
	 * @param diskPath
	 *            本地保存路径
	 * @return
	 */
	public boolean saveBitmapToDisk(String imgName, Bitmap bmp, String diskPath) {
		File file = new File(Environment.getExternalStorageDirectory(),
				diskPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		File imageFile = new File(file, imgName);
		try {
			imageFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(imageFile);
			bmp.compress(CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 获得一张本地的图片
	 * 
	 * @param path
	 *            本地图片路径
	 * @return bitmap 图片
	 */
	public static Bitmap getDiskBitmap(String path) {
		Bitmap bmp = null;
		try {
			File file = new File(path);
			if (file.exists()) {
				bmp = BitmapFactory.decodeFile(path);
			}
		} catch (OutOfMemoryError ex) {
//			logger.info("get the start_page out of the memeory");
		} catch (Exception e) {
//			logger.info("get disk image bitmap error=" + e.getMessage());
		}
		return bmp;
	}
	
	/**
	 * 从网络上获取图片，如果图片在本地存在的话就直接拿，如果不存在再去服务器上下载图片
	 * 
	 * @param networkPath
	 *            图片的网络地址
	 * @param cachePath
	 *            图片的本地缓存位置 （SD卡中的） 为null时默认为 "style13/imageCache"
	 * @return 图片的URI
	 * @throws Exception
	 */
	public Uri getImageURI(String networkPath, String cachePath) {
		try {
			// 创建缓存目录
			File cache = null;
			if (cachePath != null) {
				cache = new File(Environment.getExternalStorageDirectory(),
						cachePath);
			} else {
				cache = new File(Environment.getExternalStorageDirectory(),
						"style13/imageCache");
			}
			if (!cache.exists()) {
				cache.mkdirs();
			}
			
			String name = networkPath.substring(networkPath.lastIndexOf("/"));
			File file = new File(cache, name);
			// 如果图片存在本地缓存目录，则不去服务器下载
			if (file.exists()) {
				Log.i("image cache", "disk exists image");
				return Uri.fromFile(file);
			} else {
				Log.i("image cache", "disk not exists image");
				Log.i("image path", networkPath);
				// 从网络上获取图片
				URL url = new URL(networkPath);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				if (conn.getResponseCode() == 200) {
					InputStream is = conn.getInputStream();
					FileOutputStream fos = new FileOutputStream(file);
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
					}
					is.close();
					fos.close();
					// 返回一个URI对象
					return Uri.fromFile(file);
				}
			}
		} catch (Exception e) {
//			logger.info("获取网络图片error=" + e.getMessage());
		}
		return null;
	}
	
	/**
	 * 从网络上获取图片，如果图片在本地存在的话就直接拿，如果不存在再去服务器上下载图片
	 * 
	 * @param networkPath
	 *            图片的网络地址
	 * @param cachePath
	 *            图片的本地缓存位置 （SD卡中的） 为null时默认为 "style13/imageCache"
	 * @return 图片的SD卡path
	 * @throws Exception
	 */
	public String getImageURI2(String networkPath, String cachePath) {
		try {
			// 创建缓存目录
			File cache = null;
			if (cachePath != null) {
				cache = new File(Environment.getExternalStorageDirectory(),
						cachePath);
			} else {
				cache = new File(Environment.getExternalStorageDirectory(),
						"style13/imageCache");
			}
			if (!cache.exists()) {
				cache.mkdirs();
			}
			
			String name = networkPath.substring(networkPath.lastIndexOf("/"));
			File file = new File(cache, name);
			// 如果图片存在本地缓存目录，则不去服务器下载
			if (file.exists()) {
				Log.i("image cache", "disk exists image");
				Log.i("image cache", "file.path" + file.getPath());
				return file.getPath();
			} else {
				Log.i("image cache", "disk not exists image");
				Log.i("image path", networkPath);
				// 从网络上获取图片
				URL url = new URL(networkPath);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				if (conn.getResponseCode() == 200) {
					InputStream is = conn.getInputStream();
					FileOutputStream fos = new FileOutputStream(file);
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
					}
					is.close();
					fos.close();
					return file.getPath();
				}
			}
		} catch (Exception e) {
//			logger.info("获取网络图片error=" + e.getMessage());
		}
		return null;
	}
	
	/**
	 * 从网络上获取图片，如果图片在本地存在的话就直接拿，如果不存在再去服务器上下载图片
	 * 
	 * @param networkPath
	 *            图片的网络地址
	 * @param cachePath
	 *            图片的本地缓存位置 （SD卡中的） 为null时默认为 "style13/imageCache"
	 * 
	 * @param name
	 *            保存名称，用于匹配是否存在
	 * @return 图片的URI
	 * @throws Exception
	 */
	public Uri getImageURI2(String networkPath, String cachePath, String name) {
		try {
			// 创建缓存目录
			File cache = null;
			if (cachePath != null) {
				cache = new File(Environment.getExternalStorageDirectory(),
						cachePath);
			} else {
				cache = new File(Environment.getExternalStorageDirectory(),
						"style13/imageCache");
			}
			if (!cache.exists()) {
				cache.mkdirs();
			}
			
			// String name =
			// networkPath.substring(networkPath.lastIndexOf("/"));
			File file = new File(cache, name);
			// 如果图片存在本地缓存目录，则不去服务器下载
			if (file.exists()) {
				Log.i("image cache", "disk exists image");
				return Uri.fromFile(file);
			} else {
				Log.i("image cache", "disk not exists image");
				Log.i("image path", networkPath);
				// 从网络上获取图片
				URL url = new URL(networkPath);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				if (conn.getResponseCode() == 200) {
					InputStream is = conn.getInputStream();
					FileOutputStream fos = new FileOutputStream(file);
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
					}
					is.close();
					fos.close();
					// 返回一个URI对象
					return Uri.fromFile(file);
				}
			}
		} catch (Exception e) {
//			logger.info("获取网络图片error=" + e.getMessage());
		}
		return null;
	}
	
	/**
	 * 读取路径中的图片，缩放，然后保存为jpg格式
	 * 
	 * @param path
	 *            图片路径（本地）
	 * @param sampleSize
	 *            缩放比
	 * @return
	 */
	public String saveBefore(String path, int sampleSize) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高
		Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回bm为空
		options.inJustDecodeBounds = false;
		// 计算缩放比
		options.inSampleSize = sampleSize;
		// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
		bitmap = BitmapFactory.decodeFile(path, options);
//		return saveJPGE_After(bitmap, path);
		return "";
	}
	
	/**
	 * 读取路径中的图片，缩放指定倍数并调整图片位置，然后保存为jpg格式
	 * 
	 * @param path
	 *            图片的路径
	 * @param size
	 *            缩放指定倍数
	 * @return 图片的路径
	 */
	public String saveBefore22(String path, float size) {
		try {
			int degree = 0;
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 3;
			Bitmap bitmap = BitmapFactory.decodeFile(path, options);
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Matrix matrix = new Matrix();
			matrix.postRotate(degree);
			matrix.postScale(size, size);
			Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix,
					true);
			bitmap.recycle();
			
//			return saveJPGE_After(newbmp, path);
		} catch (Exception e) {
		}
		return path;
	}
	
	/**
	 * 读取uri的图片，缩放指定倍数并调整图片位置，然后以jpg格式保存到path中
	 * 
	 * @param uri
	 *            图片在相册中的uri
	 * @param path
	 *            图片的保存路径
	 * @return
	 */
	public String saveBefore33(String uri, String path) {
		float size = 0;
		String imgName = "/albumSendTopic.jpg";
		String newPath = null;
		
		if (checkSize(uri, 1)) {
			size = (float) 0.25;
		} else {
			size = (float) 0.5;
		}
		
		try {
			int degree = 0;
			ExifInterface exifInterface = new ExifInterface(uri);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
			Bitmap bitmap = BitmapFactory.decodeFile(uri);
			
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Matrix matrix = new Matrix();
			matrix.postRotate(degree);
			matrix.postScale(size, size);
			Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix,
					true);
			bitmap.recycle();
			
			File file = null;
			if (path != null) {
				file = new File(Environment.getExternalStorageDirectory(), path);
				newPath = path + imgName;
			} else {
				file = new File(Environment.getExternalStorageDirectory(),
						"style13/imageCache");
				newPath = Environment.getExternalStorageDirectory().toString()
						+ "/style13/imageCache" + imgName;
			}
			
			if (!file.exists()) {
				file.mkdirs();
			}
			
			File file2 = new File(newPath);
			FileOutputStream out = new FileOutputStream(file2);
			
			if (newbmp.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
				out.flush();
				out.close();
			}
			newbmp.recycle();
			return newPath;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 判断文件是否存在
	 * @param uri
	 * @return
	 */
	public static boolean isHave(String uri){
		File file = new File(uri);
		if (file.exists()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 保存bitmap为PNG
	 * 
	 * @param bitmap
	 * @param path
	 *            保存的路径
	 * @return 保存后图片的路径
	 */
	public static void savePNG(Bitmap bitmap, String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
				out.flush();
				out.close();
			}
//			bitmap.recycle();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 缩放图片为屏幕的宽度(小于150 ，放大到150)
	 * 
	 * @param bitmap
	 * @param windowWinth
	 *            屏幕宽度
	 * @return
	 */
	public Bitmap zoomBitmap2(Bitmap bitmap, int windowWinth) {
		try {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			
			Log.i("image handling", "w = " + w + "  h = " + h);
			
			if (w < 150) { // 小于150 ，放大到150
				Matrix matrix = new Matrix();
				float scaleWidth = ((float) 150 / w);
				float scaleHeight = ((float) 150 / w);
				matrix.postScale(scaleWidth, scaleHeight);
				Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix,
						true);
				return newbmp;
			} else { // 大于150，缩放到屏幕大小
				Matrix matrix = new Matrix();
				float scaleWidth = ((float) windowWinth / w);
				float scaleHeight = ((float) windowWinth / w);
				
				matrix.postScale(scaleWidth, scaleHeight);
				Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix,
						true);
				return newbmp;
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * 缩放图片为屏幕的宽度(不管图片大小)
	 * 
	 * @param bitmap
	 * @param windowWinth
	 *            屏幕宽度
	 * @return
	 */
	public Bitmap zoomBitmap3(Bitmap bitmap, int windowWinth) {
		try {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			
			Log.i("image handling", "w = " + w + "  h = " + h);
			
			Matrix matrix = new Matrix();
			float scaleWidth = ((float) windowWinth / w);
			float scaleHeight = ((float) windowWinth / w);
			
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix,
					true);
			return newbmp;
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * 缩放图片为指定的高度
	 * 
	 * @param bitmap
	 * @param width
	 *            0=不缩放
	 * @param height
	 *            0=不缩放
	 * @return
	 */
	public Bitmap zoomBitmap2Height(Bitmap bitmap, int height) {
		try {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Log.i("image handling", "w = " + w + "  h = " + h + " height = "
					+ height);
			
			Matrix matrix = new Matrix();
			float scaleWidth = ((float) height / h);
			float scaleHeight = ((float) height / h);
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix,
					true);
			
			return newbmp;
		} catch (Exception e) {
		}
		return bitmap;
	}
	
	/**
	 * 验证图片大小是否大于指定大小
	 * 
	 * @param path
	 *            图片的路径
	 * @param size
	 *            指定大小 MB
	 * @return 是否大于
	 */
	public boolean checkSize(String path, float size) {
		InputStream is = null;
		try {
			is = new FileInputStream(path);
			float s = is.available();
			Log.i("image size = ", s / 1024 + "kb");
			Log.i("image size = ", s / 1024 / 1024 + "mb");
			
			return s / 1024 / 1024 > size;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
			}
		}
		return false;
	}
	
	/**
	 * 将drawable处理为圆角bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public Bitmap getRoundBitmap(Drawable drawable) {
		Bitmap bmp = drawableToBitmap(drawable);
		
		if (bmp != null) {
			int w = bmp.getWidth();
			int pixels = w / 10;
			return toRoundCorner(bmp, pixels);
		} else {
			return null;
		}
	}
	
	/**
	 * drawable to bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable != null) {
			Bitmap bitmap = Bitmap
					.createBitmap(
							drawable.getIntrinsicWidth(),
							
							drawable.getIntrinsicHeight(),
							
							drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
									
									: Bitmap.Config.RGB_565);
			
			Canvas canvas = new Canvas(bitmap);
			
			// canvas.setBitmap(bitmap);
			
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			
			drawable.draw(canvas);
			
			return bitmap;
		} else {
			return null;
		}
		
	}
	
	/***
	 * 根据传入地址返回图像资源ID
	 */
	public int getDrawId(String icon) {
		int i = 0;
		try {
			Field field = R.drawable.class.getField(icon);
			i = field.getInt(new R.drawable());
		} catch (Exception ex) {
		}
		return i;
	}
	
	
	/**
	 * 圆角处理图片
	 * 
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), android.graphics.Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
	
	/*************
	 * setimagebitmap适应控件大小
	 * 
	 * @param target
	 * @param TARGET_WIDTH
	 * @param TARGET_HEIGHT
	 * @return
	 */
	public  Bitmap zoomBitmap(Bitmap target, int TARGET_WIDTH,
			int TARGET_HEIGHT) {
		int width = target.getWidth();
		int height = target.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) TARGET_WIDTH) / width;
		float scaleHeight = ((float) TARGET_HEIGHT) / height;
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap result = Bitmap.createBitmap(target, 0, 0, width, height,
				matrix, true);
		return result;
	}
	
	/*********
	 * 比较图片大小，如果长度比较长，那么按照长度来适配，如果高度比较高，按照高度来兼容
	 */
	public  Bitmap toAutoSize(Bitmap image, float ww, float hh) {
		if (image != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
			newOpts.inJustDecodeBounds = true;
			newOpts.inJustDecodeBounds = false;
			int w = newOpts.outWidth;
			int h = newOpts.outHeight;
//			logger.info("得到的hh的值为" + hh + "；ww的值为" + ww);
			// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
			if (w < ww && w >= h) {
				float be = ww / w;
				h = (int) be * h;
				image = zoomBitmap(image, (int) ww, h);
			} else if (w < h && h <= hh) {
				// 如果高度高的话根据宽度固定大小缩放
				float be = hh / h;
				w = (int) be * w;
				image = zoomBitmap(image, w, (int) hh);
			}
		} else {
//			logger.error("要调整的图片为null的");
		}
		return image;
	}
	
	/*********
	 * 根据宽度来适应大小
	 */
	public  Bitmap toAutoSizeWithWid(Bitmap image, float ww, float hh) {
		if (image != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
			newOpts.inJustDecodeBounds = true;
			newOpts.inJustDecodeBounds = false;
			int w = newOpts.outWidth;
			int h = newOpts.outHeight;
			if (w < ww) {
				float be = ww / w;
				h = (int) be * w;
				image = zoomBitmap(image, (int) ww, h);
			} else {
				float be = w / ww;
				h = (int) (hh * be);
				image = zoomBitmap(image, (int) ww, h);
			}
		} else {
//			logger.error("要调整的图片为null的");
		}
		return image;
	}
	
	// 将图片转换成灰色的图片
	public  Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();
		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}
}