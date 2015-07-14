package cn.shrek.base.example;

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RemoteViews;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.download.DLHandler;
import cn.shrek.base.download.DialogDLHandler;
import cn.shrek.base.download.bo.DLTask;
import cn.shrek.base.download.ui.BaseDownLoadActivity;
import cn.shrek.base.ui.ZWActivity;
import cn.shrek.base.util.BaseUtil;
import cn.shrek.base.util.thread.HandlerEnforcer;
import cn.shrek.base.util.thread.ZWThreadEnforcer;
import cn.tt100.base.R;

public class DLTestActivity extends ZWActivity {

	public static final int NOTIFICATION_ID = 0x11;

	@AutoInject(idFormat = "dl_?", clickSelector = "mClick")
	Button downTestBtn, downDialogTestBtn, downNoifyTestBtn;

	private NotificationManager mNotificationManager;
	private Notification notification;

	ZWThreadEnforcer enforcer;

	private Random mRandom = new Random();

	public OnClickListener mClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (arg0 == downTestBtn) {
				DLTask task = new DLTask(
						"http://img6.cache.netease.com/photo/0001/2014-05-13/9S48SRBA00AN0001.jpg");
				BaseUtil.downloadFile(getApplicationContext(), task,
						new DLHandler() {

							@Override
							public int threadNumConflictOnOtherThread(
									DLTask task, int oldThreadNum) {
								// TODO Auto-generated method stub
								System.out.println("=====>threadNumConflict");
								return 0;
							}

							@Override
							public boolean sdcardNoExistOnUIThread(DLTask task) {
								// TODO Auto-generated method stub
								System.out.println("=====>sdcardNoExist");
								return false;
							}

							@Override
							public void preDownloadDoingOnUIThread(DLTask task) {
								// TODO Auto-generated method stub
								System.out.println("=====>preDownloadDoing");
							}

							@Override
							public void postDownLoadingOnUIThread(DLTask task) {
								// TODO Auto-generated method stub
								System.out.println("=====>postDownLoading");
							}

							@Override
							public void openFileErrorOnOtherThread(DLTask task,
									Exception e) {
								// TODO Auto-generated method stub
								System.out.println("=====>openFileError");
							}

							@Override
							public boolean isDLFileExist(DLTask task) {
								// TODO Auto-generated method stub
								System.out.println("=====>isDLFileExist");
								return false;
							}

							@Override
							public int downLoadError(DLTask task,
									Exception exception) {
								// TODO Auto-generated method stub
								System.out.println("=====>downLoadError");
								return 0;
							}

							@Override
							public void downLoadingProgressOnOtherThread(
									DLTask task, int hasDownSize) {
								// TODO Auto-generated method stub

							}
						});
			} else if (arg0 == downDialogTestBtn) {
				DLTask task = new DLTask("http://www.zgshige.com/zgshige.apk");
				BaseUtil.downloadFile(getApplicationContext(), task,
						new DialogDLHandler(DLTestActivity.this) {

							@Override
							public boolean isDLFileExist(DLTask task) {
								// TODO Auto-generated method stub
								return false;
							}
						});
			} else if (arg0 == downNoifyTestBtn) {
				DLTask task = new DLTask("http://www.zgshige.com/zgshige.apk");
				task.isAutoOpen = false;

				BaseUtil.downloadFile(getApplicationContext(), task,
						new DLHandler() {

							@Override
							public int threadNumConflictOnOtherThread(
									DLTask task, int oldThreadNum) {
								// TODO Auto-generated method stub
								return 0;
							}

							@Override
							public boolean sdcardNoExistOnUIThread(DLTask task) {
								// TODO Auto-generated method stub
								return false;
							}

							@Override
							public void preDownloadDoingOnUIThread(DLTask task) {
								// TODO Auto-generated method stub
								notification = new Notification();
								notification.icon = R.drawable.donwload_icon;
								notification.tickerText = "下载中...";
								notification.when = System.currentTimeMillis();
								notification.contentView = new RemoteViews(
										getApplication().getPackageName(),
										R.layout.update_notify);
								notification.contentView.setTextViewText(
										R.id.notifyUI_progress, "点击查看详情");
								notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
								notification.flags |= Notification.FLAG_NO_CLEAR;
								Intent i = new Intent(DLTestActivity.this,
										BaseDownLoadActivity.class);
								i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
										| Intent.FLAG_ACTIVITY_NEW_TASK);
								PendingIntent contentIntent = PendingIntent
										.getActivity(
												DLTestActivity.this,
												R.string.app_name,
												i,
												PendingIntent.FLAG_UPDATE_CURRENT);
								notification.contentIntent = contentIntent;
								if (mNotificationManager != null) {
									mNotificationManager.notify(
											NOTIFICATION_ID, notification);
								} else {
									mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
									mNotificationManager.notify(
											NOTIFICATION_ID, notification);
								}
							}

							@Override
							public void postDownLoadingOnUIThread(DLTask task) {
								// TODO Auto-generated method stub
								Intent i = new Intent(DLTestActivity.this,
										BaseDownLoadActivity.class);
								i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
										| Intent.FLAG_ACTIVITY_NEW_TASK);
								showNotify(i, task.fileName);
							}

							@Override
							public void openFileErrorOnOtherThread(DLTask task,
									Exception e) {
								// TODO Auto-generated method stub

							}

							@Override
							public boolean isDLFileExist(DLTask task) {
								// TODO Auto-generated method stub
								return false;
							}

							@Override
							public void downLoadingProgressOnOtherThread(
									final DLTask task, final int hasDownSize) {
								// TODO Auto-generated method stub
								enforcer.enforceMainThread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										notification.contentView
												.setTextViewText(
														R.id.notifyUI_progress,
														"下载进度: "
																+ BaseUtil
																		.getFileSize(hasDownSize)
																+ "/"
																+ BaseUtil
																		.getFileSize(task.totalSize));
										mNotificationManager.notify(NOTIFICATION_ID, notification);
									}
								});
							}

							@Override
							public int downLoadError(DLTask task,
									Exception exception) {
								// TODO Auto-generated method stub
								return 0;
							}

						});
			}
		}
	};

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		enforcer = HandlerEnforcer.newInstance();
	}

	@Override
	protected void addListener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyObserver(Object oldObj, Object newObj) {
		// TODO Auto-generated method stub

	}

	private void showNotify(Intent i, String fileName) {
		notification.contentView = new RemoteViews(getPackageName(),
				R.layout.update_notify);
		notification.contentView.setTextViewText(R.id.notifyUI_progress,
				"点击打开下载内容");
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.contentView.setTextViewText(R.id.notifyUI_info, fileName
				+ "下载完成");
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(this,
				R.string.app_name, i, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.contentIntent = contentIntent;
		if (mNotificationManager != null) {
			mNotificationManager.notify(
					NOTIFICATION_ID + mRandom.nextInt(1000), notification);
		} else {
			mNotificationManager = (NotificationManager) this
					.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(
					NOTIFICATION_ID + mRandom.nextInt(1000), notification);
		}
		
		mNotificationManager.cancel(NOTIFICATION_ID);
	}

}
