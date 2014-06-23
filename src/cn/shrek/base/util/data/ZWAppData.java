package cn.shrek.base.util.data;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import cn.shrek.base.annotation.DataSave;
import cn.shrek.base.util.ZWLogger;

public abstract class ZWAppData {
	private static final String SECRET_NAME = "MINE.Secret";
	private static final String OPEN_NAME = "MINE.OPEN";

	protected static SoftReference<HashMap<Class<?>, AppDataTransfor<?>>> allTransforMap;

	// public int count;
	// public boolean isSave;
	// public String appName;

	Context ctx;

	// 是否存在公开的数据 和秘密的数据
	// protected boolean isExistSecretData,isExistOpenData;
	protected List<Field> secretDatas, openDatas;

	static {
		System.loadLibrary("ZWTool");
	}

	public ZWAppData(Context ctx) {
		this.ctx = ctx;
		initExistFlag();

		if (secretDatas.size() > 0) {
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
		}

		for (Field field : secretDatas) {
			DataSave mDataSave = field.getAnnotation(DataSave.class);
			String value = getValue(field.getName());
			try {
				Object obj = null;
				if (value == null) {
					Log.e("AppData", "属性:" + field.getName() + "获取值失败!设置缺省值~~~");
					Class<?> clazz = field.getType();
					if (Boolean.class.isAssignableFrom(clazz)
							|| boolean.class.isAssignableFrom(clazz)) {
						obj = mDataSave.defaultBoolean();
					} else if (Integer.class.isAssignableFrom(clazz)
							|| int.class.isAssignableFrom(clazz)) {
						obj = mDataSave.defaultInteger();
					} else if (float.class.isAssignableFrom(clazz)
							|| Float.class.isAssignableFrom(clazz)) {
						obj = mDataSave.defaultFloat();
					} else if (Long.class.isAssignableFrom(clazz)
							|| long.class.isAssignableFrom(clazz)) {
						obj = mDataSave.defaultLong();
					} else if (String.class.isAssignableFrom(clazz)) {
						obj = mDataSave.defaultString();
					}
				} else {
					obj = tranforString2Value(field, value);
				}
				field.setAccessible(true);
				field.set(this, obj);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		SharedPreferences sharedPreferences = null;
		for (Field field : openDatas) {
			DataSave mDataSave = field.getAnnotation(DataSave.class);
			if (sharedPreferences == null) {
				sharedPreferences = ctx.getSharedPreferences(OPEN_NAME,
						Context.MODE_PRIVATE);
			}
			Class<?> clazz = field.getType();
			Object obj = null;
			if (Boolean.class.isAssignableFrom(clazz)
					|| boolean.class.isAssignableFrom(clazz)) {
				obj = sharedPreferences.getBoolean(clazz.getSimpleName(),
						mDataSave.defaultBoolean());
			} else if (Integer.class.isAssignableFrom(clazz)
					|| int.class.isAssignableFrom(clazz)) {
				obj = sharedPreferences.getInt(clazz.getSimpleName(),
						mDataSave.defaultInteger());
			} else if (float.class.isAssignableFrom(clazz)
					|| Float.class.isAssignableFrom(clazz)) {
				obj = sharedPreferences.getFloat(clazz.getSimpleName(),
						mDataSave.defaultFloat());
			} else if (Long.class.isAssignableFrom(clazz)
					|| long.class.isAssignableFrom(clazz)) {
				obj = sharedPreferences.getLong(clazz.getSimpleName(),
						mDataSave.defaultLong());
			} else if (String.class.isAssignableFrom(clazz)) {
				obj = sharedPreferences.getString(clazz.getSimpleName(),
						mDataSave.defaultString());
			}
			field.setAccessible(true);
			try {
				field.set(this, obj);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void saveData() {
		for (Field field : secretDatas) {
//			DataSave mDataSave = field.getAnnotation(DataSave.class);
			String stringVal = tranforValue2String(field);
			putData(field.getName(), stringVal);
			Log.i("AppData", String.format("添加数据key = %s , value = %s",
					field.getName(), stringVal));
		}

		if(secretDatas.size() > 0){
			saveDataInfoFile();
		}
		
		Editor editor = null;
		for (Field field : openDatas) {
			if (editor == null) {
				SharedPreferences sharedPreferences = ctx.getSharedPreferences(OPEN_NAME,
						Context.MODE_PRIVATE);
				editor = sharedPreferences.edit();// 获取编辑器
			}
			Class<?> clazz = field.getType();
			String fieldName = field.getName();
			Object value;
			try {
				value = field.get(this);
				if (Boolean.class.isAssignableFrom(clazz)
						|| boolean.class.isAssignableFrom(clazz)) {
					editor.putBoolean(fieldName, (Boolean)value);
				} else if (Integer.class.isAssignableFrom(clazz)
						|| int.class.isAssignableFrom(clazz)) {
					editor.putInt(fieldName, (Integer)value);
				} else if (float.class.isAssignableFrom(clazz)
						|| Float.class.isAssignableFrom(clazz)) {
					editor.putFloat(fieldName, (Float)value);
				} else if (Long.class.isAssignableFrom(clazz)
						|| long.class.isAssignableFrom(clazz)) {
					editor.putLong(fieldName, (Long)value);
				} else if (String.class.isAssignableFrom(clazz)) {
					editor.putString(fieldName, value.toString());
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			editor.commit();
		}
	}

	private final void initExistFlag() {
		secretDatas = new ArrayList<Field>();
		openDatas = new ArrayList<Field>();
		Class<?> clazz = getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			DataSave mDataSave = field.getAnnotation(DataSave.class);
			if (mDataSave != null) {
				if (mDataSave.isSecret()) {
					secretDatas.add(field);
				} else {
					openDatas.add(field);
				}
			}
		}

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
	 * 
	 * @param clazz
	 * @param transfor
	 */
	protected void addTransfor(Class<?> clazz, AppDataTransfor<?> transfor) {
		if (clazz == null || transfor == null) {
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

	protected native void putData(String key, String value);

	protected native void saveDataInfoFile();

	protected native String getValue(String key);

	protected native void loadData();

	/**
	 * 
	 * @param manager
	 *            AssetManager对象 为了编译
	 */
	protected native void nativeSetAssetManager(Object manager, String filePath);

}
