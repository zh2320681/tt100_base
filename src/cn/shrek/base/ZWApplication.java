package cn.shrek.base;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import cn.shrek.base.exception.ZWAppException;
import cn.shrek.base.ui.inject.CustomInstanceFactory;
import cn.shrek.base.ui.inject.Injector;
import cn.shrek.base.util.AndroidVersionCheckUtils;
import cn.shrek.base.util.LogLevel;
import cn.shrek.base.util.ZWLogger;
import cn.shrek.base.util.logger.LoggerBo;
import cn.shrek.base.util.logger.ZWPrintToFileLogger;
import cn.shrek.base.util.net.ZWNetChangeObserver;
import cn.shrek.base.util.net.ZWNetWorkUtil.NetType;
import cn.shrek.base.util.net.ZWNetworkStateReceiver;

public class ZWApplication extends Application {
	// 是否开启debug模式
	public static boolean isDebugMode = false;
	// 是否捕获异常
	public static boolean isCaptureError = false;
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
	// 同一个 activity 是否允许同时执行同一个请求
	public static boolean isInterceptSameRequest = true;
	// 是否开始StrictMode
	public static boolean isOpenStrictMode = true;
	
	/**
	 * -----------------------
	 * 图片加载
	 * -----------------------
	 */
	//是否初始化 imageLoader
	public static boolean isInitImageLoader = false;
	//缓存本地格式  PNG OR JPEG   default JPEG
	public static String imageSaveFormat = "JPEG";
	//加载的线程数量
	public static int imageThreadPoolSize = 3;
	//内存缓存大小 单位MB  2*1024*1024
	public static int memoryCacheSize = 2;
	public static String imageDiscCacheDir= "/mnt/sdcard/uil_test";
	//disc缓存大小 单位MB  2*1024*1024
	public static int discCacheSize = 50;

	/** App异常崩溃处理器 */
	private UncaughtExceptionHandler uncaughtExceptionHandler;

	private String systemOutTAG = "测试";
	public ZWActivityManager mActivityManager;
	public static ZWPrintToFileLogger mPrintLogger;

	public int screenWidth, screenHight;
	public float density;
	//配置 必须在application中
	protected ImageLoaderConfiguration.Builder imageLoaderConfigBuilder;
	
	private ZWNetChangeObserver zwNetChangeObserver;

	
//	static { 
//		System.loadLibrary("ZWTool");
//	}
	
	@Override
	public final void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		onPreCreateApplication();
		initParameterWithProperties();

		if(isCaptureError){
			// 注册App异常崩溃处理器
			Thread.setDefaultUncaughtExceptionHandler(getUncaughtExceptionHandler());
		}
		
		/** ------------------ debug mode ----------------------- */
		if (isDebugMode) {
			ApplicationInfo info = getApplicationInfo();
			info.flags = ApplicationInfo.FLAG_DEBUGGABLE;
		}

		/** ------------------ isOpenStrictMode 2.3以上支持----------------------- */
		if (isDebugMode && isOpenStrictMode
				&& AndroidVersionCheckUtils.hasGingerbread()) {
			android.os.StrictMode.setThreadPolicy(new android.os.StrictMode.ThreadPolicy.Builder() // 构造StrictMode
					.detectDiskReads() // 当发生磁盘读操作时输出
					.detectDiskWrites()// 当发生磁盘写操作时输出
					.detectNetwork() // 访问网络时输出，这里可以替换为detectAll()
										// 就包括了磁盘读写和网络I/O
					.penaltyLog() // 以日志的方式输出
					.build());
			android.os.StrictMode.setVmPolicy(new android.os.StrictMode.VmPolicy.Builder()
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
						Activity currActivity = mActivityManager
								.currentActivity();
						if(currActivity instanceof ZWNetChangeObserver){
							((ZWNetChangeObserver)currActivity).onConnect(type);
						}
						
					}

					@Override
					public void onDisConnect() {
						// TODO Auto-generated method stub
						Activity currActivity = mActivityManager
								.currentActivity();
						if(currActivity instanceof ZWNetChangeObserver){
							((ZWNetChangeObserver)currActivity).onDisConnect();
						}
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

//		ImageLoader.getLoader(this);
		if(isInitImageLoader){
			File cacheDir = null;
			if(imageDiscCacheDir == null || "".equals(imageDiscCacheDir)){
				cacheDir = StorageUtils.getCacheDirectory(this);
			}else{
				cacheDir = new File(imageDiscCacheDir);
				if(!cacheDir.exists()){
					try {
						cacheDir.mkdir();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						cacheDir = StorageUtils.getCacheDirectory(this);
					}
				}
			}
			
			imageLoaderConfigBuilder = new ImageLoaderConfiguration.Builder(getApplicationContext())
	        .threadPoolSize(imageThreadPoolSize) // default
	        .threadPriority(Thread.NORM_PRIORITY - 1) // default
	        .tasksProcessingOrder(QueueProcessingType.FIFO) // default
	        .taskExecutor(Executors.newFixedThreadPool(imageThreadPoolSize))
	        .memoryCache(new LruMemoryCache(memoryCacheSize * 1024 * 1024))
	        .memoryCacheSize(memoryCacheSize * 1024 * 1024)
	        .memoryCacheSizePercentage(13) // default
	        .discCache(new UnlimitedDiscCache(cacheDir)) // default
	        .discCacheSize(discCacheSize * 1024 * 1024)
	        .discCacheFileNameGenerator(new Md5FileNameGenerator()) // default
	        .imageDownloader(new BaseImageDownloader(this)) // default
	        .imageDecoder(new BaseImageDecoder(false)) // default
	        .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
	        ;
			
			initImageLoaderConfig(imageLoaderConfigBuilder);
			setImageLoaderConfig();
		}
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
			isCaptureError = Boolean.parseBoolean(prop.getProperty("isCaptureError"));
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
			isInterceptSameRequest = Boolean.parseBoolean(prop
					.getProperty("isInterceptSameRequest"));
			
			isInitImageLoader =  Boolean.parseBoolean(prop
					.getProperty("isInitImageLoader"));
			imageSaveFormat = prop.getProperty("imageSaveFormat");
			imageThreadPoolSize = Integer.parseInt(prop
					.getProperty("imageThreadPoolSize"));
			memoryCacheSize = Integer.parseInt(prop
					.getProperty("memoryCacheSize"));
			imageDiscCacheDir = prop.getProperty("imageDiscCacheDir");
			discCacheSize = Integer.parseInt(prop
					.getProperty("discCacheSize"));
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
	
	/**
	 * 初始化builder
	 * @param builder
	 */
	protected void initImageLoaderConfig(Builder builder){
		
	}
	
	/**
	 * 设置config
	 */
	public void setImageLoaderConfig(){
		ImageLoader.getInstance().init(imageLoaderConfigBuilder.build());
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		// clearDataCache();
		//清空注入器
		Injector.instance().recycle();
		
		mActivityManager.popAllActivity();
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
	
	/**
	 * 获取历史的日志
	 * @return
	 */
	public List<LoggerBo> getHistoryLogs(){
		return mPrintLogger.getHistoryLogs();
	}
	
	/**
	 * 设置自定义的示例模式
	 * @param mFactory
	 */
	public void setCustomFactory(CustomInstanceFactory mFactory){
		if(mFactory == null){
			throw new NullPointerException("CustomInstanceFactory can not null!");
		}
		Injector.instance().setCustomFactory(mFactory);
	}
	
	/**
	 * 得到当前的activity
	 * @return
	 */
	public Activity getCurrActivity(){
		return mActivityManager.currentActivity();
	}
}
