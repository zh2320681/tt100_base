package cn.shrek.base.ormlite.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import cn.shrek.base.ZWApplication;
import cn.shrek.base.ormlite.DBUtil;
import cn.shrek.base.ormlite.ZWDBHelper;
import cn.shrek.base.util.ZWLogger;

/**
 * 最好就是增删改操作  查询异步最后自己实现task类
 * @author shrek
 *
 */
public abstract class DBAsyncTask extends AsyncTask<Void, Void, Integer> {
	private ZWDBHelper mHelper;
	//是否开启事务
	private boolean isTransaction;
	
	public DBAsyncTask(ZWDBHelper mHelper,boolean isTransaction){
		if (mHelper == null) {
			throw new IllegalArgumentException("数据库异步任务 至少传入 ZWDBHelper对象");
		}
		this.mHelper = mHelper;
		this.isTransaction = isTransaction;
	}
	
	public DBAsyncTask(ZWDBHelper mHelper){
		this(mHelper,false);
	}
	
	@Override
	protected final Integer doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		mHelper.lockOperator();
		int optNum = 0;
		synchronized (ZWDBHelper.LOCK_OBJ) {
			long before = System.currentTimeMillis();
			SQLiteDatabase db = mHelper.getDatabase(false);
			
			if(isTransaction){
				db.beginTransaction();  //手动设置开始事务
			}
			try {
				optNum = enforcerBackground(mHelper);
				if(isTransaction){
					db.setTransactionSuccessful(); //处理完成
				}
			} catch (Exception e) {
				// TODO: handle exception
				if(ZWApplication.isDebugMode){
					e.printStackTrace();
				}
				ZWLogger.printLog(this, "数据库操作失败 事务回滚!");
			}finally{
				if(isTransaction){
					db.endTransaction(); //处理完成
				}	
			}
			long after = System.currentTimeMillis();
			DBUtil.timeCompute(before, after);
		}
		
		mHelper.unLockOperator();
		return optNum;
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
	}
	
	protected abstract Integer enforcerBackground(ZWDBHelper mHelper);
}
