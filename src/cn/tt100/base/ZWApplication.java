package cn.tt100.base;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Properties;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import cn.tt100.base.exception.ZWAppException;
import cn.tt100.base.imageLoader.cache.disc.impl.UnlimitedDiscCache;
import cn.tt100.base.imageLoader.cache.disc.naming.Md5FileNameGenerator;
import cn.tt100.base.imageLoader.cache.memory.impl.LruMemoryCache;
import cn.tt100.base.imageLoader.core.DisplayImageOptions;
import cn.tt100.base.imageLoader.core.ImageLoader;
import cn.tt100.base.imageLoader.core.ImageLoaderConfiguration;
import cn.tt100.base.imageLoader.core.ImageLoaderConfiguration.Builder;
import cn.tt100.base.imageLoader.core.assist.QueueProcessingType;
import cn.tt100.base.imageLoader.core.decode.BaseImageDecoder;
import cn.tt100.base.imageLoader.core.download.BaseImageDownloader;
import cn.tt100.base.imageLoader.utils.StorageUtils;
import cn.tt100.base.util.AndroidVersionCheckUtils;
import cn.tt100.base.util.LogLevel;
import cn.tt100.base.util.ZWLogger;
import cn.tt100.base.util.logger.ZWPrintToFileLogger;
import cn.tt100.base.util.net.ZWNetChangeObserver;
import cn.tt100.base.util.net.ZWNetWorkUtil.NetType;
import cn.tt100.base.util.net.ZWNetworkStateReceiver;

public class ZWApplication extends Application {
	// �Ƿ���debugģʽ
	public static boolean isDebugMode = false;
	// �Ƿ��������仯
	public static boolean isMonitorNetChange = false;
	// �Ƿ�����־��ӡ
	public static boolean isLoggerPrint = false;
	// ��־����ʱ�䣨��sΪ��λ��
	public static long loggerPrintAvaidTime = 7;
	// ��־����·��
	public static String loggerPrintName = "";
	// ���ݿ�汾��
	public static int dbVersion = 1;
	// ���ݿ�����
	public static String dbName = "DEFALUT_DB_NAME";
	// DBOPerator����Чʱ�䵥λms
	public static int dbOPeratorAvailTime = 1000;
	// �Ƿ񷢳�rest ����
	public static boolean isLoadRestRequest = false;
	// ͬһ�� activity �Ƿ�����ͬʱִ��ͬһ������
	public static boolean isInterceptSameRequest = true;
	// �Ƿ�ʼStrictMode
	public static boolean isOpenStrictMode = true;
	
	/**
	 * -----------------------
	 * ͼƬ����
	 * -----------------------
	 */
	//�Ƿ��ʼ�� imageLoader
	public static boolean isInitImageLoader = false;
	//���汾�ظ�ʽ  PNG OR JPEG   default JPEG
	public static String imageSaveFormat = "JPEG";
	//���ص��߳�����
	public static int imageThreadPoolSize = 3;
	//�ڴ滺���С ��λMB  2*1024*1024
	public static int memoryCacheSize = 2;
	public static String imageDiscCacheDir= "/mnt/sdcard/uil_test";
	//disc�����С ��λMB  2*1024*1024
	public static int discCacheSize = 50;

	/** App�쳣���������� */
	private UncaughtExceptionHandler uncaughtExceptionHandler;

	private String systemOutTAG = "����";
	public ZWActivityManager mActivityManager;
	public static ZWPrintToFileLogger mPrintLogger;

	public int screenWidth, screenHight;
	public float density;
	//���� ������application��
	protected ImageLoaderConfiguration.Builder imageLoaderConfigBuilder;
	
	private ZWNetChangeObserver zwNetChangeObserver;

	@Override
	public final void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// ע��App�쳣����������
		Thread.setDefaultUncaughtExceptionHandler(getUncaughtExceptionHandler());
		onPreCreateApplication();
		initParameterWithProperties();

		/** ------------------ debug mode ----------------------- */
		if (isDebugMode) {
			ApplicationInfo info = getApplicationInfo();
			info.flags = ApplicationInfo.FLAG_DEBUGGABLE;
		}

		/** ------------------ isOpenStrictMode 2.3����֧��----------------------- */
		if (isDebugMode && isOpenStrictMode
				&& AndroidVersionCheckUtils.hasGingerbread()) {
			android.os.StrictMode.setThreadPolicy(new android.os.StrictMode.ThreadPolicy.Builder() // ����StrictMode
					.detectDiskReads() // ���������̶�����ʱ���
					.detectDiskWrites()// ����������д����ʱ���
					.detectNetwork() // ��������ʱ�������������滻ΪdetectAll()
										// �Ͱ����˴��̶�д������I/O
					.penaltyLog() // ����־�ķ�ʽ���
					.build());
			android.os.StrictMode.setVmPolicy(new android.os.StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects() // ̽��SQLite���ݿ����
					.penaltyLog() // ����־�ķ�ʽ���
					.penaltyDeath().build());

		}

		mActivityManager = ZWActivityManager.getInstance();
		// �޸�System.out�����
		System.setOut(new PrintStream(System.out) {
			@Override
			public synchronized void println(String str) {
				// TODO Auto-generated method stub
				// super.print(str);
				ZWLogger.printLog(LogLevel.INFO, systemOutTAG, str);
			}
		});

		// �������
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

		/** -------------- ��־��ӡ ------------------- */
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
	        .denyCacheImageMultipleSizesInMemory()
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
	 * ��assets/Properties�ļ��� ��ʼ�� ����ֵ
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
	
	/**
	 * ��ʼ��builder
	 * @param builder
	 */
	protected void initImageLoaderConfig(Builder builder){
		
	}
	
	/**
	 * ����config
	 */
	public void setImageLoaderConfig(){
		ImageLoader.getInstance().init(imageLoaderConfigBuilder.build());
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
	 * �ر���־��ӡ����
	 */
	public void closeLoggerPrint() {
		isLoggerPrint = false;
		mPrintLogger.close();
		mPrintLogger = null;
	}
	
	
	
}
