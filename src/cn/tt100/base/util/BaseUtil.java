package cn.tt100.base.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.WeakHashMap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;
import cn.tt100.base.R;
import cn.tt100.base.download.DLConstant;
import cn.tt100.base.download.DLHandler;
import cn.tt100.base.download.DownloadService;
import cn.tt100.base.download.Downloader;
import cn.tt100.base.download.bo.DLTask;

/**
 * 工具类
 * 
 * @author shrek
 * 
 */
public class BaseUtil {

	public static final DecimalFormat df = new DecimalFormat("#.00");

	/**
	 * 通过给定的 父View 初始化holder里面所有的View
	 * 
	 * @param parentView
	 * @param holderObj
	 * @param regex
	 *            xxx_?
	 */
	public static void initViews(Context ctx, View parentView,
			Object holderObj, String regex) {
		Class<?> clazz = holderObj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields) {
			if (View.class.isAssignableFrom(f.getType())) {
				String idName = regex.replace("?", f.getName());
				// 判断修饰
				int value = getIdValueIntoR(ctx, idName);
				f.setAccessible(true);
				try {
					f.set(holderObj, parentView.findViewById(value));
				} catch (IllegalArgumentException e) {
					ZWLogger.printLog(BaseUtil.class, f.getName() + "赋值失败!");
				} catch (IllegalAccessException e) {
					ZWLogger.printLog(BaseUtil.class, f.getName() + "赋值时访问失败!");
				}
			}
		}
	}

	/**
	 * 通过名字 得到id的值
	 * 
	 * @param idName
	 * @return
	 */
	public static int getIdValueIntoR(Context ctx, String idName) {
		return ctx.getResources().getIdentifier(idName, "id",
				ctx.getPackageName());
	}

	/**
	 * 检测网络链接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 打开文件
	 * 
	 * @param file
	 */
	public static void openFile(File file, Context context) {
		Intent intent = new Intent();

		String fName = file.getName();
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			// 文件没有后缀名
			Toast.makeText(context, "无法识别的文件类型!", Toast.LENGTH_LONG).show();
			return;
		}
		/* 获取文件的后缀名 */
//		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		// if (end.indexOf("txt") != -1 || end.indexOf("htm") != -1) {
		// intent.setClass(context, ShowTxtAndWeb.class);
		// intent.putExtra("url", file.getPath());
		// context.startActivity(intent);
		// } else {

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 设置intent的Action属性
		intent.setAction(Intent.ACTION_VIEW);
		// 获取文件file的MIME类型
		String type = getMIMEType(file);
		// 设置intent的data和Type属性。
		intent.setDataAndType(Uri.fromFile(file), type);
		try {
			context.startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(context, "该文件类型无法打开!", Toast.LENGTH_LONG).show();
		}

	}

	// 建立一个MIME类型与文件后缀名的匹配表
	static final String[][] MIME_MapTable = {
			// {后缀名， MIME类型}
			{ ".3gp", "video/3gpp" },
			{ ".apk", "application/vnd.android.package-archive" },
			{ ".asf", "video/x-ms-asf" }, { ".avi", "video/x-msvideo" },
			{ ".bin", "application/octet-stream" }, { ".bmp", "image/bmp" },
			{ ".c", "text/plain" }, { ".class", "application/octet-stream" },
			{ ".conf", "text/plain" }, { ".cpp", "text/plain" },
			{ ".doc", "application/msword" }, { ".dot", "application/msword" },

			{ ".xla", "application/vnd.ms-excel" },
			{ ".xlc", " application/vnd.ms-excel" },
			{ ".xll", "application/vnd.ms-excel" },
			{ ".xlm", " application/vnd.ms-excel" },
			{ ".xls", "application/vnd.ms-excel" },
			{ ".xlt", " application/vnd.ms-excel" },
			{ ".xlw", "application/vnd.ms-excel" },

			{ ".exe", "application/octet-stream" }, { ".gif", "image/gif" },
			{ ".gtar", "application/x-gtar" }, { ".gz", "application/x-gzip" },
			{ ".h", "text/plain" }, { ".htm", "text/html" },
			{ ".html", "text/html" }, { ".jar", "application/java-archive" },
			{ ".java", "text/plain" }, { ".jpeg", "image/jpeg" },
			{ ".jpg", "image/jpeg" }, { ".js", "application/x-javascript" },
			{ ".log", "text/plain" }, { ".m3u", "audio/x-mpegurl" },
			{ ".m4a", "audio/mp4a-latm" }, { ".m4b", "audio/mp4a-latm" },
			{ ".m4p", "audio/mp4a-latm" }, { ".m4u", "video/vnd.mpegurl" },
			{ ".m4v", "video/x-m4v" }, { ".mov", "video/quicktime" },
			{ ".mp2", "audio/x-mpeg" }, { ".mp3", "audio/x-mpeg" },
			{ ".mp4", "video/mp4" },
			{ ".mpc", "application/vnd.mpohun.certificate" },
			{ ".mpe", "video/mpeg" }, { ".mpeg", "video/mpeg" },
			{ ".mpg", "video/mpeg" }, { ".mpg4", "video/mp4" },
			{ ".mpga", "audio/mpeg" },
			{ ".msg", "application/vnd.ms-outlook" }, { ".ogg", "audio/ogg" },
			{ ".pdf", "application/pdf" }, { ".png", "image/png" },
			{ ".pps", "application/vnd.ms-powerpoint" },
			{ ".ppt", "application/vnd.ms-powerpoint" },
			{ ".prop", "text/plain" },
			{ ".rar", "application/x-rar-compressed" },
			{ ".rc", "text/plain" }, { ".rmvb", "audio/x-pn-realaudio" },
			{ ".rtf", "application/rtf" }, { ".sh", "text/plain" },
			{ ".tar", "application/x-tar" },
			{ ".tgz", "application/x-compressed" }, { ".txt", "text/plain" },

			{ ".wav", "audio/x-wav" }, { ".wma", "audio/x-ms-wma" },
			{ ".wmv", "audio/x-ms-wmv" },
			{ ".wps", "application/vnd.ms-works" }, { ".xml", "text/plain" },
			{ ".z", "application/x-compress" }, { ".zip", "application/zip" },
			{ "", "*/*" } };

	/**
	 * 根据文件后缀名获得对应的MIME类型。
	 * 
	 * @param file
	 */
	public static String getMIMEType(File file) {
		String type = "*/*";
		String fName = file.getName();
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* 获取文件的后缀名 */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "")
			return type;
		for (int i = 0; i < MIME_MapTable.length; i++) {
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	/**
	 * 将str转化为int 如果出现异常 返回0
	 * 
	 * @param str
	 * @return
	 */
	public static int parseInt(String str, int defaultValue) {
		int i = defaultValue;
		if (str != null && !str.equals("")) {
			try {
				i = Integer.parseInt(str);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				i = defaultValue;
			}
		}
		return i;
	}

	/**
	 * 判断 是不是这个Activity
	 */
	@SuppressWarnings("unchecked")
	public static <T> T judgeContextToActivity(Object obj,
			Class<? extends Context> activityClass) {
		if (obj != null) {
			Class<?> ctxClass = obj.getClass();
			if (ctxClass.hashCode() == activityClass.hashCode()) {
				
				T futureObj = (T) obj;
				return futureObj;
			}
		}
		return null;
	}

	
	/**
	 * #########################################################################
	 * ############################ 图片处理类 ###################################
	 * #########################################################################
	 */
	
	/**
	 * 通过用户指定的 大小 加载图片
	 * 
	 * @param filePath
	 * @param imgWidth
	 *            为-1采用原图大小 0 不以宽做等比缩放 其他就是缩放后的图片大小
	 * @param imgHeight
	 * @return
	 * @throws Exception
	 */
	public static Bitmap decodeBitmapWithOps(String filePath, int imgWidth,
			int imgHeight) throws Exception {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		int orgHeight = options.outHeight;
		int orgWidth = options.outWidth;
		if (imgWidth == -1) {
			imgWidth = orgWidth;
		}

		if (imgHeight == -1) {
			imgHeight = orgWidth;
		}

		int inSampleSize = 1;

		if (imgHeight == 0) {
			inSampleSize = Math.round((float) orgWidth / (float) imgWidth);
		} else if (imgWidth == 0) {
			inSampleSize = Math.round((float) orgHeight / (float) imgHeight);
		} else {
			if (orgWidth > orgHeight) {
				inSampleSize = Math
						.round((float) orgHeight / (float) imgHeight);
			} else {
				inSampleSize = Math.round((float) orgWidth / (float) imgWidth);
			}
		}
		//
		// for (int i = 0; i < 10; i++) {
		// if(inSampleSize<=(2^i)){
		// inSampleSize = 2^i;
		// break;
		// }
		// }
		options.inSampleSize = inSampleSize;

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
		return bitmap;
	}

	
	/**
	 * Bitmap → Drawable
	 */
	@SuppressWarnings("deprecation")
	public static Drawable bitmap2Drawable(Bitmap bm) {
		if (bm == null) {
			return null;
		}
		BitmapDrawable bd=new BitmapDrawable(bm);
		bd.setTargetDensity(bm.getDensity());
		return new BitmapDrawable(bm);
	}
	
	/**
	 * Drawable → Bitmap
	 */
	@SuppressWarnings("deprecation")
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		if (drawable == null) {
			return null;
		}
		// 取 drawable 的长宽
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		// 取 drawable 的颜色格式
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 建立对应 bitmap 的画布
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中
		drawable.draw(canvas);
		return bitmap;
	}
	
	/**
	 * byte[] → Bitmap
	 */
	public static Bitmap Bytes2Bimap(byte[] b) {
		if (b.length == 0) {
			return null;
		}
		return BitmapFactory.decodeByteArray(b, 0, b.length);
	}
	
	
	/**
	 * Bitmap → byte[]
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		if (bm == null) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
	
	// public static void downloadFile(Context context, DLTask mDLTask,
	// DLHandler handler){
	// if (handler != null ){
	// if (Downloader.allCallbacks == null){
	// Downloader.allCallbacks = new WeakHashMap();
	// }
	// Downloader.allCallbacks.put(paramDLTask, paramDLHandler);
	// }
	// Intent localIntent1 = new Intent(paramContext, DownloadService.class);
	// Intent localIntent2 = localIntent1.putExtra("d", paramDLTask);
	// ComponentName localComponentName =
	// paramContext.startService(localIntent1);
	// }

	/**
	 * 通过size 获取文件大小显示字符串
	 * 
	 * @param size
	 * @return
	 */
	public static String getFileSize(long size) {
		String sizeStr = "0B";
		if (size < 1024L) {
			sizeStr = size + "B";
		} else if (size >= 1024L && size < 1024 * 1024) {
			sizeStr = df.format(size / 1024.0) + "K";
		} else if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {
			sizeStr = df.format(size / (1024.0 * 1024)) + "M";
		} else {
			sizeStr = df.format(size / (1024.0 * 1024 * 1024)) + "G";
		}
		return sizeStr;
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
			os.flush();
		} catch (Exception ex) {
		}
	}

	/**
	 * 判断sd卡是否存在
	 */
	public static boolean isSdCardExist() {
		boolean isExist = false;
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			isExist = true;
		}
		return isExist;
	}

	/**
	 * 返回打开文件的意图
	 * 
	 * @param file
	 */
	public static Intent getOpenFileIntent(File file, Context context) {
		Intent intent = new Intent();

		String fName = file.getName();
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			// 文件没有后缀名
			Toast.makeText(context, "无法识别的文件类型!", Toast.LENGTH_LONG).show();
			return null;
		}
		/* 获取文件的后缀名 */
//		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 设置intent的Action属性
		intent.setAction(Intent.ACTION_VIEW);
		// 获取文件file的MIME类型
		String type = getMIMEType(file);
		// 设置intent的data和Type属性。
		intent.setDataAndType(Uri.fromFile(file), type);
		return intent;

	}

	/**
	 * 根据文件名 得到对应的 图标
	 */
	public static int getIcon(File f) {
		if (!f.isDirectory()) {
			// 文件
			String name = f.getName().toLowerCase();
			if (name.indexOf(".txt") > 0) {
				return R.drawable.mimetype_text_txt;
			} else if (name.indexOf(".doc") > 0) {
				return R.drawable.mimetype_office_doc;
			} else if (name.indexOf(".apk") > 0) {
				return R.drawable.mimetype_app_apk;
			} else if (name.indexOf(".htm") > 0) {
				return R.drawable.mimetype_htm_html;
			} else if (name.indexOf(".png") > 0 || name.indexOf(".jpg") > 0
					|| name.indexOf(".bmp") > 0 || name.indexOf(".gif") > 0) {
				return R.drawable.mimetype_img;
			} else if (name.indexOf(".pdf") > 0) {
				return R.drawable.mimetype_office_pdf;
			} else if (name.indexOf(".mp3") > 0 || name.indexOf(".wma") > 0) {
				return R.drawable.mimetype_sound;
			} else if (name.indexOf(".mp4") > 0 || name.indexOf(".rm") > 0
					|| name.indexOf(".rmvb") > 0) {
				return R.drawable.mimetype_video;
			}
		} else {
			// 文件夹
			return R.drawable.mimetype_folder;
		}

		return R.drawable.mimetype_null;
	}

	/**
	 * 得到一个可用的缓存目录(如果外部可用使用外部,否则内部)。
	 * 
	 * @param context
	 *            上下文信息
	 * @param uniqueName
	 *            目录名字
	 * @return 返回目录名字
	 */
	public static File getDiskCacheDir(Context context, String uniqueName) {
		// 检查是否安装或存储媒体是内置的,如果是这样,试着使用
		// 外部缓存 目录
		// 否则使用内部缓存 目录
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) || !isExternalStorageRemovable() ? getExternalCacheDir(
				context).getPath()
				: context.getCacheDir().getPath();

		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * 检查如果外部存储器是内置的或是可移动的。
	 * 
	 * @return 如果外部存储是可移动的(就像一个SD卡)返回为 true,否则false。
	 */
	public static boolean isExternalStorageRemovable() {
		if (AndroidVersionCheckUtils.hasGingerbread()) {
			return Environment.isExternalStorageRemovable();
		}
		return true;
	}

	/**
	 * 获得外部应用程序缓存目录
	 * 
	 * @param context
	 *            上下文信息
	 * @return 外部缓存目录
	 */
	public static File getExternalCacheDir(Context context) {
		if (AndroidVersionCheckUtils.hasFroyo()) {
			// return context.getExternalCacheDir();
			return Environment.getExternalStorageDirectory();
		}
		final String cacheDir = "/data/data/" + context.getPackageName()
				+ "/cache/";
		// Environment.getExternalStorageDirectory().getPath()
		return new File(cacheDir);
	}

	public static void downloadFile(Context context, DLTask task,
			DLHandler handler) {
		if (handler != null) {
			if (Downloader.allCallbacks == null)
				Downloader.allCallbacks = new WeakHashMap<DLTask, DLHandler>();
			Downloader.allCallbacks.put(task, handler);
			handler.preDownloadDoing(task);
		}
		Intent intent = new Intent(context, DownloadService.class);
		intent.putExtra(DLConstant.DL_TASK_OBJ, task);
		context.startService(intent);
	}
}
