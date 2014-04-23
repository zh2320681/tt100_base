package cn.tt100.base.download;

import cn.tt100.base.download.bo.DLTask;

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
	 * @return 是否删除
	 */
	public boolean isDLFileExist(DLTask task);

	/**
	 * 下载过程中 做什么
	 * @param task
	 */
	public void postDownLoading(DLTask task);

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
}
