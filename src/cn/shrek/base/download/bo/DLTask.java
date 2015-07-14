package cn.shrek.base.download.bo;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import cn.shrek.base.ZWDatabaseBo;
import cn.shrek.base.annotation.DatabaseField;
import cn.shrek.base.annotation.DatabaseTable;
import cn.shrek.base.download.DLConstant;
import cn.shrek.base.event.ZWEventPara;

@DatabaseTable(tableName="DLTask")
public class DLTask extends ZWDatabaseBo implements ZWEventPara {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5001572836597346037L;

	//任务花费的时间
	@DatabaseField
	public long costTime;
	
	//任务创建时间
	@DatabaseField
	public long createTime;
	
	//下载的线程数 默认是一个
	@DatabaseField
	public int dlThreadNum;
	
	//下载的路径
	@DatabaseField(id =true)
	public String downLoadUrl;
	
	//请求的异常信息
	@DatabaseField
	public String errorMessage;
	
	//文件名
	@DatabaseField
	public String fileName;
	
	//是否自动打开
	public boolean isAutoOpen;
	
	//是否发送event<未实现>
	public boolean isSendEvent;
	
	//是否发送广播
	public boolean isSendBrocadcast;
	
//	public boolean isShowNotify;

	//下载路径
	@DatabaseField
	public String savePath;
	
	public AtomicInteger states;

	@DatabaseField
	public long totalSize;

	public DLTask() {
		this("");
	}

	public DLTask(String paramString) {
		super();
		this.downLoadUrl = paramString;
		this.dlThreadNum = 1;
		this.isAutoOpen = true;
//		this.isShowNotify = true;
		this.isSendBrocadcast = false;
		this.isSendEvent = false;
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