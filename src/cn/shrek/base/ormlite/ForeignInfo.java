package cn.shrek.base.ormlite;

import java.lang.reflect.Field;

import cn.shrek.base.ZWDatabaseBo;
import cn.shrek.base.annotation.Foreign;

/**
 * 外键信息
 * @author shrek
 *
 */
public class ForeignInfo {
	/**
	 * 原表属性 和 对应的 新表的属性
	 */
	Field originalField,foreignField;
	Class<? extends ZWDatabaseBo> originalClazz,foreignClazz;
	String middleTableName;
	
	public ForeignInfo(){
		super();
	}

	public Field getOriginalField() {
		return originalField;
	}

	public void setOriginalField(Field originalField) {
		this.originalField = originalField;
	}

	public Field getForeignField() {
		return foreignField;
	}

	public void setForeignField(Field foreignField) {
		this.foreignField = foreignField;
	}

	public Class<? extends ZWDatabaseBo> getOriginalClazz() {
		return originalClazz;
	}

	public void setOriginalClazz(Class<? extends ZWDatabaseBo> originalClazz) {
		this.originalClazz = originalClazz;
	}

	public Class<? extends ZWDatabaseBo> getForeignClazz() {
		return foreignClazz;
	}

	public void setForeignClazz(Class<? extends ZWDatabaseBo> foreignClazz) {
		this.foreignClazz = foreignClazz;
	}

	public String getMiddleTableName() {
		return middleTableName;
	}

	public void setMiddleTableName(String middleTableName) {
		this.middleTableName = middleTableName;
	}
	
	/**
	 * 得到外键的注释
	 * @return
	 */
	public Foreign getForeignAnn(){
		return originalField.getAnnotation(Foreign.class);
	}
	
	
}
