package cn.shrek.base.download.bo;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import cn.shrek.base.ZWBo;
import cn.shrek.base.ZWDatabaseBo;
import cn.shrek.base.annotation.DatabaseField;
import cn.shrek.base.annotation.DatabaseTable;
import cn.shrek.base.download.DLConstant;
@DatabaseTable
public class DLTask extends ZWDatabaseBo implements Serializable {
	//���ػ���ʱ��
	@DatabaseField
	public long costTime;
	//���񴴽�ʱ��
	@DatabaseField
	public long createTime;
	//���ص�����
	@DatabaseField
	public int dlThreadNum;
	//���ص�·��
	@DatabaseField(id =true)
	public String downLoadUrl;
	//error��Ϣ
	@DatabaseField
	public String errorMessage;
	//�ļ���
	@DatabaseField
	public String fileName;
	//������� �Ƿ��Զ���
	public boolean isAutoOpen;
	//�Ƿ��͹㲥
	public boolean isSendBrocadcast;
	//�Ƿ���ʾ״̬��
//	public boolean isShowNotify;

	//���ֵ�·��
	@DatabaseField
	public String savePath;
	public AtomicInteger states;

	@DatabaseField
	public long totalSize;

	public DLTask() {
		this("");
	}

	public DLTask(String paramString) {
		this.downLoadUrl = paramString;
		this.dlThreadNum = 1;
		this.isAutoOpen = true;
//		this.isShowNotify = true;
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