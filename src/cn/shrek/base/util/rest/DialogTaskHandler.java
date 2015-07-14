package cn.shrek.base.util.rest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public abstract class DialogTaskHandler<T> implements AsyncTaskHandler<T> {
//	public WeakReference<Context> ctx;
//	private ProgressDialog progressDialog;
	private ProgressDialog progressDialog;
	public boolean isTaskSuccess = true;
	protected String title;
	protected String content;
	private ZWAsyncTask<T> task;
	
	public DialogTaskHandler(String title, String content) {
		// TODO Auto-generated constructor stub
//		this.ctx = new WeakReference<Context>(ctx);
		this.title = title;
		this.content = content;
	}
	
	
	@Override
	public void preDoing() {
		// TODO Auto-generated method stub
		if (getTask().judgeTaskValid()) {
			Context mContext = getTask().ctx.get();
			progressDialog = new ProgressDialog(mContext);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setTitle(this.title);
			progressDialog.setMessage(content);
			progressDialog.setCancelable(true);
			if (getTask().judgeTaskValid() && progressDialog != null
					&& !progressDialog.isShowing())
				progressDialog.show();
		}
	}

	@Override
	public abstract void postResult(ZWResult<T> result);

	@Override
	public void postError(ZWResult<T> result, Exception ex) {
		// TODO Auto-generated method stub
		showNormalError(false, "请求出错啦", ex.toString());
	}

	public void errorDoing(){
		
	}
	
	public void showNormalError(Boolean isSuccess, String errorTitle,
			String errorContent) {
		Context mContext = getTask().ctx.get();
		if (mContext != null && errorTitle != null && errorContent != null) {
			AlertDialog.Builder build = new AlertDialog.Builder(mContext);
//			if (isSuccess) {
//				build.setIcon(android.R.drawable.ic_dialog_info);
//			} else {
//				build.setIcon(android.R.drawable.ic_dialog_alert);
//			}
			build.setTitle(errorTitle)
					.setMessage(errorContent)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
									errorDoing();
								}
							}).create();
			if (getTask().judgeTaskValid()) {
				build.show();
			}
		}

	}
	


	@Override
	public void setTask(ZWAsyncTask<T> task) {
		// TODO Auto-generated method stub
		this.task = task;
	}


	@Override
	public ZWAsyncTask<T> getTask() {
		// TODO Auto-generated method stub
		return task;
	}


	@Override
	public void afterTaskDoing() {
		// TODO Auto-generated method stub
		if(progressDialog != null && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}
}
