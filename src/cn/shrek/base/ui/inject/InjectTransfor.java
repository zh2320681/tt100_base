package cn.shrek.base.ui.inject;

import java.lang.reflect.Field;

import android.content.Context;

public interface InjectTransfor {
	
	/**
	 * 设置值
	 * @param ctx
	 * @param field
	 * @param objInstance 
	 */
	void setValue(Context ctx ,Field field,Object objInstance,String defaultStr) throws Exception;
}
