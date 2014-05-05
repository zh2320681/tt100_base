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
	//�Ƿ�����־��ӡ
	public static boolean isLoggerPrint = false;
	//��־����ʱ�䣨��sΪ��λ��
	public static long loggerPrintAvaidTime = 600;
	//��־����·��
	public static String loggerPrintPath = "";
	//���ݿ�汾��
	public static int dbVersion = 1;
	//���ݿ�����
	public static String dbName = "DEFALUT_DB_NAME";
	
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
		// ע��App�쳣����������
		Thread.setDefaultUncaughtExceptionHandler(getUncaughtExceptionHandler());
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
		InputStream iStream = null;
		try {
			iStream =  mManager.open("app_setting.properties",AssetManager.ACCESS_BUFFER);
			Properties prop = new Properties();
			prop.load(iStream);
			isDebugMode = Boolean.parseBoolean(prop.getProperty("isDebugMode"));
			isMonitorNetChange = Boolean.parseBoolean(prop.getProperty("isMonitorNetChange"));
			isLoggerPrint = Boolean.parseBoolean(prop.getProperty("isLoggerPrint"));
			loggerPrintAvaidTime = Long.parseLong(prop.getProperty("loggerPrintAvaidTime"));
			loggerPrintPath = prop.getProperty("loggerPrintPath");
			dbVersion = Integer.parseInt(prop.getProperty("loggerPrintAvaidTime"));
			dbName = prop.getProperty("dbName");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
//			mManager.close();
			if(iStream != null){
				try {
					iStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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