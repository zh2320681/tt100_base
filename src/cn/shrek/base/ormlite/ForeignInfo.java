package cn.shrek.base.ormlite;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.shrek.base.ZWDatabaseBo;
import cn.shrek.base.annotation.Foreign;
import cn.shrek.base.ormlite.foreign.MiddleOperator;
import cn.shrek.base.util.BaseUtil;
import cn.shrek.base.util.ReflectUtil;

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
	/**
	 * 属性值 可能是集合 或者 实体 
	 */
	Field valueField;
	
	Class<? extends ZWDatabaseBo> originalClazz,foreignClazz;
	
	String middleTableName,originalColumnName,foreignColumnName;
	
	/**
	 * 触发器
	 */
	List<String> trigeerNameArr;
	
	MiddleOperator mMiddleOperator;
	
	public ForeignInfo(){
		super();
		trigeerNameArr = new ArrayList<String>();
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

	public String getOriginalColumnName() {
		return originalColumnName;
	}

	public void setOriginalFieldName(String originalFieldName) {
		this.originalColumnName = originalFieldName;
	}

	public String getForeignColumnName() {
		return foreignColumnName;
	}

	public void setForeignColumnName(String foreignFieldName) {
		this.foreignColumnName = foreignFieldName;
	}

	public Field getValueField() {
		return valueField;
	}

	public MiddleOperator getmMiddleOperator() {
		return mMiddleOperator;
	}

	/**
	 * 初始化值
	 */
	public void initValue(){
		String tableName1 = originalClazz.getSimpleName().toUpperCase();
		String tableName2 = foreignClazz.getSimpleName().toUpperCase();

		StringBuffer sb = new StringBuffer(DBUtil.INTERMEDIATE_CONSTRAINT);
		if (tableName1.compareTo(tableName2) > 0) {
			sb.append(tableName1 + "_" + tableName2);
		} else {
			sb.append(tableName2 + "_" + tableName1);
		}
		
		middleTableName = sb.toString();
		
		originalColumnName = DBUtil.FK_CONSTRAINT+ tableName1 +"_"+originalField.getName();
		foreignColumnName = DBUtil.FK_CONSTRAINT+ tableName2 +"_"+foreignField.getName();
		
		mMiddleOperator = new MiddleOperator(this);
	}
	
	/**
	 * 得到外键的注释
	 * @return
	 */
	public Foreign getForeignAnn(){
		return valueField.getAnnotation(Foreign.class);
	}
	
	/**
	 * 添加触发器的 名字
	 * @param trigger
	 */
	public void addTiggerName(String trigger){
		if(BaseUtil.isStringValid(trigger)){
			trigeerNameArr.add(trigger);
		}
	}
	
	/**
	 * 得到 原属性值
	 * @param host
	 * @return
	 */
	public Object getOriginalFieldValue(Object host){
		return ReflectUtil.getFieldValue(host, originalField);
	}
	
	
	/**
	 * 得到 指向的属性值
	 * @param host
	 * @return
	 */
	public Object getForeignFieldValue(Object host){
		return ReflectUtil.getFieldValue(host, foreignField);
	}
	
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return middleTableName.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if(o instanceof ForeignInfo){
			ForeignInfo info = (ForeignInfo)o;
			return middleTableName.equals(info.middleTableName);
		}
		return super.equals(o);
	}
}
