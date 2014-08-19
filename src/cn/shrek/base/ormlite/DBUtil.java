package cn.shrek.base.ormlite;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import cn.shrek.base.ZWBo;
import cn.shrek.base.ZWConstants;
import cn.shrek.base.ZWDatabaseBo;
import cn.shrek.base.annotation.DatabaseField;
import cn.shrek.base.annotation.DatabaseTable;
import cn.shrek.base.annotation.Foreign;
import cn.shrek.base.ormlite.foreign.CascadeType;
import cn.shrek.base.util.BaseUtil;
import cn.shrek.base.util.LogLevel;
import cn.shrek.base.util.ZWLogger;

public class DBUtil {

	static final String FK_CONSTRAINT = "FK_";
	private static final String NOT_NULL_CONSTRAINT = " NOT NULL ";
	private static final String UNIQUE_CONSTRAINT = " UNIQUE ";

	private static final String INDEX_CONSTRAINT = "INDEX_";

	static final String INTERMEDIATE_CONSTRAINT = "INTERMEDIATE_";
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
			TableInfo mTableInfo, boolean isExists) {
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
			String indeName = INDEX_CONSTRAINT + tableName;
			StringBuffer indexSB = new StringBuffer(
					"CREATE INDEX  IF NOT EXISTS " + indeName + " ON "
							+ tableName + "(");
			for (String indexColumn : indexColNames) {
				indexSB.append(indexColumn + (isFirstAddIndex ? "," : ""));
				isFirstAddIndex = false;
			}
			indexSB.append(");");
			ZWLogger.printLog(TAG, "创建索引的语句：" + indexSB.toString());
			mDatabase.execSQL(indexSB.toString());

			mTableInfo.indexTableName = indeName;
		}

		// 触发器
		List<String> trigeerArr = new ArrayList<String>();

		/** ---------------- 外键 ---------------------- */
		for (ForeignInfo fInfo : mTableInfo.allforeignInfos) {
			// 创建 中建表
			if (!tabbleIsExist(mDatabase, fInfo.getMiddleTableName())) {
				StringBuffer middleCreateSqlSB = new StringBuffer(
						"create table ");
				middleCreateSqlSB.append(" IF NOT EXISTS "
						+ fInfo.getMiddleTableName() + "(");
				middleCreateSqlSB.append(fInfo.getOriginalColumnName() + " "
						+ getObjMapping(fInfo.getOriginalField()) + ",");
				middleCreateSqlSB.append(fInfo.getForeignColumnName() + " "
						+ getObjMapping(fInfo.getForeignField())
						+ ",primary key(" + fInfo.getOriginalColumnName()
						+ "," + fInfo.getForeignColumnName() + "));");
				ZWLogger.printLog(TAG,
						"创建中建表的语句：" + middleCreateSqlSB.toString());

				mDatabase.execSQL(middleCreateSqlSB.toString());
			}
			trigeerArr.addAll(getTrigeerFKCaceade(fInfo));
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

	/**
	 * 删除表 同时 也删除 和该表相关的 索引 触发器 中建表
	 * 
	 * @param mDatabase
	 * @param tableInfo
	 */
	public static final void dropTable(SQLiteDatabase mDatabase,
			TableInfo tableInfo) {
		StringBuffer dropSB = new StringBuffer("DROP TABLE IF EXISTS "
				+ tableInfo.tableName);
		ZWLogger.printLog(TAG, "DROP TABLE的语句：" + dropSB.toString());
		mDatabase.execSQL(dropSB.toString());

		/** --------------- 删除索引 ------------------ */
		if (BaseUtil.isStringValid(tableInfo.indexTableName)) {
			String indexDropName = "DROP INDEX IF EXISTS "
					+ tableInfo.indexTableName + ";";
			ZWLogger.printLog(TAG, "DROP TRIGGER的语句：" + indexDropName);
			mDatabase.execSQL(indexDropName);
		}

		/** --------------- 删除外键 ------------------ */
		for (ForeignInfo fInfo : tableInfo.allforeignInfos) {
			dropForeignTable(mDatabase, fInfo);
		}

	}

	/**
	 * 删除外键 和外键相关的
	 * 
	 * @param mDatabase
	 * @param info
	 */
	public static final void dropForeignTable(SQLiteDatabase mDatabase,
			ForeignInfo info) {
		StringBuffer dropSB = new StringBuffer("DROP TABLE IF EXISTS "
				+ info.middleTableName);
		ZWLogger.printLog(TAG, "DROP TABLE的语句：" + dropSB.toString());
		mDatabase.execSQL(dropSB.toString());

		// 删除触发器
		for (String trigger : info.trigeerNameArr) {
			String dropTrigger = "DROP TRIGGER IF EXISTS " + trigger + ";";
			ZWLogger.printLog(TAG, "DROP TRIGGER的语句：" + dropTrigger);
			mDatabase.execSQL(dropTrigger);
		}
	}

	/**
	 * 主表 和中间表的 级联操作 创建触发器 & 级联操作
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
		String objTableName = getTableName(info.originalClazz);
		String objFieldName = getColumnName(info.originalField);
		String middleTableName = info.getMiddleTableName();
		String middleFieldName = info.getOriginalColumnName();

		List<String> trigeerArr = new ArrayList<String>();

		boolean isPersist = false, isMerge = false, isRefresh = false, isRemove = false;

		for (CascadeType cType : mForeign.cascade()) {

			if (cType == CascadeType.ALL) {
				isPersist = isMerge = isRefresh = isRemove = true;
				break;
			}

			switch (cType) {
			case PERSIST:
				// 插入
				isPersist = true;
				break;
			case MERGE:
				// 更新
				isMerge = true;
				break;
			case REFRESH:
				// 更新
				isRefresh = true;
				break;
			case REMOVE:
				// 删除
				isRemove = true;
				break;
			default:
				break;
			}
		}

		if (isPersist) {
			// 创建插入触发器
			String triggerTableName = middleFieldName + "_Insert";
			StringBuffer insertSB = new StringBuffer("CREATE TRIGGER "
					+ triggerTableName);
			insertSB.append(" BEFORE Insert ON " + middleTableName);
			insertSB.append(" FOR EACH ROW BEGIN ");
			insertSB.append(" SELECT RAISE(ROLLBACK,'没有这个字段名 " + objFieldName
					+ " in " + objTableName + "')  ");
			insertSB.append(" WHERE (SELECT " + objFieldName + " FROM "
					+ objTableName + " WHERE " + objFieldName + " = NEW."
					+ middleFieldName + ") IS NULL; ");
			insertSB.append(" END ");
			print("创建插入触发器 ：" + insertSB.toString());
			trigeerArr.add(insertSB.toString());

			info.addTiggerName(triggerTableName);
		}

		if (isRefresh) {
			// 创建更新触发器
			String triggerTableName = middleFieldName + "_Update";
			
			StringBuffer updateSB = new StringBuffer("CREATE TRIGGER "
					+ triggerTableName);
			updateSB.append(" BEFORE Update ON " + middleTableName);
			updateSB.append(" FOR EACH ROW BEGIN ");
			updateSB.append(" SELECT RAISE(ROLLBACK,'没有这个字段名 " + objFieldName
					+ " in " + objTableName + "')  ");
			updateSB.append(" WHERE (SELECT " + objFieldName + " FROM "
					+ objTableName + " WHERE " + objFieldName + " = NEW."
					+ middleFieldName + ") IS NULL; ");
			updateSB.append(" END ");
			print("创建更新触发器 ：" + updateSB.toString());
			trigeerArr.add(updateSB.toString());
			
			info.addTiggerName(triggerTableName);
		}

		if (isRemove) {
			// 创建Delete触发器
			String triggerTableName = middleFieldName + "_Delete";
			
			StringBuffer deleteSB = new StringBuffer("CREATE TRIGGER "
					+ triggerTableName);
			deleteSB.append(" BEFORE DELETE ON " + objTableName);
			deleteSB.append(" FOR EACH ROW BEGIN ");
			deleteSB.append(" DELETE FROM " + middleTableName + " WHERE "
					+ middleFieldName + " = OLD." + objFieldName + ";");
			deleteSB.append(" END ");
			print("创建Delete触发器 ：" + deleteSB.toString());
			trigeerArr.add(deleteSB.toString());
			
			info.addTiggerName(triggerTableName);
		}

		if (isMerge) {
			// 创建级联操作
			String triggerTableName = middleFieldName + "_Caceade_Update";
			StringBuffer caceadeUpdateSB = new StringBuffer("CREATE TRIGGER "
					+ middleFieldName + "_Caceade_Update ");
			caceadeUpdateSB.append(" AFTER Update ON " + objTableName);
			caceadeUpdateSB.append(" FOR EACH ROW BEGIN ");
			caceadeUpdateSB.append(" update " + middleTableName + " set "
					+ middleFieldName + " = new." + objFieldName + " where "
					+ middleFieldName + " = old." + objFieldName + ";");
			caceadeUpdateSB.append(" END ");
			print("创建级联操作 更新触发器 ：" + caceadeUpdateSB.toString());
			trigeerArr.add(caceadeUpdateSB.toString());
			
			info.addTiggerName(triggerTableName);
		}
		return trigeerArr;
	}

	/**
	 * 得到属性 在数据库中的字段名
	 * @param field
	 * @return
	 */
	public static final String getColumnName(Field field) {
		String fieldName = field.getAnnotation(DatabaseField.class)
				.columnName();
		if (fieldName == null || "".equals(fieldName)) {
			fieldName = field.getName();
		}
		return fieldName;
	}

	/**
	 * 返回外键名称 FK_TEACHER_ID
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
		} else if (fieldClazz.isAssignableFrom(Collection.class)) {
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
		ZWLogger.i(TAG, "属性" + field.getName() + "判定为无效的属性!");
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

	/**
	 * 判断表 是否存在
	 * 
	 * @param tableName
	 * @return
	 */
	private static boolean tabbleIsExist(SQLiteDatabase mDatabase,
			String tableName) {
		boolean result = false;
		if (tableName == null) {
			return false;
		}
		Cursor cursor = null;
		try {
			String sql = "select count(*) as c from "
					+ ZWConstants.DATABASE_NAME
					+ " where type ='table' and name ='" + tableName.trim()
					+ "' ";
			cursor = mDatabase.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					ZWLogger.i(TAG, "表名叫:"+tableName+"已经存在!!!!!");
					result = true;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return result;
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
