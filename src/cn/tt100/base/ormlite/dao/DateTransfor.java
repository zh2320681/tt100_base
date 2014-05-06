package cn.tt100.base.ormlite.dao;

import java.util.Date;

public class DateTransfor implements DBTransforDao<Date, Long> {

	@Override
	public Long parseFieldToColumn(Date fieldObj) {
		// TODO Auto-generated method stub
		return fieldObj.getTime();
	}

	@Override
	public Date parseColumnToField(Long columnObj) {
		// TODO Auto-generated method stub
		return new Date(columnObj);
	}

	@Override
	public void specialDoing() {
		// TODO Auto-generated method stub
		
	}
	
}
