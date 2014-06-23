package cn.shrek.base.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD})
public @interface AutoInject
{
	public static final String NULL_STR_VALUE = "NULL_VALUE";
	public static final int NULL_INT_VALUE = Integer.MIN_VALUE;
	
	String idFormat() default NULL_STR_VALUE;
	
	String clickSelector() default NULL_STR_VALUE; 
	
	//对 fragment的 支持 布局ID R.layout.fragment11
//	int fragmentLayoutId() default NULL_INT_VALUE;
}
