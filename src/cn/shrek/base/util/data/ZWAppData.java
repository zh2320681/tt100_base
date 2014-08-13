package cn.shrek.base.util.data;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
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
import cn.shrek.base.util.rest.ZWRequestConfig;

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
	private Charset gb2312Charset;
	
	public ZWAppData(Context ctx) {
		this.ctx = ctx;
		gb2312Charset = Charset.forName(ZWRequestConfig.GB2312_CHARSET);
		initExistFlag();

		// if (secretDatas.size() > 0) {
		// File file = new File(ctx.getFilesDir().getPath(), SECRET_NAME);
		// if (!file.exists()) {
		// try {
		// file.createNewFile();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		// nativeSetAssetManager(ctx.getAssets(), file.getPath());
		// loadData();
		// }
		SharedPreferences sharedPreferences = ctx.getSharedPreferences(
				OPEN_NAME, Context.MODE_PRIVATE);
		for (Field field : secretDatas) {
			initValue(sharedPreferences, field, true);
		}

		for (Field field : openDatas) {
			initValue(sharedPreferences, field, false);
		}
	}

	public void saveData() {

		SharedPreferences sharedPreferences = ctx.getSharedPreferences(
				OPEN_NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();// 获取编辑器

		for (Field field : secretDatas) {
			// DataSave mDataSave = field.getAnnotation(DataSave.class);
			saveValue(editor, field, true);
		}

		for (Field field : openDatas) {
			saveValue(editor, field, false);
		}
		editor.commit();
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

	/**
	 * #########################################################################
	 * ########
	 */
	// protected native void putData(String key, String value);

	// protected native void saveDataInfoFile();

	// protected native String getValue(String key);

	// protected native void loadData();

	/**
	 * 
	 * @param manager
	 *            AssetManager对象 为了编译
	 */
	// protected native void nativeSetAssetManager(Object manager, String
	// filePath);

	/**
	 * #########################################################################
	 * ########
	 */

	/**
	 * 通过key 设置属性
	 * 
	 * @param key
	 * @return
	 */
	protected void initValue(SharedPreferences sharedPreferences, Field field,
			boolean isSercet) {
		Class<?> clazz = field.getType();
		DataSave mDataSave = field.getAnnotation(DataSave.class);
		String fieldName = field.getName();
		Object obj = null;
		// 密文保存 都是string
		if (isSercet) {
			String objStr = sharedPreferences.getString(fieldName, null);
//			System.out.println("objStr密文===============>" + objStr + "  name:"
//					+ fieldName);
			if (objStr != null) {
				obj = tranforString2Value(field, decode(objStr));
			}
		} else {
			if (Boolean.class.isAssignableFrom(clazz)
					|| boolean.class.isAssignableFrom(clazz)) {
				obj = sharedPreferences.getBoolean(fieldName,
						mDataSave.defaultBoolean());
			} else if (Integer.class.isAssignableFrom(clazz)
					|| int.class.isAssignableFrom(clazz)) {
				obj = sharedPreferences.getInt(fieldName,
						mDataSave.defaultInteger());
			} else if (float.class.isAssignableFrom(clazz)
					|| Float.class.isAssignableFrom(clazz)) {
				obj = sharedPreferences.getFloat(fieldName,
						mDataSave.defaultFloat());
			} else if (Long.class.isAssignableFrom(clazz)
					|| long.class.isAssignableFrom(clazz)) {
				obj = sharedPreferences.getLong(fieldName,
						mDataSave.defaultLong());
			} else if (String.class.isAssignableFrom(clazz)) {
				obj = sharedPreferences.getString(fieldName,
						mDataSave.defaultString());
			}
		}

		if(obj == null){
			// 如果 obj没有获取到 设置默认值
			Log.e("AppData", "属性:" + field.getName() + "获取值失败!设置缺省值~~~");
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

	protected void saveValue(Editor editor, Field field, boolean isSercet) {
		Class<?> clazz = field.getType();
		String fieldName = field.getName();
		Object value;

		if (isSercet) {
			// 密文 保存
			String objStr = tranforValue2String(field);
			editor.putString(fieldName, encode(objStr));
		} else {
			try {
				value = field.get(this);
				if (Boolean.class.isAssignableFrom(clazz)
						|| boolean.class.isAssignableFrom(clazz)) {
					editor.putBoolean(fieldName, (Boolean) value);
				} else if (Integer.class.isAssignableFrom(clazz)
						|| int.class.isAssignableFrom(clazz)) {
					editor.putInt(fieldName, (Integer) value);
				} else if (float.class.isAssignableFrom(clazz)
						|| Float.class.isAssignableFrom(clazz)) {
					editor.putFloat(fieldName, (Float) value);
				} else if (Long.class.isAssignableFrom(clazz)
						|| long.class.isAssignableFrom(clazz)) {
					editor.putLong(fieldName, (Long) value);
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
		}
	}

	private static final String SERCET_KEY = "SERCET_KEY";

	private String encode(String value) {
		byte[] sercetChars = SERCET_KEY.getBytes();
		byte[] bytes = value.getBytes(gb2312Charset);
		byte[] newBytes = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			newBytes[i] = (byte) (bytes[i] ^ sercetChars[i % sercetChars.length]);
		}
//		System.out.println("编码后===============>" + new String(newBytes));
		return new String(newBytes,gb2312Charset);
	}

	private String decode(String value) {
		byte[] sercetChars = SERCET_KEY.getBytes();
		byte[] bytes = value.getBytes(gb2312Charset);
		byte[] newBytes = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			newBytes[i] = (byte) (bytes[i] ^ sercetChars[i % sercetChars.length]);
		}
//		System.out.println("转码后===============>" + new String(newBytes));
		return new String(newBytes,gb2312Charset);
	}
}
