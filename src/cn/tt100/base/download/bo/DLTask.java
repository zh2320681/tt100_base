package cn.tt100.base.download.bo;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import cn.tt100.base.download.DLConstant;

public class DLTask implements Serializable {
	//���ػ���ʱ��
	public long costTime;
	//���񴴽�ʱ��
	public long createTime;
	//���ص�����
	public int dlThreadNum;
	//���ص�·��
	public String downLoadUrl;
	//error��Ϣ
	public String errorMessage;
	//�ļ���
	public String fileName;
	//������� �Ƿ��Զ���
	public boolean isAutoOpen;
	//�Ƿ��͹㲥
	public boolean isSendBrocadcast;
	//�Ƿ���ʾ״̬��
	public boolean isShowNotify;

	//���ֵ�·��
	public String savePath;
	public AtomicInteger states;

	public long totalSize;

	public DLTask() {
		this("");
	}

	public DLTask(String paramString) {
		this.downLoadUrl = paramString;
		this.dlThreadNum = 1;
		this.isAutoOpen = true;
		this.isShowNotify = true;
		this.isSendBrocadcast = false;
		AtomicInteger localAtomicInteger = new AtomicInteger();
		this.states = localAtomicInteger;
		this.states.set(1);
	}

	public boolean equals(Object obj) {
		if (obj instanceof DLTask) {
			DLTask task =(DLTask) obj;
			return downLoadUrl.equals(task.downLoadUrl);
		}
		return false;
	}

	public File getSavePath() {
		return new File(savePath, fileName);
	}

	public int hashCode() {
		return downLoadUrl.hashCode();
	}

	public void setErrorMessage(String errorMsg) {
		this.errorMessage = errorMsg;
		if (errorMsg != null && !"".equals(errorMsg)){
			states.set(DLConstant.TASK_ERROR);
		}
	}
}