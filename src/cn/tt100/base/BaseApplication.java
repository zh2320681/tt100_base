package cn.tt100.base;

import java.io.PrintStream;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import cn.tt100.base.util.BaseLog;
import cn.tt100.base.util.LogLevel;
import cn.tt100.base.util.MyActivityManager;

public class BaseApplication extends Application {
	private String systemOutTAG= "²âÊÔ";
	public MyActivityManager mActivityManager;
	
	public int screenWidth,screenHight;
	public float density; 
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		mActivityManager = MyActivityManager.getInstance();
		//ÐÞ¸ÄSystem.outÊä³öÁ÷
		System.setOut(new PrintStream(System.out){
			@Override
			public synchronized void println(String str) {
				// TODO Auto-generated method stub
//				super.print(str);
				BaseLog.printLog(LogLevel.INFO,systemOutTAG, str);
			}
			
		});
		
		WindowManager wm = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm  = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		
		screenWidth = dm.widthPixels;
		screenHight = dm.heightPixels;
		
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
//		clearDataCache();
		mActivityManager = null;
		System.gc();
	}
	
	
}
