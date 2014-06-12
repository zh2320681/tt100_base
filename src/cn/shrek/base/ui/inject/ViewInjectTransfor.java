package cn.shrek.base.ui.inject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Observer;

import android.app.Activity;
import android.view.View.OnClickListener;
import cn.shrek.base.ModelObservable;
import cn.shrek.base.ObserverContainer;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.annotation.OberverLoad;
import cn.shrek.base.ui.ZWFragment;
import cn.shrek.base.util.BaseUtil;
import cn.shrek.base.util.ZWLogger;

public class ViewInjectTransfor implements InjectTransfor {

	private Activity act;

	@Override
	public void setValue(Activity act, Field field, Object objInstance) {
		// TODO Auto-generated method stub
		this.act = act;

		Class<?> clazz = objInstance.getClass();
		AutoInject autoInject = field.getAnnotation(AutoInject.class);
		if (autoInject != null
				&& autoInject.idFormat() != AutoInject.NULL_STR_VALUE) {
			String idFormat = autoInject.idFormat().replace("?",
					field.getName()); // main_textBtn
			int value = getIdValueIntoR(idFormat);
			field.setAccessible(true);
			try {
				if(objInstance instanceof Activity){
					field.set(objInstance, ((Activity)objInstance).findViewById(value));
				}else if(objInstance instanceof ZWFragment){
					field.set(objInstance, ((ZWFragment)objInstance).rootView.findViewById(value));
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				ZWLogger.printLog(this, field.getName() + "赋值失败!");
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				ZWLogger.printLog(this, field.getName() + "赋值时访问失败!");
			}

			// 自动设置OnClcikListener
			if (autoInject != null
					&& autoInject.clickSelector() != AutoInject.NULL_STR_VALUE) {
				Class<?> viewClazz = field.getType();
				// Object subView = f.get(this);
				Method clickMethod = null;
				try {
					clickMethod = viewClazz.getMethod("setOnClickListener",
							OnClickListener.class);
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					ZWLogger.printLog(this, field.getName()
							+ "设置setOnClickListener()方法失败!");
				}
				if (clickMethod != null) {
					Field clickField;
					try {
						clickField = clazz.getDeclaredField(autoInject
								.clickSelector());
						clickField.setAccessible(true);
						clickMethod.invoke(field.get(objInstance),
								clickField.get(objInstance));
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						ZWLogger.printLog(this,
								"没有找到方法:" + autoInject.clickSelector());
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						ZWLogger.printLog(BaseUtil.class, field.getName()
								+ "赋值失败!");
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						ZWLogger.printLog(BaseUtil.class, field.getName()
								+ "赋值时访问失败!");
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}

		// 自动设置观察绑定
		OberverLoad oberverLoad = field.getAnnotation(OberverLoad.class);
		if (oberverLoad != null) {
			field.setAccessible(true);
			Object obj;
			try {
				obj = field.get(this);
				ModelObservable mModelObservable = new ModelObservable(obj);
				if (act instanceof Observer) {
					mModelObservable.addObserver((Observer) act);
				}
				ObserverContainer.addObservable(field.getName(),
						mModelObservable);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				ZWLogger.printLog(BaseUtil.class, field.getName() + "赋值失败!");
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				ZWLogger.printLog(BaseUtil.class, field.getName() + "赋值时访问失败!");
			}
		}
	}

	private int getIdValueIntoR(String idName) {
		return act.getResources().getIdentifier(idName, "id",
				act.getPackageName());
	}
}
