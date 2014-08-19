package cn.shrek.base.ormlite;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import cn.shrek.base.ZWBo;
import cn.shrek.base.ZWDatabaseBo;
import cn.shrek.base.annotation.DatabaseField;
import cn.shrek.base.annotation.Foreign;
import cn.shrek.base.exception.ForeignKeyValidException;
import cn.shrek.base.util.BaseUtil;
import cn.shrek.base.util.ReflectUtil;
import cn.shrek.base.util.ReflectUtil.FieldCondition;
import cn.shrek.base.util.ZWLogger;

public class TableInfo {
	private static final Map<Class<? extends ZWBo>, TableInfo> tableInfoFactory = Collections
			.synchronizedMap(new WeakHashMap<Class<? extends ZWBo>, TableInfo>());
	// 所有字段名
	public List<String> allColumnNames;
	// 所有属性
	public List<Field> allField;
	// 所有外键 key:外键字段名 value:对于Teacher 的id属性
	public List<ForeignInfo> allforeignInfos;

	public Class<? extends ZWDatabaseBo> clazz;
	/**
	 * tableName：表名 indexTableName: 索引表名
	 */
	String tableName, indexTableName;

	private TableInfo(Class<? extends ZWDatabaseBo> clazz) {
		this.clazz = clazz;
		// 必须有无参数的构造方法
		try {
			clazz.getConstructor();
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			ZWLogger.e(TableInfo.this, "类" + clazz.getSimpleName()
					+ "请提供无参数的构造方法！");
			e1.printStackTrace();
		}
		tableName = DBUtil.getTableName(clazz);
		allColumnNames = new ArrayList<String>();
		
		allField = new ArrayList<Field>();
		
		this.allforeignInfos = new ArrayList<ForeignInfo>();

		List<Field> allFieldCache = ReflectUtil.getAllClassField(clazz,
				new FieldCondition() {

					@Override
					public boolean isFieldValid(Field field) {
						// TODO Auto-generated method stub
						return DBUtil.judgeFieldAvaid(field);
					}
				});

		for (Field field : allFieldCache) {
			// 属性的类型
			Class<?> fieldType = field.getType();

			/**
			 * ########## 外键判断 ##########
			 */
			Foreign foreignAnn = field.getAnnotation(Foreign.class);
			if (foreignAnn != null) {
				String originalName = foreignAnn.originalColumnName();
				String fkFieldName = foreignAnn.foreignColumnName();

				String errorInfo = "设置了无效的外键:" + field.getName();

				if (!BaseUtil.isStringValid(originalName)
						|| !BaseUtil.isStringValid(fkFieldName)) {
					ZWLogger.e(this, errorInfo);
					throw new ForeignKeyValidException(errorInfo);
				}

				Class<?> genericClazz = null;

				if (ZWDatabaseBo.class.isAssignableFrom(fieldType)) {
					genericClazz = fieldType;
				} else if (Collection.class.isAssignableFrom(fieldType)) {
					// 外键是集合类型
					Type fc = field.getGenericType(); // 关键的地方，如果是List类型，得到其Generic的类型
					if (fc == null) {
						ZWLogger.printLog(this,
								"外键指向的 类名：" + fieldType.getSimpleName()
										+ "字段名:" + fkFieldName + " list 未设置泛型");
						continue;
					}

					if (fc instanceof ParameterizedType) {// 【3】如果是泛型参数的类型
						ParameterizedType pt = (ParameterizedType) fc;
						genericClazz = (Class<?>) pt.getActualTypeArguments()[0]; // 得到泛型里的class类型对象。
					}
				}

				if (genericClazz == null) {
					continue;
				}

				Field objField = ReflectUtil.getFieldByName(genericClazz,
						fkFieldName);
				Field orgField = ReflectUtil.getFieldByName(clazz,originalName);
				
				if (DBUtil.judgeFieldAvaid(objField)) {
					ForeignInfo fInfo = new ForeignInfo();
					fInfo.valueField = field;
					fInfo.setOriginalField(orgField);
					fInfo.setForeignField(objField);
					fInfo.setOriginalClazz(clazz);
					fInfo.setForeignClazz((Class<? extends ZWDatabaseBo>) genericClazz);
					fInfo.initValue();

					allforeignInfos.add(fInfo);
				} else {
					ZWLogger.printLog(this,
							"外键指向的 类名：" + fieldType.getSimpleName() + "字段名:"
									+ fkFieldName + "不符合DataBaseField的条件!");
				}

				// } catch (NoSuchFieldException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// throw new
				// ForeignKeyValidException(errorInfo+" 类名:"+foreignName+"里面根本没有叫"+fkFieldName+"字段");
				// }
				continue;
			} else {
				// 添加字段名
				String fieldName = DBUtil.getColumnName(field);
				allField.add(field);
				allColumnNames.add(fieldName);
			}

		}

	}

	public static final TableInfo newInstance(
			Class<? extends ZWDatabaseBo> clazz) {
		TableInfo mTableInfo = null;
		if (tableInfoFactory.containsKey(clazz)) {
			mTableInfo = tableInfoFactory.get(clazz);
		}

		if (mTableInfo == null) {
			mTableInfo = new TableInfo(clazz);
			tableInfoFactory.put(clazz, mTableInfo);
		}
		return mTableInfo;
	}

	/**
	 * 通过 field name找到表字段名
	 * 
	 * @param fieldName
	 * @return
	 */
	public String getColumnByFieldStr(String fieldName) {
		for (int i = 0; i < allField.size(); i++) {
			Field field = allField.get(i);
			if (fieldName.equals(field.getName())) {
				return allColumnNames.get(i);
			}
		}
		return null;
	}

	/**
	 * 通过 field name找到表字段下标
	 * 
	 * @param fieldName
	 * @return
	 */
	public int getColumnIndexByFieldStr(String fieldName) {
		for (int i = 0; i < allField.size(); i++) {
			Field field = allField.get(i);
			if (fieldName.equals(field.getName())) {
				return i;
			}
		}
		ZWLogger.printLog(this, "类：" + clazz.toString() + " 属性名叫:" + fieldName
				+ " 找不到~~");
		return -1;
	}

	/**
	 * 通过表中 字段下标 找到 对应属性的Class对象
	 * 
	 * @param index
	 * @return
	 */
	public Class<?> getFieldType(int index) {
		// String columnName = allColumnNames.get(index);
		Field field = allField.get(index);
		Class<?> fieldType = null;
		// 判断属性是否 外键
		// if (allforeignClassMaps.containsKey(columnName)) {
		// fieldType = allforeignClassMaps.get(columnName);
		// } else {
		fieldType = field.getType();
		// }
		return fieldType;
	}

	public String getTableName() {
		return tableName;
	}

	public String getIndexTableName() {
		return indexTableName;
	}

}
