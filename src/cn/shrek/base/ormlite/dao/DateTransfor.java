package cn.shrek.base.ormlite.dao;

import java.util.Date;

import cn.shrek.base.ormlite.DBUtil;

public class DateTransfor implements DBTransforDao<Date, String> {
	
	@Override
	public String parseFieldToColumn(Date fieldObj) {
		return String.format("%tF %tT",fieldObj,fieldObj);
	}

	@Override
	public Date parseColumnToField(String columnObj) {
		// TODO Auto-generated method stub
		return DBUtil.getFormatDate(columnObj);
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
