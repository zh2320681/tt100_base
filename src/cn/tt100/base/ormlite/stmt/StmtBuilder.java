package cn.tt100.base.ormlite.stmt;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;

import cn.tt100.base.ZWBo;
import cn.tt100.base.ormlite.DBUtil;
import cn.tt100.base.ormlite.TableInfo;
import cn.tt100.base.ormlite.dao.DBTransforFactory;
import cn.tt100.base.util.ZWLogger;

public abstract class StmtBuilder{
	/**
	 * ��ֵ<����Ϊ��ֵ��������ݿ����>
	 */
	public static final int NULL_INTEGER = Integer.MIN_VALUE;
	public static final String NULL_STR = "THIS IS NULL VALUE";
	public static final Short NULL_SHORT = Short.MIN_VALUE;
	
	public static final String SPEPARANT_STR = " ";
	private static final String WHERE_KEYWORD = " WHERE ";
	public TableInfo tableInfo ;
	public StringBuffer whereBuffer;
	public StringBuffer sqlBuffer;
	
	private static final String LIKE_KEYWORD = " LIKE ";
	private static final String OR_KEYWORD = " OR ";
	private static final String AND_KEYWORD = " AND ";
	private static final String BETWEEN_KEYWORD = " BETWEEN ";
	//�Ƿ�����������
	private boolean isAddCondition = true;
	//�������� ��ֵ(  )
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
	 * ��ӵ�ֵ���� id=3  name='zhangsan'  company=1 ��� ��Ӧ company����Id=1;
	 * @param fieldName  ��������  ���Ǳ��ֶ���
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
	 * ֻ�п����������ʱ�� ���������(   
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
	 * ���������  
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
	 * like ����  �������Ӵ�
	 * @param fieldName  ������<���Ǳ���ֶ���>
	 * @param likeStr like�Ӵ�
	 * @param isAddBefor ǰ���%��
	 * @param isAddAfter ����� ͨ���?
	 * @return
	 */
	public StmtBuilder like(String fieldName,String likeStr,boolean isAddBefor,boolean isAddAfter){
		int index = tableInfo.getColumnIndexByFieldStr(fieldName);
		Field mField = tableInfo.allField.get(index);
		String columnName = tableInfo.allColumnNames.get(index); 
		Class<?> fieldType = tableInfo.getFieldType(index);
		
		if(initContinue(fieldName, mField, columnName, fieldType,false, fieldName)){
			appendWhereStr(columnName +LIKE_KEYWORD+"'"+
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
			appendWhereStr(columnName +LIKE_KEYWORD+"'"+likeStr+"'");
			isAddCondition = false;
		}
		return this;
	}
	
	
	/**
	 * between����
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
			appendWhereStr(columnName + BETWEEN_KEYWORD);
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
//				//�Ӵ�
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
			ZWLogger.printLog(this, "in �����������������ֵŶ~~~");
			return this;
		}
		if(initContinue(fieldName, mField, columnName, fieldType,true, objs)){
			appendWhereStr(columnName + " in (");
			
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
//					//�Ӵ�
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
	 * �ֶ��Ƿ� Ϊ��ֵ
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
			appendWhereStr(columnName +(isNull?"IS NULL":"IS NOT NULL"));
			isAddCondition = false;
		}
		return this;
	}
	
	/**
	 * ��ӱȽϵ�����
	 * @param compareStr �ȽϷ� = <= >= != <>
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
//				ZWLogger.printLog(StmtBuilder.this, "������� �������� �� �������Ͳ�һ�£�");
//				return this;
//			}
			appendWhereStr(columnName+compareStr);
			if(Date.class.isAssignableFrom(fieldType)
					&& obj instanceof Date){
				Date date = (Date)obj;
				appendWhereStr(DBUtil.parseDateToLong(date)+"");
			}else if(Calendar.class.isAssignableFrom(fieldType)
					&& obj instanceof Calendar){
				Calendar date = (Calendar)obj;
				appendWhereStr(DBUtil.parseCalendarToLong(date)+"");
			}else if(String.class.isAssignableFrom(fieldType)){
				//�Ӵ�
				appendWhereStr("'"+obj.toString()+"'");
			}else{
				appendWhereStr(" "+obj.toString()+" ");
			}
			isAddCondition = false;
		}
		return this;
	}
	
	
	/**
	 * ͨ�� �ṩ�� ������<���Ǳ���ֶ���> �õ������Ϣ
	 * @param fieldName �ṩ����������
	 * @param mField ��ʼ����Field
	 * @param columnName ��Ӧ ���е��ֶ���
	 * @param fieldType ����
	 * @param objs �������
	 * @param isCheckObjNull �Ƿ���Objs �Ƿ�Ϊ��
	 * @return true ��ʼ���ɹ�  false ʧ��
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
			ZWLogger.printLog(StmtBuilder.this, "������� ��������"+fieldName+"���ڱ��е�ӳ���ֶ� �����Ҳ�����");
			return false;
		}
		
		//�ж������Ƿ� ���
//		if(tableInfo.allforeignClassMaps.containsKey(columnName)){
//			fieldType = tableInfo.allforeignClassMaps.get(columnName);
//		}else{
//			fieldType = mField.getType();
//		}
		
		if(isCheckObjNull){
			for(Object obj : objs){
				//�������� �����
				if(!fieldType.isPrimitive() &&!obj.getClass().isAssignableFrom(fieldType)){
					ZWLogger.printLog(StmtBuilder.this, "������� �������� �� �������Ͳ�һ�£�");
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
	
	public abstract String getSql();
}
