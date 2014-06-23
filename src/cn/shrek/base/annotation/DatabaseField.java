package cn.shrek.base.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.FIELD })
public @interface DatabaseField {

	public static final int DEFAULT_MAX_FOREIGN_AUTO_REFRESH_LEVEL = 2;
	public static final String DEFAULT_STRING = "";
	public static final int NO_MAX_FOREIGN_AUTO_REFRESH_LEVEL_SPECIFIED = 255;

	boolean allowGeneratedIdInsert() default true;

	/**
	 * 是否可以为空
	 * 
	 * @return
	 */
	boolean canBeNull() default true;

//	String columnDefinition();

	/**
	 * 缺省的 字段名
	 * 
	 * @return
	 */
	String columnName() default "";

	/**
	 * 数据类型
	 * 
	 * @return
	 */
//	DataType dataType();

	/**
	 * 默认值
	 * 
	 * @return
	 */
	String defaultValue() default DEFAULT_STRING;

	/**
	 * 是否是外键 
	 * @return
	 */
	boolean foreign() default false;
	/**
	 * 建表的时候 是否自动创建
	 * @return
	 */
	boolean foreignAutoCreate() default false;
	/**
	 * 级联操作
	 * @return
	 */
	boolean foreignAutoRefresh() default false;

	/**
	 * 外键 对于属性名 例如 Student.java中 Teacher对象  指向 Teacher的id的属性名
	 * @return
	 */
	String foreignColumnName() default "";
	
	/**
	 * 暂时用在日期的 格式化
	 * @return
	 */
	String format() default "";

	/**
	 * Whether the field is an auto-generated id field. Default is false.
	 */
	boolean generatedId() default false;

//	public String generatedIdSequence();

	/**
	 * Whether the field is the id field or not. Default is false. Only one
	 * field can have this set in a class.
	 * 支持联合主键
	 */
	boolean id() default false;
	
	/**
	 * 是否 创建 索引
	 * 暂不支持 
	 * @return
	 */
	boolean index() default false;

//	public String indexName();

//	public int maxForeignAutoRefreshLevel();

	public boolean throwIfNull() default false;

	/**
	 * 是否唯一
	 * @return
	 */
	boolean unique() default false;

//	public String unknownEnumName();

	/**
	 * 是否试用 get set方法
	 * @return
	 */
	boolean useGetSet() default false;

	
//	public boolean version();

//	public int width();
}
