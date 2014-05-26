package cn.tt100.base.ormlite.dao;

import java.util.List;
import java.util.Map;

import cn.tt100.base.ZWBo;
import cn.tt100.base.ormlite.stmt.DeleteBuider;
import cn.tt100.base.ormlite.stmt.InsertBuider;
import cn.tt100.base.ormlite.stmt.QueryBuilder;
import cn.tt100.base.ormlite.stmt.UpdateBuider;

public interface DBDao<T extends ZWBo> {
	
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
	
	public long insertObjs(List<T> t);
	
	public long insertObjs(T... t);
	
	/**
	 * 插入对象 是否做 外键关联插入
	 * @param isAddFKObject
	 * @param t
	 * @return
	 */
	public long insertObjs(boolean isAddFKObject,T... t);
	
	public long insertObjs(boolean isAddFKObject,List<T> t);
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
	public long updateAllObjs(T t);
	
	public long updateObjs(UpdateBuider<T> mUpdateBuider);
	
	public long updateObjs(Map<String,Object> updateMap);
	
	
	/**
	 * 查询对象
	 */
	public List<T> queryAllObjs();
	
	public List<T> queryObjs(QueryBuilder mQueryBuilder);
	
	public List<T> queryObjs(String sql);
	
	public T queryFirstObj(QueryBuilder mQueryBuilder);
	
	public int queryCount(QueryBuilder mQueryBuilder);
	
	/**
	 * 连接查询 查询Worker时候 把 Company也查出来
	 * @return
	 */
	public List<T> queryJoinObjs(QueryBuilder mQueryBuilder);
}
