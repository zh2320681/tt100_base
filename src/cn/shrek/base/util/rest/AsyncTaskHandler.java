package cn.shrek.base.util.rest;

public interface AsyncTaskHandler<T> {
	public void preDoing();
	/**
	 * 任务结束后做什么
	 */
	public void afterTaskDoing();
	
	/**
	 * 客户做动作
	 * @param result
	 */
	public void postResult(ZWResult<T> result);
	
	public void postError(ZWResult<T> result,Exception ex);
	
	public void setTask(ZWAsyncTask<?> task);
	
	public ZWAsyncTask<?> getTask();
	
}
