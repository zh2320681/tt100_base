package cn.tt100.base.ormlite;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.tt100.base.BaseBo;
import cn.tt100.base.annotation.DatabaseField;
import cn.tt100.base.annotation.DatabaseTable;
import cn.tt100.base.util.BaseLog;
import cn.tt100.base.util.LogLevel;
import android.database.sqlite.SQLiteDatabase;

public class DBUtil {

	public static final String FK_CONSTRAINT = "FK_";
	public static final String NOT_NULL_CONSTRAINT = " NOT NULL ";

	/**
	 * 数据库 类型
	 */
	public static final String BLOB_COLUMN_NAME = "BLOB";
	public static final String INT_COLUMN_NAME = "INTEGER";
	public static final String NULL_COLUMN_NAM = "NULL";
	public static final String REAL_COLUMN_NAME = "REAL";
	public static final String STRING_COLUMN_NAME = "TEXT";

	public static final String SPEPARANT_STR = " ";

	public static final String YINHAO_STR = "'";

	public static final Object LOCK_OBJ = new Object();

	private static final String TAG = "DBUtil";

	public static final void createTable(SQLiteDatabase mDatabase, Class<? extends BaseBo> clazz, boolean isExists){
		String tableName = getTableName(clazz);
		StringBuffer createSqlSB = new StringBuffer("create table ");
		if (isExists){
			createSqlSB.append(" IF NOT EXISTS ");
		}
		createSqlSB.append(tableName);
		
  }

	/**
	 * 创建触发器 & 级联操作
	 * 
	 * @param mDatabase
	 * @param paramDatabaseField
	 * @param objTableName
	 *            外键关联的 表名
	 * @param objFieldName
	 *            外键关联的 表名的字段名
	 * @param fkTableName
	 * @param fkFieldName
	 */
	private static final void createTrigeerFKCaceade(SQLiteDatabase mDatabase,
			DatabaseField mDatabaseField, String objTableName,
			String objFieldName, String fkTableName, String fkFieldName) {
		// 创建插入触发器
		StringBuffer insertSB = new StringBuffer("CREATE TRIGGER "
				+ fkFieldName + "_Insert ");
		insertSB.append(" BEFORE Insert ON " + fkTableName);
		insertSB.append(" FOR EACH ROW BEGIN ");
		insertSB.append(" SELECT RAISE(ROLLBACK,'没有这个字段名 " + objFieldName
				+ " in " + objTableName + "')  ");
		insertSB.append(" WHERE (SELECT " + objFieldName + " FROM "
				+ objTableName + " WHERE " + objFieldName + " = NEW."
				+ fkFieldName + ") IS NULL; ");
		insertSB.append(" END ");
		print("创建插入触发器 ：" + insertSB.toString());

		// 创建更新触发器
		StringBuffer updateSB = new StringBuffer("CREATE TRIGGER "
				+ fkFieldName + "_Update ");
		updateSB.append(" BEFORE Update ON " + fkTableName);
		updateSB.append(" FOR EACH ROW BEGIN ");
		updateSB.append(" SELECT RAISE(ROLLBACK,'没有这个字段名 " + objFieldName
				+ " in " + objTableName + "')  ");
		updateSB.append(" WHERE (SELECT " + objFieldName + " FROM "
				+ objTableName + " WHERE " + objFieldName + " = NEW."
				+ fkFieldName + ") IS NULL; ");
		updateSB.append(" END ");
		print("创建更新触发器 ：" + updateSB.toString());

		// 创建Delete触发器
		StringBuffer deleteSB = new StringBuffer("CREATE TRIGGER "
				+ fkFieldName + "_Delete ");
		deleteSB.append(" BEFORE DELETE ON " + objTableName);
		deleteSB.append(" FOR EACH ROW BEGIN ");
		deleteSB.append(" DELETE FROM " + fkTableName + " WHERE " + fkFieldName
				+ " = OLD." + objFieldName + ";");
		deleteSB.append(" END ");
		print("创建Delete触发器 ：" + deleteSB.toString());

		if (mDatabaseField.foreignAutoRefresh()) {
			// 创建级联操作
			StringBuffer caceadeUpdateSB = new StringBuffer("CREATE TRIGGER "
					+ fkFieldName + "_Caceade_Update ");
			caceadeUpdateSB.append(" AFTER Update ON " + objTableName);
			caceadeUpdateSB.append(" FOR EACH ROW BEGIN ");
			caceadeUpdateSB.append(" update " + fkTableName + " set "
					+ fkFieldName + " = new." + objFieldName + " where "
					+ fkFieldName + " = old." + objFieldName + ";");
			caceadeUpdateSB.append(" END ");
			print("创建级联操作 更新触发器 ：" + caceadeUpdateSB.toString());
		}
	}

	/**
	 * 得到属性名
	 * 
	 * @param field
	 * @return
	 */
	public static final String getFeildName(Field field) {
		String fieldName = field.getAnnotation(DatabaseField.class)
				.columnName();
		if (fieldName == null || "".equals(fieldName)) {
			fieldName = field.getName();
		}
		return fieldName;
	}

	/**
	 * 返回外键名称
	 * FK_TEACHER_ID
	 * @param paramString
	 * @param clazz
	 * @return
	 */
	public static final String getMapFKCulmonName(String paramString,
			Class<?> clazz) {
		return "FK_" + clazz.getSimpleName() + "_" + paramString;
	}

	/**
	 * 通过 field类型 得到对应数据库的字段类型
	 * 
	 * @param field
	 * @return
	 */
	private static final String getObjMapping(Field field) {
		Class<?> fieldClazz = field.getType();
		String columnTypeName = STRING_COLUMN_NAME;
		if (fieldClazz.isAssignableFrom(String.class)) {
			columnTypeName = STRING_COLUMN_NAME;
		} else if (fieldClazz.isAssignableFrom(Date.class)
				|| fieldClazz.isAssignableFrom(Calendar.class)) {
			columnTypeName = INT_COLUMN_NAME;
		} else if (fieldClazz.isPrimitive()) {
			if (fieldClazz.isAssignableFrom(Integer.class)
					|| fieldClazz.isAssignableFrom(int.class)
					|| fieldClazz.isAssignableFrom(Boolean.class)
					|| fieldClazz.isAssignableFrom(boolean.class)) {
				columnTypeName = INT_COLUMN_NAME;
			} else if (fieldClazz.isAssignableFrom(Double.class)
					|| fieldClazz.isAssignableFrom(double.class)
					|| fieldClazz.isAssignableFrom(Float.class)
					|| fieldClazz.isAssignableFrom(float.class)) {
				columnTypeName = REAL_COLUMN_NAME;
			}
		} else if (fieldClazz.isAssignableFrom(BaseBo.class)) {
			print("可能是外键");
		} else if (fieldClazz.isAssignableFrom(List.class)) {
			print("可能是List");
		}

		return columnTypeName;
	}

	/**
	 * 得到表名
	 * 
	 * @param clazz
	 * @return
	 */
	public static final String getTableName(Class<?> clazz) {
		String tableName = clazz.getAnnotation(DatabaseTable.class).tableName();
		if (tableName == null || "".equals(tableName)) {
			tableName = clazz.getSimpleName();
		}
		return tableName;
	}

	/**
	 * 判断属性 是否有效
	 * @param field
	 * @return
	 */
	public static final  boolean judgeFieldAvaid(Field field){
		DatabaseField mDatabaseField = field.getAnnotation(DatabaseField.class);
		if(mDatabaseField != null
				&& isSupportType(field)){
			return true;
		}	
		return false;
	}
	
	
	/**
	 * 是否是 支持的类型
	 * 
	 * @param mField
	 * @return
	 */
	public static boolean isSupportType(Field mField) {
		Class<?> clazz = mField.getType();
		if (!clazz.isPrimitive() && !String.class.isAssignableFrom(clazz)
				&& !clazz.isAssignableFrom(Date.class)
				&& !clazz.isAssignableFrom(Calendar.class)
				&& !Number.class.isAssignableFrom(clazz)
				&& !List.class.isAssignableFrom(clazz)
				&& !BaseBo.class.isAssignableFrom(clazz)) {
			BaseLog.printLog(TAG, clazz.getName() + " 不支持的数据类型");
			return false;
		}
		return true;
	}

	
	private static void print(String paramString) {
		BaseLog.printLog(LogLevel.INFO, "DBUtil", paramString);
	}
}
