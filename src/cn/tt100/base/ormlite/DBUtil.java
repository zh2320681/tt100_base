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
	 * ���ݿ� ����
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
	 * ���������� & ��������
	 * 
	 * @param mDatabase
	 * @param paramDatabaseField
	 * @param objTableName
	 *            ��������� ����
	 * @param objFieldName
	 *            ��������� �������ֶ���
	 * @param fkTableName
	 * @param fkFieldName
	 */
	private static final void createTrigeerFKCaceade(SQLiteDatabase mDatabase,
			DatabaseField mDatabaseField, String objTableName,
			String objFieldName, String fkTableName, String fkFieldName) {
		// �������봥����
		StringBuffer insertSB = new StringBuffer("CREATE TRIGGER "
				+ fkFieldName + "_Insert ");
		insertSB.append(" BEFORE Insert ON " + fkTableName);
		insertSB.append(" FOR EACH ROW BEGIN ");
		insertSB.append(" SELECT RAISE(ROLLBACK,'û������ֶ��� " + objFieldName
				+ " in " + objTableName + "')  ");
		insertSB.append(" WHERE (SELECT " + objFieldName + " FROM "
				+ objTableName + " WHERE " + objFieldName + " = NEW."
				+ fkFieldName + ") IS NULL; ");
		insertSB.append(" END ");
		print("�������봥���� ��" + insertSB.toString());

		// �������´�����
		StringBuffer updateSB = new StringBuffer("CREATE TRIGGER "
				+ fkFieldName + "_Update ");
		updateSB.append(" BEFORE Update ON " + fkTableName);
		updateSB.append(" FOR EACH ROW BEGIN ");
		updateSB.append(" SELECT RAISE(ROLLBACK,'û������ֶ��� " + objFieldName
				+ " in " + objTableName + "')  ");
		updateSB.append(" WHERE (SELECT " + objFieldName + " FROM "
				+ objTableName + " WHERE " + objFieldName + " = NEW."
				+ fkFieldName + ") IS NULL; ");
		updateSB.append(" END ");
		print("�������´����� ��" + updateSB.toString());

		// ����Delete������
		StringBuffer deleteSB = new StringBuffer("CREATE TRIGGER "
				+ fkFieldName + "_Delete ");
		deleteSB.append(" BEFORE DELETE ON " + objTableName);
		deleteSB.append(" FOR EACH ROW BEGIN ");
		deleteSB.append(" DELETE FROM " + fkTableName + " WHERE " + fkFieldName
				+ " = OLD." + objFieldName + ";");
		deleteSB.append(" END ");
		print("����Delete������ ��" + deleteSB.toString());

		if (mDatabaseField.foreignAutoRefresh()) {
			// ������������
			StringBuffer caceadeUpdateSB = new StringBuffer("CREATE TRIGGER "
					+ fkFieldName + "_Caceade_Update ");
			caceadeUpdateSB.append(" AFTER Update ON " + objTableName);
			caceadeUpdateSB.append(" FOR EACH ROW BEGIN ");
			caceadeUpdateSB.append(" update " + fkTableName + " set "
					+ fkFieldName + " = new." + objFieldName + " where "
					+ fkFieldName + " = old." + objFieldName + ";");
			caceadeUpdateSB.append(" END ");
			print("������������ ���´����� ��" + caceadeUpdateSB.toString());
		}
	}

	/**
	 * �õ�������
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
	 * �����������
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
	 * ͨ�� field���� �õ���Ӧ���ݿ���ֶ�����
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
			print("���������");
		} else if (fieldClazz.isAssignableFrom(List.class)) {
			print("������List");
		}

		return columnTypeName;
	}

	/**
	 * �õ�����
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
	 * �ж����� �Ƿ���Ч
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
	 * �Ƿ��� ֧�ֵ�����
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
			BaseLog.printLog(TAG, clazz.getName() + " ��֧�ֵ���������");
			return false;
		}
		return true;
	}

	
	private static void print(String paramString) {
		BaseLog.printLog(LogLevel.INFO, "DBUtil", paramString);
	}
}
