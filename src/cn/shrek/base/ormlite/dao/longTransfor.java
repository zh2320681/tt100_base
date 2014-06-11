package cn.shrek.base.ormlite.dao;


public class longTransfor implements DBTransforDao<Long, String> {

	@Override
	public String parseFieldToColumn(Long fieldObj) {
		// TODO Auto-generated method stub
		return fieldObj+"";
	}

	@Override
	public Long parseColumnToField(String columnObj) {
		// TODO Auto-generated method stub
		return Long.parseLong(columnObj);
	}

	@Override
	public void specialDoing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getFeildValueNull() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFeildNullFeild(Long f) {
		// TODO Auto-generated method stub
		return f == null;
	}
	
}
