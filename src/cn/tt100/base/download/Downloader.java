package cn.tt100.base.download;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.RemoteViews;
import android.widget.Toast;
import cn.tt100.base.R;
import cn.tt100.base.download.bo.DLTask;
import cn.tt100.base.download.bo.DLThreadTask;
import cn.tt100.base.download.db.DLDatabaseHelper;
import cn.tt100.base.util.BaseLog;
import cn.tt100.base.util.BaseUtil;

public class Downloader{
  public static final int NOTIFICATION_ID = 0x22;
  //所有任务的回掉
  public static Map<DLTask, DLHandler> allCallbacks;
  //所有任务和 下载的线程
  public static volatile Map<DLTask, Set<DLThreadTask>> allTasks;
  
  //默认的 路径
  private static File defaultSavePath;
  
  private static final int keepAliveTime = 3; //线程池
  private static final int maxThreadPoolSize = 6; //线程池最大线程数
  
  private Context context;
  //错误的任务  用于任务失败后 重试
  private Map<DLTask, Integer> errorTasks;
  private Notification infoNotification;
  
  private DLDatabaseHelper mDBOperator;
  //执行任务线程池
  private ExecutorService mExecutorService;
  
  private NotificationManager mNotificationManager;
  private Random mRandom;
  private SimpleDateFormat mSimpleDateFormat;
  //拆分任务线程池
  private ExecutorService splitExecutor;

  public Downloader(Context mContext, NotificationManager manager){
	this.mRandom = new Random();
    this.context = mContext;
    this.mNotificationManager = manager;
    if (allTasks == null){
    	allTasks = Collections.synchronizedMap(new HashMap<DLTask, Set<DLThreadTask>>());
    }
    this.errorTasks = Collections.synchronizedMap(new HashMap<DLTask, Integer>());
    
    defaultSavePath = new File(Environment.getExternalStorageDirectory(), "baseDLLoader");
    if (!defaultSavePath.exists()){
    	 boolean bool = defaultSavePath.mkdir();
    	 if(!bool)
    		 BaseLog.printLog(Downloader.this, "baseDLLoader文件夹创建失败!");
    }
   
    this.infoNotification = new Notification(R.layout.update_notify, "下载完成", System.currentTimeMillis());
      
    mExecutorService = new ThreadPoolExecutor(maxThreadPoolSize,
			maxThreadPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>()) {
    	
    	@Override
		protected void afterExecute(Runnable r, Throwable t) {
    		
    	}
    };

    this.splitExecutor = Executors.newFixedThreadPool(1);;
 
    mDBOperator = new DLDatabaseHelper(mContext);
    this.mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
  }

  
  private void showNotify(Intent i, String fileName) {
		infoNotification.contentView = new RemoteViews(
				context.getPackageName(), R.layout.update_notify);
		infoNotification.contentView.setTextViewText(R.id.notifyUI_progress,
				"点击打开下载内容");
		infoNotification.flags = Notification.FLAG_AUTO_CANCEL;
		infoNotification.contentView.setTextViewText(R.id.notifyUI_info,
				fileName + "下载完成");
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(context,
				R.string.app_name, i, PendingIntent.FLAG_UPDATE_CURRENT);
		infoNotification.contentIntent = contentIntent;
		if (mNotificationManager != null) {
			mNotificationManager.notify(NOTIFICATION_ID+mRandom.nextInt(1000), infoNotification);
		} else {
			mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(NOTIFICATION_ID+mRandom.nextInt(1000), infoNotification);
		}
	}
  
  
  /**
   * 添加任务
   * @param paramDLTask
   */
  public void addTask(final DLTask mDLTask){
    DLHandler mHandler = getTaskHandler(mDLTask);
    if (!BaseUtil.isSdCardExist()){
      Toast.makeText(this.context, "SD卡不存在,无法下载.", 1).show();
      if (mHandler != null && !mHandler.sdcardNoExist(mDLTask)){
    	  return;
      }
    }
    
    final Set<DLTask> tasks = allTasks.keySet();
    for(DLTask task : tasks){
    	if(task.equals(mDLTask) && task.states.get() == DLConstant.TASK_RUN){
    		Toast.makeText(context, "下载列表中已经存在该下载任务,如需重新下载,请删除原任务后,再次提交下载任务.", Toast.LENGTH_SHORT).show();
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

  
  private void download(DLTask paramDLTask)
    throws Exception
  {
    if (paramDLTask.savePath == null)
    {
      String str1 = defaultSavePath.getPath();
      paramDLTask.savePath = str1;
    }
    String str2 = paramDLTask.savePath;
    String str3 = paramDLTask.fileName;
    File localFile = new File(str2, str3);
    while (true)
    {
      long l2;
      Iterator localIterator;
      try
      {
        RandomAccessFile localRandomAccessFile = new RandomAccessFile(localFile, "rw");
        if (paramDLTask.totalSize <= 0L)
          continue;
        long l1 = paramDLTask.totalSize;
        localRandomAccessFile.setLength(l1);
        localRandomAccessFile.close();
        l2 = 0L;
        Map localMap1 = allTasks;
        DLTask localDLTask1 = paramDLTask;
        if (!localMap1.containsKey(localDLTask1))
          break label717;
        Map localMap2 = allTasks;
        DLTask localDLTask2 = paramDLTask;
        Set localSet1 = (Set)localMap2.get(localDLTask2);
        localIterator = localSet1.iterator();
        if (localIterator.hasNext())
          continue;
        if (l2 != 0L)
          continue;
        StringBuilder localStringBuilder1 = new StringBuilder("任务:");
        String str4 = paramDLTask.downLoadUrl;
        String str5 = str4 + " 是新任务!";
        BaseLog.printLog(Downloader.class, str5);
        long l3 = paramDLTask.totalSize;
        if (l2 <= l3)
          continue;
        localIterator = localSet1.iterator();
        if (!localIterator.hasNext())
        {
          if (!localFile.exists())
            continue;
          long l4 = localFile.length();
          long l5 = paramDLTask.totalSize;
          if (l4 == l5)
            break label484;
          boolean bool1 = localFile.delete();
          print("检测到文件夹中,有相同的文件名存在,做了删除处理!");
          DLDatabaseHelper localDLDatabaseHelper1 = this.mDBOperator;
          Map localMap3 = allTasks;
          DLTask localDLTask3 = paramDLTask;
          Set localSet2 = (Set)localMap3.get(localDLTask3);
          DLTask localDLTask4 = paramDLTask;
          localDLDatabaseHelper1.updateTasks(localDLTask4, localSet2);
          localIterator = localSet1.iterator();
          if (localIterator.hasNext())
            break label637;
          return;
          long l6 = ((DLThreadTask)localIterator.next()).hasDownloadLength;
          l2 += l6;
          continue;
          StringBuilder localStringBuilder2 = new StringBuilder("任务:");
          String str6 = paramDLTask.downLoadUrl;
          String str7 = str6 + " 是继续上一次下载! 下载数目为:" + l2 + "B";
          BaseLog.printLog(Downloader.class, str7);
          continue;
        }
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
        String str8 = localException.toString();
        print(str8);
        Downloader localDownloader = this;
        DLTask localDLTask5 = paramDLTask;
        localDownloader.errorDoing("下载失败!", localDLTask5, localException);
        throw new Exception("file download fail");
      }
      ((DLThreadTask)localIterator.next()).hasDownloadLength = 0L;
      continue;
      label484: long l7 = paramDLTask.totalSize;
      if (l2 == l7)
      {
        DLHandler localDLHandler = getTaskHandler(paramDLTask);
        if (localDLHandler != null)
        {
          DLTask localDLTask6 = paramDLTask;
          if (!localDLHandler.isDLFileExist(localDLTask6))
          {
            StringBuilder localStringBuilder3 = new StringBuilder("任务:");
            String str9 = paramDLTask.downLoadUrl;
            String str10 = str9 + " 不用下载,上次已经存在!";
            BaseLog.printLog(Downloader.class, str10);
            postTaskDoing(paramDLTask);
            continue;
          }
        }
        boolean bool2 = localFile.delete();
        print("检测到文件夹中,有相同的文件名存在,做了覆盖处理!");
        continue;
      }
      StringBuilder localStringBuilder4 = new StringBuilder("任务:");
      String str11 = paramDLTask.downLoadUrl;
      String str12 = str11 + " 是继续上一次下载哦!";
      BaseLog.printLog(Downloader.class, str12);
      continue;
      label637: DLThreadTask localDLThreadTask = (DLThreadTask)localIterator.next();
      long l8 = localDLThreadTask.hasDownloadLength;
      long l9 = localDLThreadTask.downloadBlock;
      if (l8 > l9);
      Context localContext = this.context;
      DLDatabaseHelper localDLDatabaseHelper2 = this.mDBOperator;
      DLTask localDLTask7 = paramDLTask;
      DownloadCallable localDownloadCallable = new DownloadCallable(localContext, localDLTask7, localDLThreadTask, localDLDatabaseHelper2);
      this.mExecutorService.execute(localDownloadCallable);
      continue;
      label717: StringBuilder localStringBuilder5 = new StringBuilder("任务:");
      String str13 = paramDLTask.downLoadUrl;
      String str14 = str13 + " 在Map里面丢失,请检测!";
      BaseLog.printLog(Downloader.class, str14);
    }
  }

  public static DLTask getDLTask(String paramString)
  {
    Iterator localIterator = allTasks.keySet().iterator();
    DLTask localDLTask;
    if (!localIterator.hasNext())
      localDLTask = null;
    while (true)
    {
      return localDLTask;
      localDLTask = (DLTask)localIterator.next();
      if (!localDLTask.downLoadUrl.equals(paramString))
        break;
    }
  }

  /** @deprecated */
  private String getFileName(HttpURLConnection paramHttpURLConnection, String paramString)
  {
    monitorenter;
    try
    {
      int i = paramString.lastIndexOf('/') + 1;
      String str1 = paramString.substring(i);
      String str2 = "通过HttpURLConnection获得文件名 : " + str1;
      print(str2);
      int j;
      if (str1 != null)
      {
        String str3 = str1.trim();
        if (!"".equals(str3));
      }
      else
      {
        j = 0;
      }
      while (true)
      {
        String str4 = paramHttpURLConnection.getHeaderField(j);
        if (str4 == null)
        {
          StringBuilder localStringBuilder = new StringBuilder();
          UUID localUUID = UUID.randomUUID();
          str1 = localUUID + ".tmp";
          String str5 = "文件名 为:" + str1;
          print(str5);
        }
        String str8;
        for (Object localObject1 = str1; ; localObject1 = str8)
        {
          return localObject1;
          String str6 = paramHttpURLConnection.getHeaderFieldKey(j).toLowerCase();
          if (!"content-disposition".equals(str6))
            break;
          Pattern localPattern = Pattern.compile(".*filename=(.*)");
          String str7 = str4.toLowerCase();
          Matcher localMatcher = localPattern.matcher(str7);
          if (!localMatcher.find())
            break;
          str8 = localMatcher.group(1);
        }
        j += 1;
      }
    }
    finally
    {
      monitorexit;
    }
    throw localObject2;
  }

  private DLHandler getTaskHandler(DLTask mDLTask){
    if (allCallbacks != null){
    	return allCallbacks.get(mDLTask);
    }
    return null;
  }

  private void postTaskDoing(DLTask paramDLTask)
  {
    Object localObject1 = allTasks.remove(paramDLTask);
    Object localObject2 = allCallbacks.remove(paramDLTask);
    Object localObject3 = this.errorTasks.remove(paramDLTask);
    long l1 = new Date().getTime();
    long l2 = paramDLTask.createTime;
    long l3 = l1 - l2;
    StringBuilder localStringBuilder1 = new StringBuilder("下载任务");
    String str1 = paramDLTask.downLoadUrl;
    StringBuilder localStringBuilder2 = localStringBuilder1.append(str1).append("都完成了 \n 耗时:");
    long l4 = l3 / 1000L;
    StringBuilder localStringBuilder3 = localStringBuilder2.append(l4).append("s  实际记录耗时间：");
    long l5 = paramDLTask.costTime / 1000L;
    String str2 = l5;
    BaseLog.printLog(Downloader.class, str2);
    Object localObject4 = allTasks.remove(paramDLTask);
    paramDLTask.states.set(5);
    sendTaskProgressBroadCast(null, paramDLTask);
    this.mNotificationManager.cancel(18);
    File localFile = paramDLTask.getSavePath();
    Context localContext = this.context;
    Intent localIntent = BaseUtil.getOpenFileIntent(localFile, localContext);
    String str3 = paramDLTask.fileName;
    showNotify(localIntent, str3);
    if (paramDLTask.isAutoOpen);
    try
    {
      this.context.startActivity(localIntent);
      DLHandler localDLHandler = getTaskHandler(paramDLTask);
      if (localDLHandler != null)
        localDLHandler.postDownLoading(paramDLTask);
      return;
    }
    catch (Exception localException)
    {
      while (true)
        Toast.makeText(this.context, "该文件类型无法打开!", 1).show();
    }
  }

  private void print(String paramString)
  {
    BaseLog.printLog(this, paramString);
  }

  private final void retryErrorTask(DLTask paramDLTask, long paramLong)
  {
    Timer localTimer = new Timer();
    Downloader.3 local3 = new Downloader.3(this, paramDLTask);
    localTimer.schedule(local3, 30000L);
    StringBuilder localStringBuilder = new StringBuilder("任务:");
    String str1 = paramDLTask.downLoadUrl;
    String str2 = str1 + "  " + paramLong + "ms后重试下载!";
    BaseLog.printLog(Downloader.class, str2);
  }

  /** @deprecated */
  private void sendTaskProgressBroadCast(String paramString, DLTask paramDLTask)
  {
    monitorenter;
    try
    {
      if (paramDLTask.isSendBrocadcast)
      {
        Intent localIntent1 = new Intent();
        Intent localIntent2 = localIntent1.setAction("BROADCAST_TASK");
        Intent localIntent3 = localIntent1.putExtra("d", paramDLTask);
        Intent localIntent4 = localIntent1.putExtra("e", paramString);
        this.context.sendBroadcast(localIntent1);
      }
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  

  
  public void destroyDownloader()
  {
    this.mExecutorService.shutdown();
    this.splitExecutor.shutdown();
    this.mExecutorService = null;
    this.splitExecutor = null;
    allTasks.clear();
    this.errorTasks.clear();
    this.mDBOperator = null;
  }

  /** @deprecated */
  public void errorDoing(String paramString, DLTask paramDLTask, Exception paramException)
  {
    monitorenter;
    while (true)
    {
      try
      {
        sendTaskProgressBroadCast(paramString, paramDLTask);
        Object localObject1 = allTasks.remove(paramDLTask);
        paramDLTask.setErrorMessage(paramString);
        this.mDBOperator.updateTask(paramDLTask);
        DLHandler localDLHandler = getTaskHandler(paramDLTask);
        if (localDLHandler == null)
          continue;
        int i = localDLHandler.downLoadError(paramDLTask, paramException);
        switch (i)
        {
        case 33:
        default:
          return;
        case 34:
          if (this.errorTasks.containsKey(paramDLTask))
          {
            Object localObject2 = this.errorTasks.remove(paramDLTask);
            continue;
          }
        case 35:
        }
      }
      finally
      {
        monitorexit;
      }
      long l1 = 30000L;
      retryErrorTask(paramDLTask, l1);
      Map localMap1 = this.errorTasks;
      Integer localInteger1 = Integer.valueOf(1);
      Object localObject4 = localMap1.put(paramDLTask, localInteger1);
      continue;
      int j = ((Integer)this.errorTasks.get(paramDLTask)).intValue();
      long l2 = 0L;
      switch (j)
      {
      default:
      case 1:
      case 2:
      case 3:
      case 4:
      }
      while (true)
      {
        if (j <= 4)
          break label269;
        Object localObject5 = this.errorTasks.remove(paramDLTask);
        break;
        l2 = 30000L;
        continue;
        l2 = 60000L;
        continue;
        l2 = 300000L;
        continue;
        l2 = 600000L;
      }
      label269: Map localMap2 = this.errorTasks;
      Integer localInteger2 = Integer.valueOf(j + 1);
      Object localObject6 = localMap2.put(paramDLTask, localInteger2);
      retryErrorTask(paramDLTask, l2);
    }
  }

  public Map<String, String> getHttpResponseHeader(HttpURLConnection paramHttpURLConnection)
  {
    LinkedHashMap localLinkedHashMap = new LinkedHashMap();
    int i = 0;
    while (true)
    {
      String str1 = paramHttpURLConnection.getHeaderField(i);
      if (str1 == null)
        return localLinkedHashMap;
      String str2 = paramHttpURLConnection.getHeaderFieldKey(i);
      Object localObject = localLinkedHashMap.put(str2, str1);
      i += 1;
    }
  }

  public void printResponseHeader(HttpURLConnection paramHttpURLConnection)
  {
    Iterator localIterator = getHttpResponseHeader(paramHttpURLConnection).entrySet().iterator();
    if (!localIterator.hasNext())
      return;
    Map.Entry localEntry = (Map.Entry)localIterator.next();
    String str1;
    if (localEntry.getKey() != null)
      str1 = String.valueOf((String)localEntry.getKey());
    for (String str2 = str1 + ":"; ; str2 = "")
    {
      String str3 = String.valueOf(str2);
      StringBuilder localStringBuilder = new StringBuilder(str3);
      String str4 = (String)localEntry.getValue();
      String str5 = str4;
      print(str5);
      break;
    }
  }

  public void spliteTask(DLTask mDLTask){
	mDLTask.states.set(DLConstant.TASK_RUN);
	
    DLHandler mDLHandler = getTaskHandler(mDLTask);
    if (mDLTask.dlThreadNum > 0){
      if (mDLTask.dlThreadNum > maxThreadPoolSize){
    	  mDLTask.dlThreadNum = 1;
    	  sendTaskProgressBroadCast("任务设置线程数量异常,使用默认线程数量下载!", mDLTask);
      }
    }

    Set<DLThreadTask> dtTasks = mDBOperator.getDownloadTaskByPath(mDLTask.hashCode());
    DLTask dbDLTask = mDBOperator.getTaskByPath(mDLTask.downLoadUrl);
    sendTaskProgressBroadCast("从数据库中获取下载信息条数:"+dtTasks.size(), mDLTask);
    
    //
    if(dtTasks.size() != mDLTask.dlThreadNum){
    	
    }
    int i1 = 0;
    DLTask localDLTask4;
    int i3;
    if (localDLHandler != null)
    {
      int i2 = localSet1.size();
      localDLTask4 = paramDLTask;
      i3 = i2;
    }
    int i9;
    switch (localDLHandler.threadNumConflict(localDLTask4, i3))
    {
    default:
      if (localSet1.size() <= 0)
        break;
      int i4 = localSet1.size();
      int i5 = paramDLTask.dlThreadNum;
      int i6 = i4;
      int i7 = i5;
      if (i6 == i7)
        break;
      DLDatabaseHelper localDLDatabaseHelper3 = this.mDBOperator;
      int i8 = paramDLTask.hashCode();
      localDLDatabaseHelper3.delete(i8);
      localSet1.clear();
    case 18:
      if (localSet1.size() == 0)
      {
        i1 = 1;
        i9 = 0;
      }
    case 19:
    }
    while (true)
    {
      int i10 = paramDLTask.dlThreadNum;
      HttpURLConnection localHttpURLConnection;
      if (i9 >= i10)
      {
        DLDatabaseHelper localDLDatabaseHelper4 = this.mDBOperator;
        int i11 = paramDLTask.hashCode();
        DLDatabaseHelper localDLDatabaseHelper5 = localDLDatabaseHelper4;
        int i12 = i11;
        Set localSet2 = localSet1;
        localDLDatabaseHelper5.saveThTasks(i12, localSet2);
        Map localMap = allTasks;
        DLTask localDLTask5 = paramDLTask;
        Set localSet3 = localSet1;
        Object localObject1 = localMap.put(localDLTask5, localSet3);
        localHttpURLConnection = null;
      }
      try
      {
        String str5 = paramDLTask.downLoadUrl;
        URL localURL = new URL(str5);
        if (paramDLTask.savePath != null)
        {
          File localFile1 = new java/io/File;
          String str6 = paramDLTask.savePath;
          File localFile2 = localFile1;
          String str7 = str6;
          localFile2.<init>(str7);
          if (!localFile1.exists())
            boolean bool1 = localFile1.mkdir();
          localHttpURLConnection = (HttpURLConnection)localURL.openConnection();
          int i13 = 5000;
          localHttpURLConnection.setConnectTimeout(i13);
          String str8 = "GET";
          localHttpURLConnection.setRequestMethod(str8);
          String str9 = "Accept";
          String str10 = "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*";
          localHttpURLConnection.setRequestProperty(str9, str10);
          String str11 = "Accept-Language";
          String str12 = "zh-CN";
          localHttpURLConnection.setRequestProperty(str11, str12);
          String str13 = paramDLTask.downLoadUrl;
          String str14 = "Referer";
          String str15 = str13;
          localHttpURLConnection.setRequestProperty(str14, str15);
          String str16 = "Charset";
          String str17 = "UTF-8";
          localHttpURLConnection.setRequestProperty(str16, str17);
          String str18 = "User-Agent";
          String str19 = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)";
          localHttpURLConnection.setRequestProperty(str18, str19);
          String str20 = "Connection";
          String str21 = "Keep-Alive";
          localHttpURLConnection.setRequestProperty(str20, str21);
          localHttpURLConnection.connect();
          int i14 = localHttpURLConnection.getResponseCode();
          int i15 = 200;
          if (i14 != i15)
            break label1567;
          l1 = localHttpURLConnection.getContentLength();
          String str22 = "请求获得文件大小:" + l1;
          Downloader localDownloader3 = this;
          String str23 = str22;
          DLTask localDLTask6 = paramDLTask;
          localDownloader3.sendTaskProgressBroadCast(str23, localDLTask6);
          if (l1 > 0L)
            break label819;
          String str24 = "请求服务地址下载无响应!";
          Exception localException1 = new Exception(str24);
          Downloader localDownloader4 = this;
          String str25 = "无法通过网络路径获得文件大小!";
          DLTask localDLTask7 = paramDLTask;
          localDownloader4.errorDoing(str25, localDLTask7, localException1);
          throw localException1;
        }
      }
      catch (MalformedURLException localMalformedURLException)
      {
        long l1;
        while (true)
        {
          Downloader localDownloader5 = this;
          String str26 = "创建URL地址出现错误!";
          DLTask localDLTask8 = paramDLTask;
          localDownloader5.errorDoing(str26, localDLTask8, localMalformedURLException);
          localMalformedURLException.printStackTrace();
          while (true)
          {
            return;
            BaseLog.printLog(Downloader.class, "下载线程数目与上一次不符合,用户设置退出下载!");
          }
          DLThreadTask localDLThreadTask1 = new DLThreadTask();
          int i16 = paramDLTask.hashCode();
          localDLThreadTask1.taskHashCode = i16;
          localDLThreadTask1.threadId = i9;
          boolean bool2 = localSet1.add(localDLThreadTask1);
          i9 += 1;
          break;
          File localFile3 = defaultSavePath;
        }
        long l2 = paramDLTask.dlThreadNum;
        long l3 = l1 / l2;
        ArrayList localArrayList = new ArrayList();
        i9 = 0;
        int i17 = paramDLTask.dlThreadNum;
        Iterator localIterator1;
        if (i9 >= i17)
        {
          String str27 = paramDLTask.downLoadUrl;
          Downloader localDownloader6 = this;
          String str28 = str27;
          str29 = localDownloader6.getFileName(localHttpURLConnection, str28);
          if (i1 != 0)
          {
            paramDLTask.fileName = str29;
            long l4 = new Date().getTime();
            paramDLTask.createTime = l4;
            paramDLTask.totalSize = l1;
            localIterator1 = localSet1.iterator();
          }
        }
        else
        {
          while (true)
          {
            if (!localIterator1.hasNext())
            {
              download(paramDLTask);
              if (localHttpURLConnection == null)
                break;
              localHttpURLConnection.disconnect();
              break;
              int i18 = paramDLTask.dlThreadNum + -1;
              long l7;
              if (i9 == i18)
              {
                long l5 = paramDLTask.dlThreadNum * l3;
                long l6 = l1 - l5;
                l7 = l3 + l6;
              }
              while (true)
              {
                StringBuilder localStringBuilder2 = new StringBuilder("第").append(i9).append("线程需要下载的大小为");
                long l8 = l7;
                String str30 = l8;
                Downloader localDownloader7 = this;
                String str31 = str30;
                DLTask localDLTask9 = paramDLTask;
                localDownloader7.sendTaskProgressBroadCast(str31, localDLTask9);
                Long localLong = Long.valueOf(l7);
                boolean bool3 = localArrayList.add(localLong);
                i9 += 1;
                break;
                l7 = l3;
              }
            }
            DLThreadTask localDLThreadTask2 = (DLThreadTask)localIterator1.next();
            int i19 = localDLThreadTask2.threadId;
            long l9 = ((Long)localArrayList.get(i19)).longValue();
            localDLThreadTask2.downloadBlock = l9;
            long l10 = 0L;
            localDLThreadTask2.hasDownloadLength = l10;
            long l11 = localDLThreadTask2.threadId * l3;
            localDLThreadTask2.breakPointPosition = l11;
            int i20 = 0;
            localDLThreadTask2.costTime = i20;
            StringBuilder localStringBuilder3 = new java/lang/StringBuilder;
            StringBuilder localStringBuilder4 = localStringBuilder3;
            String str32 = "下载名称：";
            localStringBuilder4.<init>(str32);
            StringBuilder localStringBuilder5 = localStringBuilder3.append(str29);
            String str33 = "  下载线程===>";
            StringBuilder localStringBuilder6 = localStringBuilder5.append(str33);
            int i21 = localDLThreadTask2.threadId;
            StringBuilder localStringBuilder7 = localStringBuilder6;
            int i22 = i21;
            StringBuilder localStringBuilder8 = localStringBuilder7.append(i22);
            String str34 = "  已经下载长度====>";
            StringBuilder localStringBuilder9 = localStringBuilder8.append(str34);
            long l12 = localDLThreadTask2.hasDownloadLength;
            StringBuilder localStringBuilder10 = localStringBuilder9;
            long l13 = l12;
            StringBuilder localStringBuilder11 = localStringBuilder10.append(l13);
            String str35 = "  断点位置====>";
            StringBuilder localStringBuilder12 = localStringBuilder11.append(str35);
            long l14 = localDLThreadTask2.breakPointPosition;
            StringBuilder localStringBuilder13 = localStringBuilder12;
            long l15 = l14;
            String str36 = l15;
            Downloader localDownloader8 = this;
            String str37 = str36;
            localDownloader8.print(str37);
          }
        }
      }
      catch (IOException localIOException)
      {
        String str29;
        while (true)
        {
          Downloader localDownloader9 = this;
          String str38 = "网络数据传输异常!";
          DLTask localDLTask10 = paramDLTask;
          localDownloader9.errorDoing(str38, localDLTask10, localIOException);
          localIOException.printStackTrace();
          if (localHttpURLConnection == null)
            continue;
          localHttpURLConnection.disconnect();
        }
        DLDatabaseHelper localDLDatabaseHelper6 = this.mDBOperator;
        String str39 = paramDLTask.downLoadUrl;
        paramDLTask = localDLDatabaseHelper6.getTaskByPath(str39);
        Iterator localIterator2 = localSet1.iterator();
        while (localIterator2.hasNext())
        {
          DLThreadTask localDLThreadTask3 = (DLThreadTask)localIterator2.next();
          StringBuilder localStringBuilder14 = new StringBuilder("下载名称：").append(str29).append("  下载线程===>");
          int i23 = localDLThreadTask3.threadId;
          StringBuilder localStringBuilder15 = localStringBuilder14.append(i23).append("  已经下载长度====>");
          long l16 = localDLThreadTask3.hasDownloadLength;
          StringBuilder localStringBuilder16 = localStringBuilder15.append(l16).append("  断点位置====>");
          long l17 = localDLThreadTask3.breakPointPosition;
          String str40 = l17;
          Downloader localDownloader10 = this;
          String str41 = str40;
          localDownloader10.print(str41);
        }
      }
      catch (Exception localException2)
      {
        while (true)
        {
          label819: Downloader localDownloader11 = this;
          String str42 = "下载地址连接失败,请确认下载地址是否有效";
          DLTask localDLTask11 = paramDLTask;
          localDownloader11.errorDoing(str42, localDLTask11, localException2);
          localException2.printStackTrace();
          if (localHttpURLConnection == null)
            continue;
          localHttpURLConnection.disconnect();
        }
        label1567: String str43 = "请求服务地址下载无响应!";
        Exception localException3 = new Exception(str43);
        Downloader localDownloader12 = this;
        String str44 = "请求服务地址下载无响应!";
        DLTask localDLTask12 = paramDLTask;
        localDownloader12.errorDoing(str44, localDLTask12, localException3);
        throw localException3;
      }
      finally
      {
        if (localHttpURLConnection != null)
          localHttpURLConnection.disconnect();
      }
    }
    throw localObject2;
  }
}
