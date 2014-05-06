package cn.tt100.base.ormlite.dao;

import java.util.List;

import cn.tt100.base.ZWBo;
import cn.tt100.base.ormlite.stmt.DeleteBuider;
import cn.tt100.base.ormlite.stmt.InsertBuider;

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
	
}
