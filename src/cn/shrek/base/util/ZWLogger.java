package cn.shrek.base.util;

import android.util.Log;
import cn.shrek.base.ZWApplication;
import cn.shrek.base.util.logger.ILogger;

public class ZWLogger{
	public static boolean isDebug = true;
	
	public static void printLog(Object obj,String msg){
		printLog(LogLevel.INFO, obj.getClass().getSimpleName(), msg);
	}
	
	public static void printLog(String tag,String msg){
		printLog(LogLevel.INFO, tag, msg);
	}
	
	public static void printLog(LogLevel mLogLevel,String tag,String msg){
		if(isDebug){
			switch (mLogLevel) {
			case DEBUG:
				Log.d(tag, "#"+msg);
				break;
			case WARNING:
				Log.w(tag, "#"+msg);
				break;
			case ERROR:
				Log.e(tag, "#"+msg);
				break;
			case INFO:
			default:
				Log.i(tag, "#"+msg);
				break;
			}
			
			/** -------------- 日志打印 ------------------- */
			if(ZWApplication.isLoggerPrint){
				ZWApplication.mPrintLogger.println(mLogLevel, tag, msg);
			}
			
		}
	}

	public static void d(String tag, String message) {
		// TODO Auto-generated method stub
		printLog(LogLevel.DEBUG, tag, message);
	}

	public  static void i(String tag, String message) {
		// TODO Auto-generated method stub
		printLog(LogLevel.INFO, tag, message);
	}

	public static void w(String tag, String message) {
		// TODO Auto-generated method stub
		printLog(LogLevel.WARNING, tag, message);
	}

	public static void e(String tag, String message) {
		// TODO Auto-generated method stub
		printLog(LogLevel.ERROR, tag, message);
	}

	public static void d(Object obj, String message) {
		// TODO Auto-generated method stub
		d( obj.getClass().getSimpleName(), message);
	}

	public static void i(Object obj, String message) {
		// TODO Auto-generated method stub
		i(obj.getClass().getSimpleName(), message);
	}

	public static void w(Object obj, String message) {
		// TODO Auto-generated method stub
		w(obj.getClass().getSimpleName(), message);
	}

	public static void e(Object obj, String message) {
		// TODO Auto-generated method stub
		e(obj.getClass().getSimpleName(), message);
	}
}
