package cn.shrek.base.ormlite.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.database.Cursor;
import cn.shrek.base.ZWBo;
import cn.shrek.base.ZWDatabaseBo;
import cn.shrek.base.ormlite.DBUtil;
import cn.shrek.base.ormlite.TableInfo;
import cn.shrek.base.ormlite.ZWDBHelper;
import cn.shrek.base.ormlite.stmt.DeleteBuider;
import cn.shrek.base.ormlite.stmt.InsertBuider;
import cn.shrek.base.ormlite.stmt.QueryBuilder;
import cn.shrek.base.ormlite.stmt.UpdateBuider;
import cn.shrek.base.util.AndroidVersionCheckUtils;
import cn.shrek.base.util.ZWLogger;

public class DBDaoImpl<T extends ZWDatabaseBo> implements DBDao<T> {
	private Class<T> clazz;
	private ZWDBHelper helper;

	public DBDaoImpl(Class<T> clazz1, ZWDBHelper helper) {
		this.clazz = clazz1;
		this.helper = helper;
	}

	@Override
	public InsertBuider<T> insertBuider() {
		// TODO Auto-generated method stub
		InsertBuider<T> builder = new InsertBuider<T>(clazz);
		return builder;
	}

	@Override
	public long insertObj(T t) {
		// TODO Auto-generated method stub
		return insertObjs(t);
	}

	@Override
	public long insertObjs(Collection<T> t) {
		// TODO Auto-generated method stub
		// T[] ts = (T[]) t.toArray();
		return insertObjs(false, false, t);
	}

	public long insertObjs(boolean isAddFKObject, boolean isUpdateWhenExist,
			T... t) {
		List<T> list = new ArrayList<T>();
		for (T obj : t) {
			list.add(obj);
		}
		return insertObjs(isAddFKObject, isUpdateWhenExist, list);
	}

	@Override
	public long insertOrUpdateObjs(Collection<T> t) {
		return insertObjs(false, true, t);
	}

	@Override
	public long insertOrUpdateObjs(T... t) {
		return insertObjs(false, true, t);
	}

	@Override
	public long insertObjs(boolean isAddFKObject, boolean isUpdateWhenExist,
			Collection<T> t) {
		Set<Object> allFKs = new HashSet<Object>();
		InsertBuider<T> buider = insertBuider();
		for (T obj : t) {
			// 先插入外键值
			Set<Object> objs = buider.getForeignKeyObjs(obj);
			allFKs.addAll(objs);
		}

		int optNum = 0;

		if (isAddFKObject) {
			for (Object fkObject : allFKs) {
				if (fkObject instanceof ZWDatabaseBo) {
					ZWDatabaseBo bo = (ZWDatabaseBo) fkObject;
					Class<? extends ZWDatabaseBo> fkClazz = (Class<? extends ZWDatabaseBo>) fkObject
							.getClass();
					DBDao dao = helper.getDao(fkClazz);
					try {
						optNum += dao.insertObj(bo);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		for (T obj : t) {
			buider.addValue(obj);
			try {
				helper.getDatabase(false).insert(
						buider.tableInfo.getTableName(), null, buider.cvs);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				if (isUpdateWhenExist) {
					updateObj(obj);
				}
			}
			optNum++;
		}
		return optNum;
	}

	@Override
	public long insertObjs(T... t) {
		// TODO Auto-generated method stub
		List<T> list = new ArrayList<T>();
		for (T obj : t) {
			list.add(obj);
		}
		return insertObjs(list);
		// int optNum = 0;
		// for(T obj : t){
		// InsertBuider<T> buider = insertBuider();
		// buider.addValue(obj);
		// helper.getDatabase(false).insert(buider.tableInfo.tableName, null,
		// buider.cvs);
		// optNum++;
		// }
		// return optNum;
	}

	/** -------------------删除-------------------------- */
	@Override
	public DeleteBuider deleteBuider() {
		// TODO Auto-generated method stub
		return new DeleteBuider(clazz);
	}

	@Override
	public long deleteObjs(DeleteBuider builder) {
		// TODO Auto-generated method stub
		long optNum = helper.getDatabase(false).delete(
				builder.getTableNameWithAliases(), builder.getWhereSql(), null);
		return optNum;
	}

	@Override
	public long deleteAll() {
		// TODO Auto-generated method stub
		DeleteBuider builder = deleteBuider();
		return deleteObjs(builder);
	}

	/**
	 * 不推荐试用
	 * 
	 * @param whereSql
	 *            条件的sql的语句
	 */
	@Override
	public long deleteObj(String whereSql) {
		// TODO Auto-generated method stub
		TableInfo info = TableInfo.newInstance(clazz);
		long optNum = helper.getDatabase(false).delete(info.getTableName(),
				whereSql, null);
		return optNum;
	}

	@Override
	public void clearObj(T t) {
		// TODO Auto-generated method stub
		TableInfo info = TableInfo.newInstance(clazz);
		for (int j = 0; j < info.allField.size(); j++) {
			Field field = info.allField.get(j);
			Class<?> typeClazz = info.getFieldType(j);
			field.setAccessible(true);
			try {
				field.set(t, DBTransforFactory.getFieldNullValue(typeClazz));
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/** ------------------- 更新 ----------------------- */
	@Override
	public UpdateBuider<T> updateBuider() {
		// TODO Auto-generated method stub
		return new UpdateBuider<T>(clazz);
	}

	@Override
	public long updateObj(T t) {
		// TODO Auto-generated method stub
		UpdateBuider<T> updateBuider = updateBuider();
		updateBuider.addValue(t);
		return updateObj(updateBuider);
	}

	@Override
	public long updateObj(UpdateBuider<T> mUpdateBuider) {
		// TODO Auto-generated method stub
		return helper.getDatabase(false).update(
				mUpdateBuider.tableInfo.getTableName(), mUpdateBuider.cvs,
				mUpdateBuider.getWhereSql(), null);
	}

	@Override
	public long updateObj(Map<String, Object> updateMap) {
		// TODO Auto-generated method stub
		UpdateBuider<T> updateBuider = updateBuider();
		updateBuider.addValue(updateMap);
		return updateObj(updateBuider);
	}

	/**
	 * 替换 对象
	 */
	@Override
	public long replaceObj(T t) {
		UpdateBuider<T> updateBuider = updateBuider();
		updateBuider.addValue(t);
		return replaceObj(updateBuider);
	}

	@Override
	public long replaceObj(UpdateBuider<T> mUpdateBuider) {
		return helper.getDatabase(false)
				.replace(mUpdateBuider.tableInfo.getTableName(), null,
						mUpdateBuider.cvs);
	}

	@Override
	public long replaceObj(Map<String, Object> updateMap) {
		UpdateBuider<T> updateBuider = updateBuider();
		updateBuider.addValue(updateMap);
		return replaceObj(updateBuider);
	}

	@Override
	public long replaceObjs(Collection<T> ts) {
		long sum = 0;
		for (T obj : ts) {
			sum += replaceObj(obj);
		}
		return sum;
	}

	@Override
	public long replaceObjs(T... ts) {
		List<T> list = new ArrayList<T>();
		for (T obj : ts) {
			list.add(obj);
		}
		return replaceObjs(list);
	}

	@Override
	public QueryBuilder queryBuilder() {
		// TODO Auto-generated method stub
		return new QueryBuilder(clazz);
	}

	@Override
	public List<T> queryAllObjs() {
		// TODO Auto-generated method stub
		QueryBuilder mQueryBuilder = queryBuilder();
		mQueryBuilder.addSelectColumn("*");
		return queryObjs(mQueryBuilder);
	}

	@Override
	public List<T> queryObjs(QueryBuilder mQueryBuilder) {
		// TODO Auto-generated method stub
		String sql = mQueryBuilder.getSql();
		mQueryBuilder.cycle();
		return queryObjs(sql);
	}

	@Override
	public List<T> queryObjs(String sql) {
		// TODO Auto-generated method stub
		List<T> list = new ArrayList<T>();
		Cursor cursor = helper.getDatabase(true).rawQuery(sql, null);
		while (cursor.moveToNext()) {
			T t = parseCurser(cursor);
			if (t != null) {
				list.add(t);
			}
		}
		cursor.close();
		return list;
	}

	@Override
	public T queryFirstObj(QueryBuilder mQueryBuilder) {
		// TODO Auto-generated method stub
		mQueryBuilder.limitIndex = 1;
		mQueryBuilder.offsetIndex = 0;
		List<T> list = queryObjs(mQueryBuilder);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public int queryCount(QueryBuilder mQueryBuilder) {
		// TODO Auto-generated method stub
		mQueryBuilder.clearSelectSection();
		mQueryBuilder.limitIndex = -1;
		mQueryBuilder.offsetIndex = -1;
		mQueryBuilder.addSelectColumn("COUNT(id)");
		Cursor cursor = helper.getDatabase(true).rawQuery(
				mQueryBuilder.getSql(), null);
		mQueryBuilder.cycle();
		int countNum = 0;
		if (cursor.moveToNext()) {
			countNum = cursor.getInt(0);
		}
		cursor.close();
		return countNum;
	}

	public <F extends ZWDatabaseBo> F parseCurser(Cursor cursor,
			Class<F> giveClazz) {
		TableInfo info = TableInfo.newInstance(giveClazz);

		String logColumnName = null;
		Object logObj = null;
		try {
			F obj = giveClazz.getConstructor().newInstance();
			for (int i = 0; i < info.allColumnNames.size(); i++) {
				String columnName = info.allColumnNames.get(i);
				logColumnName = columnName;
				Field field = info.allField.get(i);

				// 属性类型
				Class<?> fieldType = info.getFieldType(i);

				int index = cursor.getColumnIndex(columnName);
				if (index == -1) {
					continue;
				}
				Object columnValue = new Object();
				// 兼容性 2.3
				if (AndroidVersionCheckUtils.hasHoneycomb()) {
					switch (cursor.getType(index)) {
					case Cursor.FIELD_TYPE_STRING:
						columnValue = cursor.getString(index);
						break;
					case Cursor.FIELD_TYPE_FLOAT:
						columnValue = cursor.getFloat(index);
						break;
					case Cursor.FIELD_TYPE_INTEGER:
						columnValue = cursor.getInt(index);
						break;
					case Cursor.FIELD_TYPE_NULL:
						columnValue = null;
						break;
					case Cursor.FIELD_TYPE_BLOB:
						columnValue = cursor.getBlob(index);
						break;
					}
				} else {

					if (Integer.class.isAssignableFrom(fieldType)
							|| int.class.isAssignableFrom(fieldType)
							|| Boolean.class.isAssignableFrom(fieldType)
							|| boolean.class.isAssignableFrom(fieldType)
							|| Date.class.isAssignableFrom(fieldType)) {
						columnValue = cursor.getInt(index);
					} else if (Long.class.isAssignableFrom(fieldType)
							|| long.class.isAssignableFrom(fieldType)
							|| Calendar.class.isAssignableFrom(fieldType)) {
						columnValue = cursor.getLong(index);
					} else if (Float.class.isAssignableFrom(fieldType)
							|| float.class.isAssignableFrom(fieldType)) {
						columnValue = cursor.getFloat(index);
					} else {
						columnValue = cursor.getString(index);
					}

				}

				// 从字段的值 转换为 Java里面的值
				Object fieldValues = DBTransforFactory.getFieldValue(
						columnValue, fieldType);
				// 方便log输出
				logObj = fieldValues;
				field.setAccessible(true);
				field.set(obj, fieldValues);

			}
			return obj;
		} catch (Exception e) {
			ZWLogger.printLog(DBDaoImpl.this, "给字段名:" + logColumnName + "赋值，值:"
					+ logObj + ",失败!");
			e.printStackTrace();
		}
		// catch (IllegalAccessException e) {
		// e.printStackTrace();
		// } catch (IllegalArgumentException e) {
		// e.printStackTrace();
		// } catch (InvocationTargetException e) {
		// e.printStackTrace();
		// } catch (NoSuchMethodException e) {
		// e.printStackTrace();
		// }
		return null;
	}

	public T parseCurser(Cursor cursor) {
		return parseCurser(cursor, clazz);
	}

	/**
	 * 连接主要是左连接查询 例如Employee查询的时候 顺便把所属公司Company 查出来 1.得到所有的外键
	 */
	@Override
	public List<T> queryJoinObjs(QueryBuilder mQueryBuilder) {
//		char aliases = 'A';
//		mQueryBuilder.setTableAliases(aliases + "");
//		// TODO Auto-generated method stub
//		for (Map.Entry<String, Field> entry : mQueryBuilder.tableInfo.allforeignMaps
//				.entrySet()) {
//			aliases++;
//
//			String fkName = entry.getKey();
//			Field fkField = entry.getValue();
//			Class<?> fkClazz = mQueryBuilder.tableInfo.allforeignClassMaps
//					.get(fkName);
//
//			// TableInfo fkInfo = TableInfo.newInstance((Class<ZWBo>)fkClazz);
//			mQueryBuilder.joinSB.append("LEFT JOIN "
//					+ DBUtil.getTableName(fkClazz) + " " + aliases + " ON ");
//			mQueryBuilder.joinSB.append(mQueryBuilder
//					.getColumnNameWithAliases(fkName)
//					+ " = "
//					+ aliases
//					+ "."
//					+ DBUtil.getColumnName(fkField));
//			mQueryBuilder.joinSelect.append("," + aliases + ".*");
//		}
		return queryObjs(mQueryBuilder);
	}
}
