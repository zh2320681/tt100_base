package cn.tt100.base.ormlite.dao;

import java.util.List;

import cn.tt100.base.ZWBo;
import cn.tt100.base.ormlite.stmt.InsertBuider;

public interface DBDao<T extends ZWBo> {
	
	/**
	 * �õ� ����� ������
	 * @return
	 */
	public InsertBuider<T> insertBuider();
	
	/**
	 * �������
	 * @param t
	 * @return
	 */
	public long insertObj(T t);
	
	public long insertObjs(List<T> t);
	
	public long insertObjs(T... t);
}
