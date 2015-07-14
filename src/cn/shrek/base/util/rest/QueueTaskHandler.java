package cn.shrek.base.util.rest;

/**
 * 队列任务的执行方法
 * @author shrek
 *
 */
public interface QueueTaskHandler {
	
	void setTask(ZWAsyncTask<?> task);
	
	/**
	 * 队列任务前做什么
	 */
	void preDoing();
	
	/**
	 * 单个任务 执行前 做什么
	 * @param config
	 */
	void singleTaskPreDoing();
	
	/**
	 * 单个任务结束前 做什么动作
	 * @param config
	 */
	void singleTaskAfterDoing();
	
	/**
	 * 队列任务结束的时候做什么
	 */
	void afterTaskDoing();
	
	/**
	 * 异常的时候做什么
	 * @param ex
	 */
	void postError(Exception ex);
}
