package cn.shrek.base.ormlite.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.shrek.base.ZWDatabaseBo;
import cn.shrek.base.ormlite.DBUtil;
import cn.shrek.base.ormlite.ForeignInfo;
import cn.shrek.base.ormlite.TableInfo;
import cn.shrek.base.ormlite.ZWDBHelper;
import cn.shrek.base.ormlite.stmt.DeleteBuider;
import cn.shrek.base.ormlite.stmt.InsertBuider;
import cn.shrek.base.ormlite.stmt.QueryBuilder;
import cn.shrek.base.ormlite.stmt.UpdateBuider;
import cn.shrek.base.util.ReflectUtil;

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
		return insertObjs(false, t);
	}

	public long insertObjs(boolean isAddFKObject, T... t) {
		List<T> list = new ArrayList<T>();
		for (T obj : t) {
			list.add(obj);
		}
		return insertObjs(isAddFKObject, list);
	}

	@Override
	public long insertOrUpdateObjs(Collection<T> t) {
		return insertObjs(false, t);
	}

	@Override
	public long insertOrUpdateObjs(T... t) {
		return insertObjs(false, t);
	}

	@Override
	public long insertObjs(boolean isAddFKObject, Collection<T> collectT) {
		Set<Object> allFKs = new HashSet<Object>();
		InsertBuider<T> buider = insertBuider();
		for (T obj : collectT) {
			// 先插入外键值
			Set<Object> objs = buider.getForeignKeyObjs(obj);
			allFKs.addAll(objs);
		}

		int optNum = 0;

		if (isAddFKObject) {
			// 插入 对应外表值
			for (Object fkObject : allFKs) {
				if (fkObject instanceof ZWDatabaseBo) {
					ZWDatabaseBo bo = (ZWDatabaseBo) fkObject;
					Class<? extends ZWDatabaseBo> fkClazz = bo.getClass();
					DBDao dao = helper.getDao(fkClazz);
					try {
						optNum += dao.replaceObj(bo);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		SQLiteDatabase sd = helper.getDatabase(false);

		for (T obj : collectT) {
			try {
				sd.replace(buider.tableInfo.getTableName(), null,
						buider.getContentValue(obj));
				optNum++;

				if (isAddFKObject) {
					// 插入 中建表
					Map<ForeignInfo, List<ContentValues>> map = buider
							.getForgienValue(obj);
					for (Map.Entry<ForeignInfo, List<ContentValues>> entry : map
							.entrySet()) {
						ForeignInfo info = entry.getKey();
						List<ContentValues> cvses = entry.getValue();
						for (ContentValues cvs : cvses) {
							optNum += info.getmMiddleOperator()
									.replace(sd, cvs);
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
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
			Object objValue = DBTransforFactory.getFieldNullValue(typeClazz);
			ReflectUtil.setFieldValue(t, field, objValue);
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
		helper.getDatabase(false)
				.replace(mUpdateBuider.tableInfo.getTableName(), null,
						mUpdateBuider.cvs);
		return 1;
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
	public List<T> queryJoinAllObjs() {
		QueryBuilder mQueryBuilder = queryBuilder();
		mQueryBuilder.addSelectColumn("*");
		return queryJoinObjs(mQueryBuilder);
	}

	@Override
	public List<T> queryObjs(QueryBuilder mQueryBuilder) {
		// TODO Auto-generated method stub
		String sql = mQueryBuilder.getSql();
		mQueryBuilder.cycle();
		return queryObjs(false, sql);
	}

	/**
	 * 连接主要是左连接查询 例如Employee查询的时候 顺便把所属公司Company 查出来 1.得到所有的外键
	 */
	@Override
	public List<T> queryJoinObjs(QueryBuilder mQueryBuilder) {
		String sql = mQueryBuilder.getSql();
		mQueryBuilder.cycle();
		return queryObjs(true, sql);
	}

	@Override
	public List<T> queryObjs(String sql) {
		// TODO Auto-generated method stub
		return queryObjs(false, sql);
	}

	@Override
	public List<T> queryJoinObjs(String sql){
		return queryObjs(true, sql);
	}
	
	/**
	 * 查询
	 * 
	 * @param isJoin
	 *            是否连接查询
	 * @param sql
	 * @return
	 */
	private List<T> queryObjs(boolean isJoin, String sql) {
		List<T> list = new ArrayList<T>();
		Cursor cursor = helper.getDatabase(true).rawQuery(sql, null);

		TableInfo info = TableInfo.newInstance(clazz);
		List<ForeignInfo> fInfos = info.allforeignInfos;
		while (cursor.moveToNext()) {
			T t = parseCurser(cursor);
			if (t == null) {
				continue;
			}
			list.add(t);
		}

		// 连接查询
		if(isJoin){
			for (ForeignInfo fInfo : fInfos) {
				fInfo.getmMiddleOperator().joinSelect(helper, list);
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

	public T parseCurser(Cursor cursor) {
		return DBUtil.parseCurser(cursor, clazz);
	}

}
