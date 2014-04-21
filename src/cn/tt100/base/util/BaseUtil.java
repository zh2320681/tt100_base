package cn.tt100.base.util;

import java.io.File;
import java.lang.reflect.Field;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

/**
 * 工具类
 * @author shrek
 *
 */
public class BaseUtil {
	/**
	 * 通过给定的 父View 初始化holder里面所有的View
	 * @param parentView
	 * @param holderObj
	 * @param regex  xxx_?
	 */
	public static void initViews(Context ctx,View parentView,Object holderObj,String regex){
		Class<?> clazz = holderObj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for(Field f : fields){
			if(View.class.isAssignableFrom(f.getType())){
				String idName = regex.replace("?", f.getName());
				//判断修饰
				int value = getIdValueIntoR(ctx,idName);
				f.setAccessible(true);
				try {
					f.set(holderObj, parentView.findViewById(value));
				} catch (IllegalArgumentException e) {
					BaseLog.printLog(BaseUtil.class, f.getName()+"赋值失败!");
				} catch (IllegalAccessException e) {
					BaseLog.printLog(BaseUtil.class, f.getName()+"赋值时访问失败!");
				}
			}
		}
	}
	
	
	/**
	 * 通过名字 得到id的值
	 * @param idName
	 * @return
	 */
	public static int getIdValueIntoR(Context ctx , String idName){
		return ctx.getResources().getIdentifier(idName, "id", ctx.getPackageName());
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
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
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
	 * 判断 是不是这个类
	 */
	public static <T> T judgeContextToActivity(Object obj , Class<? extends Context> activityClass){
		if(obj != null){
			Class<?> ctxClass = obj.getClass();
			if(ctxClass.hashCode() == activityClass.hashCode()){
				T futureObj = (T)obj;
				return futureObj;
			}
		}
		return null;
	}
	
	
}
