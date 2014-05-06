package cn.tt100.base.ormlite.stmt;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
				Object obj = DBTransforFactory.getColumnValue(field.get(t));
				if(obj instanceof String){
					String str = (String) obj;
					obj = str.substring(1, str.length()-1);
				}
				Method method = ContentValues.class.getMethod("put",String.class, obj.getClass());
				method.invoke(cvs, columnName,obj);
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

}
