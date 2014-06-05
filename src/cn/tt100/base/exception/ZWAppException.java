package cn.tt100.base.exception;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Looper;
import cn.tt100.base.ZWApplication;

public class ZWAppException implements UncaughtExceptionHandler {
	public static final String TAG = "CrashHandler";
	private static ZWAppException instance;
	private Context mContext;
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	private ZWAppException(Context context) {
		mContext = context;
		// �õ�default handler
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	public static ZWAppException getInstance(Context context) {
		if (instance == null) {
			instance = new ZWAppException(context);
		}
		instance.mContext = context;
		return instance;
	}

	@Override
	public synchronized void uncaughtException(Thread thread, Throwable ex) {
		ex.printStackTrace();
		if (handleException(ex)) {
			return;
		}
		if (mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(10);
		}
	}

	/**
	 * �Զ��������,�ռ�������Ϣ ���ʹ��󱨸�Ȳ������ڴ����
	 * 
	 * @param ex
	 * @return true:��������˸��쳣��Ϣ;���򷵻�false
	 */
	private boolean handleException(final Throwable ex) {
		if (ex == null) {
			return false;
		}
		new Thread() {
			@Override
			public void run() {
				try {
					Looper.prepareMainLooper();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext)
						.setTitle("���������").setCancelable(false)
						.setMessage("���������...")
						.setPositiveButton("�ر�APP", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								android.os.Process
										.killProcess(android.os.Process.myPid());
								System.exit(10);
							}
						});
				// .setNegativeButton("��������", new OnClickListener() {
				//
				// @Override
				// public void onClick(DialogInterface arg0, int arg1) {
				// // TODO Auto-generated method stub
				// final Intent intent =
				// mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
				// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// mContext.startActivity(intent);
				// }
				//
				// });
				if (ZWApplication.isDebugMode) {
					// mBuilder.setNegativeButton("debug", new OnClickListener()
					// {
					//
					// @Override
					// public void onClick(DialogInterface arg0, int arg1) {
					// // TODO Auto-generated method stub
					// Intent intent = new Intent(mContext,
					// ErrorActivity.class);
					// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
					// Intent.FLAG_ACTIVITY_NEW_TASK);
					// mContext.startActivity(intent);
					// }
					//
					// });
					StackTraceElement[] stes = ex.getStackTrace();
					StringBuffer sb = new StringBuffer();
					sb.append("----�쳣����---\n" + ex.toString() + "\n\n");
					sb.append("----�쳣����---\n");
					for (StackTraceElement ste : stes) {
						sb.append(ste.toString() + "\n");
					}
					mBuilder.setMessage(sb.toString());
				}
				try {
					mBuilder.create().show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Looper.myLooper().quit();
					e.printStackTrace();
					System.exit(0);
				}

				// ȡ��Ϣ���е�һ��
//				try {
//					Looper mainLooper = Looper.getMainLooper();
//					Field queueField = Looper.class.getDeclaredField("mQueue");
//					queueField.setAccessible(true);
//					MessageQueue queue = (MessageQueue) queueField.get(mainLooper);
//					
//					Method nextMethod = MessageQueue.class.getDeclaredMethod("next");
//					nextMethod.setAccessible(true);
//					Message msg = (Message) nextMethod.invoke(queue);
//					if (msg == null) {
//						return;
//					}
//					Field field = Message.class.getDeclaredField("target");
//					field.setAccessible(true);
//					Handler target = (Handler) field.get(msg);
//					target.dispatchMessage(msg);
//					final long newIdent = Binder.clearCallingIdentity();
//					msg.recycle();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
				/**
				 * ��������ֵ��쳣
				 */
				 Looper.loop();
			}
		}.start();

		return true;
	}
}