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
	 * �õ� ��ѯ�� ������
	 * @return
	 */
	public QueryBuilder queryBuilder();
	
	/**
	 * �������
	 * @param t
	 * @return
	 */
	public long insertObj(T t);
	
	public long insertObjs(List<T> t);
	
	public long insertObjs(T... t);
	
	/**
	 * ������� �Ƿ��� �����������
	 * @param isAddFKObject
	 * @param t
	 * @return
	 */
	public long insertObjs(boolean isAddFKObject,T... t);
	
	public long insertObjs(boolean isAddFKObject,List<T> t);
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
	public long updateAllObjs(T t);
	
	public long updateObjs(UpdateBuider<T> mUpdateBuider);
	
	public long updateObjs(Map<String,Object> updateMap);
	
	
	/**
	 * ��ѯ����
	 */
	public List<T> queryAllObjs();
	
	public List<T> queryObjs(QueryBuilder mQueryBuilder);
	
	public List<T> queryObjs(String sql);
	
	public T queryFirstObj(QueryBuilder mQueryBuilder);
	
	public int queryCount(QueryBuilder mQueryBuilder);
	
	/**
	 * ���Ӳ�ѯ ��ѯWorkerʱ�� �� CompanyҲ�����
	 * @return
	 */
	public List<T> queryJoinObjs(QueryBuilder mQueryBuilder);
}
