package cn.tt100.base.ormlite.task;

import android.app.ProgressDialog;
import android.content.Context;
import cn.tt100.base.ormlite.ZWDBHelper;
/**
 * 到弹出框的数据库异步请求
 * @author shrek
 *
 */
public abstract class DBDialogAsyncTask extends DBAsyncTask {
	private String title;
	private String message;
	private ProgressDialog progressDialog;
	Context ctx;

	public DBDialogAsyncTask(Context ctx ,ZWDBHelper mHelper, boolean isTransaction,String title,String message) {
		super(mHelper, isTransaction);
		// TODO Auto-generated constructor stub
		this.title = title;
		this.message = message;
		
		this.ctx = ctx;
	}

	@Override
	protected final void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		progressDialog = new ProgressDialog(ctx);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setTitle(this.title);
		progressDialog.setMessage(message);
		progressDialog.setCancelable(false);
		progressDialog.show();
		onPreDoing();
	}
	
	protected void onPreDoing(){
		
	}
	
	@Override
	protected abstract Integer doInBackground(ZWDBHelper mHelper, Object... arg0);

	
	protected void onPostDoing(Integer result){
		
	}

	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(progressDialog != null && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
		onPostDoing(result);
	}
	
	
	
}
