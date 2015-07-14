package cn.shrek.base.download;

import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;
import cn.shrek.base.download.bo.DLTask;
import cn.shrek.base.download.ui.BaseDownLoadActivity;
import cn.tt100.base.R;

public class DownloadService extends Service {

	private static final String TAG = "UpdateService";

	public static AtomicBoolean isServiceShutDown;
	private Downloader mDownloader;

	public IBinder onBind(Intent paramIntent) {
		return null;
	}

	public void onCreate() {
		super.onCreate();
		isServiceShutDown = new AtomicBoolean(false);

		mDownloader = new Downloader(this);
	}

	public void onDestroy() {
		super.onDestroy();
		mDownloader.destroyDownloader();
		isServiceShutDown.set(true);;
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			DLTask task = (DLTask) intent.getSerializableExtra("d");
//			if (localDLTask.isShowNotify) {
//				showNotify();
//			}
			mDownloader.addTask(task);
		}
		return super.onStartCommand(intent, flags, startId);
	}

//	private void showNotify() {
//		notification.contentView = new RemoteViews(getApplication()
//				.getPackageName(), R.layout.update_notify);
//		notification.contentView.setTextViewText(R.id.notifyUI_progress,
//				"点击查看详情");
//		notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
//		notification.flags |= Notification.FLAG_NO_CLEAR;
//		Intent i = new Intent(this, BaseDownLoadActivity.class);
//		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//				| Intent.FLAG_ACTIVITY_NEW_TASK);
//		PendingIntent contentIntent = PendingIntent.getActivity(this,
//				R.string.app_name, i, PendingIntent.FLAG_UPDATE_CURRENT);
//		notification.contentIntent = contentIntent;
//		if (mNotificationManager != null) {
//			mNotificationManager.notify(NOTIFICATION_ID, notification);
//		} else {
//			mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//			mNotificationManager.notify(NOTIFICATION_ID, notification);
//		}
//	}

}
