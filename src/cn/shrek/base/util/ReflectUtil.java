package cn.shrek.base.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
	 * 获取 field的值
	 * @param hostObj
	 * @param field
	 */
	public static Object getFieldValue(Object hostObj, Field field){
		field.setAccessible(true);
		try {
			Object fieldValue = field.get(hostObj);
			return fieldValue;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
			if (fieldCondition != null && !fieldCondition.isFieldValid(field)) {
				continue;
			}
			boolean isFind = false;
			for (int i = 0; i < declaredFields.length; i++) {
				Field oldField = declaredFields[i];
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

	/**
	 * 得到方法的 所有参数
	 * @return
	 */
//	public static List<Method> getMethodParas(Method method){
//	}
	
	/**
	 * 通过 名字和参数类型 返回方法
	 * @param hostClazz
	 * @param methodName
	 * @param paras
	 * @return
	 */
	public static Method getMethodByName(Class<?> hostClazz , String methodName , Class<?>...paras){
		Method method = null;
		try {
			method = hostClazz.getMethod(methodName, paras);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return method;
	}
	
	
	/**
	 * 执行方法
	 * @param method
	 * @param host
	 * @param paras
	 */
	public static void invokeMethod(Method method,Object host , Object...paras){
		try {
			method.invoke(host, paras);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<Field> getAllClassField(Class<?> clazz) {
		return getAllClassField(clazz, null);
	}
	
	/**
	 * 通过无参的构造方法 得到实例
	 * @param clazz
	 * @return
	 */
	public static <F> F getInstance(Class<F> clazz){
		try {
			F obj = clazz.getConstructor().newInstance();
			return obj;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static interface FieldCondition {

		boolean isFieldValid(Field field);
	}

	
}

