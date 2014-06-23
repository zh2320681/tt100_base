package cn.shrek.base.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD})
public @interface DataSave {
	
	//是否 秘密保存
	boolean isSecret() default false;
	
	boolean defaultBoolean() default false;
	
	int defaultInteger() default 0;
	
	String defaultString() default "";
	
	long defaultLong() default 0L;
	
	float defaultFloat() default 0;
	
}
