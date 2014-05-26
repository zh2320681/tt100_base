package cn.tt100.base.ormlite.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.database.Cursor;
import cn.tt100.base.ZWBo;
import cn.tt100.base.ormlite.DBUtil;
import cn.tt100.base.ormlite.TableInfo;
import cn.tt100.base.ormlite.ZWDBHelper;
import cn.tt100.base.ormlite.stmt.DeleteBuider;
import cn.tt100.base.ormlite.stmt.InsertBuider;
import cn.tt100.base.ormlite.stmt.QueryBuilder;
import cn.tt100.base.ormlite.stmt.UpdateBuider;
import cn.tt100.base.util.ZWLogger;

public class DBDaoImpl<T extends ZWBo> implements DBDao<T>{
	private Class<T> clazz;
	private ZWDBHelper helper;
	
	public DBDaoImpl(Class<T> clazz1,ZWDBHelper helper){
		this.clazz = clazz1;
		this.helper = helper;
	}
	
	@Override
	public InsertBuider<T> insertBuider() {
		// TODO Auto-generated method stub
		InsertBuider<T> builder = new InsertBuider<T>(clazz);
		return builder;
	}

	@Override
	public long insertObj(T t) {
		// TODO Auto-generated method stub
		return insertObjs(t);
	}

	@Override
	public long insertObjs(List<T> t) {
		// TODO Auto-generated method stub
//		T[] ts = (T[]) t.toArray();
		return insertObjs(false,t);
	}

	public long insertObjs(boolean isAddFKObject,T... t){
		List<T> list = new ArrayList<T>();
		for(T obj : t){
			list.add(obj);
		}
		return insertObjs(isAddFKObject, list);
	}
	
	@Override
	public long insertObjs(boolean isAddFKObject,List<T> t){
		Set<Object> allFKs = new HashSet<Object>();
		InsertBuider<T> buider = insertBuider();
		for(T obj : t){
			//�Ȳ������ֵ
			Set<Object> objs = buider.getForeignKeyObjs(obj);
			allFKs.addAll(objs);
		}
		
		int optNum = 0;
		
		if(isAddFKObject){
			for(Object fkObject : allFKs){
				if(fkObject instanceof ZWBo){
					ZWBo bo = (ZWBo)fkObject;
					Class<? extends ZWBo> fkClazz = (Class<? extends ZWBo>) fkObject.getClass();
					DBDao dao = helper.getDao(fkClazz);
					try {
						optNum += dao.insertObj(bo);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	
		
		for(T obj : t){
			buider.addValue(obj);
			helper.getDatabase(false).insert(buider.tableInfo.tableName, null, buider.cvs);
			optNum++;
		}
		return optNum;
	}
	
	
	@Override
	public long insertObjs(T... t) {
		// TODO Auto-generated method stub
		List<T> list = new ArrayList<T>();
		for(T obj : t){
			list.add(obj);
		}
		return insertObjs(list);
//		int optNum = 0;
//		for(T obj : t){
//			InsertBuider<T> buider = insertBuider();
//			buider.addValue(obj);
//			helper.getDatabase(false).insert(buider.tableInfo.tableName, null, buider.cvs);
//			optNum++;
//		}
//		return optNum;
	}

	/** -------------------ɾ��-------------------------- */
	@Override
	public DeleteBuider deleteBuider() {
		// TODO Auto-generated method stub
		return new DeleteBuider(clazz);
	}

	@Override
	public long deleteObjs(DeleteBuider builder) {
		// TODO Auto-generated method stub
		long optNum = helper.getDatabase(false).delete(builder.getTableNameWithAliases(), builder.getWhereSql(), null);
		return optNum;
	}

	@Override
	public long deleteAll() {
		// TODO Auto-generated method stub
		DeleteBuider builder = deleteBuider();
		return deleteObjs(builder);
	}

	/**
	 * ���Ƽ�����  
	 * @param whereSql ������sql�����
	 */
	@Override
	public long deleteObj(String whereSql) {
		// TODO Auto-generated method stub
		TableInfo info = TableInfo.newInstance(clazz);
		long optNum = helper.getDatabase(false).delete(info.tableName, whereSql, null);
		return optNum;
	}

	
	@Override
	public void clearObj(T t) {
		// TODO Auto-generated method stub
		TableInfo info = TableInfo.newInstance(clazz);
		for (int j = 0; j < info.allField.size(); j++) {
			Field field = info.allField.get(j);
			Class<?> typeClazz = info.getFieldType(j);
			field.setAccessible(true);
			try {
				field.set(t, DBTransforFactory.getFieldNullValue(typeClazz));
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/** ------------------- ���� ----------------------- */
	@Override
	public UpdateBuider<T> updateBuider() {
		// TODO Auto-generated method stub
		return new UpdateBuider<T>(clazz);
	}

	@Override
	public long updateAllObjs(T t) {
		// TODO Auto-generated method stub
		UpdateBuider<T> updateBuider = updateBuider();
		updateBuider.addValue(t);
		return updateObjs(updateBuider);
	}

	@Override
	public long updateObjs(UpdateBuider<T> mUpdateBuider) {
		// TODO Auto-generated method stub
		return helper.getDatabase(false).update(mUpdateBuider.tableInfo.tableName, mUpdateBuider.cvs, mUpdateBuider.getWhereSql(), null);
	}

	@Override
	public long updateObjs(Map<String, Object> updateMap) {
		// TODO Auto-generated method stub
		UpdateBuider<T> updateBuider = updateBuider();
		updateBuider.addValue(updateMap);
		return updateObjs(updateBuider);
	}

	@Override
	public QueryBuilder queryBuilder() {
		// TODO Auto-generated method stub
		return new QueryBuilder(clazz);
	}

	@Override
	public List<T> queryAllObjs() {
		// TODO Auto-generated method stub
		QueryBuilder mQueryBuilder = queryBuilder();
		mQueryBuilder.addSelectColumn("*");
		return queryObjs(mQueryBuilder);
	}

	@Override
	public List<T> queryObjs(QueryBuilder mQueryBuilder) {
		// TODO Auto-generated method stub
		String sql = mQueryBuilder.getSql();
		mQueryBuilder.cycle();
		return queryObjs(sql);
	}

	@Override
	public List<T> queryObjs(String sql) {
		// TODO Auto-generated method stub
		List<T> list = new ArrayList<T>();
		Cursor cursor = helper.getDatabase(true).rawQuery(sql, null);
		while(cursor.moveToNext()){
			T t = parseCurser(cursor);
			if(t != null){
				list.add(t);
			}
		}
		cursor.close();
		return list;
	}

	@Override
	public T queryFirstObj(QueryBuilder mQueryBuilder) {
		// TODO Auto-generated method stub
		mQueryBuilder.limitIndex = 1;
		mQueryBuilder.offsetIndex = 0;
		List<T> list = queryObjs(mQueryBuilder);
		if(list.size() > 0){
			return list.get(0);
		}
		return null;
	}

	@Override
	public int queryCount(QueryBuilder mQueryBuilder) {
		// TODO Auto-generated method stub
		mQueryBuilder.clearSelectSection();
		mQueryBuilder.limitIndex = -1;
		mQueryBuilder.offsetIndex = -1;
		mQueryBuilder.addSelectColumn("COUNT(id)");
		Cursor cursor = helper.getDatabase(true).rawQuery(mQueryBuilder.getSql(), null);
		mQueryBuilder.cycle();
		int countNum = 0;
		if(cursor.moveToNext()){
			countNum = cursor.getInt(0);
		}
		cursor.close();
		return countNum;
	}

	public <F extends ZWBo> F parseCurser(Cursor cursor,Class<F> giveClazz){
		TableInfo info = TableInfo.newInstance(giveClazz);
		
		String logColumnName=null;
		Object logObj = null;
		try {
			F obj = giveClazz.getConstructor().newInstance();
			for (int i = 0; i < info.allColumnNames.size(); i++) {
				String columnName = info.allColumnNames.get(i);
				logColumnName = columnName;
				Field field = info.allField.get(i);
				
				//��������
				Class<?> fieldType = info.getFieldType(i);
				
				int index = cursor.getColumnIndex(columnName);
				if(index == -1){
					continue;
				}
				Object columnValue = new Object();
				switch(cursor.getType(index)){
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
				/**
				 * �ж������Ƿ� ���  
				 * ��� Worker ����Company  �����ݿ��̳��� Company ID  ����Ҫ��new Company()  ��IDֵ����Company 
				 * �ڰ�Company ��Woker����
				 */
				if (info.allforeignClassMaps.containsKey(columnName)) {
//					Field foreignField = info.allforeignMaps.get(columnName);
					Class<?> forgienClazz =  info.allforeignClassMaps.get(columnName);
					//���ָ��Ķ���
//					Object forgienObj = forgienClazz.getConstructor().newInstance();
//					foreignField.setAccessible(true);
					//���ֶε�ֵ ת��Ϊ Java�����ֵ
//					Object fieldValues =  DBTransforFactory.getFieldValue(columnValue, foreignField.getType());
//					foreignField.set(forgienObj, fieldValues);
					Object forgienObj = parseCurser(cursor, (Class<? extends ZWBo>)forgienClazz);
					//����log���
					logObj = forgienObj;
					field.set(obj, forgienObj);
				}else {
					//���ֶε�ֵ ת��Ϊ Java�����ֵ
					Object fieldValues =  DBTransforFactory.getFieldValue(columnValue, fieldType);
					//����log���
					logObj = fieldValues;
					field.set(obj, fieldValues);
				}
			}
			return obj;
		} catch (Exception e) {
			ZWLogger.printLog(DBDaoImpl.this, "���ֶ���:"+logColumnName+"��ֵ��ֵ:"+logObj+",ʧ��!");
			e.printStackTrace();
		}
//		catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		}
		return null;
	}
	
	public T parseCurser(Cursor cursor){
		return parseCurser(cursor, clazz);
	}

	/**
	 * ������Ҫ�������Ӳ�ѯ
	 * ����Employee��ѯ��ʱ�� ˳���������˾Company �����
	 * 1.�õ����е����
	 */
	@Override
	public List<T> queryJoinObjs(QueryBuilder mQueryBuilder) {
		char aliases = 'A';
		mQueryBuilder.setTableAliases(aliases+"");
		// TODO Auto-generated method stub
		for(Map.Entry<String, Field> entry : mQueryBuilder.tableInfo.allforeignMaps.entrySet()){
			aliases++;
			
			String fkName = entry.getKey();
			Field fkField = entry.getValue();
			Class<?> fkClazz = mQueryBuilder.tableInfo.allforeignClassMaps.get(fkName);
			
//			TableInfo fkInfo = TableInfo.newInstance((Class<ZWBo>)fkClazz);
			mQueryBuilder.joinSB.append("LEFT JOIN "+DBUtil.getTableName(fkClazz)+" "+aliases+" ON ");
			mQueryBuilder.joinSB.append(mQueryBuilder.getColumnNameWithAliases(fkName)+" = "+aliases+"."+
					DBUtil.getFeildName(fkField));
			mQueryBuilder.joinSelect.append(","+aliases+".*");
		}
		return queryObjs(mQueryBuilder);
	}
}
