package cn.tt100.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Properties;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import cn.tt100.base.exception.ZWAppException;
import cn.tt100.base.imageLoader.ImageLoader;
import cn.tt100.base.util.AndroidVersionCheckUtils;
import cn.tt100.base.util.LogLevel;
import cn.tt100.base.util.ZWLogger;
import cn.tt100.base.util.logger.ZWPrintToFileLogger;
import cn.tt100.base.util.net.ZWNetChangeObserver;
import cn.tt100.base.util.net.ZWNetWorkUtil.NetType;
import cn.tt100.base.util.net.ZWNetworkStateReceiver;

public class ZWApplication extends Application {
	// 是否开启debug模式
	public static boolean isDebugMode = false;
	// 是否监听网络变化
	public static boolean isMonitorNetChange = false;
	// 是否开启日志打印
	public static boolean isLoggerPrint = false;
	// 日志保存时间（以s为单位）
	public static long loggerPrintAvaidTime = 7;
	// 日志保存路径
	public static String loggerPrintName = "";
	// 数据库版本号
	public static int dbVersion = 1;
	// 数据库名称
	public static String dbName = "DEFALUT_DB_NAME";
	// DBOPerator的有效时间单位ms
	public static int dbOPeratorAvailTime = 1000;
	// 是否发出rest 请求
	public static boolean isLoadRestRequest = false;
	// 是否开始StrictMode
	public static boolean isOpenStrictMode = true;

	/** App异常崩溃处理器 */
	private UncaughtExceptionHandler uncaughtExceptionHandler;

	private String systemOutTAG = "测试";
	public ZWActivityManager mActivityManager;
	public static ZWPrintToFileLogger mPrintLogger;

	public int screenWidth, screenHight;
	public float density;

	private ZWNetChangeObserver zwNetChangeObserver;

	@Override
	public final void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// 注册App异常崩溃处理器
		Thread.setDefaultUncaughtExceptionHandler(getUncaughtExceptionHandler());
		onPreCreateApplication();
		initParameterWithProperties();

		/** ------------------ debug mode ----------------------- */
		if (isDebugMode) {
			ApplicationInfo info = getApplicationInfo();
			info.flags = ApplicationInfo.FLAG_DEBUGGABLE;
		}

		/** ------------------ isOpenStrictMode 2.3以上支持----------------------- */
		if (isDebugMode && isOpenStrictMode
				&& AndroidVersionCheckUtils.hasGingerbread()) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() // 构造StrictMode
					.detectDiskReads() // 当发生磁盘读操作时输出
					.detectDiskWrites()// 当发生磁盘写操作时输出
					.detectNetwork() // 访问网络时输出，这里可以替换为detectAll()
										// 就包括了磁盘读写和网络I/O
					.penaltyLog() // 以日志的方式输出
					.build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects() // 探测SQLite数据库操作
					.penaltyLog() // 以日志的方式输出
					.penaltyDeath().build());

		}

		mActivityManager = ZWActivityManager.getInstance();
		// 修改System.out输出流
		System.setOut(new PrintStream(System.out) {
			@Override
			public synchronized void println(String str) {
				// TODO Auto-generated method stub
				// super.print(str);
				ZWLogger.printLog(LogLevel.INFO, systemOutTAG, str);
			}
		});

		// 网络监听
		if (isMonitorNetChange) {
			try {

				zwNetChangeObserver = new ZWNetChangeObserver() {

					@Override
					public void onConnect(NetType type) {
						// TODO Auto-generated method stub
						super.onConnect(type);
						ZWActivity currActivity = mActivityManager
								.currentActivity();
						currActivity.onConnect(type);
					}

					@Override
					public void onDisConnect() {
						// TODO Auto-generated method stub
						super.onDisConnect();
						ZWActivity currActivity = mActivityManager
								.currentActivity();
						currActivity.onDisConnect();
					}
				};
				ZWNetworkStateReceiver.registerObserver(zwNetChangeObserver);
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
			}
		}

		/** -------------- 日志打印 ------------------- */
		if (isLoggerPrint) {
			mPrintLogger = new ZWPrintToFileLogger(getApplicationContext());
			mPrintLogger.open();
		}

		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
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
	private void initParameterWithProperties() {
		AssetManager mManager = getAssets();
		InputStream iStream = null;
		try {
			iStream = mManager.open("app_setting.properties",
					AssetManager.ACCESS_BUFFER);
			Properties prop = new Properties();
			prop.load(iStream);
			isDebugMode = Boolean.parseBoolean(prop.getProperty("isDebugMode"));
			isMonitorNetChange = Boolean.parseBoolean(prop
					.getProperty("isMonitorNetChange"));
			isLoggerPrint = Boolean.parseBoolean(prop
					.getProperty("isLoggerPrint"));
			loggerPrintAvaidTime = Long.parseLong(prop
					.getProperty("loggerPrintAvaidTime"));
			loggerPrintName = prop.getProperty("loggerPrintName");
			dbVersion = Integer.parseInt(prop.getProperty("dbVersion"));
			dbName = prop.getProperty("dbName");
			dbOPeratorAvailTime = Integer.parseInt(prop
					.getProperty("dbOPeratorAvailTime"));
			isLoadRestRequest = Boolean.parseBoolean(prop
					.getProperty("isLoadRestRequest"));
			isOpenStrictMode = Boolean.parseBoolean(prop
					.getProperty("isOpenStrictMode"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// mManager.close();
			if (iStream != null) {
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
		// clearDataCache();
		mActivityManager = null;
		closeLoggerPrint();
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
	 * 
	 * @return
	 */
	public UncaughtExceptionHandler getUncaughtExceptionHandler() {
		if (uncaughtExceptionHandler == null) {
			uncaughtExceptionHandler = ZWAppException.getInstance(this);
		}
		return uncaughtExceptionHandler;
	}

	/**
	 * 关闭日志打印功能
	 */
	public void closeLoggerPrint() {
		isLoggerPrint = false;
		mPrintLogger.close();
		mPrintLogger = null;
	}
}
