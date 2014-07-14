package cn.shrek.base.ormlite.stmt;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;

import cn.shrek.base.ZWBo;
import cn.shrek.base.ormlite.DBUtil;
import cn.shrek.base.ormlite.TableInfo;
import cn.shrek.base.ormlite.dao.DBTransforFactory;
import cn.shrek.base.util.ZWLogger;

public abstract class StmtBuilder{
	/**
	 * 空值<设置为空值不会对数据库操作>
	 */
	public static final int NULL_INTEGER = Integer.MIN_VALUE;
	public static final String NULL_STR = "THIS IS NULL VALUE";
	public static final Short NULL_SHORT = Short.MIN_VALUE;
	
	public static final String SPEPARANT_STR = " ";
	private static final String WHERE_KEYWORD = " WHERE ";
	public TableInfo tableInfo ;
	public StringBuffer whereBuffer;
	public StringBuffer sqlBuffer;
	//tableAliases 表的别名
	protected String tableAliases;
	
	private static final String LIKE_KEYWORD = " LIKE ";
	private static final String OR_KEYWORD = " OR ";
	private static final String AND_KEYWORD = " AND ";
	private static final String BETWEEN_KEYWORD = " BETWEEN ";
	//是否可以添加条件
	private boolean isAddCondition = true;
	//括号数量 空值(  )
	private int bracketsNum = 0;
	
	public StmtBuilder(Class<? extends ZWBo> clazz){
		tableInfo = TableInfo.newInstance(clazz);
		whereBuffer = new StringBuffer();
//		sqlBuffer = new StringBuffer();
	}
	
	
	public StmtBuilder and(){
		if(!isAddCondition){
			appendWhereStr(AND_KEYWORD);
			isAddCondition = true;
		}
		return this;
	}
	
	public StmtBuilder or(){
		if(!isAddCondition){
			appendWhereStr(OR_KEYWORD);
			isAddCondition = true;
		}
		return this;
	}
	
	
	/**
	 * 添加等值条件 id=3  name='zhangsan'  company=1 外键 对应 company表中Id=1;
	 * @param fieldName  是属性名  不是表字段名
	 * @param obj 
	 * @return
	 */
	public StmtBuilder eq(String fieldName,Object obj){
		return compare("=", fieldName, obj);
	}
	
	public StmtBuilder notEq(String fieldName,Object obj){
		return compare("<>", fieldName, obj);
	}
	
	
	public StmtBuilder isNull(String fieldName){
		return isNull(true, fieldName);
	}
	
	
	public StmtBuilder isNotNull(String fieldName){
		return isNull(false, fieldName);
	}
	
	/**
	 * 只有可以添加条件时候 添加左括号(   
	 * @return
	 */
	public StmtBuilder leftBrackets(){
		if(isAddCondition){
			appendWhereStr("(");
			bracketsNum++;
		}
		return this;
	}
	
	/**
	 * 添加右括号  
	 * @return
	 */
	public StmtBuilder rightBrackets(){
		if(!isAddCondition && bracketsNum > 0){
			appendWhereStr(")");
			bracketsNum--;
		}
		return this;
	}
	
	/**
	 * like 描述  必须是子串
	 * @param fieldName  属性名<不是表的字段名>
	 * @param likeStr like子串
	 * @param isAddBefor 前面加%？
	 * @param isAddAfter 后面加 通配符?
	 * @return
	 */
	public StmtBuilder like(String fieldName,String likeStr,boolean isAddBefor,boolean isAddAfter){
		int index = tableInfo.getColumnIndexByFieldStr(fieldName);
		Field mField = tableInfo.allField.get(index);
		String columnName = tableInfo.allColumnNames.get(index); 
		Class<?> fieldType = tableInfo.getFieldType(index);
		
		if(initContinue(fieldName, mField, columnName, fieldType,false, fieldName)){
			appendWhereStr(getColumnNameWithAliases(columnName) +LIKE_KEYWORD+"'"+
					(isAddBefor?"%":"")+likeStr+(isAddAfter?"%'":"'"));
			isAddCondition = false;
		}
		return this;
	}
	
	
	public StmtBuilder like(String fieldName,String likeStr){
		int index = tableInfo.getColumnIndexByFieldStr(fieldName);
		Field mField = tableInfo.allField.get(index);
		String columnName = tableInfo.allColumnNames.get(index); 
		Class<?> fieldType = tableInfo.getFieldType(index);
		
		if(initContinue(fieldName, mField, columnName, fieldType,false, fieldName)){
			appendWhereStr(getColumnNameWithAliases(columnName) +LIKE_KEYWORD+"'"+likeStr+"'");
			isAddCondition = false;
		}
		return this;
	}
	
	
	/**
	 * between条件
	 * @param fieldName
	 * @param beforObj
	 * @param afterObj
	 * @return
	 */
	public StmtBuilder between(String fieldName,Object beforObj,Object afterObj){
		int index = tableInfo.getColumnIndexByFieldStr(fieldName);
		Field mField = tableInfo.allField.get(index);
		String columnName = tableInfo.allColumnNames.get(index); 
		Class<?> fieldType = tableInfo.getFieldType(index);
		
		if(initContinue(fieldName, mField, columnName, fieldType,true, beforObj,afterObj)){
			appendWhereStr(getColumnNameWithAliases(columnName) + BETWEEN_KEYWORD);
//			if(Date.class.isAssignableFrom(fieldType)
//					&& beforObj instanceof Date
//					&& afterObj instanceof Date){
//				Date date1 = (Date)beforObj;
//				Date date2 = (Date)afterObj;
//				appendWhereStr(DBUtil.parseDateToLong(date1)+ AND_KEYWORD+DBUtil.parseDateToLong(date2));
//			}else if(Calendar.class.isAssignableFrom(fieldType)
//					&& beforObj instanceof Calendar
//					&& afterObj instanceof Calendar){
//				Calendar date1 = (Calendar)beforObj;
//				Calendar date2 = (Calendar)afterObj;
//				appendWhereStr(DBUtil.parseCalendarToLong(date1)+ AND_KEYWORD+DBUtil.parseCalendarToLong(date2));
//			}else if(String.class.isAssignableFrom(fieldType)){
//				//子串
//				appendWhereStr("'"+beforObj.toString()+"'"+AND_KEYWORD+"'"+afterObj+"'");
//			}else{
//				appendWhereStr(" "+beforObj.toString()+" "+AND_KEYWORD+" "+afterObj+" ");
//			}
			appendWhereStr(DBTransforFactory.getColumnValue(beforObj)+" "+AND_KEYWORD+" "+DBTransforFactory.getColumnValue(afterObj)+" ");
			isAddCondition = false;
		}
		return this;
	}
	
	public StmtBuilder in(String fieldName,Object... objs){
		int index = tableInfo.getColumnIndexByFieldStr(fieldName);
		Field mField = tableInfo.allField.get(index);
		String columnName = tableInfo.allColumnNames.get(index); 
		Class<?> fieldType = tableInfo.getFieldType(index);
		
		if(objs == null || objs.length == 0){
			ZWLogger.printLog(this, "in 的条件（）里面得有值哦~~~");
			return this;
		}
		if(initContinue(fieldName, mField, columnName, fieldType,true, objs)){
			appendWhereStr(getColumnNameWithAliases(columnName) + " in (");
			
			boolean isFirstAdd = true;
			for(Object obj : objs){
				if(!isFirstAdd){
					appendWhereStr(",");	
				}
				isFirstAdd = false;
//				if(Date.class.isAssignableFrom(fieldType)){
//					Date date = (Date)obj;
//					appendWhereStr(DBUtil.parseDateToLong(date)+"");
//				}else if(Calendar.class.isAssignableFrom(fieldType)){
//					Calendar date = (Calendar)obj;
//					appendWhereStr(DBUtil.parseCalendarToLong(date)+"");
//				}else if(String.class.isAssignableFrom(fieldType)){
//					//子串
//					appendWhereStr("'"+obj.toString()+"'");
//				}else{
//					appendWhereStr(" "+obj.toString()+" ");
//				}
				appendWhereStr(" "+DBTransforFactory.getColumnValue(obj)+" ");
			}
			
			
			isAddCondition = false;
			
			appendWhereStr(")");
		}
		return this;
	}
	
	/**
	 * 字段是否 为空值
	 * @param isNull
	 * @param fieldName
	 * @return
	 */
	private StmtBuilder isNull(boolean isNull,String fieldName){
		int index = tableInfo.getColumnIndexByFieldStr(fieldName);
		Field mField = tableInfo.allField.get(index);
		String columnName = tableInfo.allColumnNames.get(index); 
		Class<?> fieldType = tableInfo.getFieldType(index);
		
		if(initContinue(fieldName, mField, columnName, fieldType,false, fieldName)){
			appendWhereStr(getColumnNameWithAliases(columnName) +(isNull?" IS NULL ":" IS NOT NULL "));
			isAddCondition = false;
		}
		return this;
	}
	
	/**
	 * 添加比较的条件
	 * @param compareStr 比较符 = <= >= != <>
	 * @param fieldName 
	 * @param obj
	 * @return
	 */
	public StmtBuilder compare(String compareStr,String fieldName,Object obj){
		int index = tableInfo.getColumnIndexByFieldStr(fieldName);
		Field mField = tableInfo.allField.get(index);
		String columnName = tableInfo.allColumnNames.get(index); 
		Class<?> fieldType = tableInfo.getFieldType(index);
		
		if(initContinue(fieldName, mField, columnName, fieldType,true, obj)){
//			if(obj.getClass().isAssignableFrom(fieldType)){
//				ZWLogger.printLog(StmtBuilder.this, "添加条件 属性类型 和 参数类型不一致！");
//				return this;
//			}
			appendWhereStr(getColumnNameWithAliases(columnName)+compareStr);
			if(Date.class.isAssignableFrom(fieldType)
					&& obj instanceof Date){
				Date date = (Date)obj;
				appendWhereStr(DBUtil.parseDateToLong(date)+"");
			}else if(Calendar.class.isAssignableFrom(fieldType)
					&& obj instanceof Calendar){
				Calendar date = (Calendar)obj;
				appendWhereStr(DBUtil.parseCalendarToLong(date)+"");
			}else if(String.class.isAssignableFrom(fieldType)){
				//子串
				appendWhereStr("'"+obj.toString()+"'");
			}else{
				appendWhereStr(" "+obj.toString()+" ");
			}
			isAddCondition = false;
		}
		return this;
	}
	
	
	/**
	 * 通过 提供的 属性名<不是表的字段名> 得到相关信息
	 * @param fieldName 提供的属性名称
	 * @param mField 初始化的Field
	 * @param columnName 对应 表中的字段名
	 * @param fieldType 类型
	 * @param objs 多个参数
	 * @param isCheckObjNull 是否检测Objs 是否为空
	 * @return true 初始化成功  false 失败
	 */
	private boolean initContinue(String fieldName,Field mField,String columnName,Class<?> fieldType,boolean isCheckObjNull,Object... objs){
		if(fieldName == null || "".equals(fieldName)
				|| (isCheckObjNull && objs == null)){
			for(Object obj : objs){
				if(obj == null){
					throw new NullPointerException("where condition must not null!");
				}
			}
		}

//		for (int i = 0; i < tableInfo.allField.size(); i++) {
//			Field field = tableInfo.allField.get(i);
//			if(fieldName.equals(field.getName())){
//				mField = field;
//				columnName = tableInfo.allColumnNames.get(i);
//				break;
//			}
//		}
		
		if(mField == null || columnName == null){
			ZWLogger.printLog(StmtBuilder.this, "添加条件 属性名："+fieldName+"对于表中的映射字段 根本找不到！");
			return false;
		}
		
		//判断属性是否 外键
//		if(tableInfo.allforeignClassMaps.containsKey(columnName)){
//			fieldType = tableInfo.allforeignClassMaps.get(columnName);
//		}else{
//			fieldType = mField.getType();
//		}
		
		if(isCheckObjNull){
			for(Object obj : objs){
				//基础类型 不检测
				if(!fieldType.isPrimitive() &&!obj.getClass().isAssignableFrom(fieldType)){
					ZWLogger.printLog(StmtBuilder.this, "添加条件 属性类型 和 参数类型不一致！");
					return false;
				}
			}
		}
		
		return true;
	}
	
	
	protected void appendWhereStr(String str){
		if(whereBuffer.length() == 0){
			whereBuffer.append(WHERE_KEYWORD);
		}
		whereBuffer.append(str);
	}
	
	
	public void cycle(){
		whereBuffer = null;
		sqlBuffer = null;
	}
	
	
	public String getWhereSql(){
		return whereBuffer.toString();
	}
	
	/**
	 * 判断表名 是否加上别名
	 * @param tableName
	 * @return
	 */
	public String getTableNameWithAliases(){ 
//		if(tableAliases != null){
//			return tableInfo.tableName+" "+tableAliases;
//		}
		return tableInfo.tableName;
	}
	
	
	
	public String getColumnNameWithAliases(String columnName){
//		if(tableAliases != null){
//			return tableAliases+"."+columnName;
//		}
		return columnName;
	}
	
	/**
	 * 设置表的别名 (SQLite只有 Select支持别名)
	 * @param tableAliases
	 */
	public void setTableAliases(String tableAliases) {
		this.tableAliases = tableAliases;
	}

	

	public String getTableAliases() {
		return tableAliases;
	}


	public abstract String getSql();
}
