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
	//�Ƿ���debugģʽ
	public static boolean isDebugMode = false;
	//�Ƿ��������仯
	public static boolean isMonitorNetChange = false;
	/** App�쳣���������� */
	private UncaughtExceptionHandler uncaughtExceptionHandler;
	
	private String systemOutTAG= "����";
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
		//�޸�System.out�����
		System.setOut(new PrintStream(System.out){
			@Override
			public synchronized void println(String str) {
				// TODO Auto-generated method stub
//				super.print(str);
				ZWLogger.printLog(LogLevel.INFO,systemOutTAG, str);
			}
		});
		// ע��App�쳣����������
		Thread.setDefaultUncaughtExceptionHandler(getUncaughtExceptionHandler());
		
		//�������
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
	 * ��assets/Properties�ļ��� ��ʼ�� ����ֵ
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
	 * ����Applicationǰ ��ʲô����
	 */
	protected void onPreCreateApplication() {
		// TODO Auto-generated method stub
	}
	
	/**
	 * ����Application֮�� ��ʲô����
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
	 * ���� App�쳣����������
	 * 
	 * @param uncaughtExceptionHandler
	 */
	public void setUncaughtExceptionHandler(
			UncaughtExceptionHandler uncaughtExceptionHandler) {
		this.uncaughtExceptionHandler = uncaughtExceptionHandler;
	}

	/**
	 * �õ�App�쳣����������
	 * @return
	 */
	private UncaughtExceptionHandler getUncaughtExceptionHandler() {
		if (uncaughtExceptionHandler == null) {
			uncaughtExceptionHandler = ZWAppException.getInstance(this);
		}
		return uncaughtExceptionHandler;
	}
}
