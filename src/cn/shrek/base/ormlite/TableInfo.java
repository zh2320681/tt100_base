package cn.shrek.base.ormlite;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import cn.shrek.base.ZWBo;
import cn.shrek.base.annotation.DatabaseField;
import cn.shrek.base.util.ZWLogger;

public class TableInfo {
	private static final Map<Class<? extends ZWBo>, TableInfo> tableInfoFactory = Collections
			.synchronizedMap(new WeakHashMap<Class<? extends ZWBo>, TableInfo>());
	// �����ֶ���
	public List<String> allColumnNames;
	// ��������
	public List<Field> allField;
	// ������� key:����ֶ��� value:����Teacher ��id����
	public Map<String, Field> allforeignMaps;
	// ������� key:����ֶ��� value:����Teacher.class
	public Map<String, Class<?>> allforeignClassMaps;
	public Class<? extends ZWBo> clazz;
	public String tableName;

	private TableInfo(Class<? extends ZWBo> clazz) {
		this.clazz = clazz;
		//�������޲����Ĺ��췽��
		try {
			clazz.getConstructor();
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			ZWLogger.printLog(TableInfo.this,"��"+clazz.getSimpleName()+"���ṩ�޲����Ĺ��췽����");
			e1.printStackTrace();
		}
		tableName = DBUtil.getTableName(clazz);
		allField = new ArrayList<Field>();
		allColumnNames = new ArrayList<String>();

		this.allforeignMaps = new HashMap<String, Field>();
		allforeignClassMaps = new HashMap<String, Class<?>>();
		//�õ�������������� ������ ����
		Field[] declaredFields = clazz.getDeclaredFields();
		//�õ����������public���� ���� ����
		Field[] publicFields = clazz.getFields();
		List<Field> fields = new ArrayList<Field>();
		for (Field field : declaredFields) {
			fields.add(field);
		}
		for (Field field : publicFields) {
			boolean isFind = false;
			for (int i = 0; i < declaredFields.length; i++) {
				Field oldField = declaredFields[i];
				if(oldField.getName().equals(field.getName())){
					isFind = true;
					break;
				}
			}
			if(!isFind){
				fields.add(field);
			}
		}
		
		for (Field field : fields) {
			DatabaseField mDatabaseField = field
					.getAnnotation(DatabaseField.class);
			if (DBUtil.judgeFieldAvaid(field)) {
				allField.add(field);

				// ���Ե�����
				Class<?> fieldType = field.getType();
				/**
				 * ########## ����ж�
				 */
				String foreignColumnName = mDatabaseField.foreignColumnName();
				if (foreignColumnName != null && !"".equals(foreignColumnName)) {

					if (ZWBo.class.isAssignableFrom(fieldType)) {
						// �����BO����
						Field objField;
						try {
							objField = fieldType.getField(foreignColumnName);
							if (DBUtil.judgeFieldAvaid(objField)) {
								String fkColumnName = DBUtil
										.getMapFKCulmonName(foreignColumnName,
												fieldType);
								allColumnNames.add(fkColumnName);
								allforeignMaps.put(fkColumnName, objField);
								allforeignClassMaps
										.put(fkColumnName, fieldType);
							} else {
								ZWLogger.printLog(this,
										"���ָ��� ������" + fieldType.getSimpleName()
												+ "�ֶ���:" + foreignColumnName
												+ "������DataBaseField������!");
							}

						} catch (NoSuchFieldException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else if (List.class.isAssignableFrom(fieldType)) {
						// ����Ǽ�������
						Type fc = field.getGenericType(); // �ؼ��ĵط��������List���ͣ��õ���Generic������
						if (fc == null) {
							ZWLogger.printLog(this,
									"���ָ��� ������" + fieldType.getSimpleName()
											+ "�ֶ���:" + foreignColumnName
											+ " list δ���÷���");
							continue;
						}

						if (fc instanceof ParameterizedType) {// ��3������Ƿ��Ͳ���������
							ParameterizedType pt = (ParameterizedType) fc;
							Class<?> genericClazz = (Class<?>) pt
									.getActualTypeArguments()[0]; // ��4��
																	// �õ��������class���Ͷ���
							Field objField;
							try {
								objField = genericClazz
										.getField(foreignColumnName);
								if (DBUtil.judgeFieldAvaid(objField)) {
									String fkColumnName = DBUtil
											.getMapFKCulmonName(
													foreignColumnName,
													fieldType);
									allColumnNames.add(fkColumnName);
									allforeignMaps.put(fkColumnName, objField);
									allforeignClassMaps.put(fkColumnName,
											fieldType);
								} else {
									ZWLogger.printLog(this, "���ָ��� ������"
											+ fieldType.getSimpleName()
											+ "�ֶ���:" + foreignColumnName
											+ "������DataBaseField������!");
								}
							} catch (NoSuchFieldException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					} else {
						ZWLogger.printLog(this, fieldType.getSimpleName()
								+ "���� BaseBo or List������ ��������Ϊ�����");
					}
					continue;
				}

				// ����ֶ���
				String fieldName = DBUtil.getFeildName(field);
				allColumnNames.add(fieldName);
			} else {

			}

		}

	}

	public static final TableInfo newInstance(Class<? extends ZWBo> clazz) {
		TableInfo mTableInfo = null;
		if (tableInfoFactory.containsKey(clazz)) {
			mTableInfo = tableInfoFactory.get(clazz);
		}

		if (mTableInfo == null) {
			mTableInfo = new TableInfo(clazz);
			tableInfoFactory.put(clazz, mTableInfo);
		}
		return mTableInfo;
	}

	/**
	 * ͨ�� field name�ҵ����ֶ���
	 * 
	 * @param fieldName
	 * @return
	 */
	public String getColumnByFieldStr(String fieldName) {
		for (int i = 0; i < allField.size(); i++) {
			Field field = allField.get(i);
			if (fieldName.equals(field.getName())) {
				return allColumnNames.get(i);
			}
		}
		return null;
	}

	/**
	 * ͨ�� field name�ҵ����ֶ��±�
	 * 
	 * @param fieldName
	 * @return
	 */
	public int getColumnIndexByFieldStr(String fieldName) {
		for (int i = 0; i < allField.size(); i++) {
			Field field = allField.get(i);
			if (fieldName.equals(field.getName())) {
				return i;
			}
		}
		ZWLogger.printLog(this, "�ࣺ"+clazz.toString()+" ��������:"+fieldName+" �Ҳ���~~");
		return -1;
	}
	
	/**
	 * ͨ������ �ֶ��±� �ҵ� ��Ӧ���Ե�Class����
	 * @param index
	 * @return
	 */
	public Class<?> getFieldType(int index) {
		String columnName = allColumnNames.get(index);
		Field field = allField.get(index);
		Class<?> fieldType = null;
		// �ж������Ƿ� ���
		if (allforeignClassMaps.containsKey(columnName)) {
			fieldType = allforeignClassMaps.get(columnName);
		} else {
			fieldType = field.getType();
		}
		return fieldType;
	}
}
