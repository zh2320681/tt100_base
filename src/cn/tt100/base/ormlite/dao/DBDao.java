package cn.tt100.base.ormlite.dao;

import java.util.List;

import cn.tt100.base.ZWBo;
import cn.tt100.base.ormlite.stmt.DeleteBuider;
import cn.tt100.base.ormlite.stmt.InsertBuider;
import cn.tt100.base.ormlite.stmt.UpdateBuider;

public interface DBDao<T extends ZWBo> {
	
	/**
	 * �õ� ����� ������
	 * @return
	 */
	public InsertBuider<T> insertBuider();
	
	/**
	 * �õ� ɾ���� ������
	 * @return
	 */
	public DeleteBuider deleteBuider();
	
	/**
	 * �õ� ���µ� ������
	 * @return
	 */
	public UpdateBuider<T> updateBuider();
	
	/**
	 * �������
	 * @param t
	 * @return
	 */
	public long insertObj(T t);
	
	public long insertObjs(List<T> t);
	
	public long insertObjs(T... t);
	
	/**
	 * ɾ������
	 */
	public long deleteObjs(DeleteBuider builder);
	
	public long deleteAll();
	
	public long deleteObj(String whereSql);
	
	/**
	 * ��ն���
	 */
	public void clearObj(T t);
	
	
	/**
	 * ���� ����
	 */
//	public long updateObjs();
	
}
