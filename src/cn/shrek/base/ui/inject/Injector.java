package cn.shrek.base.ui.inject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.util.ZWLogger;

/**
 * 注入器
 * @author shrek
 *
 */
public class Injector {
	private static Injector instance;
	
	private Map<Class<?>,InjectTransfor> supportInject;
	
	//缓存数据
	private ConcurrentMap<String,Identity> saveCacheObjs;
	
	private ConcurrentMap<Class<?>,Identity> defaultInstances;
	
	private CustomInstanceFactory mFactory;
	
	private Injector(){
		supportInject = new HashMap<Class<?>, InjectTransfor>();
		supportInject.put(View.class, new ViewInjectTransfor());
		supportInject.put(LayoutInflater.class, new LayoutInflaterIT());
		supportInject.put(Fragment.class, new FragmentInjectTransfor());
		
		saveCacheObjs = new ConcurrentHashMap<String, Identity>();
		
		defaultInstances = new ConcurrentHashMap<Class<?>,Identity>();
	}
	
	public static Injector instance(){
		if(instance == null){
			instance = new Injector();
		}
		return instance;
	}
	
	public void injectValue(Context atc, Object objIntance){
//		Class<?> clazz = field.getClass();
		
		Class<?> clazz = objIntance.getClass();
		//得到所有属性(不包括子类)
		Field[] declaredFields = clazz.getDeclaredFields();
		Field[] publicFields = clazz.getFields();
		List<Field> allFields = new ArrayList<Field>();
		for(Field f : declaredFields){
			if(f.isAnnotationPresent(AutoInject.class)){
				allFields.add(f);
			}
		}
		
		for(Field f : publicFields){
			if(!f.isAnnotationPresent(AutoInject.class)){
				continue;
			}
			boolean isExist = false;
			for(int i = 0;i<declaredFields.length;i++){
				if(declaredFields[i].getName().equals(f.getName())){
					isExist = true;
					break;
				}
			}
			
			if(!isExist){
				allFields.add(f);
			}
		}
		
		
		//初始化 默认的实例
		if(defaultInstances.size() == 0 && mFactory != null){
			defaultInstances.putAll(mFactory.getDefaultInstance());
		}
		
		for (Field f : allFields) {
			Class<?> fieldClazz = f.getType();
			f.setAccessible(true);
			
			if(customInstance(f, objIntance)){
				continue;
			}
			
			
			for(Map.Entry<Class<?>,InjectTransfor> entry : supportInject.entrySet()){
				if(entry.getKey().isAssignableFrom(fieldClazz)){
					entry.getValue().setValue(atc, f, objIntance);
					break;
				}
			}
		}
		
		
		
	}
	
	
	public boolean customInstance(Field f, Object objIntance){
		if(mFactory == null){
			return false;
		}
		
		Class<?> fieldClazz = f.getType();
		AutoInject auto = f.getAnnotation(AutoInject.class);
		Identity value = null;
		if(auto.tag()!=null && !auto.tag().equals(AutoInject.NULL_STR_VALUE)){
			if(saveCacheObjs.containsKey(auto.tag())){
				value = saveCacheObjs.get(auto.tag());
			}else{
				value = mFactory.getInstanceByTag(auto.tag());
				if(auto.isSingleInstance()){
					saveCacheObjs.put(auto.tag(), value);
				}
			}
			
		}else{
			//获得默认的值
			value = getDefaultInstance(fieldClazz);
		}
		
		if(value != null){
			try {
				f.set(objIntance, value);
				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ZWLogger.printLog(this, f.getName() + "赋值失败!");
			}
		}
		
		return false;
	}
	
	
	public Identity getDefaultInstance(Class<?> typeClazz){
		return defaultInstances.get(typeClazz);
	}
	
	public void setCustomFactory(CustomInstanceFactory mFactory){
		this.mFactory = mFactory;
	}
	
	
	/**
	 * 回收
	 */
	public void recycle(){
		if(mFactory != null){
			mFactory = null;
		}
		
		for(Map.Entry<Class<?>, Identity> entry: defaultInstances.entrySet()){
			entry.getValue().recycle();
		}
		defaultInstances.clear();
		
		for(Map.Entry<String, Identity> entry: saveCacheObjs.entrySet()){
			entry.getValue().recycle();
		}
		saveCacheObjs.clear();
		
	}
}
