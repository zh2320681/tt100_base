package cn.shrek.base.ormlite.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import cn.shrek.base.ZWDatabaseBo;
import cn.shrek.base.ormlite.stmt.DeleteBuider;
import cn.shrek.base.ormlite.stmt.InsertBuider;
import cn.shrek.base.ormlite.stmt.QueryBuilder;
import cn.shrek.base.ormlite.stmt.UpdateBuider;

public interface DBDao<T extends ZWDatabaseBo> {
	
	/**
	 * 得到 插入的 构造器
	 * @return
	 */
	public InsertBuider<T> insertBuider();
	
	/**
	 * 得到 删除的 构造器
	 * @return
	 */
	public DeleteBuider deleteBuider();
	
	/**
	 * 得到 更新的 构造器
	 * @return
	 */
	public UpdateBuider<T> updateBuider();
	
	/**
	 * 得到 查询的 构造器
	 * @return
	 */
	public QueryBuilder queryBuilder();
	
	/**
	 * 插入对象
	 * @param t
	 * @return
	 */
	public long insertObj(T t);
	
	public long insertObjs(Collection<T> t);
	
	public long insertObjs(T... t);
	
	/**
	 * 插入对象 是否做 外键关联插入
	 * @param isAddFKObject
	 * @param t
	 * @return
	 */
	public long insertObjs(boolean isAddFKObject,T... t);
	
	public long insertObjs(boolean isAddFKObject,Collection<T> t);
	
//	public long insertOrUpdateObjs(Collection<T> t);
//	
//	public long insertOrUpdateObjs(T... t);
	public long insertObj(boolean isAddFKObject , T t);
	
	/**
	 * 删除对象
	 */
	public long deleteObjs(DeleteBuider builder);
	
	public long deleteAll();
	
	public long deleteObj(String whereSql);
	
	/**
	 * 清空对象
	 */
	public void clearObj(T t);
	
	
	/**
	 * 更新 对象
	 */
	public long updateObj(T t);
	
	public long updateObj(UpdateBuider<T> mUpdateBuider);
	
	public long updateObj(Map<String,Object> updateMap);
	
	
	/**
	 * 替换 对象
	 */
	public long replaceObj(T t);
	
	public long replaceObj(UpdateBuider<T> mUpdateBuider);
	
	public long replaceObj(Map<String,Object> updateMap);
	
	public long replaceObjs(Collection<T> ts);
	
	public long replaceObjs(T... ts);
	
	/**
	 * 查询对象
	 */
	public List<T> queryAllObjs();
	
	public List<T> queryJoinAllObjs();
	
	public List<T> queryObjs(QueryBuilder mQueryBuilder);
	
	public List<T> queryJoinObjs(QueryBuilder mQueryBuilder);
	
	public List<T> queryObjs(String sql);
	
	public List<T> queryJoinObjs(String sql,String... fkParas);
	
	public T queryFirstObj(QueryBuilder mQueryBuilder);
	
	public T queryJoinFirstObj(QueryBuilder mQueryBuilder);
	
	public int queryCount(QueryBuilder mQueryBuilder);
	
	
}
