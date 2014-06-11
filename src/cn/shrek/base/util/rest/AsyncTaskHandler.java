package cn.shrek.base.util.rest;

public interface AsyncTaskHandler<T> {
	public void preDoing();
	/**
	 * �����������ʲô
	 */
	public void afterTaskDoing();
	
	/**
	 * �ͻ�������
	 * @param result
	 */
	public void postResult(ZWResult<T> result);
	
	public void postError(ZWResult<T> result,Exception ex);
	
	public void setTask(ZWAsyncTask<T> task);
	
	public ZWAsyncTask<T> getTask();
	
}
