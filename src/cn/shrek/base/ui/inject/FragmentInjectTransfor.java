package cn.shrek.base.ui.inject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import cn.shrek.base.ZWConstants;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.util.ZWLogger;

public class FragmentInjectTransfor implements InjectTransfor {

	private Context act;

	@Override
	public void setValue(Context act, Field field, Object objInstance,String defaultStr) throws Exception{
		// TODO Auto-generated method stub
		if (act instanceof FragmentActivity) {
			this.act = act;
			FragmentActivity fAct = (FragmentActivity) act;

			AutoInject autoInject = field.getAnnotation(AutoInject.class);
			if (autoInject == null) {
				return;
			}

			Object fragment = null;
			if (autoInject.idFormat() != ZWConstants.NULL_STR_VALUE) {
				String idFormat = autoInject.idFormat().replace("?",
						field.getName()); // main_textBtn
				int value = getIdValueIntoR(idFormat);
				if (value != 0) {
					fragment = fAct.getSupportFragmentManager()
							.findFragmentById(value);
				} 
			}
			
			if(fragment == null){
				String idFormat = defaultStr.replace("?",field.getName()); // main_textBtn
				int value = getIdValueIntoR(idFormat);
				if (value != 0) {
					fragment = fAct.getSupportFragmentManager()
							.findFragmentById(value);
				} 
			}
			
			if(fragment == null){
				// 未通过 idFormat找到 尝试用类名字直接找
				fragment = getFragment(field);
			}
			
			field.setAccessible(true);
			field.set(objInstance, fragment);
		}
	}

	private Object getFragment(Field field) throws Exception{
		ZWLogger.printLog(this, "属性名:" + field.getName() + "尝试用Fragment的名字查找");
		Class<?> clazz = field.getType();
		return clazz.getConstructor().newInstance();
	}

	private int getIdValueIntoR(String idName) {
		return act.getResources().getIdentifier(idName, "id",
				act.getPackageName());
	}

}
