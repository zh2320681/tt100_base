package cn.tt100.base.ormlite.dao;

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

}
