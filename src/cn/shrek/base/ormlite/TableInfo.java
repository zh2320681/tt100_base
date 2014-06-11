package cn.shrek.base.ormlite;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import cn.shrek.base.ZWBo;
import cn.shrek.base.annotation.DatabaseField;
import cn.shrek.base.util.ZWLogger;

public class TableInfo {
	private static final Map<Class<? extends ZWBo>, TableInfo> tableInfoFactory = Collections
			.synchronizedMap(new WeakHashMap<Class<? extends ZWBo>, TableInfo>());
	// 所有字段名
	public List<String> allColumnNames;
	// 所有属性
	public List<Field> allField;
	// 所有外键 key:外键字段名 value:对于Teacher 的id属性
	public Map<String, Field> allforeignMaps;
	// 所有外键 key:外键字段名 value:对于Teacher.class
	public Map<String, Class<?>> allforeignClassMaps;
	public Class<? extends ZWBo> clazz;
	public String tableName;

	private TableInfo(Class<? extends ZWBo> clazz) {
		this.clazz = clazz;
		//必须有无参数的构造方法
		try {
			clazz.getConstructor();
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			ZWLogger.printLog(TableInfo.this,"类"+clazz.getSimpleName()+"请提供无参数的构造方法！");
			e1.printStackTrace();
		}
		tableName = DBUtil.getTableName(clazz);
		allField = new ArrayList<Field>();
		allColumnNames = new ArrayList<String>();

		this.allforeignMaps = new HashMap<String, Field>();
		allforeignClassMaps = new HashMap<String, Class<?>>();
		//得到本类的所有属性 不包括 父类
		Field[] declaredFields = clazz.getDeclaredFields();
		//得到本类的所有public属性 包括 父类
		Field[] publicFields = clazz.getFields();
		List<Field> fields = new ArrayList<Field>();
		for (Field field : declaredFields) {
			fields.add(field);
		}
		for (Field field : publicFields) {
			boolean isFind = false;
			for (int i = 0; i < declaredFields.length; i++) {
				Field oldField = declaredFields[i];
				if(oldField.getName().equals(field.getName())){
					isFind = true;
					break;
				}
			}
			if(!isFind){
				fields.add(field);
			}
		}
		
		for (Field field : fields) {
			DatabaseField mDatabaseField = field
					.getAnnotation(DatabaseField.class);
			if (DBUtil.judgeFieldAvaid(field)) {
				allField.add(field);

				// 属性的类型
				Class<?> fieldType = field.getType();
				/**
				 * ########## 外键判断
				 */
				String foreignColumnName = mDatabaseField.foreignColumnName();
				if (foreignColumnName != null && !"".equals(foreignColumnName)) {

					if (ZWBo.class.isAssignableFrom(fieldType)) {
						// 外键是BO类型
						Field objField;
						try {
							objField = fieldType.getField(foreignColumnName);
							if (DBUtil.judgeFieldAvaid(objField)) {
								String fkColumnName = DBUtil
										.getMapFKCulmonName(foreignColumnName,
												fieldType);
								allColumnNames.add(fkColumnName);
								allforeignMaps.put(fkColumnName, objField);
								allforeignClassMaps
										.put(fkColumnName, fieldType);
							} else {
								ZWLogger.printLog(this,
										"外键指向的 类名：" + fieldType.getSimpleName()
												+ "字段名:" + foreignColumnName
												+ "不符合DataBaseField的条件!");
							}

						} catch (NoSuchFieldException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else if (List.class.isAssignableFrom(fieldType)) {
						// 外键是集合类型
						Type fc = field.getGenericType(); // 关键的地方，如果是List类型，得到其Generic的类型
						if (fc == null) {
							ZWLogger.printLog(this,
									"外键指向的 类名：" + fieldType.getSimpleName()
											+ "字段名:" + foreignColumnName
											+ " list 未设置泛型");
							continue;
						}

						if (fc instanceof ParameterizedType) {// 【3】如果是泛型参数的类型
							ParameterizedType pt = (ParameterizedType) fc;
							Class<?> genericClazz = (Class<?>) pt
									.getActualTypeArguments()[0]; // 【4】
																	// 得到泛型里的class类型对象。
							Field objField;
							try {
								objField = genericClazz
										.getField(foreignColumnName);
								if (DBUtil.judgeFieldAvaid(objField)) {
									String fkColumnName = DBUtil
											.getMapFKCulmonName(
													foreignColumnName,
													fieldType);
									allColumnNames.add(fkColumnName);
									allforeignMaps.put(fkColumnName, objField);
									allforeignClassMaps.put(fkColumnName,
											fieldType);
								} else {
									ZWLogger.printLog(this, "外键指向的 类名："
											+ fieldType.getSimpleName()
											+ "字段名:" + foreignColumnName
											+ "不符合DataBaseField的条件!");
								}
							} catch (NoSuchFieldException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					} else {
						ZWLogger.printLog(this, fieldType.getSimpleName()
								+ "不是 BaseBo or List的子类 不能设置为外键！");
					}
					continue;
				}

				// 添加字段名
				String fieldName = DBUtil.getFeildName(field);
				allColumnNames.add(fieldName);
			} else {

			}

		}

	}

	public static final TableInfo newInstance(Class<? extends ZWBo> clazz) {
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
		ZWLogger.printLog(this, "类："+clazz.toString()+" 属性名叫:"+fieldName+" 找不到~~");
		return -1;
	}
	
	/**
	 * 通过表中 字段下标 找到 对应属性的Class对象
	 * @param index
	 * @return
	 */
	public Class<?> getFieldType(int index) {
		String columnName = allColumnNames.get(index);
		Field field = allField.get(index);
		Class<?> fieldType = null;
		// 判断属性是否 外键
		if (allforeignClassMaps.containsKey(columnName)) {
			fieldType = allforeignClassMaps.get(columnName);
		} else {
			fieldType = field.getType();
		}
		return fieldType;
	}
}
