package cn.shrek.base.download;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;
import cn.shrek.base.download.bo.DLTask;
import cn.shrek.base.download.bo.DLThreadTask;
import cn.shrek.base.download.db.DLDatabaseHelper;
import cn.shrek.base.util.BaseUtil;
import cn.shrek.base.util.ZWLogger;

public class Downloader {
	public static final int NOTIFICATION_ID = 0x22;
	// ��������Ļص�
	public static Map<DLTask, DLHandler> allCallbacks;
	// ��������� ���ص��߳�
	public static volatile Map<DLTask, Set<DLThreadTask>> allTasks;

	// Ĭ�ϵ� ·��
	private static File defaultSavePath;

	private static final int keepAliveTime = 3; // �̳߳�
	private static final int maxThreadPoolSize = 6; // �̳߳�����߳���

	private Context context;
	// ��������� ��������ʧ�ܺ� ����
	private Map<DLTask, Integer> errorTasks;
	// private Notification infoNotification;

	private DLDatabaseHelper mDBOperator;
	// ִ�������̳߳�
	private ExecutorService mExecutorService;

	private Random mRandom;
	private SimpleDateFormat mSimpleDateFormat;
	// ��������̳߳�
	private ExecutorService splitExecutor;

	public Downloader(Context mContext) {
		this.mRandom = new Random();
		this.context = mContext;
		if (allTasks == null) {
			allTasks = Collections
					.synchronizedMap(new HashMap<DLTask, Set<DLThreadTask>>());
		}
		this.errorTasks = Collections
				.synchronizedMap(new HashMap<DLTask, Integer>());

		defaultSavePath = new File(Environment.getExternalStorageDirectory(),
				"baseDLLoader");
		if (!defaultSavePath.exists()) {
			boolean bool = defaultSavePath.mkdir();
			if (!bool)
				ZWLogger.i(Downloader.this, "baseDLLoader�ļ��д���ʧ��!");
		}

		// this.infoNotification = new Notification(R.layout.update_notify,
		// "�������", System.currentTimeMillis());

		mExecutorService = new ThreadPoolExecutor(maxThreadPoolSize,
				maxThreadPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>()) {

			@Override
			protected void afterExecute(Runnable r, Throwable t) {
				if (r instanceof DownloadCallable) {
					DownloadCallable callable = (DownloadCallable) r;
					DLTask dlTask = callable.getTask();

					final Set<DLThreadTask> sets = allTasks.get(dlTask);
					boolean isFinish = true;
					for (DLThreadTask dtTask1 : sets) {
						if (!dtTask1.isFinish()) {
							isFinish = false;
							break;
						}
					}

					if (isFinish) {
						postTaskDoing(dlTask);
					}
				}

			}
		};

		this.splitExecutor = Executors.newFixedThreadPool(1);

		mDBOperator = new DLDatabaseHelper(mContext);
		this.mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	}

	// private void showNotify(Intent i, String fileName) {
	// infoNotification.contentView = new RemoteViews(
	// context.getPackageName(), R.layout.update_notify);
	// infoNotification.contentView.setTextViewText(R.id.notifyUI_progress,
	// "�������������");
	// infoNotification.flags = Notification.FLAG_AUTO_CANCEL;
	// infoNotification.contentView.setTextViewText(R.id.notifyUI_info,
	// fileName + "�������");
	// i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
	// | Intent.FLAG_ACTIVITY_NEW_TASK);
	// PendingIntent contentIntent = PendingIntent.getActivity(context,
	// R.string.app_name, i, PendingIntent.FLAG_UPDATE_CURRENT);
	// infoNotification.contentIntent = contentIntent;
	// if (mNotificationManager != null) {
	// mNotificationManager.notify(
	// NOTIFICATION_ID + mRandom.nextInt(1000), infoNotification);
	// } else {
	// mNotificationManager = (NotificationManager) context
	// .getSystemService(Context.NOTIFICATION_SERVICE);
	// mNotificationManager.notify(
	// NOTIFICATION_ID + mRandom.nextInt(1000), infoNotification);
	// }
	// }

	/**
	 * �������
	 * 
	 * @param paramDLTask
	 */
	public void addTask(final DLTask mDLTask) {
		DLHandler mHandler = getTaskHandler(mDLTask);
		if (!BaseUtil.isSdCardExist()) {
			Toast.makeText(this.context, "SD��������,�޷�����.", Toast.LENGTH_SHORT)
					.show();
			if (mHandler != null && !mHandler.sdcardNoExistOnUIThread(mDLTask)) {
				return;
			}
		}

		final Set<DLTask> tasks = allTasks.keySet();
		for (DLTask task : tasks) {
			if (task.equals(mDLTask)
					&& task.states.get() == DLConstant.TASK_RUN) {
				Toast.makeText(context,
						"�����б����Ѿ����ڸ���������,������������,��ɾ��ԭ�����,�ٴ��ύ��������.",
						Toast.LENGTH_SHORT).show();
				return;
			}
		}

		splitExecutor.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				spliteTask(mDLTask);
			}
		});
	}

	private void download(DLTask mDLTask) throws Exception {
		if (mDLTask.savePath == null) {
			mDLTask.savePath = defaultSavePath.getPath();
		}
		File localFile = new File(mDLTask.savePath, mDLTask.fileName);

		// ��� ��¼���ܹ����ص�
		long sumSize = 0;
		final Set<DLThreadTask> dtTasks = allTasks.get(mDLTask);
		for (DLThreadTask dtTask : dtTasks) {
			sumSize += dtTask.hasDownloadLength;
		}
		DLHandler handler = getTaskHandler(mDLTask);
		if (localFile.exists()) {
//			if (localFile.length() == mDLTask.totalSize
//					&& sumSize == localFile.length()) { // ���Դ�ļ������ļ���С��һ��
				if (handler != null && !handler.isDLFileExist(mDLTask)) {
					localFile.delete();
					print("��⵽�ļ�����,����ͬ���ļ�������,����ɾ������!");
					// ����߳����ؼ�¼
					for (DLThreadTask dtTask : dtTasks) {
						dtTask.hasDownloadLength = 0;
					}
					mDBOperator.updateTasks(mDLTask, dtTasks);
				} else if (handler != null && handler.isDLFileExist(mDLTask)) {
					postTaskDoing(mDLTask);
					print("����" + mDLTask.downLoadUrl + " ��������,�ϴ��Ѿ�����!");
					return;
				}
			// } else {
			// print("��⵽�ļ�����,����ͬ���ļ�������,���˸��Ǵ���!");
			// }
		}

		try {
			for (DLThreadTask dtTask : dtTasks) {// �����߳̽�������
				// long downLength = task.getHasDownloadLength();

				if (!dtTask.isFinish()) {
					/*
					 * �ж��߳��Ƿ��Ѿ��������,����������� ����߳����� ���̳߳�
					 */
					DownloadCallable dc = new DownloadCallable(context,
							mDLTask, dtTask, mDBOperator);
					mExecutorService.execute(dc);
				}
			}

		} catch (Exception e) {
			print(e.toString());
			sendTaskProgressBroadCast("����ʧ��!", mDLTask);
			throw new Exception("file download fail");
		}
	}

	public static DLTask getDLTask(String url) {
		final Set<DLTask> tasks = allTasks.keySet();
		for (DLTask task : tasks) {
			if (task.downLoadUrl != null && task.downLoadUrl.equals(url)) {
				return task;
			}
		}
		return null;
	}

	/**
	 * ��ȡ�ļ���
	 */
	private String getFileName(HttpURLConnection conn, String downloadPath) {
		String filename = downloadPath
				.substring(downloadPath.lastIndexOf('/') + 1);
		print("ͨ��HttpURLConnection����ļ��� : " + filename);
		if (filename == null || "".equals(filename.trim())) {// �����ȡ�����ļ�����
			for (int i = 0;; i++) {
				String mine = conn.getHeaderField(i);
				if (mine == null)
					break;
				if ("content-disposition".equals(conn.getHeaderFieldKey(i)
						.toLowerCase())) {
					Matcher m = Pattern.compile(".*filename=(.*)").matcher(
							mine.toLowerCase());
					if (m.find())
						return m.group(1);
				}
			}
			filename = UUID.randomUUID() + ".tmp";// Ĭ��ȡһ���ļ���
		}
		print("�ļ��� Ϊ:" + filename);
		return filename;
	}

	private DLHandler getTaskHandler(DLTask mDLTask) {
		if (allCallbacks != null) {
			return allCallbacks.get(mDLTask);
		}
		return null;
	}

	/**
	 * ������ɺ� ��ʲô
	 * 
	 * @param paramDLTask
	 */
	private void postTaskDoing(DLTask mDLTask) {
		long deffTime = mDLTask.createTime - new Date().getTime();
		ZWLogger.i(Downloader.class, "��������" + mDLTask.downLoadUrl
				+ "������� \n ��ʱ:" + (deffTime / 1000L) + "s  ʵ�ʼ�¼��ʱ�䣺"
				+ mDLTask.costTime / 1000L);

		mDLTask.states.set(DLConstant.TASK_SUCESS);
		sendTaskProgressBroadCast(null, mDLTask);
		// if(){
		// mNotificationManager.cancel(NOTIFICATION_ID);
		// }

		Intent intent = BaseUtil.getOpenFileIntent(mDLTask.getSavePath(),
				context);
		// showNotify(intent, mDLTask.fileName);

		DLHandler task = getTaskHandler(mDLTask);
		if (task != null) {
			task.postDownLoadingOnUIThread(mDLTask);
		}

		if (mDLTask.isAutoOpen) {
			try {
				this.context.startActivity(intent);
				return;
			} catch (Exception exception) {
				Toast.makeText(this.context, "���ļ������޷���!", 1).show();
				if (task != null) {
					task.openFileErrorOnOtherThread(mDLTask, exception);
				}
			}
		}

		allTasks.remove(mDLTask);
		allCallbacks.remove(mDLTask);
		errorTasks.remove(mDLTask);
	}

	private void print(String message) {
		ZWLogger.i(this, message);
	}

	/**
	 * ������������
	 * 
	 * @param mDLTask
	 * @param delayTime
	 */
	private final void retryErrorTask(final DLTask mDLTask, long delayTime) {
		Timer localTimer = new Timer();
		TimerTask timeTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				print("����:" + mDLTask.downLoadUrl + " ���ڿ�ʼ��������!");
				Intent intent = new Intent(context, DownloadService.class);
				intent.putExtra("d", mDLTask);
				context.startService(intent);
			}
		};
		localTimer.schedule(timeTask, delayTime);
		print("����:" + mDLTask.downLoadUrl + "  " + delayTime + "ms����������!");
	}

	/**
	 * ���͹㲥
	 * */
	private void sendTaskProgressBroadCast(String info, DLTask mDLTask) {
		if (mDLTask.isSendBrocadcast) {
			Intent intent = new Intent();
			intent.setAction(DLConstant.BROADCAST_TASK);
			intent.putExtra(DLConstant.DL_TASK_OBJ, mDLTask);
			intent.putExtra(DLConstant.DL_TASK_MSG, info);
			this.context.sendBroadcast(intent);
		}
	}

	public void destroyDownloader() {
		this.mExecutorService.shutdown();
		this.splitExecutor.shutdown();
		this.mExecutorService = null;
		this.splitExecutor = null;
		allTasks.clear();
		this.errorTasks.clear();
		this.mDBOperator = null;
	}

	/**
	 * ���ִ�����ʲô
	 * 
	 * @param errorInfo
	 * @param mDLTask
	 * @param mException
	 */
	public void errorDoing(String errorInfo, DLTask mDLTask,
			Exception mException) {
		sendTaskProgressBroadCast(errorInfo, mDLTask);
		allTasks.remove(mDLTask);
		mDLTask.setErrorMessage(errorInfo);
		mDBOperator.updateTask(mDLTask);
		DLHandler mDLHandler = getTaskHandler(mDLTask);
		if (mDLHandler != null) {
			long delayTime = 0L;
			switch (mDLHandler.downLoadError(mDLTask, mException)) {
			case DLConstant.ERROR_AGAIN_ONCE:
				if (errorTasks.containsKey(mDLTask)) {
					errorTasks.remove(mDLTask);
				} else {
					delayTime = 30000L;
					errorTasks.put(mDLTask, 1);
				}
				break;
			case DLConstant.ERROR_AGAIN_HEARTBEAT:
				int lastCount = errorTasks.get(mDLTask).intValue();
				lastCount++;
				switch (lastCount) {
				case 1:
					delayTime = 30000L;
					break;
				case 2:
					delayTime = 60000L;
					break;
				case 3:
					delayTime = 300000L;
					break;
				case 4:
					delayTime = 600000L;
					break;
				default:

					break;
				}
				errorTasks.put(mDLTask, lastCount);
				break;
			default:
				break;
			}

			if (delayTime != 0) {
				retryErrorTask(mDLTask, delayTime);
			}
		}

	}

	/**
	 * ��ȡHttp��Ӧͷ�ֶ�
	 * 
	 * @param http
	 * @return
	 */
	public Map<String, String> getHttpResponseHeader(HttpURLConnection http) {
		Map<String, String> header = new LinkedHashMap<String, String>();
		for (int i = 0;; i++) {
			String mine = http.getHeaderField(i);
			if (mine == null)
				break;
			header.put(http.getHeaderFieldKey(i), mine);
		}
		return header;
	}

	/**
	 * ��ӡHttpͷ�ֶ�
	 * 
	 * @param http
	 */
	public void printResponseHeader(HttpURLConnection http) {
		Map<String, String> header = getHttpResponseHeader(http);
		for (Map.Entry<String, String> entry : header.entrySet()) {
			String key = entry.getKey() != null ? entry.getKey() + ":" : "";
			print(key + entry.getValue());
		}
	}

	public void spliteTask(DLTask mDLTask) {
		mDLTask.states.set(DLConstant.TASK_RUN);

		DLHandler mDLHandler = getTaskHandler(mDLTask);
		if (mDLTask.dlThreadNum > 0) {
			if (mDLTask.dlThreadNum > maxThreadPoolSize) {
				mDLTask.dlThreadNum = 1;
				sendTaskProgressBroadCast("���������߳������쳣,ʹ��Ĭ���߳���������!", mDLTask);
			}
		}

		Set<DLThreadTask> dtTasks = mDBOperator.getDownloadTaskByPath(mDLTask
				.hashCode());
		sendTaskProgressBroadCast("�����ݿ��л�ȡ������Ϣ����:" + dtTasks.size(), mDLTask);

		//
		if (dtTasks.size() > 0 && dtTasks.size() != mDLTask.dlThreadNum) {
			if (mDLHandler != null) {
				switch (mDLHandler.threadNumConflictOnOtherThread(mDLTask, dtTasks.size())) {
				case DLConstant.CONFLICT_LAST:
					DLTask dbDLTask = mDBOperator
							.getTaskByPath(mDLTask.downLoadUrl);
					mDLTask = dbDLTask;
					break;
				case DLConstant.CONFLICT_RETURN:
					print("�����߳���Ŀ����һ�β�����,�û������˳�����!");
					return;
				default:
					// �����Ϊ��
					mDBOperator.delete(mDLTask.hashCode());
					dtTasks.clear();
					break;
				}
			} else {
				// �����Ϊ��
				mDBOperator.delete(mDLTask.hashCode());
				dtTasks.clear();
			}
		}

		// ��ʼ�������߳�
		boolean isTaskSizeNoMatch = false;
		if (dtTasks.size() == 0) {
			for (int i = 0; i < mDLTask.dlThreadNum; i++) {
				DLThreadTask dtt = new DLThreadTask();
				dtt.threadId = i;
				dtt.taskHashCode = dtTasks.hashCode();
				dtTasks.add(dtt);
			}
			mDBOperator.saveThTasks(mDLTask.hashCode(), dtTasks);
			isTaskSizeNoMatch = true;
		}
		allTasks.put(mDLTask, dtTasks);

		/*
		 * ���� ��ȡ�ļ���Ϣ
		 */
		HttpURLConnection conn = null;
		try {
			File saveDir = null;
			if (mDLTask.savePath != null) {
				saveDir = new File(mDLTask.savePath);
				if (!saveDir.exists()) {
					saveDir.mkdir();
				}
			} else {
				mDLTask.savePath = defaultSavePath.getPath();
			}

			URL url = new URL(mDLTask.downLoadUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty(
					"Accept",
					"image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty("Referer", mDLTask.downLoadUrl);
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty(
					"User-Agent",
					"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.connect();
			if (conn.getResponseCode() == 200) {
				long fileSize = conn.getContentLength();// ������Ӧ��ȡ�ļ���С
				if(fileSize <= 0){
					fileSize = BaseUtil.parseLong(conn.getHeaderField("File-Length"));
				}
				sendTaskProgressBroadCast("�������ļ���С:" + fileSize, mDLTask);
				mDLTask.totalSize = fileSize;
				if (fileSize <= 0) {
					Exception e = new Exception("�޷�ͨ���������ļ���С!");
					errorDoing("�������ļ���С:" + fileSize, mDLTask, e);
					throw e;
				}

				// ����ÿ���߳�Ӧ�����ص����ݳ���
				long avgSize = fileSize / mDLTask.dlThreadNum;
				HashMap<Integer, Long> block = new HashMap<Integer, Long>();
				for (int i = 0; i < mDLTask.dlThreadNum; i++) {
					if (i == mDLTask.dlThreadNum - 1) {
						block.put(i, avgSize
								+ (fileSize - (avgSize * mDLTask.dlThreadNum)));

					} else {
						block.put(i, avgSize);
					}
					print("��" + (i + 1) + "�߳���Ҫ���صĴ�СΪ" + block.get(i));
					sendTaskProgressBroadCast("��" + (i + 1) + "�߳���Ҫ���صĴ�СΪ"
							+ block.get(i), mDLTask);
				}
				
		
				if(mDLTask.fileName == null){
					mDLTask.fileName = getFileName(conn, mDLTask.downLoadUrl);
				} else {
					print("�����ļ��� : " + mDLTask.fileName);
				}
				
				// File saveFile = new File(mDLTask.savePath, fileName);//
				// ���������ļ�
				mDLTask.totalSize = fileSize;
				if (isTaskSizeNoMatch) {
					/*
					 * Դ�ļ������� �����ؼ�¼ ������������
					 */
					for (DLThreadTask dtTask : dtTasks) {
						dtTask.breakPointPosition = avgSize * dtTask.threadId;
						dtTask.downloadBlock = block.get(dtTask.threadId);
						dtTask.taskHashCode = mDLTask.hashCode();
						// tasks.get(i).setGuid(UUID.randomUUID().toString());
						dtTask.costTime = 0;
						dtTask.hasDownloadLength = 0;
						// tasks.add(tasks.get(i));
					}

				} else {
					// ���ݿ������ؼ�¼
					for (DLThreadTask dtTask : dtTasks) {
						dtTask.breakPointPosition = avgSize * dtTask.threadId;
						dtTask.downloadBlock = block.get(dtTask.threadId);
						dtTask.taskHashCode = mDLTask.hashCode();

						sendTaskProgressBroadCast("�������ƣ�" + mDLTask.fileName
								+ "  �����߳�===>" + dtTask.threadId
								+ "  �Ѿ����س���====>" + dtTask.hasDownloadLength,
								mDLTask);
					}
				}
				mDBOperator.updateTask(mDLTask);
				mDBOperator.updateTasks(mDLTask, dtTasks);

				download(mDLTask);
			} else {
				String errorInfo = "��������ַ��������Ӧ!";
				Exception e = new Exception(errorInfo);
				errorDoing(errorInfo, mDLTask, e);
				throw e;
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			String errorInfo = "����URL��ַ���ִ���!";
			errorDoing(errorInfo, mDLTask, e);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			String errorInfo = "�������ݴ����쳣!";
			errorDoing(errorInfo, mDLTask, e);
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			String errorInfo = "���ص�ַ����ʧ��,��ȷ�����ص�ַ�Ƿ���Ч";
			errorDoing(errorInfo, mDLTask, e);
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
}
