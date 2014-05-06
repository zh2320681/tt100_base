package cn.tt100.base.ormlite.dao;

public class BooleanTransfor implements DBTransforDao<Boolean, Integer> {

	@Override
	public Integer parseFieldToColumn(Boolean fieldObj) {
		// TODO Auto-generated method stub
		return fieldObj?1:0;
	}

	@Override
	public Boolean parseColumnToField(Integer columnObj) {
		// TODO Auto-generated method stub
		return columnObj == 1;
	}

	@Override
	public void specialDoing() {
		// TODO Auto-generated method stub
		
	}
	
}
