package cn.tt100.base.ormlite.dao;

import cn.tt100.base.ormlite.stmt.StmtBuilder;

public class StringTransfor implements DBTransforDao<String, String> {

	@Override
	public String parseFieldToColumn(String fieldObj) {
		// TODO Auto-generated method stub
		return "'"+fieldObj+"'";
	}

	@Override
	public String parseColumnToField(String columnObj) {
		// TODO Auto-generated method stub
		return columnObj;
	}

	@Override
	public void specialDoing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getFeildValueNull() {
		// TODO Auto-generated method stub
		return StmtBuilder.NULL_STR;
	}

	@Override
	public boolean isFeildNullFeild(String f) {
		// TODO Auto-generated method stub
		return StmtBuilder.NULL_STR.equals(f);
	}

}
