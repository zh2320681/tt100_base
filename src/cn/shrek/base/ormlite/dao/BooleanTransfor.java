package cn.shrek.base.ormlite.dao;

public class BooleanTransfor implements DBTransforDao<Boolean, Integer> {

	@Override
	public Integer parseFieldToColumn(Boolean fieldObj) {
		// TODO Auto-generated method stub
		return fieldObj?1:0;
	}

	@Override
	public Boolean parseColumnToField(Integer columnObj) {
		// TODO Auto-generated method stub
		if(columnObj == null){
			return false;
		}
		return columnObj == 1;
	}

	@Override
	public void specialDoing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getFeildValueNull() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFeildNullFeild(Boolean f) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
