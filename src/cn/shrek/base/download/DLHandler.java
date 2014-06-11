package cn.shrek.base.download;

import cn.shrek.base.download.bo.DLTask;

public interface DLHandler {
	/**
	 * ����ʱ����ִ���
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
	public void postDownLoading(DLTask task);
	
	/**
	 * ���ع����� ����
	 */
	public void downLoadingProgress(DLTask task,int hasDownSize);

	/**
	 * ����ǰ��ʲô<UI���������߳���>
	 * @param task
	 */
	public void preDownloadDoing(DLTask task);

	/**
	 * sdcardû�е�ʱ�� ��ʲô����
	 * @param task
	 * @return �Ƿ�����
	 */
	public boolean sdcardNoExist(DLTask task);

	/**
	 * �����߳� ��ͻ ��ô��
	 * @param task
	 * @param oldThreadNum  ԭ�������߳�
	 * @return  CONFLICT_DEFAULT
	 */
	public int threadNumConflict(DLTask task, int oldThreadNum);
	
	/**
	 * ������ɺ� ���ļ�ʧ��
	 * @param task
	 * @param e
	 */
	public void openFileError(DLTask task, Exception e);
}
