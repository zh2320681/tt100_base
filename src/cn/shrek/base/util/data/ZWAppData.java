package cn.shrek.base.util.data;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.ormlite.dao.BooleanTransfor;
import cn.shrek.base.ormlite.dao.CalendarTransfor;
import cn.shrek.base.ormlite.dao.DBTransforDao;
import cn.shrek.base.ormlite.dao.DateTransfor;
import cn.shrek.base.ormlite.dao.StringTransfor;
import cn.shrek.base.util.ZWLogger;

public abstract class ZWAppData {
	private static final String SECRET_NAME = "MINE.Secret";

	protected static SoftReference<HashMap<Class<?>, AppDataTransfor<?>>> allTransforMap;

	public int count;
	public boolean isSave;
	public String appName;

	static {
		System.loadLibrary("ZWTool");
	}

	public ZWAppData(Context ctx) {
		File file = new File(ctx.getFilesDir().getPath(), SECRET_NAME);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		nativeSetAssetManager(ctx.getAssets(), file.getPath());
		loadData();
		try {
			Class<?> clazz = getClass();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				String value = getValue(field.getName());
				if (value == null) {
					Log.e("AppData", "属性:" + field.getName() + "获取值失败!");
				} else {
					Object obj = tranforString2Value(field, value);
					field.setAccessible(true);
					field.set(this, obj);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveData() {
		Class<?> clazz = getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			AutoInject inject = field.getAnnotation(AutoInject.class);
			if (inject != null) {
				String stringVal = tranforValue2String(field);
				putData(field.getName(), stringVal);
				Log.i("AppData",
						String.format("添加数据key = %s , value = %s",
								field.getName(), stringVal));
			}
		}
		saveDataInfoFile();
	}

	/**
	 * 把值转换为 string
	 * 
	 * @return
	 */
	private String tranforValue2String(Field field) {
		Class<?> clazz = field.getType();
		try {
			Object value = field.get(this);
			AppDataTransfor transfor = getMap().get(clazz);
			if (transfor != null) {
				return transfor.toString(value);
			} else {
				return value.toString();
			}
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
	 * 将jni的string 转化为值
	 * 
	 * @param field
	 * @param string
	 * @return
	 */
	private Object tranforString2Value(Field field, String string) {
		Class<?> clazz = field.getType();
		AppDataTransfor transfor = getMap().get(clazz);
		if (transfor != null) {
			return transfor.parse2Obj(string);
		} else {
			return string;
		}
	}
	
	/**
	 * 添加转化器
	 * @param clazz
	 * @param transfor
	 */
	protected void addTransfor(Class<?> clazz , AppDataTransfor<?> transfor){
		if(clazz == null || transfor == null){
			throw new IllegalArgumentException("空值的参数");
		}
		addTransfor(clazz, transfor);
	}

	private static HashMap<Class<?>, AppDataTransfor<?>> getMap() {
		HashMap<Class<?>, AppDataTransfor<?>> map = null;
		if (allTransforMap != null) {
			map = allTransforMap.get();
		}
		if (map == null) {
			ZWLogger.printLog("DBTransforFactory", "allTransforMap 已被回收!");
			// throw new Exception("allTransforMap 已被回收!");

			map = new HashMap<Class<?>, AppDataTransfor<?>>();
			map.put(Boolean.class, new BooelanADT());
			map.put(boolean.class, new BooelanADT());
			map.put(Integer.class, new IntegerADT());
			map.put(int.class, new IntegerADT());
			map.put(Date.class, new DateADT());
			map.put(Calendar.class, new CalendarADT());
			allTransforMap = new SoftReference<HashMap<Class<?>, AppDataTransfor<?>>>(
					map);
		}
		return map;
	}

	public native void putData(String key, String value);

	public native void saveDataInfoFile();

	public native String getValue(String key);

	public native void loadData();

	/**
	 * 
	 * @param manager
	 *            AssetManager对象 为了编译
	 */
	private native void nativeSetAssetManager(Object manager, String filePath);
}
