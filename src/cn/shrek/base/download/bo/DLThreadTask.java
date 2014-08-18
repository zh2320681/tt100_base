package cn.shrek.base.download.bo;

import cn.shrek.base.ZWDatabaseBo;
import cn.shrek.base.annotation.DatabaseField;
import cn.shrek.base.annotation.DatabaseTable;

@DatabaseTable
public class DLThreadTask extends ZWDatabaseBo{
	//�ϵ�λ��
	@DatabaseField
	public volatile long breakPointPosition;
	//����ʱ��
	@DatabaseField
	public int costTime;
	//Ҫ���صĳ���
	@DatabaseField
	public long downloadBlock;
	//�Ѿ����صĳ���
	@DatabaseField
	public volatile long hasDownloadLength;
	//���ݿ������
	@DatabaseField(id = true)
	public int idCode;

	@DatabaseField
	public int taskHashCode;
	//�̵߳�id
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
