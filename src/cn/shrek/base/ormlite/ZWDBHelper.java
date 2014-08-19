package cn.shrek.base.ormlite;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.ReflectionUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import cn.shrek.base.ZWApplication;
import cn.shrek.base.ZWBo;
import cn.shrek.base.ZWDatabaseBo;
import cn.shrek.base.exception.TableInfoInvalidException;
import cn.shrek.base.ormlite.dao.DBDao;
import cn.shrek.base.ormlite.dao.DBDaoImpl;
import cn.shrek.base.ormlite.dao.DBTransforFactory;
import cn.shrek.base.util.AndroidVersionCheckUtils;
import cn.shrek.base.util.ReflectUtil;
import cn.shrek.base.util.ZWLogger;

public abstract class ZWDBHelper extends SQLiteOpenHelper {
	private static final int CLOSE_DBOPERATOR = 0x89;

	private static Map<Class<? extends ZWDatabaseBo>, DBDao> allDBDaos = new HashMap<Class<? extends ZWDatabaseBo>, DBDao>();
	// DatabaseErrorHandler是API 11的
	// private static DatabaseErrorHandler mErrorHandler = new
	// DefaultDatabaseErrorHandler();
	private static SQLiteDatabase currentDBOperator;

	private static final Object SQLITEDATABASE_LOCK = new Object();
	public static final Object LOCK_OBJ = new Object();
	private static Handler mHandler;
	
	private Class<? extends ZWDatabaseBo>[] loadDbBos;

	public ZWDBHelper(Context context) {
		super(context, ZWApplication.dbName, null, ZWApplication.dbVersion);
		// TODO Auto-generated constructor stub
		if (mHandler == null) {
			mHandler = new Handler(Looper.getMainLooper(),new Handler.Callback() {
				
				@Override
				public boolean handleMessage(Message msg) {
					// TODO Auto-generated method stub
					switch (msg.what) {
					case CLOSE_DBOPERATOR:
						if (currentDBOperator != null) {
							currentDBOperator.close();
							currentDBOperator = null;
						}
						break;
					default:
						break;
					}
					return true;
				}
			}); 
			loadDbBos = loadDatabaseClazz();
			// Looper.prepare();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase arg0){
		createTables(arg0, loadDbBos);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2){
		dropTables(arg0, loadDbBos);
		onCreate(arg0);
	}
	
	public abstract Class<? extends ZWDatabaseBo>[] loadDatabaseClazz();

	public SQLiteDatabase getDatabase(boolean isReadOnly) {
		synchronized (SQLITEDATABASE_LOCK) {
			if (currentDBOperator != null && currentDBOperator.isOpen()) {
				//之前写的
//				if (currentDBOperator.isReadOnly() == isReadOnly) {
//					mHandler.removeMessages(CLOSE_DBOPERATOR);
//					mHandler.sendEmptyMessageDelayed(CLOSE_DBOPERATOR,
//							ZWApplication.dbOPeratorAvailTime);
//					return currentDBOperator;
//				} else {
//					mHandler.removeMessages(CLOSE_DBOPERATOR);
//					currentDBOperator.close();
//				}
				mHandler.removeMessages(CLOSE_DBOPERATOR);
				mHandler.sendEmptyMessageDelayed(CLOSE_DBOPERATOR,
						ZWApplication.dbOPeratorAvailTime);
				return currentDBOperator;
			}
//			if (isReadOnly) {
//				currentDBOperator = getReadableDatabase();
//			} else {
//				currentDBOperator = getWritableDatabase();
//			}
			try {
				currentDBOperator = getWritableDatabase();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				ZWLogger.e(ZWDBHelper.class, "创建数据库出现异常,尝试获取只读操作");
				if(isReadOnly){
					currentDBOperator = getReadableDatabase();
				}else{
					e.printStackTrace();
				}
			}
			mHandler.sendEmptyMessageDelayed(CLOSE_DBOPERATOR,
					ZWApplication.dbOPeratorAvailTime);
			return currentDBOperator;
		}

	}

	/**
	 * 得到操作类
	 * 
	 * @param clazz
	 * @return
	 */
	public <T extends ZWDatabaseBo> DBDao<T> getDao(Class<T> clazz) {
		if (allDBDaos.containsKey(clazz)) {
			return allDBDaos.get(clazz);
		}

		DBDao<T> dao = new DBDaoImpl<T>(clazz, this);
		allDBDaos.put(clazz, dao);
		return dao;
	}

	/**
	 * 创建多个表
	 * 
	 * @param arg0
	 * @param createTablClss
	 */
	private void createTables(SQLiteDatabase arg0,
			Class<? extends ZWDatabaseBo>... createTablClss) {
		for (Class<? extends ZWDatabaseBo> clazz : createTablClss) {
			TableInfo info = TableInfo.newInstance(clazz);
			if(info == null){
				throw new TableInfoInvalidException("获取不到 类:"+clazz.getSimpleName()+" TableInfo对象!");
			}
			DBUtil.createTable(arg0, info, true);
		}
	}

	/**
	 * 删除多张表
	 * 
	 * @param arg0
	 * @param createTablClss
	 */
	private void dropTables(SQLiteDatabase arg0,
			Class<? extends ZWDatabaseBo>... createTablClss) {
		for (Class<? extends ZWDatabaseBo> clazz : createTablClss) {
			TableInfo info = TableInfo.newInstance(clazz);
			if(info == null){
				throw new TableInfoInvalidException("获取不到 类:"+clazz.getSimpleName()+" TableInfo对象!");
			}
			DBUtil.dropTable(arg0, info);
		}
	}

	/**
	 * 不用生成 dao对象 直接做操作
	 * 
	 * @param sql
	 * @param objClass
	 * @return
	 */
	public <ParseObjec> ParseObjec queryObj(String sql,
			Class<ParseObjec> objClass) {
		Cursor cursor = getDatabase(true).rawQuery(sql, null);
		ParseObjec obj = null;
		if (cursor.moveToNext()) {
			obj = getObjByCursor(cursor, objClass);
		}
		return obj;
	}

	/**
	 * 不用生成 dao对象 直接做操作
	 * 
	 * @param sql
	 * @param objClass
	 * @return
	 */
	public <ParseObjec> List<ParseObjec> queryObjs(String sql,
			Class<ParseObjec> objClass) {
		Cursor cursor = getDatabase(true).rawQuery(sql, null);
		List<ParseObjec> list = new ArrayList<ParseObjec>();
		while (cursor.moveToNext()) {
			ParseObjec obj = getObjByCursor(cursor, objClass);
			if (obj != null) {
				list.add(obj);
			}
		}
		return list;
	}

	/**
	 * 通过sql查询 得到map的映射
	 * 
	 * @param sql
	 * @return
	 */
	public Map<String, Object> queryMap(String sql) {
		Cursor cursor = getDatabase(true).rawQuery(sql, null);
		Map<String, Object> map = new HashMap<String, Object>();
		if (cursor.moveToNext()) {
			int columnCount = cursor.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				String columnName = cursor.getColumnName(i);
				Object obj = getObjectValueByCursor(cursor, i);
				map.put(columnName, obj);
			}
		}
		return map;
	}

	public List<Map<String, Object>> queryMaps(String sql) {
		Cursor cursor = getDatabase(true).rawQuery(sql, null);
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();

		while (cursor.moveToNext()) {
			int columnCount = cursor.getColumnCount();
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < columnCount; i++) {
				String columnName = cursor.getColumnName(i);
				Object obj = getObjectValueByCursor(cursor, i);
				map.put(columnName, obj);
			}
			lists.add(map);
		}
		return lists;
	}

	/**
	 * 通过游标 直接得到对象
	 * 
	 * @param cursor
	 * @param objClass
	 * @return
	 */
	private <ParseObjec> ParseObjec getObjByCursor(Cursor cursor,
			Class<ParseObjec> objClass) {
		ParseObjec obj = null;
		try {
			obj = objClass.getConstructor().newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ZWLogger.e(objClass.getSimpleName(), "请提供一个无参的构造方法!");
			e.printStackTrace();
			return null;
		}
		
		int columnCount = cursor.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			String columnName = cursor.getColumnName(i);
			Field field = ReflectUtil.getFieldByName(objClass, columnName);
			// 属性类型
			Class<?> fieldType = field.getType();
			// 从字段的值 转换为 Java里面的值
			Object fieldValue = DBTransforFactory.getFieldValue(
					getObjectValueByCursor(cursor, i), fieldType);
			ReflectUtil.setFieldValue(obj, field, fieldValue);
		}
		
		return obj;
	}

	/**
	 * 通过下标 得到游标里面的值
	 * 
	 * @param cursor
	 * @param index
	 * @return
	 */
	private Object getObjectValueByCursor(Cursor cursor, int index) {
		Object columnValue = new Object();
		// 兼容性 2.3
		if (AndroidVersionCheckUtils.hasHoneycomb()) {
			switch (cursor.getType(index)) {
			case Cursor.FIELD_TYPE_STRING:
				columnValue = cursor.getString(index);
				break;
			case Cursor.FIELD_TYPE_FLOAT:
				columnValue = cursor.getFloat(index);
				break;
			case Cursor.FIELD_TYPE_INTEGER:
				columnValue = cursor.getInt(index);
				break;
			case Cursor.FIELD_TYPE_NULL:
				columnValue = null;
				break;
			case Cursor.FIELD_TYPE_BLOB:
				columnValue = cursor.getBlob(index);
				break;
			}
		} else {

			columnValue = cursor.getString(index);

		}

		return columnValue;
	}

}
