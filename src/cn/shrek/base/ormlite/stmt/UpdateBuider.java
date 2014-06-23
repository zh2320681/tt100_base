package cn.shrek.base.ormlite.stmt;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import android.content.ContentValues;
import cn.shrek.base.ZWBo;
import cn.shrek.base.ormlite.dao.DBTransforFactory;

public class UpdateBuider<T extends ZWBo> extends StmtBuilder {
	public ContentValues cvs;

	public UpdateBuider(Class<T> clazz) {
		super(clazz);
		// TODO Auto-generated constructor stub
		cvs = new ContentValues();

	}

	@Override
	public String getSql() {
		// TODO Auto-generated method stub
		return null;
	}

//	public void addValue(T t) {
//		addValue(t);
//	}
	
	/**
	 * 设置更新的内容  
	 * @param t
	 * @param specials 特殊的 不需要的更新的 例如boolean 必须设置 没法设置空值  
	 */
	public void addValue(T t , String... specials) {
		for (int i = 0; i < tableInfo.allField.size(); i++) {
			Field field = tableInfo.allField.get(i);
			try {
				boolean isSpe = false;
				for(String str : specials){
					if(field.getName().equals(str)){
						isSpe = true;
						break;
					}
				}
				
				Object obj = DBTransforFactory.getColumnValue(field.get(t));
				if (obj instanceof String) {
					String str = (String) obj;
					obj = str.substring(1, str.length() - 1);
				}
				
				if (!DBTransforFactory.isFieldNullValue(obj) && !isSpe) {
					String columnName = tableInfo.allColumnNames.get(i);
					Method method = ContentValues.class.getMethod("put",
							String.class, obj.getClass());
					method.invoke(cvs, columnName, obj);
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

	
	public void addValue(Map<String, Object> maps){
		for (int i = 0; i < tableInfo.allField.size(); i++) {
			Field field = tableInfo.allField.get(i);
			String columnName = tableInfo.allColumnNames.get(i);
			if(!maps.containsKey(field.getName())){
				continue;
			}
			try {
				Object obj = DBTransforFactory.getColumnValue(maps.get(field.getName()));
				if (obj instanceof String) {
					String str = (String) obj;
					obj = str.substring(1, str.length() - 1);
				}
				
				if (!DBTransforFactory.isFieldNullValue(obj)) {
					
					Method method = ContentValues.class.getMethod("put",
							String.class, obj.getClass());
					method.invoke(cvs, columnName, obj);
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
	
	protected void appendWhereStr(String str){
		if(whereBuffer.length() == 0){
//			whereBuffer.append(WHERE_KEYWORD);
		}
		whereBuffer.append(str);
	}
}
