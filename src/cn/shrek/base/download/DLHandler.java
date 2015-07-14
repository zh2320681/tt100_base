package cn.shrek.base.download;

import cn.shrek.base.download.bo.DLTask;

public interface DLHandler {
	/**
	 * ����ʱ����ִ��� <��һ�������߳�>
	 * @param task
	 * @param exception
	 * @return
	 */
	public int downLoadError(DLTask task,
			Exception exception);

	/**
	 * ����ʱ�� �ļ����� 
	 * @param task
	 * @return �Ƿ�ɾ�� true ��ɾ��   false ɾ��
	 */
	public boolean isDLFileExist(DLTask task);

	/**
	 * ������� ��ʲô
	 * @param task
	 */
	public void postDownLoadingOnUIThread(DLTask task);
	
	/** 
	 * ���ع����� ���� <��UI�߳�>
	 */
	public void downLoadingProgressOnOtherThread(DLTask task,int hasDownSize);

	/**
	 * ����ǰ��ʲô<UI���������߳���>
	 * @param task
	 */
	public void preDownloadDoingOnUIThread(DLTask task);

	/**
	 * sdcardû�е�ʱ�� ��ʲô���� <UI�߳�>
	 * @param task
	 * @return �Ƿ�����
	 */
	public boolean sdcardNoExistOnUIThread(DLTask task);

	/**
	 * �����߳� ��ͻ ��ô�� <��UI�߳�>
	 * @param task
	 * @param oldThreadNum  ԭ�������߳�
	 * @return  CONFLICT_DEFAULT
	 */
	public int threadNumConflictOnOtherThread(DLTask task, int oldThreadNum);
	
	/**
	 * ������ɺ� ���ļ�ʧ�� <��UI�߳�>
	 * @param task
	 * @param e
	 */
	public void openFileErrorOnOtherThread(DLTask task, Exception e);
}
