package cn.shrek.base.ui.inject;

import java.lang.reflect.Field;

import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.util.ZWLogger;
import android.app.Activity;
import android.view.LayoutInflater;

public class LayoutInflaterIT implements InjectTransfor {

	@Override
	public void setValue(Activity act, Field field, Object objInstance) {
		// TODO Auto-generated method stub
		AutoInject autoInject = field
				.getAnnotation(AutoInject.class);
		if (autoInject != null) {
			field.setAccessible(true);
			try {
				field.set(objInstance, LayoutInflater.from(act));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				ZWLogger.printLog(this, field.getName() + "赋值失败!");
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				ZWLogger.printLog(this, field.getName() + "赋值时访问失败!");
			}
		}
	}

}
