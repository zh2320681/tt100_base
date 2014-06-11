package cn.shrek.base.ormlite.dao;

import java.util.Date;

public class DateTransfor implements DBTransforDao<Date, Integer> {

	@Override
	public Integer parseFieldToColumn(Date fieldObj) {
		// TODO Auto-generated method stub
		return (int) fieldObj.getTime();
	}

	@Override
	public Date parseColumnToField(Integer columnObj) {
		// TODO Auto-generated method stub
		return new Date(columnObj);
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
	public boolean isFeildNullFeild(Date f) {
		// TODO Auto-generated method stub
		return f == null;
	}
	
}
