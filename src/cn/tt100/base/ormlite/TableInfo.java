package cn.tt100.base.ormlite;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import cn.tt100.base.BaseBo;
import cn.tt100.base.util.BaseLog;

public class TableInfo<T extends BaseBo, ID> {
	private static final Map<Class<? extends BaseBo>, TableInfo<? extends BaseBo, ?>> tableInfoFactory = Collections
			.synchronizedMap(new WeakHashMap<Class<? extends BaseBo>, TableInfo<? extends BaseBo, ?>>());
	//所有字段名
	public List<String> allColumnNames;
	//所有属性
	public List<Field> allField;
	//所有外键
	public Map<String, Field> allforeignMaps;
	public Class<? extends BaseBo> clazz;
	public String tableName;

	
	private TableInfo(Class<? extends BaseBo> clazz){
    this.clazz = clazz;
    tableName = DBUtil.getTableName(clazz);;
    allField = new ArrayList<Field>();
    allColumnNames = new ArrayList<String>();
    
    this.allforeignMaps = new HashMap<String, Field>();
    Field[] fields = clazz.getDeclaredFields();
    for(Field field : fields){
    	
    }
    
    
    int i = arrayOfField.length;
    int j = 0;
    Field localField1;
    DatabaseField localDatabaseField;
    while (true)
    {
      if (j >= i)
        return;
      localField1 = arrayOfField[j];
      localDatabaseField = (DatabaseField)localField1.getAnnotation(DatabaseField.class);
      if ((localDatabaseField != null) && (isSupportType(localField1)))
        break;
      j += 1;
    }
    boolean bool1 = this.allField.add(localField1);
    Class localClass1 = localField1.getType();
    String str2 = localDatabaseField.foreignColumnName();
    if ((str2 != null) && (!"".equals(str2)))
    {
      if ((BaseBo.class.isAssignableFrom(localClass1)) || (List.class.isAssignableFrom(localClass1)))
        break label264;
      String str3 = String.valueOf(localClass1.getSimpleName());
      StringBuilder localStringBuilder1 = new StringBuilder(str3);
      String str4 = "不是 BaseBo or List的子类 不能设置为外键！";
      String str5 = str4;
      BaseLog.printLog(this, str5);
    }
    while (true)
    {
      label237: List localList = this.allColumnNames;
      String str6 = DBUtil.getFeildName(localField1);
      boolean bool2 = localList.add(str6);
      break;
      label264: if (((DatabaseTable)localClass1.getAnnotation(DatabaseTable.class) != null) || (List.class.isAssignableFrom(localClass1)))
      {
        Field localField2 = null;
        while (true)
        {
          try
          {
            if (!BaseBo.class.isAssignableFrom(localClass1))
              break label360;
            localField2 = localClass1.getDeclaredField(str2);
            if (localField2 == null)
              break label470;
            String str7 = DBUtil.getMapFKCulmonName(str2, localClass1);
            Object localObject = this.allforeignMaps.put(str7, localField2);
            boolean bool3 = this.allColumnNames.add(str7);
          }
          catch (NoSuchFieldException localNoSuchFieldException)
          {
            localNoSuchFieldException.printStackTrace();
          }
          break label237;
          label360: Type localType = localField1.getGenericType();
          if (!(localType instanceof ParameterizedType))
            continue;
          localClass1 = (Class)((ParameterizedType)localType).getActualTypeArguments()[0];
          if (!BaseBo.class.isAssignableFrom(localClass1))
          {
            String str8 = "List的泛型";
            StringBuilder localStringBuilder2 = new StringBuilder(str8);
            String str9 = localClass1.getSimpleName();
            StringBuilder localStringBuilder3 = localStringBuilder2.append(str9);
            String str10 = "不是 BaseBo  不能设置为外键！";
            String str11 = str10;
            BaseLog.printLog(this, str11);
            continue;
          }
          localField2 = localClass1.getDeclaredField(str2);
        }
        label470: String str12 = String.valueOf(localClass1.getSimpleName());
        StringBuilder localStringBuilder4 = new StringBuilder(str12);
        String str13 = "表中叫";
        StringBuilder localStringBuilder5 = localStringBuilder4.append(str13).append(str2);
        String str14 = "成员变量找不到,不能设置为外键！";
        String str15 = str14;
        BaseLog.printLog(this, str15);
        continue;
      }
      BaseLog.printLog(this, localClass1.getName() +" 类型没有DatabaseTable的注解,无法创建关系~~");
    }
  }

	public static final TableInfo<? extends BaseBo, ?> newInstance(
			Class<? extends BaseBo> clazz) {
		TableInfo<? extends BaseBo, ?> mTableInfo = null;
		if (tableInfoFactory.containsKey(clazz)){
			mTableInfo = tableInfoFactory.get(clazz);
		}
			
		if (mTableInfo == null) {
			mTableInfo = new TableInfo(clazz);
			tableInfoFactory.put(clazz,
					mTableInfo);
		}
		return mTableInfo;
	}

	/**
	 * 是否是 支持的类型
	 * @param mField
	 * @return
	 */
	public boolean isSupportType(Field mField) {
		Class<?> clazz = mField.getType();
		if (!clazz.isPrimitive()
				&& !String.class.isAssignableFrom(clazz)
				&& !clazz.isAssignableFrom(Date.class)
				&& !clazz.isAssignableFrom(Calendar.class)
				&& !Number.class.isAssignableFrom(clazz)
				&& !List.class.isAssignableFrom(clazz)
				&& !BaseBo.class.isAssignableFrom(clazz)) {
			BaseLog.printLog(this, clazz.getName()+ " 不支持的数据类型");
			return false;
		}
		return true;
	}
}
