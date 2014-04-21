package cn.tt100.base.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


import android.renderscript.Element.DataType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.FIELD })
public @interface DatabaseField {

	public static final int DEFAULT_MAX_FOREIGN_AUTO_REFRESH_LEVEL = 2;
	public static final String DEFAULT_STRING = "";
	public static final int NO_MAX_FOREIGN_AUTO_REFRESH_LEVEL_SPECIFIED = 255;

	boolean allowGeneratedIdInsert() default true;

	/**
	 * �Ƿ����Ϊ��
	 * 
	 * @return
	 */
	boolean canBeNull() default true;

	String columnDefinition();

	/**
	 * ȱʡ�� �ֶ���
	 * 
	 * @return
	 */
	String columnName() default "";

	/**
	 * ��������
	 * 
	 * @return
	 */
	DataType dataType();

	/**
	 * Ĭ��ֵ
	 * 
	 * @return
	 */
	String defaultValue() default DEFAULT_STRING;

	/**
	 * �Ƿ������ 
	 * @return
	 */
	boolean foreign() default false;
	/**
	 * �����ʱ�� �Ƿ��Զ�����
	 * @return
	 */
	boolean foreignAutoCreate() default false;
	/**
	 * ��������
	 * @return
	 */
	boolean foreignAutoRefresh() default false;

	/**
	 * ��� ���������� ���� Student.java�� Teacher����  ָ�� Teacher��id��������
	 * @return
	 */
	String foreignColumnName() default "";
	
	/**
	 * ��ʱ�������ڵ� ��ʽ��
	 * @return
	 */
	String format() default "";

	/**
	 * Whether the field is an auto-generated id field. Default is false.
	 */
	boolean generatedId() default false;

	public String generatedIdSequence();

	/**
	 * Whether the field is the id field or not. Default is false. Only one
	 * field can have this set in a class.
	 * ֧����������
	 */
	boolean id() default false;
	
	/**
	 * �Ƿ� ���� ����
	 * �ݲ�֧�� 
	 * @return
	 */
	boolean index() default false;

	public String indexName();

	public int maxForeignAutoRefreshLevel();

	public boolean throwIfNull();

	/**
	 * �Ƿ�Ψһ
	 * @return
	 */
	boolean unique() default false;

	public String unknownEnumName();

	/**
	 * �Ƿ����� get set����
	 * @return
	 */
	boolean useGetSet() default false;

	
	public boolean version();

	public int width();
}
