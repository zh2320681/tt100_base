package cn.tt100.base.ormlite;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Message;
import cn.tt100.base.ZWApplication;
import cn.tt100.base.ZWBo;
import cn.tt100.base.example.bean.Company;
import cn.tt100.base.example.bean.Employee;
import cn.tt100.base.ormlite.dao.DBDao;
import cn.tt100.base.ormlite.dao.DBDaoImpl;

public class ZWDBHelper extends SQLiteOpenHelper {
	private static final int CLOSE_DBOPERATOR = 0x89;
	
	private static Map<Class<? extends ZWBo>,DBDao> allDBDaos = new HashMap<Class<? extends ZWBo>, DBDao>();
	private static DatabaseErrorHandler mErrorHandler = new DefaultDatabaseErrorHandler();
	private static SQLiteDatabase currentDBOperator;
	
	private static final Object SQLITEDATABASE_LOCK = new Object();
	public static final Object LOCK_OBJ = new Object();
	private static Handler mHandler;
	
	public ZWDBHelper(Context context) {
		super(context, ZWApplication.dbName, null, ZWApplication.dbVersion, mErrorHandler);
		// TODO Auto-generated constructor stub
		if(mHandler == null){
			mHandler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					super.handleMessage(msg);
					switch (msg.what) {
					case CLOSE_DBOPERATOR:
						if(currentDBOperator != null){
							currentDBOperator.close();
							currentDBOperator = null;
						}
						break;
					default:
						break;
					}
				}
				
			};
			
//			Looper.prepare();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		createTables(arg0, Company.class,Employee.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		dropTables(arg0, Company.class,Employee.class);
		onCreate(arg0);
	}

	
	public SQLiteDatabase getDatabase(boolean isReadOnly){
		synchronized (SQLITEDATABASE_LOCK) {
			if(currentDBOperator != null && currentDBOperator.isOpen()){
				if(currentDBOperator.isReadOnly() == isReadOnly){
					mHandler.removeMessages(CLOSE_DBOPERATOR);
					mHandler.sendEmptyMessageDelayed(CLOSE_DBOPERATOR, ZWApplication.dbOPeratorAvailTime);
					return currentDBOperator;
				}else {
					mHandler.removeMessages(CLOSE_DBOPERATOR);
					currentDBOperator.close();
				}
			}
			if(isReadOnly){
				currentDBOperator = getReadableDatabase();
			}else{
				currentDBOperator = getWritableDatabase();
			}	

			
			mHandler.sendEmptyMessageDelayed(CLOSE_DBOPERATOR, ZWApplication.dbOPeratorAvailTime);
			return currentDBOperator;
		}
		
		
	}
	
	
	/**
	 * 得到操作类
	 * @param clazz
	 * @return
	 */
	public <T extends ZWBo> DBDao<T> getDao(Class<T> clazz){
		if(allDBDaos.containsKey(clazz)){
			return allDBDaos.get(clazz);
		}
		
		DBDao<T> dao = new DBDaoImpl<T>(clazz, this);
		allDBDaos.put(clazz, dao);
		return dao;
	}
	
	/**
	 * 创建多个表
	 * @param arg0
	 * @param createTablClss
	 */
	protected void createTables(SQLiteDatabase arg0,Class<? extends ZWBo>... createTablClss){
		for(Class<? extends ZWBo> clazz : createTablClss){
			DBUtil.createTable(arg0, clazz, true);
		}
	}
	
	
	protected void dropTables(SQLiteDatabase arg0,Class<? extends ZWBo>... createTablClss){
		for(Class<? extends ZWBo> clazz : createTablClss){
			DBUtil.dropTable(arg0, clazz);
		}
	}
	
}
