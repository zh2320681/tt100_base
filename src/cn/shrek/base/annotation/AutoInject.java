package cn.shrek.base.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.shrek.base.ZWConstants;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD})
public @interface AutoInject
{
	
	String idFormat() default ZWConstants.NULL_STR_VALUE;
	
	String clickSelector() default ZWConstants.NULL_STR_VALUE; 
	
	//对于自定义 注入方式
	String tag() default ZWConstants.NULL_STR_VALUE;
	
	boolean isSingleInstance() default false;
	
	/**
	 * 键盘焦点(EditText设置无效)
	 * @return
	 */
	boolean isKeyboardFocus() default false; 
	
	//对 fragment的 支持 布局ID R.layout.fragment11
//	int fragmentLayoutId() default NULL_INT_VALUE;
}
