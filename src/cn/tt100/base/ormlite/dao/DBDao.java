package cn.tt100.base.ormlite.dao;

import java.util.List;

import cn.tt100.base.ZWBo;
import cn.tt100.base.ormlite.stmt.DeleteBuider;
import cn.tt100.base.ormlite.stmt.InsertBuider;
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
	 * 插入对象
	 * @param t
	 * @return
	 */
	public long insertObj(T t);
	
	public long insertObjs(List<T> t);
	
	public long insertObjs(T... t);
	
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
//	public long updateObjs();
	
}
