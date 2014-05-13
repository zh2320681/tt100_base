package cn.tt100.base.download.bo;

import cn.tt100.base.ZWBo;
import cn.tt100.base.annotation.DatabaseField;
import cn.tt100.base.annotation.DatabaseTable;

@DatabaseTable
public class DLThreadTask extends ZWBo{
	//断点位置
	@DatabaseField
	public volatile long breakPointPosition;
	//花费时间
	@DatabaseField
	public int costTime;
	//要下载的长度
	@DatabaseField
	public long downloadBlock;
	//已经下载的长度
	@DatabaseField
	public volatile long hasDownloadLength;
	//数据库的主键
	@DatabaseField(id = true)
	public int idCode;

	@DatabaseField
	public int taskHashCode;
	//线程的id
	@DatabaseField
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
