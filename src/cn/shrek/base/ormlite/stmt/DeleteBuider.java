package cn.shrek.base.ormlite.stmt;

import cn.shrek.base.ZWBo;
import cn.shrek.base.ZWDatabaseBo;

public class DeleteBuider extends StmtBuilder {
	
	public DeleteBuider(Class<? extends ZWDatabaseBo> clazz) {
		super(clazz);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getSql() {
		// TODO Auto-generated method stub
		return "DELETE FROM "+tableInfo.tableName+" WHERE "+getWhereSql();
	}

	protected void appendWhereStr(String str){
		if(whereBuffer.length() == 0){
//			whereBuffer.append(WHERE_KEYWORD);
		}
		whereBuffer.append(str);
	}
}
