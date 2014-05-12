package cn.tt100.base.util.rest;

public class DoNothingHandler<T> implements AsyncTaskHandler<T> {

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
	public void setTask(ZWAsyncTask<T> task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ZWAsyncTask<T> getTask() {
		// TODO Auto-generated method stub
		return null;
	}

}
