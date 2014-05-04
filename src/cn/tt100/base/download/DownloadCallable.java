package cn.tt100.base.download;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import cn.tt100.base.download.bo.DLTask;
import cn.tt100.base.download.bo.DLThreadTask;
import cn.tt100.base.download.db.DLDatabaseHelper;
import cn.tt100.base.util.ZWLogger;

public class DownloadCallable implements Runnable {
	private Context context;

	private long beginTime;
	private int controlUpdateCache = 0;
	private DLThreadTask dtTask;
	private DLDatabaseHelper mDBOperator;

	public Object obj;
	private DLTask task;

	public DownloadCallable(Context context, DLTask mDLTask,
			DLThreadTask mDLThreadTask,
			DLDatabaseHelper mDLDatabaseHelper) {
		this.obj = new Object();;
		this.task = mDLTask;
		this.context = context;
		this.dtTask = mDLThreadTask;
		this.controlUpdateCache = 0;
		this.mDBOperator = mDLDatabaseHelper;
	}

	private void sendBroadCast(String info) {
		if (task.isSendBrocadcast) {
			Intent intent = new Intent();
			intent.setAction("BROADCAST_TASK");
			intent.putExtra("d", task);
			intent.putExtra("e", info);
			context.sendBroadcast(intent);
		}
		ZWLogger.printLog(DownloadCallable.class, "下载的文件名: "+task.fileName+" 下载线程id："+dtTask.threadId
				+" 已经下载#####################===>"+dtTask.hasDownloadLength+"/"+dtTask.downloadBlock);
	}

	public DLThreadTask getDtTask() {
		return this.dtTask;
	}

	public DLTask getTask() {
		return this.task;
	}

	@Override
	public void run() {
		if(!dtTask.isFinish()){//未下载完成
			task.states.set(DLConstant.TASK_RUN);
			task.setErrorMessage("");
			
			beginTime = System.currentTimeMillis()/1000;
			InputStream inStream =null;
			RandomAccessFile threadfile = null;
			try {
				URL dlUrl = new URL(task.downLoadUrl);
				HttpURLConnection http = (HttpURLConnection) dlUrl.openConnection();
				http.setConnectTimeout(10000);
				http.setReadTimeout(80000);
				http.setRequestMethod("GET");
				http.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
				http.setRequestProperty("Accept-Language", "zh-CN");
				http.setRequestProperty("Referer", dlUrl.toString()); 
				http.setRequestProperty("Charset", "UTF-8");
				long startPos = dtTask.breakPointPosition;
				long endPos = dtTask.breakPointPosition - dtTask.hasDownloadLength + dtTask.downloadBlock-1;
//					startPos = ((downloader.getFileSize()/4)*3) + downLength;//开始位置
//					endPos = downloader.getFileSize();//结束位置
//				}else{
//					startPos = block * (threadId - 1) + downLength;//开始位置
//					endPos = block * threadId -1;//结束位置
//				}
				
				ZWLogger.printLog(DownloadCallable.this, "线程"+dtTask.threadId+"开始下载的位置 ===>"+startPos+"   下载结束的位置===>"+
						endPos);
				http.setRequestProperty("Range", "bytes=" + startPos + "-"+ endPos);//设置获取实体数据的范围
				http.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
				http.setRequestProperty("Connection", "Keep-Alive");
				
				inStream = http.getInputStream();
				byte[] buffer = new byte[1024];
				int offset = 0;
//				System.out.println("task.getSavePath()==>"+task.getSavePath());
				threadfile = new RandomAccessFile(task.getSavePath(), "rwd");
				threadfile.seek(startPos);
				while ((offset = inStream.read(buffer, 0, 1024)) != -1) {
//					if(task.getStates() == DownloadThreadTask.TASK_PAUSE){
//						Log.i(TAG,task.getFileName()+"用户手动关闭线程 " + task.getThreadId());
//						this.wait();
//						Log.i(TAG,task.getFileName()+"用户手动关闭线程运行 " + task.getThreadId());
//					}
//					else if(task.getStates() == DownloadThreadTask.TASK_RUN){
//						this.notify();
//					}
					if(DownloadService.isServiceShutDown.get()){
						task.setErrorMessage("下载器关闭!");
						sendBroadCast("下载器关闭!");
						threadfile.close();
						inStream.close();
						return;
					}
					
					if(task.states.get() == DLConstant.TASK_PAUSE){
						task.setErrorMessage("用户暂停下载任务!");
						sendBroadCast("用户暂停下载任务!");
						threadfile.close();
						inStream.close();
						return;
					}
					
					if(task.states.get() == DLConstant.TASK_ERROR){
						task.setErrorMessage("其他下载线程出现错误!");
						sendBroadCast("其他下载线程出现错误!");
						threadfile.close();
						inStream.close();
						return;
					}
					
					threadfile.write(buffer, 0, offset);
					dtTask.hasDownloadLength += offset;
//					downLength += offset;
					controlUpdateCache++;
					if(controlUpdateCache >= 200){
//						downloader.update(this.threadId, dataBuffer.get(dataBuffer.size()-1));
//						dataBuffer.clear();
						dtTask.costTime += (System.currentTimeMillis()/1000-beginTime);
						task.setErrorMessage("");
						mDBOperator.updateThreadTask(dtTask);
						controlUpdateCache = 0;
						beginTime = System.currentTimeMillis()/1000;
						sendBroadCast("");
					}
				}
				
				dtTask.costTime += (System.currentTimeMillis()/1000-beginTime);
				task.setErrorMessage("");
				mDBOperator.updateThreadTask(dtTask);
				controlUpdateCache = 0;
				beginTime = System.currentTimeMillis()/1000;
				
				threadfile.close();
				inStream.close();
				//任务完成检测
				if(dtTask.isFinish()){
//					this.isShutDownThread = true;
					sendBroadCast("下载的文件名: " + task.fileName + " 下载线程id："+dtTask.threadId+" 下载完成!");
					task.states.set(DLConstant.TASK_SUCESS);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				dtTask.hasDownloadLength = -1;
				sendBroadCast("下载的文件名: " + task.fileName + " 下载线程id："+dtTask.threadId+" 下载出现异常!");
				task.setErrorMessage("下载时网络出现异常!");
//				sendBroadCast("下载时网络出现异常!");
				mDBOperator.updateTask(task);
			}finally{
				if(inStream!=null){try {inStream.close();} catch (IOException e) {}} 
				if(threadfile!=null){try {threadfile.close();} catch (IOException e) {}}
			}
		}
	}
}
