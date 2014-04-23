package cn.tt100.base.download.bo;

public class DLThreadTask {
	//�ϵ�λ��
	public volatile long breakPointPosition;
	//����ʱ��
	public int costTime;
	//Ҫ���صĳ���
	public long downloadBlock;
	//�Ѿ����صĳ���
	public volatile long hasDownloadLength;
	//���ݿ������
	public int idCode;

	public int taskHashCode;
	//�̵߳�id
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
