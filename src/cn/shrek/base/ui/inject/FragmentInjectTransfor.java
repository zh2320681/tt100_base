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
					//δͨ�� idFormat�ҵ�  ������������ֱ����
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
				ZWLogger.printLog(this, field.getName() + "��ֵʧ��!");
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				ZWLogger.printLog(this, field.getName() + "��ֵʱ����ʧ��!");
			}
			
		}
	}

	
	private Object getFragment(Field field){
		ZWLogger.printLog(this, "������:"+field.getName()+"������Fragment�����ֲ���");
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
