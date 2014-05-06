package cn.tt100.base.ormlite.dao;

import java.lang.ref.SoftReference;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cn.tt100.base.ormlite.stmt.StmtBuilder;
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
	
	public static Object getFieldNullValue(Class<?> clazz){
		HashMap<Class<?>, DBTransforDao<?, ?>> map;
		try {
			map = getMap();
			DBTransforDao dao = map.get(clazz);
			if(dao == null){
				if(int.class.isAssignableFrom(clazz)
						|| Integer.class.isAssignableFrom(clazz)){
					return StmtBuilder.NULL_INTEGER;
				}else if(short.class.isAssignableFrom(clazz)
						|| Short.class.isAssignableFrom(clazz)){
					return StmtBuilder.NULL_SHORT;
				}
			}else{
				return dao.getFeildValueNull();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public static boolean isFieldNullValue(Object object){
		HashMap<Class<?>, DBTransforDao<?, ?>> map;
		try {
			map = getMap();
			Class<?> objClazz = object.getClass();
			DBTransforDao dao = map.get(objClazz);
			if(dao == null){
				if(int.class.isAssignableFrom(objClazz)
						|| Integer.class.isAssignableFrom(objClazz)){
					return Integer.valueOf(object.toString()) == StmtBuilder.NULL_INTEGER;
				}else if(short.class.isAssignableFrom(objClazz)
						|| Short.class.isAssignableFrom(objClazz)){
					return Short.valueOf(object.toString()) == StmtBuilder.NULL_SHORT;
				}
			}else{
				return dao.isFeildNullFeild(object);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
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
