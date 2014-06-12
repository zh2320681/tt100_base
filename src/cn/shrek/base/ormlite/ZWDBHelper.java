package cn.shrek.base.ormlite;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Message;
import cn.shrek.base.ZWApplication;
import cn.shrek.base.ZWBo;
import cn.shrek.base.ormlite.dao.DBDao;
import cn.shrek.base.ormlite.dao.DBDaoImpl;
import cn.shrek.base.ormlite.dao.DBTransforFactory;
import cn.shrek.base.util.ZWLogger;

public abstract class ZWDBHelper extends SQLiteOpenHelper {
	private static final int CLOSE_DBOPERATOR = 0x89;

	private static Map<Class<? extends ZWBo>, DBDao> allDBDaos = new HashMap<Class<? extends ZWBo>, DBDao>();
	//DatabaseErrorHandler��API 11��
//	private static DatabaseErrorHandler mErrorHandler = new DefaultDatabaseErrorHandler();
	private static SQLiteDatabase currentDBOperator;

	private static final Object SQLITEDATABASE_LOCK = new Object();
	public static final Object LOCK_OBJ = new Object();
	private static Handler mHandler;

	public ZWDBHelper(Context context) {
		super(context, ZWApplication.dbName, null, ZWApplication.dbVersion);
		// TODO Auto-generated constructor stub
		if (mHandler == null) {
			mHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					super.handleMessage(msg);
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
				}

			};

			// Looper.prepare();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public abstract void onCreate(SQLiteDatabase arg0);

	@SuppressWarnings("unchecked")
	@Override
	public abstract void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2);

	public SQLiteDatabase getDatabase(boolean isReadOnly) {
		synchronized (SQLITEDATABASE_LOCK) {
			if (currentDBOperator != null && currentDBOperator.isOpen()) {
				if (currentDBOperator.isReadOnly() == isReadOnly) {
					mHandler.removeMessages(CLOSE_DBOPERATOR);
					mHandler.sendEmptyMessageDelayed(CLOSE_DBOPERATOR,
							ZWApplication.dbOPeratorAvailTime);
					return currentDBOperator;
				} else {
					mHandler.removeMessages(CLOSE_DBOPERATOR);
					currentDBOperator.close();
				}
			}
			if (isReadOnly) {
				currentDBOperator = getReadableDatabase();
			} else {
				currentDBOperator = getWritableDatabase();
			}

			mHandler.sendEmptyMessageDelayed(CLOSE_DBOPERATOR,
					ZWApplication.dbOPeratorAvailTime);
			return currentDBOperator;
		}

	}

	/**
	 * �õ�������
	 * 
	 * @param clazz
	 * @return
	 */
	public <T extends ZWBo> DBDao<T> getDao(Class<T> clazz) {
		if (allDBDaos.containsKey(clazz)) {
			return allDBDaos.get(clazz);
		}

		DBDao<T> dao = new DBDaoImpl<T>(clazz, this);
		allDBDaos.put(clazz, dao);
		return dao;
	}

	/**
	 * ���������
	 * 
	 * @param arg0
	 * @param createTablClss
	 */
	protected void createTables(SQLiteDatabase arg0,
			Class<? extends ZWBo>... createTablClss) {
		for (Class<? extends ZWBo> clazz : createTablClss) {
			DBUtil.createTable(arg0, clazz, true);
		}
	}

	/**
	 * ɾ�����ű�
	 * 
	 * @param arg0
	 * @param createTablClss
	 */
	protected void dropTables(SQLiteDatabase arg0,
			Class<? extends ZWBo>... createTablClss) {
		for (Class<? extends ZWBo> clazz : createTablClss) {
			DBUtil.dropTable(arg0, clazz);
		}
	}

	/**
	 * �������� dao���� ֱ��������
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
	 * �������� dao���� ֱ��������
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
			if(obj != null){
				list.add(obj);
			}
		}
		return list;
	}
	
	/**
	 * ͨ��sql��ѯ �õ�map��ӳ��
	 * @param sql
	 * @return
	 */
	public Map<String,Object> queryMap(String sql){
		Cursor cursor = getDatabase(true).rawQuery(sql, null);
		Map<String,Object>  map  = new HashMap<String, Object>();
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
	
	public List<Map<String,Object>> queryMaps(String sql){
		Cursor cursor = getDatabase(true).rawQuery(sql, null);
		List<Map<String,Object>> lists = new ArrayList<Map<String,Object>>();
		
		while (cursor.moveToNext()) {
			int columnCount = cursor.getColumnCount();
			Map<String,Object>  map  = new HashMap<String, Object>();
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
	 * ͨ���α� ֱ�ӵõ�����
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
			ZWLogger.printLog(objClass.getSimpleName(), "���ṩһ���޲εĹ��췽��!");
			e.printStackTrace();
			return null;
		}
		int columnCount = cursor.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			String columnName = cursor.getColumnName(i);
			// �������쳣 ���׳�
			Field field = null;
			try {
				field = objClass.getField(columnName);
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block

			}
			if (field == null) {
				try {
					field = objClass.getDeclaredField(columnName);
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					ZWLogger.printLog(objClass.getSimpleName(), "����:"
							+ objClass.getSimpleName() + "�Ҳ�����" + columnName
							+ "������!");
					e.printStackTrace();
					return null;
				}
			}
			// ��������
			Class<?> fieldType = field.getType();

			// ���ֶε�ֵ ת��Ϊ Java�����ֵ
			Object fieldValues = DBTransforFactory.getFieldValue(
					getObjectValueByCursor(cursor, i), fieldType);
			try {
				field.setAccessible(true);
				field.set(obj, fieldValues);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				ZWLogger.printLog(objClass.getSimpleName(), "���ֶ���:"
						+ columnName + "��ֵ��ֵ:" + fieldValues + ",ʧ��!");
				e.printStackTrace();
			}
		}
		return obj;
	}

	/**
	 * ͨ���±� �õ��α������ֵ
	 * 
	 * @param cursor
	 * @param index
	 * @return
	 */
	private Object getObjectValueByCursor(Cursor cursor, int index) {
		Object columnValue = new Object();
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
		return columnValue;
	}

}