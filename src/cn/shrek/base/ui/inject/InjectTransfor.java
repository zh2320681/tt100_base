package cn.shrek.base.ui.inject;

import java.lang.reflect.Field;

import android.app.Activity;

public interface InjectTransfor {
	
	/**
	 * ����ֵ
	 * @param ctx
	 * @param field
	 * @param objInstance 
	 */
	void setValue(Activity ctx ,Field field,Object objInstance);
}
