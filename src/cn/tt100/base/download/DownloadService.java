package cn.tt100.base.download;

import com.wei.util.download.BaseDownLoadActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;
import cn.tt100.base.R;
import cn.tt100.base.download.bo.DLTask;

public class DownloadService extends Service {
	public static final int NOTIFICATION_ID = 0x11;

	private static final String TAG = "UpdateService";

	public static boolean isServiceShutDown = false;
	private Downloader mDownloader;

	private NotificationManager mNotificationManager;
	private Notification notification;

	public IBinder onBind(Intent paramIntent) {
		return null;
	}

	public void onCreate() {
		super.onCreate();
		mNotificationManager = (NotificationManager) getSystemService("notification");
		notification = new Notification(R.drawable.donwload_icon, "下载中...",
				System.currentTimeMillis());
		isServiceShutDown = false;

		mDownloader = new Downloader(this, mNotificationManager);
		;
	}

	public void onDestroy() {
		super.onDestroy();
		mDownloader.destroyDownloader();
		if (mNotificationManager != null) {
			mNotificationManager.cancelAll();
			mNotificationManager = null;
		}
		isServiceShutDown = true;
	}

	public int onStartCommand(Intent intent, int paramInt1, int paramInt2) {
		if (intent != null) {
			DLTask localDLTask = (DLTask) intent.getSerializableExtra("d");
			if (localDLTask.isShowNotify) {
				showNotify();
			}
			mDownloader.addTask(localDLTask);
		}
		return super.onStartCommand(intent, paramInt1, paramInt2);
	}

	private void showNotify() {
		notification.contentView = new RemoteViews(getApplication()
				.getPackageName(), R.layout.update_notify);
		notification.contentView.setTextViewText(R.id.notifyUI_progress,
				"点击查看详情");
		notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
		notification.flags |= Notification.FLAG_NO_CLEAR;
//		Intent i = new Intent(this, BaseDownLoadActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(this,
				R.string.app_name, i, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.contentIntent = contentIntent;
		if (mNotificationManager != null) {
			mNotificationManager.notify(NOTIFICATION_ID, notification);
		} else {
			mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(NOTIFICATION_ID, notification);
		}
	}

}
