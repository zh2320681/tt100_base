
package cn.tt100.base.exception;

import java.lang.Thread.UncaughtExceptionHandler;

import cn.tt100.base.ZWApplication;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Looper;

public class ZWAppException implements UncaughtExceptionHandler {
	public static final String TAG = "CrashHandler";
	private static ZWAppException instance;
	private Context mContext;
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	private ZWAppException(Context context) {
		init(context);
	}

	public static ZWAppException getInstance(Context context) {
		if (instance == null) {
			instance = new ZWAppException(context);
		}
		return instance;
	}

	private void init(Context context) {
		mContext = context;
		//得到default handler
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		ex.printStackTrace();
		if (!handleException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(10);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return true;
		}
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext).setTitle("提示")
						.setCancelable(false).setMessage("程序崩溃了...")
						.setNeutralButton("关闭APP", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								android.os.Process
										.killProcess(android.os.Process.myPid());
								System.exit(10);
							}
						}).setNeutralButton("重新启动", new OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								final Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());  
						        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
						        mContext.startActivity(intent);
							}
							
						});
						if(ZWApplication.isDebugMode){
							mBuilder.setNeutralButton("debug", new OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub
									
								}
								
							});
						}
						mBuilder.create().show();
				Looper.loop();
			}
		}.start();
		return true;
	}
}