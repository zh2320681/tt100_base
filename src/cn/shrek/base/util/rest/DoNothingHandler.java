package cn.shrek.base.util.rest;

public class DoNothingHandler<T> implements AsyncTaskHandler<T> {

	private ZWAsyncTask<?> task;
	
	@Override
	public void preDoing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterTaskDoing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postResult(ZWResult<T> result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postError(ZWResult<T> result, Exception ex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTask(ZWAsyncTask<?> task) {
		// TODO Auto-generated method stub
		this.task = task;
	}

	@Override
	public ZWAsyncTask<?> getTask() {
		// TODO Auto-generated method stub
		return task;
	}

	
	
}
