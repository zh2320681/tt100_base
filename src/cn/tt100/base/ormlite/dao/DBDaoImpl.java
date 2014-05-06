package cn.tt100.base.ormlite.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.tt100.base.ZWBo;
import cn.tt100.base.ormlite.TableInfo;
import cn.tt100.base.ormlite.ZWDBHelper;
import cn.tt100.base.ormlite.stmt.DeleteBuider;
import cn.tt100.base.ormlite.stmt.InsertBuider;

public class DBDaoImpl<T extends ZWBo> implements DBDao<T>{
	private Class<T> clazz;
	private static final Object LOCK_OBJ = new Object();
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
		int optNum = 0;
		for(T obj : t){
			InsertBuider<T> buider = insertBuider();
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

	/** -------------------删除-------------------------- */
	@Override
	public DeleteBuider deleteBuider() {
		// TODO Auto-generated method stub
		return new DeleteBuider(clazz);
	}

	@Override
	public long deleteObjs(DeleteBuider builder) {
		// TODO Auto-generated method stub
		long optNum = helper.getDatabase(false).delete(builder.tableInfo.tableName, builder.getWhereSql(), null);
		return optNum;
	}

	@Override
	public long deleteAll() {
		// TODO Auto-generated method stub
		DeleteBuider builder = deleteBuider();
		return deleteObjs(builder);
	}

	/**
	 * 不推荐试用  
	 * @param whereSql 条件的sql的语句
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

}
