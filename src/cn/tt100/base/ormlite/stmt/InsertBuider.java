package cn.tt100.base.ormlite.stmt;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import android.content.ContentValues;
import cn.tt100.base.ZWBo;
import cn.tt100.base.ormlite.dao.DBTransforFactory;

public class InsertBuider<T extends ZWBo> extends StmtBuilder {
	public ContentValues cvs;
	
	public InsertBuider(Class<T> clazz) {
		super(clazz);
		// TODO Auto-generated constructor stub
		cvs = new ContentValues();
		
	}

	@Override
	public String getSql() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public void addValue(T t){
		for (int i = 0; i < tableInfo.allField.size(); i++) {
			Field field = tableInfo.allField.get(i);
			String columnName = tableInfo.allColumnNames.get(i);

			try {
				Object fieldValue = field.get(t);
				if(tableInfo.allforeignMaps.containsKey(columnName)){
					/**
					 * fieldValue
					 * 如果是外键 例如Worker的Company  这边要存的是 Company ID 但是Company对象要先存 因为 有外键约束 
					 */
					Field foreignField = tableInfo.allforeignMaps.get(columnName);
					fieldValue = foreignField.get(fieldValue);
				}
				Object obj = DBTransforFactory.getColumnValue(fieldValue);
				if(obj instanceof String){
					String str = (String) obj;
					obj = str.substring(1, str.length()-1);
				}
				if(!DBTransforFactory.isFieldNullValue(obj)){
					Method method = ContentValues.class.getMethod("put",String.class, obj.getClass());
					method.invoke(cvs, columnName,obj);
				}
				
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	/**
	 * 得到这个对象 里面所有外键 的对象值
	 * @param t
	 * @return
	 */
	public Set<Object> getForeignKeyObjs(T t){
		Set<Object> objs = new HashSet<Object>();
		for (int i = 0; i < tableInfo.allColumnNames.size(); i++) {
			String columnName = tableInfo.allColumnNames.get(i);
			if(tableInfo.allforeignMaps.containsKey(columnName)){
				Field field = tableInfo.allField.get(i);
				try {
					Object fieldValue = field.get(t);
					if(fieldValue != null){
						objs.add(fieldValue);
					}
					
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return objs;
	}

}
