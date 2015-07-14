package cn.shrek.base.ui.inject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Observer;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import cn.shrek.base.ModelObservable;
import cn.shrek.base.ObserverContainer;
import cn.shrek.base.ZWConstants;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.annotation.OberverLoad;
import cn.shrek.base.ui.ZWCustomView;
import cn.shrek.base.ui.ZWFragment;
import cn.shrek.base.ui.inject.rule.KeyboardFocus;

public class ViewInjectTransfor implements InjectTransfor {

	private Context act;

	@Override
	public void setValue(Context act, Field field, Object objInstance,
			String defaultStr) throws Exception {
		// TODO Auto-generated method stub
		this.act = act;

		Class<?> clazz = objInstance.getClass();
		AutoInject autoInject = field.getAnnotation(AutoInject.class);
		if (autoInject != null) {
			String idFormatStr = defaultStr;
			if (autoInject.idFormat() != ZWConstants.NULL_STR_VALUE) {
				idFormatStr = autoInject.idFormat(); // main_textBtn
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
			} else if (objInstance instanceof ZWCustomView) {
				field.set(objInstance, ((ZWCustomView) objInstance)
						.getRootView().findViewById(value));
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

			if (autoInject != null && autoInject.isKeyboardFocus()) {
				Object obj = field.get(objInstance);
				if (objInstance instanceof KeyboardFocus && obj != null
						&& obj instanceof View) {
					((KeyboardFocus) objInstance).addKeyboardFocus((View) obj);
				}
			}

			if (autoInject != null
					&& autoInject.clickSelector() != ZWConstants.NULL_STR_VALUE) {
				// 自动设置观察绑定
				OberverLoad oberverLoad = field
						.getAnnotation(OberverLoad.class);
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
	}

	private int getIdValueIntoR(String idName) {
		return act.getResources().getIdentifier(idName, "id",
				act.getPackageName());
	}

}
