package cn.shrek.base.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.shrek.base.ZWConstants;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
public @interface Controller
{
   int layoutId() default ZWConstants.NULL_INT_VALUE;
   
   String idFormat() default ZWConstants.NULL_STR_VALUE;
   
   //是否监听网络<只对activity有效>
   boolean isMonitorNetwork() default false;

}
