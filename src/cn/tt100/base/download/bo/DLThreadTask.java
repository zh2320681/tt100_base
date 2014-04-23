package cn.tt100.base.download.bo;

public class DLThreadTask {
	//断点位置
	public volatile long breakPointPosition;
	//花费时间
	public int costTime;
	//要下载的长度
	public long downloadBlock;
	//已经下载的长度
	public volatile long hasDownloadLength;
	//数据库的主键
	public int idCode;

	public int taskHashCode;
	//线程的id
	public int threadId;

	public boolean isFinish() {
		if (hasDownloadLength >= downloadBlock){
			return true;
		}
		return false;
	}

	public void setDownloadBlock(long paramLong) {
		this.downloadBlock = paramLong;
	}

	public void setIdCode() {
		this.idCode = taskHashCode * 10+threadId;
	}

}
