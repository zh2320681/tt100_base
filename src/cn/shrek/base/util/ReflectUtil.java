package cn.shrek.base.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 反射的工具类
 * 
 * @author shrek
 *
 */
public class ReflectUtil {

	/**
	 * 通过 名字 找到field对象
	 * 
	 * @param clazz
	 * @param fieldName
	 * @return
	 */
	public static Field getFieldByName(Class<?> clazz, String fieldName) {
		Field field = null;
		try {
			field = clazz.getField(fieldName);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block

		}
		if (field == null) {
			try {
				field = clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				String clazzName = clazz.getSimpleName();
				ZWLogger.printLog(clazzName, "类名:" + clazzName + "找不到叫"
						+ fieldName + "属性名!");
				e.printStackTrace();
				return null;
			}
		}
		return field;
	}

	/**
	 * 给属性设值
	 * 
	 * @param hostObj
	 * @param field
	 * @param value
	 */
	public static void setFieldValue(Object hostObj, Field field, Object value) {
		try {
			field.setAccessible(true);
			field.set(hostObj, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ZWLogger.printLog(hostObj, "给字段名:" + field.getName() + "赋值，值:"
					+ value + "失败!");
			e.printStackTrace();
		}
	}

	/**
	 * 得到 class的所有属性
	 * 
	 * @param clazz
	 * @param fieldCondition
	 *            属性条件
	 * @return
	 */
	public static List<Field> getAllClassField(Class<?> clazz,
			FieldCondition fieldCondition) {
		// 得到本类的所有属性 不包括 父类
		Field[] declaredFields = clazz.getDeclaredFields();
		// 得到本类的所有public属性 包括 父类
		Field[] publicFields = clazz.getFields();
		List<Field> fields = new ArrayList<Field>();

		for (Field field : declaredFields) {
			if (fieldCondition == null || fieldCondition.isFieldValid(field)) {
				fields.add(field);
			}
		}
		for (Field field : publicFields) {
			boolean isFind = false;
			for (int i = 0; i < declaredFields.length; i++) {
				Field oldField = declaredFields[i];
				if (fieldCondition != null && !fieldCondition.isFieldValid(field)) {
					continue;
				}
				if ( oldField.getName().equals(field.getName())) {
					isFind = true;
					break;
				}
			}
			if (!isFind) {
				fields.add(field);
			}
		}
		return fields;
	}

	
	public static List<Field> getAllClassField(Class<?> clazz) {
		return getAllClassField(clazz, null);
	}
	
	public static interface FieldCondition {

		boolean isFieldValid(Field field);
	}

	
}

