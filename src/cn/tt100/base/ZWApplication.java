package cn.tt100.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Properties;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import cn.tt100.base.exception.ZWAppException;
import cn.tt100.base.imageLoader.ImageLoader;
import cn.tt100.base.util.LogLevel;
import cn.tt100.base.util.ZWLogger;
import cn.tt100.base.util.net.ZWNetChangeObserver;
import cn.tt100.base.util.net.ZWNetWorkUtil.NetType;
import cn.tt100.base.util.net.ZWNetworkStateReceiver;

public class ZWApplication extends Application {
	//是否开启debug模式
	public static boolean isDebugMode = false;
	//是否监听网络变化
	public static boolean isMonitorNetChange = false;
	/** App异常崩溃处理器 */
	private UncaughtExceptionHandler uncaughtExceptionHandler;
	
	private String systemOutTAG= "测试";
	public ZWActivityManager mActivityManager;
	
	public int screenWidth,screenHight;
	public float density; 
	
	private ZWNetChangeObserver zwNetChangeObserver;
	
	@Override
	public final void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		onPreCreateApplication();
		initParameterWithProperties();
		mActivityManager = ZWActivityManager.getInstance();
		//修改System.out输出流
		System.setOut(new PrintStream(System.out){
			@Override
			public synchronized void println(String str) {
				// TODO Auto-generated method stub
//				super.print(str);
				ZWLogger.printLog(LogLevel.INFO,systemOutTAG, str);
			}
		});
		// 注册App异常崩溃处理器
		Thread.setDefaultUncaughtExceptionHandler(getUncaughtExceptionHandler());
		
		//网络监听
		if(isMonitorNetChange){
			try {
				final ZWActivity currActivity = mActivityManager.currentActivity();
				zwNetChangeObserver = new ZWNetChangeObserver(){

					@Override
					public void onConnect(NetType type) {
						// TODO Auto-generated method stub
						super.onConnect(type);
						currActivity.onConnect(type);
					}

					@Override
					public void onDisConnect() {
						// TODO Auto-generated method stub
						super.onDisConnect();
						currActivity.onDisConnect();
					}
				};
				ZWNetworkStateReceiver.registerObserver(zwNetChangeObserver);
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block	
			}
		}
		
		WindowManager wm = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm  = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		
		screenWidth = dm.widthPixels;
		screenHight = dm.heightPixels;
		
		ImageLoader.getLoader(this);
		
		onAfterCreateApplication();
	}
	
	
	/**
	 * 从assets/Properties文件中 初始化 参数值
	 */
	private void initParameterWithProperties(){
		AssetManager mManager = getAssets();
		try {
			InputStream iStream =  mManager.open("app_setting.properties",AssetManager.ACCESS_BUFFER);
			Properties prop = new Properties();
			prop.load(iStream);
			isDebugMode = Boolean.getBoolean(prop.get("isDebugMode").toString());
			isMonitorNetChange = Boolean.getBoolean(prop.get("isMonitorNetChange").toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			mManager.close();
		}
	}

	/**
	 * 创建Application前 做什么动作
	 */
	protected void onPreCreateApplication() {
		// TODO Auto-generated method stub
	}
	
	/**
	 * 创建Application之后 做什么动作
	 */
	protected void onAfterCreateApplication() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
//		clearDataCache();
		mActivityManager = null;
		System.gc();
	}
	
	
	/**
	 * 设置 App异常崩溃处理器
	 * 
	 * @param uncaughtExceptionHandler
	 */
	public void setUncaughtExceptionHandler(
			UncaughtExceptionHandler uncaughtExceptionHandler) {
		this.uncaughtExceptionHandler = uncaughtExceptionHandler;
	}

	/**
	 * 得到App异常崩溃处理器
	 * @return
	 */
	private UncaughtExceptionHandler getUncaughtExceptionHandler() {
		if (uncaughtExceptionHandler == null) {
			uncaughtExceptionHandler = ZWAppException.getInstance(this);
		}
		return uncaughtExceptionHandler;
	}
}
