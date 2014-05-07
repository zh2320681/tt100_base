package cn.tt100.base.ormlite.stmt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tt100.base.ZWBo;
import cn.tt100.base.util.ZWLogger;

/**
 * ��ѯ�Ĺ�����
 * order by  group by having Limit 9 Offset 10
 * @author shrek
 *
 */
public class QueryBuilder extends StmtBuilder {
	private static final String SELECT_KEYWORD = "SELECT ";
	private static final String ORDER_KEYWORD = " ORDER BY ";
	private static final String GROUP_KEYWORD = " GROUP BY ";
	private static final String HAVING_KEYWORD = " HAVING ";
	//��ѯ����
	private List<String> queryColumns,groupColumns,orderColumns;
	private Map<String,String> orderColumnsMap;
	public String havingStr;
	//limitIndex Ϊ��ѯ������  offsetIndex ����������
	public int limitIndex,offsetIndex;
	
	public QueryBuilder(Class<? extends ZWBo> clazz) {
		super(clazz);
		// TODO Auto-generated constructor stub
		sqlBuffer = new StringBuffer(SELECT_KEYWORD+" ");
		//order by ���� key:������ value��asc or desc
		orderColumnsMap = new HashMap<String, String>();
		orderColumns = new ArrayList<String>();
		queryColumns = new ArrayList<String>();
		groupColumns = new ArrayList<String>();
		
//		havingStr = new StringBuffer();
		limitIndex = offsetIndex = -1;
	}

	/**
	 * ���order ����  ȥ�ظ�
	 * @param fieldName
	 * @param isAsc �Ƿ�����
	 */
	public void addOrderByCon(String fieldName,boolean isAsc){
		if(orderColumnsMap.containsKey(fieldName)){
			orderColumnsMap.put(fieldName, isAsc?"ASC":"DESC");
			orderColumns.add(fieldName);
		}
	}
	
	/**
	 * ��� group ������
	 * @param fieldName
	 */
	public void addGroupByCon(String... fieldNames){
		for(String fieldName : fieldNames){
			if(fieldName==null || fieldName.equals("")){
				continue;
			}
			for(String groupStr : groupColumns){
				if(groupStr.equals(fieldName)){
					return;
				}
			}
			groupColumns.add(fieldName);
		}
	}
	
	/**
	 * ������  �������� count(id) ֧�ֱ��� name as name1
	 * @param fieldName
	 */
	public void addSelectColumn(String... fieldNames){
		for(String addStr : fieldNames){
			if(addStr==null || addStr.equals("")){
				continue;
			}
			for(String hasAddStr : queryColumns){
				if(addStr == null || addStr.equals(hasAddStr)){
					break;
				}
			}
			queryColumns.add(addStr);
		}
	}
	
	
	@Override
	public String getSql() {
		// TODO Auto-generated method stu
		/** ------------------- ��Ӳ�ѯ���ֶ� ------------------------- */
		if(queryColumns.size() == 0){
			queryColumns.add("*");
		}
		
		for (int j = 0; j < queryColumns.size(); j++) {
			String hasAddStr = queryColumns.get(j);
			int leftKH = hasAddStr.indexOf("(");
			int rightKH = hasAddStr.indexOf(")");
			int xIndex = hasAddStr.indexOf("*");
			int asIndex = hasAddStr.indexOf(" as ");
			
			String columnName = null;
			if(leftKH != -1 && rightKH != -1){
				//����
				String fieldName = hasAddStr.substring(leftKH+1, rightKH);
				columnName = tableInfo.getColumnByFieldStr(fieldName);
				if(columnName == null){
					continue;
				}
				columnName = hasAddStr.replace(fieldName, columnName);
			}else if(asIndex != -1){
				//�б���
				String fieldName = hasAddStr.substring(0, asIndex);
				columnName = tableInfo.getColumnByFieldStr(fieldName);
				if(columnName == null){
					continue;
				}
				columnName = hasAddStr.replace(fieldName, columnName);
			}else if(xIndex != -1){
				//* ��ѯ����
				columnName = hasAddStr;
			}else{
				columnName = tableInfo.getColumnByFieldStr(hasAddStr);
				if(columnName == null){
					continue;
				}
			}
			
			if(j != 0){
				sqlBuffer.append(",");
			}
			sqlBuffer.append(columnName);
		}
		
		sqlBuffer.append(" FROM "+tableInfo.tableName+" ");
		/** ------------------- ���where���ֶ� ------------------------- */
		sqlBuffer.append(getWhereSql());
		/** ------------------- ���group by���ֶ� ------------------------- */
		for (int i = 0; i < groupColumns.size(); i++) {
			String groupStr = groupColumns.get(i);
			String columnName = tableInfo.getColumnByFieldStr(groupStr);
			if(columnName == null){
				continue;
			}
			if(i == 0){
				sqlBuffer.append(GROUP_KEYWORD);
			}else{
				sqlBuffer.append(",");
			}
			sqlBuffer.append(columnName);
		}
		
		/** ------------------- ���Having���ֶ� ------------------------- */
		if(havingStr != null && "".equals(havingStr)){
			sqlBuffer.append(HAVING_KEYWORD+havingStr);
		}
		/** ------------------- ���order by���ֶ� ------------------------- */
		for (int i = 0; i < orderColumns.size(); i++) {
			String orderStr = orderColumns.get(i);
			String columnName = tableInfo.getColumnByFieldStr(orderStr);
			if(columnName == null){
				continue;
			}
			if(i == 0){
				sqlBuffer.append(ORDER_KEYWORD);
			}else{
				sqlBuffer.append(",");
			}
			sqlBuffer.append(columnName+" "+ orderColumnsMap.get(orderStr));
		}
		/** ------------------- ���limit���ֶ� ------------------------- */
		if(limitIndex != -1){
			sqlBuffer.append(" LIMIT "+limitIndex);
		}
		
		if(offsetIndex != -1){
			sqlBuffer.append(" OFFSET  "+offsetIndex);
		}
		
		ZWLogger.printLog(this, "��ѯ��SQLΪ:"+sqlBuffer.toString());
		return sqlBuffer.toString();
	}

	/**
	 * ��ղ�ѯ��section
	 */
	public void clearSelectSection(){
		queryColumns.clear();
	}
	
	@Override
	public void cycle() {
		// TODO Auto-generated method stub
		super.cycle();
		queryColumns.clear();
		groupColumns.clear();
		orderColumns.clear();
		
		queryColumns = null;
		groupColumns = null;
		orderColumns = null;
		
		orderColumnsMap.clear();
		orderColumnsMap = null;
		
		havingStr = null;
	}
}
