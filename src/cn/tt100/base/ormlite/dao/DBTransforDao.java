package cn.tt100.base.ormlite.dao;
/**
 * ������ת Field <----> Column
 * 1.��������������ת
 * 2.boolean ----> int
 * 3.Date Calendar ----> int
 * 4.String ----> Text
 * @author shrek
 *
 */
public interface DBTransforDao<F,C> {
	
	/**
	 * ��Java�� ת��Ϊ ���ݿ�洢����
	 * @param fieldObj
	 * @return
	 */
	public C parseFieldToColumn(F fieldObj);
	
	/**
	 * ���ݿ�洢���� ת�� Java��
	 * @param columnObj
	 * @return
	 */
	public F parseColumnToField(C columnObj);
	
	/**
	 * �������
	 */
	public void specialDoing();
}
