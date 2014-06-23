package cn.shrek.base.ui.inject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.util.ZWLogger;

public class FragmentInjectTransfor implements InjectTransfor {

	private Activity act;
	
	@Override
	public void setValue(Activity act, Field field, Object objInstance) {
		// TODO Auto-generated method stub
		if (act instanceof FragmentActivity) {
			this.act = act;
			FragmentActivity fAct = (FragmentActivity) act;

			AutoInject autoInject = field.getAnnotation(AutoInject.class);
			if(autoInject == null){
				return;
			}
			
			Object fragment = null;
			if (autoInject.idFormat() != AutoInject.NULL_STR_VALUE) {
				String idFormat = autoInject.idFormat().replace("?",
						field.getName()); // main_textBtn
				int value = getIdValueIntoR(idFormat);
				if(value == 0){
					//未通过 idFormat找到  尝试用类名字直接找
					fragment = getFragment(field);
				}else{
					fragment = fAct.getSupportFragmentManager()
							.findFragmentById(value);
				}
				
			}else{
				fragment = getFragment(field);
			}
			field.setAccessible(true);
			try {
				field.set(objInstance, fragment);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				ZWLogger.printLog(this, field.getName() + "赋值失败!");
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				ZWLogger.printLog(this, field.getName() + "赋值时访问失败!");
			}
			
		}
	}

	
	private Object getFragment(Field field){
		ZWLogger.printLog(this, "属性名:"+field.getName()+"尝试用Fragment的名字查找");
		Class<?> clazz = field.getType();
		try {
			return clazz.getConstructor().newInstance();
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
	
	private int getIdValueIntoR(String idName) {
		return act.getResources().getIdentifier(idName, "id",
				act.getPackageName());
	}
	
}
