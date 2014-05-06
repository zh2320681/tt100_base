package cn.tt100.base.ormlite.dao;

import java.lang.ref.SoftReference;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cn.tt100.base.util.ZWLogger;

public class DBTransforFactory {
	public static SoftReference<HashMap<Class<?>, DBTransforDao<?, ?>>> allTransforMap;
	
	static{
		HashMap<Class<?>,DBTransforDao<?,?>> map = new HashMap<Class<?>, DBTransforDao<?,?>>();
		map.put(Boolean.class, new BooleanTransfor());
		map.put(Date.class, new DateTransfor());
		map.put(Calendar.class, new CalendarTransfor());
		map.put(String.class, new StringTransfor());
		allTransforMap = new SoftReference<HashMap<Class<?>,DBTransforDao<?,?>>>(map);
	}
	
	/**
	 * 得到属性值 通过从数据库得到的值
	 * @param column
	 * @param clazzType
	 * @return
	 */
	public static Object getFieldValue(Object column,Class<?> clazzType){
		HashMap<Class<?>, DBTransforDao<?, ?>> map;
		try {
			map = getMap();
			DBTransforDao dao = map.get(clazzType);
			if(dao == null){
				return column;
			}
			return dao.parseColumnToField(column);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public static Object getColumnValue(Object fieldValue){
		HashMap<Class<?>, DBTransforDao<?, ?>> map;
		try {
			map = getMap();
			DBTransforDao dao = map.get(fieldValue.getClass());
			if(dao == null){
				return fieldValue;
			}
			return dao.parseFieldToColumn(fieldValue);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	private static HashMap<Class<?>, DBTransforDao<?, ?>> getMap() throws Exception{
		HashMap<Class<?>, DBTransforDao<?, ?>> map = allTransforMap.get();
		if(map == null){
			ZWLogger.printLog("DBTransforFactory", "allTransforMap 已被回收!");
			throw new Exception("allTransforMap 已被回收!");
		}
		return map;
	}
}
