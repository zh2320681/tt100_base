package cn.shrek.base.ormlite;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import cn.shrek.base.ZWBo;
import cn.shrek.base.ZWDatabaseBo;
import cn.shrek.base.annotation.DatabaseField;
import cn.shrek.base.annotation.DatabaseTable;
import cn.shrek.base.annotation.Foreign;
import cn.shrek.base.ormlite.foreign.CascadeType;
import cn.shrek.base.util.BaseUtil;
import cn.shrek.base.util.LogLevel;
import cn.shrek.base.util.ZWLogger;

public class DBUtil {

	private static final String FK_CONSTRAINT = "FK_";
	private static final String NOT_NULL_CONSTRAINT = " NOT NULL ";
	private static final String UNIQUE_CONSTRAINT = " UNIQUE ";

	private static final String INDEX_CONSTRAINT = "INDEX_";

	private static final String INTERMEDIATE_CONSTRAINT = "INTERMEDIATE_";
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

	public static final void createTable(SQLiteDatabase mDatabase,
			Class<? extends ZWDatabaseBo> clazz, boolean isExists) {
		TableInfo mTableInfo = TableInfo.newInstance(clazz);
		if (mTableInfo == null) {
			throw new NullPointerException("无法获取" + clazz.getSimpleName()
					+ "表描述对象 TableInfo");
		}

		String tableName = mTableInfo.tableName;
		StringBuffer createSqlSB = new StringBuffer("create table ");
		if (isExists) {
			createSqlSB.append(" IF NOT EXISTS ");
		}
		createSqlSB.append(tableName + "(");

		// 主键
		StringBuffer premarySB = new StringBuffer();
		// 索引<支持组合索引>
		List<String> indexColNames = new ArrayList<String>();
		
		boolean isFirstAddField = true;
		for (int i = 0; i < mTableInfo.allColumnNames.size(); i++) {
			String columnName = mTableInfo.allColumnNames.get(i);
			Field columnField = mTableInfo.allField.get(i);

			DatabaseField mDatabaseField = columnField
					.getAnnotation(DatabaseField.class);
			/** ---------------- -- ---------------------- */
			if (!isFirstAddField) {
				createSqlSB.append(",");
			} else {
				isFirstAddField = false;
			}

			String columnTypeStr = getObjMapping(columnField);
			createSqlSB.append(columnName + " " + columnTypeStr);

			/** ---------------- 约束 ---------------------- */
			if (!mDatabaseField.canBeNull()) {
				createSqlSB.append(NOT_NULL_CONSTRAINT);
			}
			if (mDatabaseField.unique()) {
				createSqlSB.append(UNIQUE_CONSTRAINT);
			}

			String defaultStr = mDatabaseField.defaultValue();
			if (defaultStr != null && !"".equals(defaultStr)) {
				if (columnTypeStr.equals(STRING_COLUMN_NAME)) {
					createSqlSB.append("DEFAULT '" + defaultStr + "' ");
				} else {
					createSqlSB.append("DEFAULT " + defaultStr + " ");
				}
			}

			/** ---------------- 索引 ---------------------- */
			if (mDatabaseField.index()) {
				indexColNames.add(columnName);
			}

			/** ---------------- 主键 ---------------------- */
			if (mDatabaseField.id()) {
				String gapStr = " ";
				if (premarySB.length() != 0) {
					gapStr = ",";
				}
				premarySB.append(gapStr + columnName);
			}
		}

		if (premarySB.length() > 0) {
			createSqlSB.append(",primary key(" + premarySB.toString() + ")");
		}
		createSqlSB.append(");");
		ZWLogger.printLog(TAG, "创建表的语句：" + createSqlSB.toString());
		mDatabase.execSQL(createSqlSB.toString());

		/** ---------------- 创建索引 ---------------------- */
		if (indexColNames.size() > 0) {
			boolean isFirstAddIndex = true;
			StringBuffer indexSB = new StringBuffer(
					"CREATE INDEX  IF NOT EXISTS " + INDEX_CONSTRAINT
							+ tableName + " ON " + tableName + "(");
			for (String indexColumn : indexColNames) {
				indexSB.append(indexColumn + (isFirstAddIndex ? "," : ""));
				isFirstAddIndex = false;
			}
			indexSB.append(");");
			ZWLogger.printLog(TAG, "创建索引的语句：" + indexSB.toString());
			mDatabase.execSQL(indexSB.toString());
		}

		// 触发器
		List<String> trigeerArr = new ArrayList<String>();
		
		for(ForeignInfo fInfo : mTableInfo.allforeignInfos){
			/** ---------------- 外键 ---------------------- */
			Field fkColumnField = mTableInfo.allforeignMaps.get(columnName);
			if (fkColumnField != null) {
				Class<?> objClazz = mTableInfo.allforeignClassMaps
						.get(columnName);
				// 有外键 得到外键 指向的 那个表名
				trigeerArr.addAll(getTrigeerFKCaceade(mDatabaseField,
						getTableName(objClazz), getFeildName(fkColumnField),
						tableName, columnName));
			}
		}
		/** ---------------- 创建触发器 ---------------------- */
		for (String trige : trigeerArr) {
			try {
				mDatabase.execSQL(trige);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
	}

	public static final void dropTable(SQLiteDatabase mDatabase,
			Class<? extends ZWDatabaseBo> clazz) {
		TableInfo mTableInfo = TableInfo.newInstance(clazz);
		if (mTableInfo == null) {
			throw new NullPointerException("无法获取" + clazz.getSimpleName()
					+ "表描述对象 TableInfo");
		}
		StringBuffer dropSB = new StringBuffer("DROP TABLE "
				+ mTableInfo.tableName);
		ZWLogger.printLog(TAG, "DROP TABLE的语句：" + dropSB.toString());
		mDatabase.execSQL(dropSB.toString());
	}

	/**
	 * 主表 和中间表的 级联操作
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
	private static final List<String> getTrigeerFKCaceade(ForeignInfo info) {
		Foreign mForeign = info.getForeignAnn(); 
		String fkTableName= getTableName(info.originalClazz);
		String fkFieldName= getFeildName(info.originalField);
		String objTableName = getTableName(info.foreignClazz);
		String objFieldName = getFeildName(info.foreignField);
		
		List<String> trigeerArr = new ArrayList<String>();
		
		boolean isPersist = false,isMerge = false,isRefresh = false ,isRemove = false;
		
		for(CascadeType cType : mForeign.cascade()){
			
			if(cType == CascadeType.ALL){
				isPersist = isMerge = isRefresh = isRemove = true;
				break;
			}
			
			switch (cType) {
			case PERSIST:
				//插入
				isPersist = true;
				break;
			case MERGE:
				//更新
				isMerge = true;
				break;
			case REFRESH:
				//更新
				isRefresh = true;
				break;
			case REMOVE:
				//删除
				isRemove = true;
				break;
			default:
				break;
			}
		}
		
		if(isPersist){
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
			trigeerArr.add(insertSB.toString());
		}
		
		if(isRefresh){
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
			trigeerArr.add(updateSB.toString());
		}

		if(isRemove){
			// 创建Delete触发器
			StringBuffer deleteSB = new StringBuffer("CREATE TRIGGER "
					+ fkFieldName + "_Delete ");
			deleteSB.append(" BEFORE DELETE ON " + objTableName);
			deleteSB.append(" FOR EACH ROW BEGIN ");
			deleteSB.append(" DELETE FROM " + fkTableName + " WHERE " + fkFieldName
					+ " = OLD." + objFieldName + ";");
			deleteSB.append(" END ");
			print("创建Delete触发器 ：" + deleteSB.toString());
			trigeerArr.add(deleteSB.toString());
		}

		if (isMerge) {
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
			trigeerArr.add(caceadeUpdateSB.toString());
		}
		return trigeerArr;
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
	 * 返回外键名称 FK_TEACHER_ID
	 * 
	 * @param paramString
	 * @param clazz
	 * @return
	 */
	public static final String getMapFKCulmonName(String paramString,
			Class<?> clazz) {
		return FK_CONSTRAINT + clazz.getSimpleName() + "_" + paramString;
	}

	/**
	 * 得到中建表的 名称
	 * 
	 * @param clazz1
	 * @param clazz2
	 * @return
	 */
	public static final String getIntermediateTableName(Class<?> clazz1,
			Class<?> clazz2) {
		String tableName1 = clazz1.getSimpleName().toUpperCase();
		String tableName2 = clazz2.getSimpleName().toUpperCase();

		StringBuffer sb = new StringBuffer(INTERMEDIATE_CONSTRAINT);
		if (tableName1.compareTo(tableName2) > 0) {
			sb.append(tableName1 + "_" + tableName2);
		} else {
			sb.append(tableName2 + "_" + tableName1);
		}
		return sb.toString();
	}

	/**
	 * 判断外键的名字是否 有效 Teacher.id
	 * 
	 * @param fkName
	 * @return
	 */
	public static final boolean isFKNameValid(String fkName) {
		if (BaseUtil.isStringValid(fkName)) {
			int index = fkName.indexOf(".");
			if (index > 0 && index < (fkName.length() - 1)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 通过 fkName 得到 对于的 class Name
	 * 
	 * @param fkName
	 * @return
	 */
	public static final String getClazzNameByFkName(String fkName) {
		int index = fkName.indexOf(".");
		return fkName.substring(0, index);
	}

	/**
	 * 通过 fkName 得到 对于的 外键属性 Name
	 * 
	 * @param fkName
	 * @return
	 */
	public static final String getFieldNameByFkName(String fkName) {
		int index = fkName.indexOf(".");
		return fkName.substring(index + 1, fkName.length());
	}

	/**
	 * 通过 field类型 得到对应数据库的字段类型
	 * 
	 * @param field
	 * @return
	 */
	public static final String getObjMapping(Field field) {
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
		} else if (fieldClazz.isAssignableFrom(ZWBo.class)) {
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
	 * 
	 * @param field
	 * @return
	 */
	public static final boolean judgeFieldAvaid(Field field) {
		DatabaseField mDatabaseField = field.getAnnotation(DatabaseField.class);
		Foreign mForeign = field.getAnnotation(Foreign.class);

		Class<?> clazz = field.getType();

		if ((mDatabaseField != null && isSupportType(field))
				|| (mForeign != null && (Collection.class
						.isAssignableFrom(clazz) || ZWDatabaseBo.class
						.isAssignableFrom(clazz)))) {
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
				&& !ZWDatabaseBo.class.isAssignableFrom(clazz)) {
			ZWLogger.e(TAG, clazz.getName() + " 不支持的数据类型");
			return false;
		}
		return true;
	}

	private static void print(String paramString) {
		ZWLogger.printLog(LogLevel.INFO, "DBUtil", paramString);
	}

	/**
	 * 时间 和 long之间转换
	 * 
	 * @param obj
	 * @return
	 */
	public static long parseDateToLong(Date obj) {
		return obj.getTime();
	}

	public static long parseCalendarToLong(Calendar obj) {
		return parseDateToLong(obj.getTime());
	}

	public static Date parseLongToDate(long value) {
		return new Date(value);
	}

	public static Calendar parseLongToCalendar(long value) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(parseLongToDate(value));
		return cal;
	}

	public static void timeCompute(long before, long after) {
		ZWLogger.printLog("数据库操作:", "数据库操作耗时:" + (after - before) + "毫秒!");
	}
}
