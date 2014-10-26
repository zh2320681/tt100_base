package cn.shrek.base.ormlite.stmt;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import cn.shrek.base.ZWDatabaseBo;
import cn.shrek.base.ormlite.ForeignInfo;
import cn.shrek.base.ormlite.dao.DBTransforFactory;
import cn.shrek.base.util.ReflectUtil;

public class InsertBuider<T extends ZWDatabaseBo> extends StmtBuilder {
	// public ContentValues cvs;
	// private Map<ForeignInfo,List<ContentValues>> foreignCvses;

	public InsertBuider(Class<T> clazz) {
		super(clazz);
		// TODO Auto-generated constructor stub
		// cvs = new ContentValues();
		// foreignCvses = new HashMap<ForeignInfo, List<ContentValues>>();
	}

	@Override
	public String getSql() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 把所有属性值 添加ContentValues
	 * 
	 * @param t
	 */
	public ContentValues getContentValue(T t) {
		ContentValues cvs = new ContentValues();
		for (int i = 0; i < tableInfo.allField.size(); i++) {
			Field field = tableInfo.allField.get(i);
			String columnName = tableInfo.allColumnNames.get(i);

			Object fieldValue = ReflectUtil.getFieldValue(t, field);
			putValueIntoCvs(cvs, columnName, fieldValue);
		}
		return cvs;
	}

	/**
	 * 把值 放入 ContentValues
	 * 
	 * @param contentValues
	 * @param key
	 * @param value
	 */
	private void putValueIntoCvs(ContentValues contentValues, String key,
			Object value) {
		Object obj = value;
		if (!(obj instanceof String)) {
			obj = DBTransforFactory.getColumnValue(value);
		}
		if (!DBTransforFactory.isFieldNullValue(obj)) {
			Method method = ReflectUtil.getMethodByName(ContentValues.class,
					"put", String.class, obj.getClass());
			ReflectUtil.invokeMethod(method, contentValues, key, obj);
		}
	}

	/**
	 * 添加外键信息
	 * 
	 * @param t
	 */
	public Map<ForeignInfo, List<ContentValues>> getForgienValue(T t) {
		Map<ForeignInfo, List<ContentValues>> foreignCvses = new HashMap<ForeignInfo, List<ContentValues>>();

		List<ForeignInfo> infos = tableInfo.allforeignInfos;
		for (ForeignInfo info : infos) {
			String orgColumnName = info.getOriginalColumnName();
			String foreignColumnName = info.getForeignColumnName();

			Object orgValue = info.getOriginalFieldValue(t);
			Object fieldValue = ReflectUtil.getFieldValue(t,
					info.getValueField());

			List<ContentValues> cvses = foreignCvses.get(info);
			if (cvses == null) {
				cvses = new ArrayList<ContentValues>();
			}

			if (fieldValue instanceof Collection) {
				// 集合
				Collection<?> collection = (Collection<?>) fieldValue;
				for (Object obj : collection) {
					ContentValues cv = new ContentValues();
					putValueIntoCvs(cv, orgColumnName, orgValue);

					Object foreignValue = ReflectUtil.getFieldValue(obj,
							info.getForeignField());
					putValueIntoCvs(cv, foreignColumnName, foreignValue);

					cvses.add(cv);
				}
			} else if (fieldValue instanceof ZWDatabaseBo) {
				ContentValues cv = new ContentValues();
				putValueIntoCvs(cv, orgColumnName, orgValue);

				Object foreignValue = ReflectUtil.getFieldValue(fieldValue,
						info.getForeignField());
				putValueIntoCvs(cv, foreignColumnName, foreignValue);

				cvses.add(cv);
			}

			if (cvses.size() > 0) {
				foreignCvses.put(info, cvses);
			}
		}
		return foreignCvses;
	}

	/**
	 * 得到这个对象 里面所有外键 的对象值
	 * 
	 * @param t
	 * @return
	 */
	public Set<Object> getForeignKeyObjs(T t) {
		Set<Object> objs = new HashSet<Object>();

		List<ForeignInfo> infos = tableInfo.allforeignInfos;
		for (ForeignInfo info : infos) {
			Object fieldValue = ReflectUtil.getFieldValue(t,
					info.getValueField());
			if(fieldValue == null){
				continue;
			}
			if (fieldValue instanceof Collection) {
				objs.addAll((Collection) fieldValue);
			} else {
				objs.add(fieldValue);
			}
		}
		return objs;
	}

//	public Map<ForeignInfo, List<ContentValues>> getForeignCvses() {
//		return foreignCvses;
//	}

}
