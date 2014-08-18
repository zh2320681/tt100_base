package cn.shrek.base.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.shrek.base.ormlite.foreign.CascadeType;
import cn.shrek.base.ormlite.foreign.MappingType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.FIELD })
public @interface Foreign {
	/**
	 * 建表的时候 是否自动创建
	 * @return
	 */
	boolean foreignAutoCreate() default false;

	/**
	 * 级联操作
	 * @return
	 */
	CascadeType[] cascade() default {};

	/**
	 * 外键 对于属性名 例如 Student.java中 Teacher对象  指向 Teacher的id的属性名
	 * @return Teacher.id
	 */
	String foreignColumnName();
	
	/**
	 * 原来的 属性名  例如 Student.java ---> Teacher  id
	 * @return
	 */
	String originalColumnName();
	
	/**
	 * 映射关系
	 * @return
	 */
	MappingType mappingType() default MappingType.NONE;

}
