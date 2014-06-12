package cn.shrek.base.ui.inject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 注入器
 * @author shrek
 *
 */
public class Injector {
	private static Injector instance;
	
	private Map<Class<?>,InjectTransfor> supportInject;
	
	private Injector(){
		supportInject = new HashMap<Class<?>, InjectTransfor>();
		supportInject.put(View.class, new ViewInjectTransfor());
		supportInject.put(LayoutInflater.class, new LayoutInflaterIT());
		supportInject.put(Fragment.class, new FragmentInjectTransfor());
	}
	
	public static Injector instance(){
		if(instance == null){
			instance = new Injector();
		}
		return instance;
	}
	
	public void injectValue(Activity atc, Object objIntance){
//		Class<?> clazz = field.getClass();
		
		Class<?> clazz = objIntance.getClass();
		//得到所有属性(不包括子类)
		Field[] declaredFields = clazz.getDeclaredFields();
		Field[] publicFields = clazz.getFields();
		List<Field> allFields = new ArrayList<Field>();
		for(Field f : declaredFields){
			allFields.add(f);
		}
		
		for(Field f : publicFields){
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
		
		for (Field f : allFields) {
			Class<?> fieldClazz = f.getType();
			for(Map.Entry<Class<?>,InjectTransfor> entry : supportInject.entrySet()){
				if(entry.getKey().isAssignableFrom(fieldClazz)){
					entry.getValue().setValue(atc, f, objIntance);
					break;
				}
			}
		}
		
	}
}
