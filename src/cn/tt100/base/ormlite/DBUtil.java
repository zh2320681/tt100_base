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
    StringBuffer localStringBuffer1 = new StringBuffer();
    Object localObject = new StringBuilder("create table ");
    if (paramBoolean)
      str2 = " IF NOT EXISTS ";
    int m;
    while (true)
    {
      String str3 = str2 + str1 + " (";
      localStringBuffer1.<init>(str3);
      ArrayList localArrayList = new ArrayList();
      TableInfo localTableInfo = TableInfo.newInstance(paramClass);
      int i = 1;
      int j = 0;
      int k = localTableInfo.allField.size();
      if (j >= k)
      {
        if (localArrayList.size() > 0)
        {
          m = 1;
          StringBuffer localStringBuffer2 = localStringBuffer1.append(",primary key(");
          localObject = localArrayList.iterator();
          if (((Iterator)localObject).hasNext())
            break;
          StringBuffer localStringBuffer3 = localStringBuffer1.append(")");
        }
        else
        {
          StringBuffer localStringBuffer4 = localStringBuffer1.append(");");
          StringBuilder localStringBuilder1 = new StringBuilder("create SQL: ");
          String str4 = localStringBuffer1.toString();
          print(str4);
          return;
          str2 = "";
          continue;
        }
      }
      else
      {
        Field localField1 = (Field)localTableInfo.allField.get(j);
        Class localClass = localField1.getType();
        String str5 = (String)localTableInfo.allColumnNames.get(j);
        DatabaseField localDatabaseField = (DatabaseField)localField1.getAnnotation(DatabaseField.class);
        Field localField2 = (Field)localTableInfo.allforeignMaps.get(str5);
        if (localField2 != null)
        {
          str6 = "";
          if (i != 0)
            i = 0;
          while (true)
          {
            String str7 = String.valueOf(str6);
            StringBuilder localStringBuilder2 = new StringBuilder(str7).append(str5).append(" ");
            String str8 = getObjMapping(localField2);
            String str9 = str8 + " ";
            String str10 = str9;
            StringBuffer localStringBuffer5 = localStringBuffer1.append(str10);
            if (List.class.isAssignableFrom(localClass))
            {
              Type localType = localField1.getGenericType();
              if ((localType instanceof ParameterizedType))
                localClass = (Class)((ParameterizedType)localType).getActualTypeArguments()[0];
            }
            PrintStream localPrintStream = System.out;
            StringBuilder localStringBuilder3 = new StringBuilder("=======>");
            String str11 = localClass.toString();
            String str12 = str11;
            localPrintStream.println(str12);
            String str13 = getTableName(localClass);
            String str14 = getFeildName(localField2);
            createTrigeerFKCaceade(paramSQLiteDatabase, localDatabaseField, str13, str14, str1, str5);
            j += 1;
            break;
            str6 = ", ";
          }
        }
        String str15 = getObjMapping(localField1);
        String str6 = "";
        if (i != 0)
          i = 0;
        while (true)
        {
          String str16 = String.valueOf(str6);
          String str17 = str16 + str5 + " " + str15 + " ";
          StringBuffer localStringBuffer6 = new StringBuffer(str17);
          if (!localDatabaseField.canBeNull())
            StringBuffer localStringBuffer7 = localStringBuffer6.append(" NOT NULL  ");
          String str18 = localDatabaseField.defaultValue();
          if (!str15.equals("TEXT"))
          {
            String str19 = String.valueOf(str18);
            String str20 = str19 + " ";
            StringBuffer localStringBuffer8 = localStringBuffer6.append(str20);
          }
          if (localDatabaseField.id())
            boolean bool = localArrayList.add(str5);
          if (localDatabaseField.generatedId())
            StringBuffer localStringBuffer9 = localStringBuffer6.append("Autoincrement ");
          if (localDatabaseField.unique())
            StringBuffer localStringBuffer10 = localStringBuffer6.append("UNIQUE ");
          String str21 = localStringBuffer6.toString();
          StringBuffer localStringBuffer11 = localStringBuffer1.append(str21);
          break;
          str6 = ", ";
        }
      }
    }
    String str22 = (String)((Iterator)localObject).next();
    StringBuilder localStringBuilder4 = new java/lang/StringBuilder;
    if (m != 0);
    for (String str2 = ""; ; str2 = ",")
    {
      String str23 = String.valueOf(str2);
      localStringBuilder4.<init>(str23);
      String str24 = str22;
      String str25 = str24;
      StringBuffer localStringBuffer12 = localStringBuffer1.append(str25);
      m = 0;
      break;
    }
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
	 * 
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

	private static void print(String paramString) {
		BaseLog.printLog(LogLevel.INFO, "DBUtil", paramString);
	}
}
