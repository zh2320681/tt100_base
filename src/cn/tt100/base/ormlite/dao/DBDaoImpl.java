package cn.tt100.base.ormlite.dao;

import java.util.ArrayList;
import java.util.List;

import cn.tt100.base.ZWBo;
import cn.tt100.base.ormlite.ZWDBHelper;
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

}
