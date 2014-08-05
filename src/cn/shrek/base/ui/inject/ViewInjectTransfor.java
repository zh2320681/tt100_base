package cn.shrek.base.ui.inject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Observer;

import android.app.Activity;
import android.content.Context;
import android.view.View.OnClickListener;
import cn.shrek.base.ModelObservable;
import cn.shrek.base.ObserverContainer;
import cn.shrek.base.ZWConstants;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.annotation.Controller;
import cn.shrek.base.annotation.OberverLoad;
import cn.shrek.base.ui.ZWFragment;

public class ViewInjectTransfor implements InjectTransfor {

	private Context act;

	@Override
	public void setValue(Context act, Field field, Object objInstance)
			throws Exception {
		// TODO Auto-generated method stub
		this.act = act;

		Class<?> clazz = objInstance.getClass();
		AutoInject autoInject = field.getAnnotation(AutoInject.class);
		if (autoInject != null) {
			String idFormatStr = null;
			if (autoInject.idFormat() != ZWConstants.NULL_STR_VALUE) {
				idFormatStr = autoInject.idFormat(); // main_textBtn
			} else {
				idFormatStr = getClassIdFormat(objInstance);
			}
			String idFormat = idFormatStr.replace("?", field.getName()); // main_textBtn
			int value = getIdValueIntoR(idFormat);
			field.setAccessible(true);

			if (objInstance instanceof Activity) {
				field.set(objInstance,
						((Activity) objInstance).findViewById(value));
			} else if (objInstance instanceof ZWFragment) {
				field.set(objInstance,
						((ZWFragment) objInstance).rootView.findViewById(value));
			}

			// 自动设置OnClcikListener
			if (autoInject != null
					&& autoInject.clickSelector() != ZWConstants.NULL_STR_VALUE) {
				Class<?> viewClazz = field.getType();
				// Object subView = f.get(this);
				Method clickMethod = null;
				clickMethod = viewClazz.getMethod("setOnClickListener",
						OnClickListener.class);

				if (clickMethod != null) {
					Field clickField;

					clickField = clazz.getDeclaredField(autoInject
							.clickSelector());
					clickField.setAccessible(true);
					clickMethod.invoke(field.get(objInstance),
							clickField.get(objInstance));

				}
			}

			// 自动设置观察绑定
			OberverLoad oberverLoad = field.getAnnotation(OberverLoad.class);
			if (oberverLoad != null) {
				field.setAccessible(true);
				Object obj;

				obj = field.get(this);
				ModelObservable mModelObservable = new ModelObservable(obj);
				if (act instanceof Observer) {
					mModelObservable.addObserver((Observer) act);
				}
				ObserverContainer.addObservable(field.getName(),
						mModelObservable);
			}
		}
	}

	private int getIdValueIntoR(String idName) {
		return act.getResources().getIdentifier(idName, "id",
				act.getPackageName());
	}

	/**
	 * 得到类的 idFormat MainActivity ----> main_?
	 * 
	 * @param instance
	 */
	private String getClassIdFormat(Object instance) {
		Class<?> clazz = instance.getClass();
		Controller selecor = clazz.getAnnotation(Controller.class);

		if (selecor != null && selecor.idFormat() != ZWConstants.NULL_STR_VALUE) {
			return selecor.idFormat();
		}
		// activity的布局名字
		String layoutInfoStr = clazz.getSimpleName().toLowerCase()
				.replace("activity", "");
		// char[] chars = layoutInfoStr.toCharArray();
		// StringBuffer sb = new StringBuffer();
		// for (int i = 0; i < chars.length; i++) {
		// char c = chars[i];
		// if((c >= 'A' && c<='Z') || (c>='1' && c<='9')){
		// sb.append(c);
		// }
		// }
		return layoutInfoStr + "_?";
	}
}
