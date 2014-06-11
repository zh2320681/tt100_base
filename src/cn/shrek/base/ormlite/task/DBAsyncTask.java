package cn.shrek.base.ormlite.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import cn.shrek.base.ZWApplication;
import cn.shrek.base.ormlite.DBUtil;
import cn.shrek.base.ormlite.ZWDBHelper;
import cn.shrek.base.util.ZWLogger;

/**
 * ��þ�����ɾ�Ĳ���  ��ѯ�첽����Լ�ʵ��task��
 * @author shrek
 *
 */
public abstract class DBAsyncTask extends AsyncTask<Object, Void, Integer> {
	private ZWDBHelper mHelper;
	//�Ƿ�������
	private boolean isTransaction;
	
	public DBAsyncTask(ZWDBHelper mHelper,boolean isTransaction){
		if (mHelper == null) {
			throw new IllegalArgumentException("���ݿ��첽���� ���ٴ��� ZWDBHelper����");
		}
		this.mHelper = mHelper;
		this.isTransaction = isTransaction;
	}
	
	public DBAsyncTask(ZWDBHelper mHelper){
		this(mHelper,false);
	}
	
	@Override
	protected final Integer doInBackground(Object... arg0) {
		// TODO Auto-generated method stub
		int optNum = 0;
		synchronized (ZWDBHelper.LOCK_OBJ) {
			long before = System.currentTimeMillis();
			SQLiteDatabase db = mHelper.getDatabase(!isTransaction);
			if(isTransaction){
				db.beginTransaction();  //�ֶ����ÿ�ʼ����
			}
			try {
				optNum = doInBackground(mHelper);
				if(isTransaction){
					db.setTransactionSuccessful(); //�������
				}
			} catch (Exception e) {
				// TODO: handle exception
				if(ZWApplication.isDebugMode){
					e.printStackTrace();
				}
				ZWLogger.printLog(this, "���ݿ����ʧ�� ����ع�!");
			}finally{
				if(isTransaction){
					db.endTransaction(); //�������
				}	
			}
			long after = System.currentTimeMillis();
			DBUtil.timeCompute(before, after);
		}
		return optNum;
	}
	
	
	protected abstract Integer doInBackground(ZWDBHelper mHelper,Object... arg0);
}
