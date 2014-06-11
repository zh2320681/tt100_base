package cn.shrek.base.download;

import cn.shrek.base.download.bo.DLTask;

public interface DLHandler {
	/**
	 * 下载时候出现错误
	 * @param task
	 * @param exception
	 * @return
	 */
	public int downLoadError(DLTask task,
			Exception exception);

	/**
	 * 下载时候 文件存在 
	 * @param task
	 * @return 是否不删除 true 不删除   false 删除
	 */
	public boolean isDLFileExist(DLTask task);

	/**
	 * 下载完成 做什么
	 * @param task
	 */
	public void postDownLoading(DLTask task);
	
	/**
	 * 下载过程中 返回
	 */
	public void downLoadingProgress(DLTask task,int hasDownSize);

	/**
	 * 下载前做什么<UI动作在主线程做>
	 * @param task
	 */
	public void preDownloadDoing(DLTask task);

	/**
	 * sdcard没有的时候 做什么动作
	 * @param task
	 * @return 是否下载
	 */
	public boolean sdcardNoExist(DLTask task);

	/**
	 * 下载线程 冲突 怎么办
	 * @param task
	 * @param oldThreadNum  原来几个线程
	 * @return  CONFLICT_DEFAULT
	 */
	public int threadNumConflict(DLTask task, int oldThreadNum);
	
	/**
	 * 下载完成后 打开文件失败
	 * @param task
	 * @param e
	 */
	public void openFileError(DLTask task, Exception e);
}
