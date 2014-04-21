package cn.tt100.base.util;

import android.util.Log;

public class BaseLog {
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
			
		}
	}
	
	
}
