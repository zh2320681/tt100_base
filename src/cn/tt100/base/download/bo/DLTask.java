package cn.tt100.base.download.bo;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import cn.tt100.base.ZWBo;
import cn.tt100.base.annotation.DatabaseField;
import cn.tt100.base.annotation.DatabaseTable;
import cn.tt100.base.download.DLConstant;
@DatabaseTable
public class DLTask extends ZWBo implements Serializable {
	//下载花费时间
	@DatabaseField
	public long costTime;
	//任务创建时间
	@DatabaseField
	public long createTime;
	//下载的数量
	@DatabaseField
	public int dlThreadNum;
	//下载的路径
	@DatabaseField(id =true)
	public String downLoadUrl;
	//error信息
	@DatabaseField
	public String errorMessage;
	//文件名
	@DatabaseField
	public String fileName;
	//下载完后 是否自动打开
	public boolean isAutoOpen;
	//是否发送广播
	public boolean isSendBrocadcast;
	//是否显示状态栏
//	public boolean isShowNotify;

	//保持的路径
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